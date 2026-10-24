import logging
from typing import Annotated

from fastapi import APIRouter, Depends, File, Form, HTTPException, UploadFile
from pydantic import ValidationError
from sqlalchemy.ext.asyncio import AsyncSession
from starlette.concurrency import run_in_threadpool

from freshlink_api.ai_model import service
from freshlink_api.ai_model.catalog import (
    PRODUCE_NAMES,
    normalize_produce,
)
from freshlink_api.api.schemas import PredictionResponse
from freshlink_api.db.models import AnalysisHistory
from freshlink_api.db.session import get_db_session

router = APIRouter(tags=["prediction"])
logger = logging.getLogger(__name__)


@router.post("/predict", response_model=PredictionResponse)
async def predict(
    image: Annotated[UploadFile, File()],
    session: Annotated[AsyncSession, Depends(get_db_session)],
    produce: Annotated[str, Form()] = "Tomato",
) -> PredictionResponse:
    image_bytes = await image.read()
    if not image_bytes:
        raise HTTPException(status_code=422, detail="Image file must not be empty")

    try:
        normalized_produce = normalize_produce(produce)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc

    try:
        prediction_data = await run_in_threadpool(
            service.predict_produce, image_bytes, PRODUCE_NAMES[normalized_produce]
        )
    except (TypeError, ValueError) as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    except Exception as exc:
        logger.exception("Freshness model inference failed")
        raise HTTPException(
            status_code=503,
            detail="Freshness analysis is temporarily unavailable. Please try again later.",
        ) from exc

    try:
        prediction = PredictionResponse.model_validate(prediction_data)
    except (TypeError, ValidationError) as exc:
        logger.exception("Freshness model returned an invalid response")
        raise HTTPException(
            status_code=503,
            detail="Freshness analysis returned an invalid result. Please try again later.",
        ) from exc

    session.add(
        AnalysisHistory(
            produce=prediction.produce,
            freshness_score=prediction.freshness_score,
            quality_stage=prediction.quality_stage,
            quality_window=prediction.quality_window,
            reasons=prediction.reasons,
        )
    )
    await session.commit()
    return prediction
