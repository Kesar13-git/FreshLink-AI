# FreshLink ML workflow

## What is included

- `freshlink_api/ai_model/inference.py`: MobileNetV3-Small inference and the image preprocessing
  used by the API.
- `freshlink_api/ai_model/catalog.py` and `service.py`: supported produce categories and the
  lazy backend-facing inference entry point.
- `freshlink_api/ai_model/models/freshness_mobilenetv3_small.pth`: trained checkpoint used by
  the app through FastAPI.
- `freshlink_api/ai_model/evaluation.py`: reproducible evaluation of the same final
  `quality_stage` returned by the API.
- `tests/test_model_inference.py` and `tests/test_model_evaluation.py`: inference-contract,
  dataset parsing, and metric/report tests.
- `ml/reference_results/`: the original evaluation report and confusion-matrix image supplied
  with the model project.

The supplied model project did not contain training code, so this repository does not claim to
reproduce checkpoint training. Its evaluation command measures the existing checkpoint; it does
not retrain or fine-tune it.

## Dataset format and location

Keep the image dataset outside the application repository. It is not needed to run the API, and
committing all images would add hundreds of megabytes to the source repository. The supplied
dataset is currently outside this project at
`C:\Users\Shreya\Downloads\Processed Data\Processed Data`.

The evaluator expects its `--dataset-root` to contain one folder per produce/stage combination.
Folder names must begin with `Fresh`, `Semi fresh` (also accepts `Semi_Fresh`), or `Rotten`, and
include a supported produce name. For example:

```text
held-out-test/
  Fresh Banana/
  Semi fresh Banana/
  Rotten Banana/
  Fresh Tomato/
  Semi fresh Tomato/
  Rotten Tomato/
```

The evaluator maps those labels to the model's `Fresh`, `Moderate`, and `Poor` output classes,
respectively. Each folder may contain supported image files directly or in nested folders.

## Run an evaluation

From `backend/`, install the AI and development dependencies if they are not already installed:

```powershell
python -m pip install -e ".[dev,ai]"
```

Then point the evaluator at a **held-out test set**:

```powershell
python -m freshlink_api.ai_model.evaluation `
  --dataset-root "C:\path\to\held-out-test" `
  --output-dir "ml\evaluation_runs\checkpoint-evaluation"
```

The command writes `evaluation_report.json` (including per-image predictions, per-class precision,
recall, F1, support, and per-produce accuracy/F1) and `confusion_matrix.csv`. It uses the final
stage returned by the same `predict()` function as `/predict`, rather than a separate
evaluation-only label conversion.

The supplied `Processed Data` directory is organized by freshness and produce, but is not a
documented held-out split and includes augmented images. Do not use the entire directory to claim
generalization accuracy: first create a test split grouped by original source image, keeping every
augmented variant of an image in only one split. The evaluator itself does not split or augment
data.

## Existing reported result

`reference_results/evaluation_report.txt` reports 84.02% accuracy on 2,134 samples, and
`reference_results/confusion_matrix.png` is the corresponding supplied matrix. These are preserved
as source-project results, not independently reproduced here. The source project did not include
the exact test split, training/evaluation script, or split manifest needed to verify that score.

## Model limitations

The network predicts a visual freshness stage from the image. The selected produce name is
validated and included in the response, but it does not condition the classifier's logits. Results
are not food-safety determinations and are not exact expiry-date predictions.
