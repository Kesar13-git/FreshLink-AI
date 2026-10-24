from typing import Annotated

from fastapi import APIRouter, Depends, Query
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from freshlink_api.api.schemas import HistoryRecord
from freshlink_api.db.models import AnalysisHistory
from freshlink_api.db.session import get_db_session

router = APIRouter(tags=["history"])


@router.get("/history", response_model=list[HistoryRecord])
async def get_history(
    session: Annotated[AsyncSession, Depends(get_db_session)],
    limit: Annotated[int, Query(ge=1, le=200)] = 50,
) -> list[HistoryRecord]:
    result = await session.scalars(
        select(AnalysisHistory).order_by(AnalysisHistory.created_at.desc()).limit(limit)
    )
    return [HistoryRecord.model_validate(record) for record in result]
