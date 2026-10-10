from datetime import datetime
from uuid import uuid4

from sqlalchemy import JSON, CheckConstraint, DateTime, Float, Integer, String, func
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class AnalysisHistory(Base):
    __tablename__ = "analysis_history"
    __table_args__ = (
        CheckConstraint(
            "freshness_score >= 0 AND freshness_score <= 100",
            name="ck_analysis_history_score_range",
        ),
    )

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=lambda: str(uuid4()))
    produce: Mapped[str] = mapped_column(String(100), nullable=False)
    freshness_score: Mapped[int] = mapped_column(Integer, nullable=False)
    quality_stage: Mapped[str] = mapped_column(String(50), nullable=False)
    quality_window: Mapped[str] = mapped_column(String(50), nullable=False)
    reasons: Mapped[list[str]] = mapped_column(JSON, nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), nullable=False, server_default=func.now()
    )


class InventoryItem(Base):
    __tablename__ = "inventory_items"
    __table_args__ = (
        CheckConstraint("stock_units >= 0", name="ck_inventory_stock_nonnegative"),
        CheckConstraint("current_price >= 0", name="ck_inventory_price_nonnegative"),
        CheckConstraint(
            "quality_score >= 0 AND quality_score <= 100", name="ck_inventory_score_range"
        ),
        CheckConstraint("quality_window_days >= 0", name="ck_inventory_window_nonnegative"),
        CheckConstraint("avg_daily_demand >= 0", name="ck_inventory_demand_nonnegative"),
        CheckConstraint("reorder_point >= 0", name="ck_inventory_reorder_nonnegative"),
    )

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=lambda: str(uuid4()))
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    category: Mapped[str] = mapped_column(String(100), nullable=False)
    stock_units: Mapped[int] = mapped_column(Integer, nullable=False)
    unit: Mapped[str] = mapped_column(String(30), nullable=False)
    current_price: Mapped[float] = mapped_column(Float, nullable=False)
    quality_score: Mapped[int] = mapped_column(Integer, nullable=False)
    quality_window_days: Mapped[int] = mapped_column(Integer, nullable=False)
    avg_daily_demand: Mapped[float] = mapped_column(Float, nullable=False)
    reorder_point: Mapped[int] = mapped_column(Integer, nullable=False)
    supplier: Mapped[str] = mapped_column(String(150), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), nullable=False, server_default=func.now()
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        server_default=func.now(),
        onupdate=func.now(),
    )
