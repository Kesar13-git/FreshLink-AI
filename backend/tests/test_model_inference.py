from io import BytesIO

import pytest
from PIL import Image

from freshlink_api.ai_model.catalog import PRODUCE_NAMES, normalize_produce
from freshlink_api.ai_model.inference import predict


def test_supported_produce_categories_are_normalized() -> None:
    assert len(PRODUCE_NAMES) == 8
    assert normalize_produce("  TOMATO ") == "tomato"


def test_checkpoint_runs_inference_and_returns_api_contract() -> None:
    image_buffer = BytesIO()
    Image.new("RGB", (64, 64), color=(128, 96, 64)).save(image_buffer, format="JPEG")

    result = predict(image_buffer.getvalue(), "Tomato")

    assert set(result) == {
        "produce",
        "freshness_score",
        "quality_stage",
        "quality_window",
        "reasons",
    }
    assert result["produce"] == "Tomato"
    assert isinstance(result["freshness_score"], int)
    assert 0 <= result["freshness_score"] <= 100
    assert result["quality_stage"] in {"Fresh", "Moderate", "Poor"}
    assert result["quality_window"] in {"3-4 days", "1-2 days", "0-1 day"}
    assert result["reasons"]


def test_inference_rejects_invalid_image() -> None:
    with pytest.raises(ValueError, match="Invalid or unsupported image"):
        predict(b"not an image", "Tomato")


def test_inference_rejects_unsupported_produce() -> None:
    from freshlink_api.ai_model.catalog import UnsupportedProduceError

    with pytest.raises(UnsupportedProduceError):
        normalize_produce("Apple")
