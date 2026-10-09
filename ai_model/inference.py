from pathlib import Path
from io import BytesIO

import torch
import torch.nn as nn
from torchvision import transforms, models
from PIL import Image


# ============================================================
# FRESHLINK AI - INFERENCE MODULE
# ============================================================

PROJECT_ROOT = Path(__file__).resolve().parent.parent

MODEL_PATH = (
    PROJECT_ROOT
    / "models"
    / "freshness_mobilenetv3_small.pth"
)

IMAGE_SIZE = 224

SUPPORTED_PRODUCE = {
    "banana",
    "bittermelon",
    "cucumber",
    "eggplant",
    "orange",
    "papaya",
    "pineapple",
    "tomato",
}

CLASSES = [
    "Fresh",
    "Moderate",
    "Poor"
]

CLASS_TO_INDEX = {
    "Fresh": 0,
    "Moderate": 1,
    "Poor": 2
}

DEVICE = torch.device(
    "cuda" if torch.cuda.is_available() else "cpu"
)


# ============================================================
# IMAGE PREPROCESSING
# ============================================================

_transform = transforms.Compose([
    transforms.Resize((IMAGE_SIZE, IMAGE_SIZE)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])


# ============================================================
# MODEL LOADING
# ============================================================

def _load_model():

    checkpoint = torch.load(
        MODEL_PATH,
        map_location=DEVICE,
        weights_only=False
    )

    model = models.mobilenet_v3_small(
        weights=None
    )

    model.classifier[3] = nn.Linear(
        model.classifier[3].in_features,
        len(CLASSES)
    )

    model.load_state_dict(
        checkpoint["model_state_dict"]
    )

    model = model.to(DEVICE)

    model.eval()

    return model


# Load ONCE when the backend imports this module.
MODEL = _load_model()


# ============================================================
# CUSTOM ERROR
# ============================================================

class UnsupportedProduceError(ValueError):
    """Raised when the requested produce is not supported."""
    pass


# ============================================================
# PRODUCE NORMALIZATION
# ============================================================

def _normalize_produce(produce_hint: str) -> str:

    if not isinstance(produce_hint, str):
        raise UnsupportedProduceError(
            "produce_hint must be a string."
        )

    produce = produce_hint.strip().lower()

    if produce not in SUPPORTED_PRODUCE:
        supported = ", ".join(
            sorted(SUPPORTED_PRODUCE)
        )

        raise UnsupportedProduceError(
            f"Unsupported produce: '{produce_hint}'. "
            f"Supported produce: {supported}"
        )

    return produce


def _display_produce(produce: str) -> str:

    names = {
        "banana": "Banana",
        "bittermelon": "Bittermelon",
        "cucumber": "Cucumber",
        "eggplant": "Eggplant",
        "orange": "Orange",
        "papaya": "Papaya",
        "pineapple": "Pineapple",
        "tomato": "Tomato"
    }

    return names[produce]


# ============================================================
# SCORE CALCULATION
# ============================================================

def _calculate_score(probabilities):

    # Visual-quality anchor values.
    #
    # Fresh    -> 90
    # Moderate -> 55
    # Poor     -> 15

    score = (
        probabilities[0] * 90
        + probabilities[1] * 55
        + probabilities[2] * 15
    )

    score = round(float(score))

    return max(0, min(100, score))


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

def _generate_reasons(stage: str):

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

def predict(
    image_bytes: bytes,
    produce_hint: str
) -> dict:

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

    produce = _normalize_produce(
        produce_hint
    )

    # ----------------------------
    # Validate image
    # ----------------------------

    if not isinstance(image_bytes, bytes):
        raise ValueError(
            "image_bytes must be bytes."
        )

    if len(image_bytes) == 0:
        raise ValueError(
            "image_bytes is empty."
        )

    try:

        image = Image.open(
            BytesIO(image_bytes)
        ).convert("RGB")

    except Exception as exc:

        raise ValueError(
            "Invalid or unsupported image data."
        ) from exc

    # ----------------------------
    # Preprocess
    # ----------------------------

    tensor = _transform(image)

    tensor = tensor.unsqueeze(0)

    tensor = tensor.to(DEVICE)

    # ----------------------------
    # Model inference
    # ----------------------------

    with torch.no_grad():

        outputs = MODEL(tensor)

        probabilities = torch.softmax(
            outputs,
            dim=1
        )[0]

    probabilities = probabilities.cpu().tolist()

    # ----------------------------
    # Score
    # ----------------------------

    score = _calculate_score(
        probabilities
    )

    # ----------------------------
    # Stage
    # ----------------------------

    stage = _score_to_stage(
        score
    )

    # ----------------------------
    # Quality window
    # ----------------------------

    quality_window = _quality_window(
        stage
    )

    # ----------------------------
    # Reasons
    # ----------------------------

    reasons = _generate_reasons(
        stage
    )

    # ----------------------------
    # Exact API contract
    # ----------------------------

    return {
        "produce": _display_produce(produce),

        "freshness_score": score,

        "quality_stage": stage,

        "quality_window": quality_window,

        "reasons": reasons
    }