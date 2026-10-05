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

> **Note:** Retailer Mode currently uses mock/sample data. Backend, database and AI model integration will be added as development progresses.

---

## 🧠 AI-Based Freshness Assessment

FreshLink AI is designed to analyze visible characteristics of fresh produce from images.

The planned AI pipeline takes:

**Input:**
- Produce image
- Produce category

**Output:**
- Produce name
- Freshness/quality score
- Quality stage
- Estimated quality window
- Visual reasons supporting the result

The current Android prototype uses mock analysis data for demonstration. The trained computer-vision model will replace the mock analysis during the AI integration stage.

### Supported Produce Categories

The current prototype includes:

- 🍅 Tomato
- 🍎 Apple
- 🍌 Banana
- 🥬 Spinach

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

🛠️ Technology Stack
Mobile Application
- Kotlin
- Android
- Jetpack Compose
- Material 3
- Android Studio
Backend
- Python
- FastAPI
- REST API
Artificial Intelligence / Machine Learning
- Python
- Computer Vision
- OpenCV
- PyTorch / TensorFlow
- Scikit-learn
Database
- Supabase
Cloud / Deployment
- AWS
Version Control
- Git
- GitHub
🔌 Backend Integration
The Android application has been structured so that the analysis system can switch between mock data and the actual FastAPI backend.
Architecture
Android UI
     ↓
ProduceAnalysisRepository
     ↓
 ┌───────────────┬────────────────────┐
 │               │                    │
Mock Repository  FastAPI Repository   │
                 ↓                    │
             FastAPI                  │
                 ↓                    │
              AI Model                │
                 ↓                    │
             Supabase                 │

The planned analysis endpoint is:
POST /api/v1/analyze

The request will contain:
- Produce category
- Produce image
The expected response contains:
{
  "produce": "Tomato",
  "freshness_score": 88,
  "quality_stage": "Fresh",
  "quality_window": "3-4 days",
  "reasons": [
    "Good visible colour",
    "Low visible damage"
  ]
}

📊 Retail Decision Support
The Retailer Mode is designed to provide recommendations rather than automatically making business decisions.
Potential outputs include:
- Waste-risk level
- Demand forecast
- Suggested discount
- Suggested price
- Replenishment recommendation
- Inventory alerts
Pricing recommendations are intended to support retailer decision-making and do not automatically change product prices.
📂 Project Structure
FreshLink-AI/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   ├── com/example/
│           │   │   └── Consumer application files
│           │   │
│           │   └── com/freshlinkai/
│           │       └── retailer/
│           │           ├── data/
│           │           ├── ui/
│           │           └── RetailerMode.kt
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── .env.example
├── .gitignore
├── README.md
├── build.gradle.kts
└── settings.gradle.kts

🚀 Running the Application
Prerequisites
- Android Studio
- Android SDK
- JDK compatible with the project
- Android emulator or physical Android device
Steps
1. Clone the repository:
git clone https://github.com/Kesar13-git/FreshLink-AI.git

2. Open the project in Android Studio.
3. Allow Gradle to sync and download required dependencies.
4. Select an Android emulator or connect a physical Android device.
5. Build and run the application.
Current Prototype
The current prototype uses mock data for produce analysis and Retailer Mode.
Backend and AI model integration will replace these mock components during the integration stage.
🔄 Development Roadmap
Phase 1 — Application Prototype
- Consumer Android application
- Produce scanning interface
- Analysis result interface
- Comparison and Best Pick
- Freshness Guide
- History
- Retailer Mode
Phase 2 — AI Model Development
- Dataset preparation
- Image preprocessing
- Computer-vision model training
- Model evaluation
- Freshness/quality prediction
- Inference pipeline
Phase 3 — Backend & Database
- FastAPI backend
- Supabase database
- API integration
- Scan history storage
- Retailer data management
Phase 4 — Cloud Deployment
- Dockerization
- AWS deployment
- Backend integration with Android
- End-to-end testing
Phase 5 — Final Integration
- Android → FastAPI → AI Model → Supabase
- Retailer analytics integration
- Demand forecasting
- Dynamic pricing recommendations
- Replenishment recommendations
- Performance and accuracy evaluation
👩‍💻 Team
Member	Responsibility
Kesar Deaulkar	Consumer Android App & Final Integration
Manasvi Ambavale	AI / Computer Vision, Dataset & Model Evaluation
Raina Mitra	Retailer Mode
Shreya Gharat	Backend, AWS & Supabase


🎯 Project Goal
FreshLink AI aims to bridge the gap between freshness assessment, consumer decision-making and retail intelligence.
The long-term goal is to create a unified platform that helps:
Consumers
→ make better produce-selection decisions
Retailers
→ make better inventory, pricing and replenishment decisions
Food-Waste Reduction
→ identify produce that requires timely action
FreshLink AI doesn't just tell you whether produce looks fresh; it helps you decide what to buy and helps retailers decide what to sell first.

📌 Project Status
Current Status: Prototype + Retailer Mode Integration
✅ Consumer Android application
✅ Consumer scanning workflow
✅ Produce analysis UI
✅ Compare Produce
✅ Best Pick
✅ Freshness Guide
✅ Scan History
✅ Retailer Mode
✅ Retailer Dashboard
✅ Inventory
✅ Quality Monitoring
✅ Demand Forecasting UI
✅ Dynamic Pricing UI
✅ Replenishment UI
✅ GitHub integration  
🔄 AI model integration — In progress
🔄 FastAPI backend integration — In progress
🔄 Supabase integration — In progress
🔄 AWS deployment — Planned
🔄 End-to-end integration — Planned  
📄 Disclaimer
FreshLink AI is an academic/project prototype intended for decision support and demonstration purposes.
Visual freshness assessment cannot determine complete food safety, internal quality, pathogen contamination or chemical contamination. Results should not be treated as a guarantee of food safety.
📜 License
This project is developed as an academic project.
