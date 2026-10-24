# FreshLink AI - Inference Module

## Overview

FreshLink AI provides visual produce-quality estimation using a
MobileNetV3-Small image classification model.

The current model predicts one of three visual quality stages:

- Fresh
- Moderate
- Poor

The model is intended for visual quality estimation only.

It is NOT a food-safety detector and does NOT provide an exact
expiry date.

---

## Supported Produce

The current model supports:

- Banana
- Bittermelon
- Cucumber
- Eggplant
- Orange
- Papaya
- Pineapple
- Tomato

Unsupported produce categories are rejected by the inference module.

---

## Model

Model:

MobileNetV3-Small

Task:

Freshness-stage classification

Classes:

Fresh / Moderate / Poor

Input:

RGB image

Input size:

224 x 224 pixels

Normalization:

Mean:
[0.485, 0.456, 0.406]

Standard deviation:
[0.229, 0.224, 0.225]

The inference module performs resizing and normalization internally.
The backend does not need to preprocess the image.

---

## Model Artifact

The trained model is stored at:

models/freshness_mobilenetv3_small.pth

The model is loaded once when the inference module is imported.

It is NOT loaded for every prediction request.

---

## Callable Interface

Use:

```python
from ai_model import predict

result = predict(
    image_bytes=image_bytes,
    produce_hint="Tomato"
)