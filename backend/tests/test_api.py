import asyncio
from collections.abc import AsyncIterator, Iterator

import pytest
from fastapi.testclient import TestClient
from PIL import Image
from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.pool import NullPool

from freshlink_api.db.models import Base
from freshlink_api.db.session import get_db_session
from freshlink_api.main import app


@pytest.fixture
def client(tmp_path, monkeypatch) -> Iterator[TestClient]:
    monkeypatch.setenv(
        "SUPABASE_DB_URL",
        "postgresql://postgres.test:password@localhost:5432/postgres",
    )
    engine = create_async_engine(
        f"sqlite+aiosqlite:///{tmp_path / 'test.db'}",
        poolclass=NullPool,
    )

    async def create_schema() -> None:
        async with engine.begin() as connection:
            await connection.run_sync(Base.metadata.create_all)

    asyncio.run(create_schema())
    session_factory = async_sessionmaker(engine, expire_on_commit=False)

    async def override_db_session() -> AsyncIterator[AsyncSession]:
        async with session_factory() as session:
            yield session

    app.dependency_overrides[get_db_session] = override_db_session
    try:
        with TestClient(app) as test_client:
            yield test_client
    finally:
        app.dependency_overrides.pop(get_db_session, None)
        asyncio.run(engine.dispose())


def test_health_reports_database_connection(client: TestClient) -> None:
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok", "database": "connected"}


def test_predict_persists_model_contract_to_history(
    client: TestClient, monkeypatch: pytest.MonkeyPatch
) -> None:
    from freshlink_api.ai_model import service

    monkeypatch.setattr(
        service,
        "predict_produce",
        lambda image_bytes, produce_hint: {
            "produce": "Tomato",
            "freshness_score": 88,
            "quality_stage": "Fresh",
            "quality_window": "3-4 days",
            "reasons": ["Good visible quality", "Low estimated deterioration"],
        },
    )
    response = client.post(
        "/predict",
        data={"produce": "tomato"},
        files={"image": ("tomato.jpg", b"test image", "image/jpeg")},
    )

    assert response.status_code == 200
    assert response.json() == {
        "produce": "Tomato",
        "freshness_score": 88,
        "quality_stage": "Fresh",
        "quality_window": "3-4 days",
        "reasons": ["Good visible quality", "Low estimated deterioration"],
    }
    history = client.get("/history")
    assert history.status_code == 200
    assert len(history.json()) == 1
    assert history.json()[0]["produce"] == "Tomato"
    assert history.json()[0]["freshness_score"] == 88
    assert history.json()[0]["id"]
    assert history.json()[0]["created_at"]


def test_predict_runs_real_model_and_persists_result(client: TestClient) -> None:
    from io import BytesIO

    image_buffer = BytesIO()
    Image.new("RGB", (64, 64), color=(128, 96, 64)).save(image_buffer, format="JPEG")
    response = client.post(
        "/predict",
        data={"produce": "Tomato"},
        files={"image": ("tomato.jpg", image_buffer.getvalue(), "image/jpeg")},
    )

    assert response.status_code == 200
    prediction = response.json()
    assert prediction["produce"] == "Tomato"
    assert 0 <= prediction["freshness_score"] <= 100
    assert prediction["quality_stage"] in {"Fresh", "Moderate", "Poor"}

    history = client.get("/history")
    assert history.status_code == 200
    assert len(history.json()) == 1
    assert history.json()[0]["freshness_score"] == prediction["freshness_score"]


def test_predict_requires_a_nonempty_image(client: TestClient) -> None:
    missing_image = client.post("/predict", data={"produce": "Tomato"})
    empty_image = client.post(
        "/predict",
        data={"produce": "Tomato"},
        files={"image": ("empty.jpg", b"", "image/jpeg")},
    )

    assert missing_image.status_code == 422
    assert empty_image.status_code == 422
    assert client.get("/history").json() == []


def test_predict_rejects_unsupported_produce(client: TestClient) -> None:
    response = client.post(
        "/predict",
        data={"produce": "Apple"},
        files={"image": ("apple.jpg", b"image bytes", "image/jpeg")},
    )

    assert response.status_code == 422
    assert "Apple" in response.json()["detail"]
    assert client.get("/history").json() == []


def test_predict_reports_model_failure_without_persisting(
    client: TestClient, monkeypatch: pytest.MonkeyPatch
) -> None:
    from freshlink_api.ai_model import service

    def fail_inference(image_bytes: bytes, produce_hint: str) -> dict[str, object]:
        raise RuntimeError("model weights are unavailable")

    monkeypatch.setattr(service, "predict_produce", fail_inference)
    response = client.post(
        "/predict",
        data={"produce": "Tomato"},
        files={"image": ("tomato.jpg", b"valid image", "image/jpeg")},
    )

    assert response.status_code == 503
    assert "temporarily unavailable" in response.json()["detail"]
    assert client.get("/history").json() == []


def test_inventory_and_retail_recommendations_use_persisted_items(
    client: TestClient,
) -> None:
    item = {
        "name": "Spinach",
        "category": "Leafy Green",
        "stock_units": 4,
        "unit": "kg",
        "current_price": 70,
        "quality_score": 69,
        "quality_window_days": 2,
        "avg_daily_demand": 3.5,
        "reorder_point": 10,
        "supplier": "GreenFarm Foods",
    }
    created = client.post("/inventory", json=item)

    assert created.status_code == 201
    assert created.json()["name"] == "Spinach"

    inventory = client.get("/inventory").json()
    shelf_life = client.get("/shelf-life").json()
    demand = client.get("/demand").json()
    waste_risk = client.get("/waste-risk").json()
    pricing = client.get("/pricing").json()
    replenishment = client.get("/replenishment").json()

    assert len(inventory) == 1
    assert shelf_life[0]["estimated_quality_window_days"] == 2
    assert shelf_life[0]["estimated_days_of_supply"] == 1.1
    assert demand[0]["forecast_7_day"] == 24
    assert waste_risk[0]["risk"] == "High"
    assert pricing[0]["discount_percent"] == 20
    assert replenishment[0]["recommended_order_qty"] == 30
    assert replenishment[0]["urgency"] == "High"

    deleted = client.delete(f"/inventory/{created.json()['id']}")
    assert deleted.status_code == 204
    assert client.get("/inventory").json() == []


def test_inventory_rejects_negative_stock(client: TestClient) -> None:
    response = client.post(
        "/inventory",
        json={
            "name": "Tomato",
            "category": "Fruit",
            "stock_units": -1,
            "unit": "kg",
            "current_price": 48,
            "quality_score": 88,
            "quality_window_days": 3,
            "avg_daily_demand": 9,
            "reorder_point": 24,
            "supplier": "GreenFarm Foods",
        },
    )

    assert response.status_code == 422
