from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from freshlink_api.api.schemas import (
    DemandForecast,
    InventoryCreate,
    InventoryRecord,
    PricingRecommendation,
    ReplenishmentRecommendation,
    ShelfLifeRecord,
    WasteRiskRecord,
)
from freshlink_api.db.models import InventoryItem
from freshlink_api.db.session import get_db_session

router = APIRouter(tags=["retailer"])


async def load_inventory(session: AsyncSession) -> list[InventoryItem]:
    return list(await session.scalars(select(InventoryItem).order_by(InventoryItem.name)))


def risk_for(item: InventoryItem) -> str:
    days_of_supply = (
        item.stock_units / item.avg_daily_demand if item.avg_daily_demand > 0 else float("inf")
    )
    if item.quality_window_days <= 2 or days_of_supply > item.quality_window_days:
        return "High"
    if item.quality_window_days <= 5 or item.stock_units <= item.reorder_point:
        return "Medium"
    return "Low"


@router.get("/inventory", response_model=list[InventoryRecord])
async def get_inventory(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> list[InventoryRecord]:
    return [InventoryRecord.model_validate(item) for item in await load_inventory(session)]


@router.post(
    "/inventory",
    response_model=InventoryRecord,
    status_code=status.HTTP_201_CREATED,
)
async def add_inventory_item(
    item: InventoryCreate,
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> InventoryRecord:
    record = InventoryItem(**item.model_dump())
    session.add(record)
    await session.commit()
    await session.refresh(record)
    return InventoryRecord.model_validate(record)


@router.get("/shelf-life", response_model=list[ShelfLifeRecord])
async def get_shelf_life(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> list[ShelfLifeRecord]:
    items = await load_inventory(session)
    return [
        ShelfLifeRecord(
            product_id=item.id,
            product_name=item.name,
            stock_units=item.stock_units,
            unit=item.unit,
            estimated_quality_window_days=item.quality_window_days,
            estimated_days_of_supply=(
                round(item.stock_units / item.avg_daily_demand, 1)
                if item.avg_daily_demand > 0
                else None
            ),
        )
        for item in items
    ]


@router.get("/demand", response_model=list[DemandForecast])
async def get_demand(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> list[DemandForecast]:
    forecasts = []
    for item in await load_inventory(session):
        trend = 18 if item.avg_daily_demand >= 6 else 9 if item.avg_daily_demand >= 4 else -4
        forecasts.append(
            DemandForecast(
                product_id=item.id,
                product_name=item.name,
                current_stock=item.stock_units,
                avg_daily_demand=item.avg_daily_demand,
                forecast_7_day=round(item.avg_daily_demand * 7),
                trend_percent=trend,
                trend_label="Rising" if trend > 9 else "Stable" if trend > 0 else "Softening",
            )
        )
    return forecasts


@router.get("/waste-risk", response_model=list[WasteRiskRecord])
async def get_waste_risk(
    session: Annotated[AsyncSession, Depends(get_db_session)],
    include_low: Annotated[bool, Query()] = False,
) -> list[WasteRiskRecord]:
    records = []
    for item in await load_inventory(session):
        risk = risk_for(item)
        if risk == "Low" and not include_low:
            continue
        is_high = risk == "High"
        records.append(
            WasteRiskRecord(
                product_id=item.id,
                product_name=item.name,
                stock_units=item.stock_units,
                estimated_quality_window=f"{item.quality_window_days} days",
                risk=risk,
                reason=(
                    "Estimated quality window is short relative to current stock."
                    if is_high
                    else "Sell-through and quality window should be monitored."
                ),
                suggested_action=(
                    "Consider a manual markdown or promotion."
                    if is_high
                    else "Monitor daily demand and quality."
                ),
            )
        )
    return records


@router.get("/pricing", response_model=list[PricingRecommendation])
async def get_pricing(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> list[PricingRecommendation]:
    recommendations = []
    for item in await load_inventory(session):
        risk = risk_for(item)
        if risk == "Low" and item.quality_score >= 80:
            continue
        discount = 20 if risk == "High" else 10 if risk == "Medium" else 0
        recommendations.append(
            PricingRecommendation(
                product_id=item.id,
                product_name=item.name,
                current_price=item.current_price,
                suggested_price=round(item.current_price * (1 - discount / 100), 2),
                discount_percent=discount,
                reason=(
                    "Short estimated quality window with higher waste risk."
                    if risk == "High"
                    else "A modest markdown may improve sell-through."
                ),
                confidence=item.quality_score,
            )
        )
    return recommendations


@router.get("/replenishment", response_model=list[ReplenishmentRecommendation])
async def get_replenishment(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> list[ReplenishmentRecommendation]:
    recommendations = []
    for item in await load_inventory(session):
        if item.stock_units > item.reorder_point and item.stock_units >= item.avg_daily_demand * 3:
            continue
        order_quantity = max(
            round(item.avg_daily_demand * 7 + item.reorder_point - item.stock_units),
            0,
        )
        if order_quantity == 0:
            continue
        urgent = item.stock_units <= item.reorder_point
        recommendations.append(
            ReplenishmentRecommendation(
                product_id=item.id,
                product_name=item.name,
                current_stock=item.stock_units,
                recommended_order_qty=order_quantity,
                supplier=item.supplier,
                reason=(
                    "Stock is at or below the reorder point."
                    if urgent
                    else "Projected 7-day demand may reduce safety stock."
                ),
                urgency="High" if urgent else "Plan",
            )
        )
    return recommendations


@router.delete("/inventory/{item_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_inventory_item(
    item_id: str,
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> None:
    item = await session.get(InventoryItem, item_id)
    if item is None:
        raise HTTPException(status_code=404, detail="Inventory item not found")
    await session.delete(item)
    await session.commit()
