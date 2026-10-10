"""Lazy entry point for model inference, keeping API-only tests lightweight."""

from typing import Any


def predict_produce(image_bytes: bytes, produce_hint: str) -> dict[str, Any]:
    from freshlink_api.ai_model.inference import predict

    return predict(image_bytes=image_bytes, produce_hint=produce_hint)
