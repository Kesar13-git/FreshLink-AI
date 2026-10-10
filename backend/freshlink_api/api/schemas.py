from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field


class PredictionResponse(BaseModel):
    produce: str
    freshness_score: int = Field(ge=0, le=100)
    quality_stage: str
    quality_window: str
    reasons: list[str]


class HistoryRecord(PredictionResponse):
    id: str
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)


class InventoryCreate(BaseModel):
    name: str = Field(min_length=1, max_length=100)
    category: str = Field(min_length=1, max_length=100)
    stock_units: int = Field(ge=0)
    unit: str = Field(min_length=1, max_length=30)
    current_price: float = Field(ge=0)
    quality_score: int = Field(ge=0, le=100)
    quality_window_days: int = Field(ge=0)
    avg_daily_demand: float = Field(ge=0)
    reorder_point: int = Field(ge=0)
    supplier: str = Field(min_length=1, max_length=150)


class InventoryRecord(InventoryCreate):
    id: str
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)


class ShelfLifeRecord(BaseModel):
    product_id: str
    product_name: str
    stock_units: int
    unit: str
    estimated_quality_window_days: int
    estimated_days_of_supply: float | None


class DemandForecast(BaseModel):
    product_id: str
    product_name: str
    current_stock: int
    avg_daily_demand: float
    forecast_7_day: int
    trend_percent: int
    trend_label: str


class WasteRiskRecord(BaseModel):
    product_id: str
    product_name: str
    stock_units: int
    estimated_quality_window: str
    risk: str
    reason: str
    suggested_action: str


class PricingRecommendation(BaseModel):
    product_id: str
    product_name: str
    current_price: float
    suggested_price: float
    discount_percent: int
    reason: str
    confidence: int


class ReplenishmentRecommendation(BaseModel):
    product_id: str
    product_name: str
    current_stock: int
    recommended_order_qty: int
    supplier: str
    reason: str
    urgency: str
