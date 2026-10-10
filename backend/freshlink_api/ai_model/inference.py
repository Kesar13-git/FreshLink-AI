from io import BytesIO
from pathlib import Path
from typing import Any

import torch
from PIL import Image
from torch import nn
from torchvision import models, transforms

from freshlink_api.ai_model.catalog import PRODUCE_NAMES, normalize_produce

# ============================================================
# FRESHLINK AI - INFERENCE MODULE
# ============================================================

MODEL_PATH = (
    Path(__file__).resolve().parent / "models" / "freshness_mobilenetv3_small.pth"
)

IMAGE_SIZE = 224

CLASSES = ["Fresh", "Moderate", "Poor"]

DEVICE = torch.device(
    "cuda" if torch.cuda.is_available() else "cpu"
)


# ============================================================
# IMAGE PREPROCESSING
# ============================================================

_transform = transforms.Compose(
    [
        transforms.Resize((IMAGE_SIZE, IMAGE_SIZE)),
        transforms.ToTensor(),
        transforms.Normalize(
            mean=[0.485, 0.456, 0.406],
            std=[0.229, 0.224, 0.225],
        ),
    ]
)


# ============================================================
# MODEL LOADING
# ============================================================

def _load_model() -> nn.Module:
    checkpoint = torch.load(
        MODEL_PATH,
        map_location=DEVICE,
        weights_only=True,
    )

    model = models.mobilenet_v3_small(weights=None)
    model.classifier[3] = nn.Linear(model.classifier[3].in_features, len(CLASSES))
    model.load_state_dict(checkpoint["model_state_dict"])
    model = model.to(DEVICE)
    model.eval()
    return model


# Load once on the first inference request.
MODEL = _load_model()


def _calculate_score(probabilities: list[float]) -> int:
    score = (
        probabilities[0] * 90 + probabilities[1] * 55 + probabilities[2] * 15
    )
    return max(0, min(100, round(score)))


# ============================================================
# QUALITY STAGE
# ============================================================

def _score_to_stage(score: int) -> str:
    if score >= 70:
        return "Fresh"

    if score >= 40:
        return "Moderate"

    return "Poor"


# ============================================================
# QUALITY WINDOW
# ============================================================

def _quality_window(stage: str) -> str:
    windows = {
        "Fresh": "3-4 days",
        "Moderate": "1-2 days",
        "Poor": "0-1 day"
    }

    return windows[stage]


# ============================================================
# HUMAN-READABLE REASONS
# ============================================================

def _generate_reasons(stage: str) -> list[str]:
    reasons = {

        "Fresh": [
            "Good visible quality",
            "Low estimated deterioration"
        ],

        "Moderate": [
            "Intermediate visible quality",
            "Some estimated deterioration"
        ],

        "Poor": [
            "Low visible quality",
            "Higher estimated deterioration"
        ]
    }

    return reasons[stage]


# ============================================================
# MAIN INFERENCE FUNCTION
# ============================================================

def predict(image_bytes: bytes, produce_hint: str) -> dict[str, Any]:

    """
    Predict visual freshness quality.

    Parameters
    ----------
    image_bytes : bytes
        JPEG/PNG image bytes received from the backend.

    produce_hint : str
        User-selected produce category.

    Returns
    -------
    dict
        Exact FreshLink response contract:

        {
            "produce": str,
            "freshness_score": int,
            "quality_stage": str,
            "quality_window": str,
            "reasons": list[str]
        }

    Raises
    ------
    ValueError
        If image bytes are invalid.

    UnsupportedProduceError
        If produce_hint is unsupported.
    """

    # ----------------------------
    # Validate produce
    # ----------------------------

    produce = normalize_produce(produce_hint)

    # ----------------------------
    # Validate image
    # ----------------------------

    if not isinstance(image_bytes, bytes):
        raise TypeError("image_bytes must be bytes.")
    if not image_bytes:
        raise ValueError("image_bytes is empty.")

    try:

        image = Image.open(BytesIO(image_bytes)).convert("RGB")
    except Exception as exc:
        raise ValueError("Invalid or unsupported image data.") from exc

    tensor = _transform(image).unsqueeze(0).to(DEVICE)
    with torch.inference_mode():
        probabilities = torch.softmax(MODEL(tensor), dim=1)[0].cpu().tolist()

    score = _calculate_score(probabilities)
    stage = _score_to_stage(score)
    return {
        "produce": PRODUCE_NAMES[produce],
        "freshness_score": score,
        "quality_stage": stage,
        "quality_window": _quality_window(stage),
        "reasons": _generate_reasons(stage),
    }