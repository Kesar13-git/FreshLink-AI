# FreshLink AI

### AI-Powered Fresh Produce Decision Support Platform

> **From Freshness Detection to Freshness Intelligence.**

FreshLink AI is an AI-powered mobile platform designed to help consumers assess the visible quality and freshness of fresh produce using a smartphone camera, while also helping retailers make data-driven decisions related to shelf-life, demand, pricing, inventory and food-waste reduction.

The system combines **Consumer Mode** and **Retailer Mode** within a single Android application.

---

## 🌱 Problem Statement

Consumers often find it difficult to judge the freshness and quality of fruits and vegetables, especially when they have limited experience in selecting produce.

At the same time, retailers face challenges such as:

- Produce quality deterioration
- Short remaining quality windows
- Overstocking and understocking
- Unpredictable demand
- Avoidable food waste
- Difficulty deciding when discounts should be recommended

FreshLink AI aims to address these challenges through AI-assisted visual quality assessment and retail decision support.

---

## 💡 Proposed Solution

FreshLink AI provides two integrated modes:

### 👤 Consumer Mode

Consumers can:

- Capture or select an image of produce
- Select the produce category
- Receive an estimated visual freshness/quality score
- View the quality stage
- View an estimated quality window
- Understand the visual reasons behind the result
- Compare produce items
- Get a recommended "Best Pick"
- Learn freshness and storage practices
- View previous scan history

### 🏪 Retailer Mode

Retailers can:

- View a retailer dashboard
- Manage inventory information
- Monitor produce quality
- Monitor estimated quality windows
- Identify waste-risk items
- View demand forecasts
- Receive dynamic pricing/discount recommendations
- Receive replenishment recommendations
- View basic retail analytics

> **Note:** The consumer scan flow now uses the trained model through the FastAPI backend.
> Retailer screens still use sample data by default, although backend inventory and
> recommendation endpoints are available.

---

## 🧠 AI-Based Freshness Assessment

FreshLink AI's FastAPI backend runs a trained MobileNetV3-Small classifier to estimate visible
produce quality from images. It validates the selected produce category, preprocesses the uploaded
image, runs inference, and returns a score, stage, estimated quality window, and explanatory text.

The inference pipeline takes:

**Input:**
- Produce image
- Produce category

**Output:**
- Produce name
- Freshness/quality score
- Quality stage
- Estimated quality window
- Visual reasons supporting the result

Backend mode sends the selected produce category and image to the FastAPI service, which runs
the trained MobileNetV3-Small model. Offline mock mode remains available for demos.

### Supported Produce Categories

The trained model supports Banana, Bittermelon, Cucumber, Eggplant, Orange, Papaya, Pineapple,
and Tomato. The scan screen uses this same category set so unsupported produce is not sent to
the model.

---

## ⚠️ Important Limitation

FreshLink AI provides an **estimated visual quality/freshness assessment**.

The system does **not** claim to:

- Determine whether food is completely safe to eat
- Detect pathogens
- Detect internal contamination
- Detect chemical residues
- Determine an exact expiry date

The estimated quality window is intended as a decision-support estimate rather than an exact expiry prediction.

---

## 📱 Application Features

### Consumer Mode

- Welcome Screen
- Home Dashboard
- Scan Produce
- Camera Input
- Gallery Input
- Produce Category Selection
- AI Analysis Result
- Freshness Score
- Quality Stage
- Estimated Quality Window
- Visual Reasons
- Compare Produce
- Best Pick
- Freshness Guide
- Storage Guidance
- Scan History
- Profile / Settings

### Retailer Mode

- Retailer Dashboard
- Inventory Management
- Quality Monitoring
- Shelf-Life Monitoring
- Waste-Risk Identification
- Demand Forecasting
- Dynamic Pricing Recommendations
- Replenishment Recommendations
- Retail Analytics

---

## 🏗️ System Architecture

