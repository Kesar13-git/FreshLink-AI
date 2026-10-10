from collections.abc import AsyncIterator
from contextlib import asynccontextmanager

from fastapi import FastAPI
from sqlalchemy.engine import make_url
from sqlalchemy.ext.asyncio import async_sessionmaker, create_async_engine

from freshlink_api.api.routes.health import router as health_router
from freshlink_api.api.routes.history import router as history_router
from freshlink_api.api.routes.predict import router as predict_router
from freshlink_api.api.routes.retail import router as retail_router
from freshlink_api.core.config import get_settings


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncIterator[None]:
    settings = get_settings()
    database_url = make_url(settings.supabase_db_url.get_secret_value())
    if database_url.drivername in {"postgres", "postgresql"}:
        database_url = database_url.set(drivername="postgresql+asyncpg")

    engine = create_async_engine(
        database_url,
        pool_pre_ping=True,
        connect_args={"ssl": "require"},
    )
    app.state.session_factory = async_sessionmaker(engine, expire_on_commit=False)
    try:
        yield
    finally:
        await engine.dispose()


def create_app() -> FastAPI:
    application = FastAPI(
        title="FreshLink AI API",
        version="0.1.0",
        lifespan=lifespan,
    )
    application.include_router(health_router)
    application.include_router(predict_router)
    application.include_router(history_router)
    application.include_router(retail_router)
    return application


app = create_app()
