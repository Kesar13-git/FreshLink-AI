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