```text
                 FreshLink AI
                      │
          ┌───────────┴───────────┐
          │                       │
    Consumer Mode           Retailer Mode
          │                       │
          └───────────┬───────────┘
                      │
                Android App
                      │
          Kotlin + Jetpack Compose
                      │
          Produce Analysis Repository
                      │
          ┌───────────┴───────────┐
          │                       │
     Mock Repository        FastAPI Backend
                                  │
                         AI / ML Model
                                  │
                         Supabase Database
                                  │
                              AWS Hosting
```

## FastAPI backend

The backend in `backend/` uses FastAPI, async SQLAlchemy, and `asyncpg` for direct
Supabase PostgreSQL access. Apply `supabase/migrations/20261008210000_create_freshlink_tables.sql`
in the Supabase SQL Editor before using database-backed routes. `GET /health` runs `SELECT 1`
and returns HTTP 503 if the database check fails.

From the `backend` directory, set up and run the service in PowerShell:

```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -e ".[dev,ai]"
Copy-Item .env.example .env
# Set SUPABASE_DB_URL in .env using the Supabase Dashboard's Session pooler connection string.
uvicorn freshlink_api.main:app --reload --host 0.0.0.0
```

The AI extra installs PyTorch and torchvision, which are large downloads. The trained checkpoint
is packaged at `backend/freshlink_api/ai_model/models/freshness_mobilenetv3_small.pth`; the
dataset is not required to serve predictions. The API documentation is available at
`http://127.0.0.1:8000/docs`. The current routes are:

- `POST /predict`: multipart `produce` and `image`; runs the trained model and persists successful
  predictions to analysis history. Unsupported categories and invalid images return HTTP 422;
  unavailable model inference returns HTTP 503.
- `GET /history`: newest analysis records first; optional `limit` from 1 through 200.
- `GET` / `POST` / `DELETE /inventory`: list, create, and remove inventory records.
- `GET /shelf-life`, `/demand`, `/waste-risk`, `/pricing`, and `/replenishment`: estimates
  calculated from persisted inventory inputs.

`POST /inventory` expects `name`, `category`, `stock_units`, `unit`, `current_price`,
`quality_score`, `quality_window_days`, `avg_daily_demand`, `reorder_point`, and `supplier`.
Use the interactive API docs to add initial inventory; the retailer endpoints intentionally
return empty lists until inventory records exist.

Retail recommendations are deterministic prototype estimates, not model outputs.
Android now defaults to `AnalysisMode.BACKEND`; set `CURRENT_ANALYSIS_MODE` to
`AnalysisMode.MOCK` in `app/src/main/java/com/example/config/AppConfig.kt` to work offline.
The emulator base URL is `http://10.0.2.2:8000`; keep the server bound to `0.0.0.0` for
emulator access. Camera captures and selected gallery images are uploaded as actual bytes.
Retailer inventory, demand, waste-risk, pricing, and replenishment screens also read the
backend API. Sales/analytics values remain unavailable because no sales endpoint or data model
has been requested yet.

### ML evaluation

The backend includes the trained checkpoint, inference/preprocessing code, supported-produce
catalog, evaluation tooling, and model inference/evaluation tests. The supplied model project did
not contain training code, so checkpoint training is not currently reproducible from this
repository. The source project’s evaluation report and confusion matrix are preserved in
`backend/ml/reference_results/`; their reported 84.02% accuracy has not been independently
reproduced.

To evaluate the model against a held-out dataset, see [`backend/ml/README.md`](backend/ml/README.md).
The evaluator writes a JSON report with per-class metrics and predictions plus a confusion-matrix
CSV. The supplied dataset is kept outside the repository; it is not needed for inference and does
not have a documented held-out split, so it must not be treated as an unbiased test set as-is.

Validate the backend with:

```powershell
python -m pytest
python -m pytest tests/test_api.py::test_predict_persists_exact_mock_contract_to_history
ruff check .
```

Keep database credentials in `backend/.env`; never commit that file. These endpoints currently
have no authentication or user/store scoping and are suitable only for local prototyping until
an authentication and authorization policy is added.
