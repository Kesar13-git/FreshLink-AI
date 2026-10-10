"""Evaluate the serving prediction pipeline against a labeled image directory."""

import argparse
import csv
import json
import re
from collections.abc import Callable
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from freshlink_api.ai_model.catalog import PRODUCE_NAMES

CLASSES = ("Fresh", "Moderate", "Poor")
IMAGE_SUFFIXES = {".bmp", ".jpeg", ".jpg", ".png", ".webp"}
LABEL_PREFIX = re.compile(r"^(semi[\s_-]*fresh|fresh|rotten)(?:\b|[\s_-])", re.IGNORECASE)

PredictionFunction = Callable[[bytes, str], dict[str, Any]]


@dataclass(frozen=True)
class EvaluationSample:
    image_path: Path
    produce: str
    expected_stage: str


def _parse_stage(folder_name: str) -> str:
    match = LABEL_PREFIX.match(folder_name.strip())
    if match is None:
        raise ValueError(
            f"Cannot determine freshness label from folder name '{folder_name}'. "
            "Expected a Fresh, Semi-fresh, or Rotten prefix."
        )

    label = re.sub(r"[\s_-]+", "", match.group(1)).casefold()
    return {"fresh": "Fresh", "semifresh": "Moderate", "rotten": "Poor"}[label]


def _parse_produce(folder_name: str) -> str:
    folded_name = folder_name.casefold()
    for produce in sorted(PRODUCE_NAMES.values(), key=len, reverse=True):
        if re.search(rf"(?<![a-z]){re.escape(produce.casefold())}(?![a-z])", folded_name):
            return produce

    raise ValueError(
        f"Cannot determine a supported produce category from folder name '{folder_name}'."
    )


def discover_samples(dataset_root: Path) -> list[EvaluationSample]:
    """Read class labels from top-level folders such as 'Semi fresh Banana(4-7)'."""
    if not dataset_root.is_dir():
        raise FileNotFoundError(f"Dataset directory does not exist: {dataset_root}")

    samples: list[EvaluationSample] = []
    observed_stages: set[str] = set()
    folders = sorted(path for path in dataset_root.iterdir() if path.is_dir())
    if not folders:
        raise ValueError(f"No class folders found in dataset directory: {dataset_root}")

    for folder in folders:
        expected_stage = _parse_stage(folder.name)
        produce = _parse_produce(folder.name)
        image_paths = sorted(
            path
            for path in folder.rglob("*")
            if path.is_file() and path.suffix.casefold() in IMAGE_SUFFIXES
        )
        if not image_paths:
            raise ValueError(f"No supported images found in class folder: {folder}")

        observed_stages.add(expected_stage)
        samples.extend(
            EvaluationSample(path, produce, expected_stage) for path in image_paths
        )

    missing_stages = set(CLASSES) - observed_stages
    if missing_stages:
        raise ValueError(
            "Dataset must contain samples from all freshness classes; missing: "
            + ", ".join(sorted(missing_stages))
        )

    return samples


def _predict_image(image_bytes: bytes, produce_hint: str) -> dict[str, Any]:
    from freshlink_api.ai_model.inference import predict

    return predict(image_bytes, produce_hint)


def _classification_metrics(
    expected: list[str], predicted: list[str]
) -> dict[str, Any]:
    matrix = [[0 for _ in CLASSES] for _ in CLASSES]
    for actual, result in zip(expected, predicted, strict=True):
        matrix[CLASSES.index(actual)][CLASSES.index(result)] += 1

    per_class: dict[str, dict[str, float | int]] = {}
    for index, class_name in enumerate(CLASSES):
        true_positive = matrix[index][index]
        support = sum(matrix[index])
        predicted_count = sum(row[index] for row in matrix)
        precision = true_positive / predicted_count if predicted_count else 0.0
        recall = true_positive / support if support else 0.0
        f1 = (
            2 * precision * recall / (precision + recall)
            if precision + recall
            else 0.0
        )
        per_class[class_name] = {
            "precision": precision,
            "recall": recall,
            "f1_score": f1,
            "support": support,
        }

    count = len(expected)
    accuracy = sum(matrix[index][index] for index in range(len(CLASSES))) / count
    return {
        "sample_count": count,
        "accuracy": accuracy,
        "macro_precision": sum(
            float(metrics["precision"]) for metrics in per_class.values()
        )
        / len(CLASSES),
        "macro_recall": sum(float(metrics["recall"]) for metrics in per_class.values())
        / len(CLASSES),
        "macro_f1_score": sum(
            float(metrics["f1_score"]) for metrics in per_class.values()
        )
        / len(CLASSES),
        "class_order": list(CLASSES),
        "per_class": per_class,
        "confusion_matrix": matrix,
    }


def evaluate_dataset(
    dataset_root: Path, predictor: PredictionFunction | None = None
) -> dict[str, Any]:
    samples = discover_samples(dataset_root)
    run_prediction = predictor or _predict_image
    expected: list[str] = []
    predicted: list[str] = []

    for sample in samples:
        result = run_prediction(sample.image_path.read_bytes(), sample.produce)
        stage = result.get("quality_stage")
        if stage not in CLASSES:
            raise ValueError(
                f"Model returned unsupported quality stage {stage!r} "
                f"for image {sample.image_path}"
            )
        expected.append(sample.expected_stage)
        predicted.append(stage)

    metrics = _classification_metrics(expected, predicted)
    per_produce: dict[str, dict[str, float | int]] = {}
    for produce in sorted({sample.produce for sample in samples}):
        produce_pairs = [
            (sample.expected_stage, result)
            for sample, result in zip(samples, predicted, strict=True)
            if sample.produce == produce
        ]
        produce_metrics = _classification_metrics(
            [actual for actual, _ in produce_pairs],
            [result for _, result in produce_pairs],
        )
        per_produce[produce] = {
            "sample_count": produce_metrics["sample_count"],
            "accuracy": produce_metrics["accuracy"],
            "macro_f1_score": produce_metrics["macro_f1_score"],
        }

    metrics["dataset_root"] = str(dataset_root.resolve())
    metrics["model"] = "MobileNetV3-Small"
    metrics["per_produce"] = per_produce
    metrics["prediction_contract"] = (
        "Final quality_stage returned by the same predict() function used by the API"
    )
    metrics["samples"] = [
        {
            "image": str(sample.image_path.relative_to(dataset_root)),
            "produce": sample.produce,
            "expected_stage": sample.expected_stage,
            "predicted_stage": result,
        }
        for sample, result in zip(samples, predicted, strict=True)
    ]
    return metrics


def write_results(results: dict[str, Any], output_dir: Path) -> None:
    output_dir.mkdir(parents=True, exist_ok=True)
    report_path = output_dir / "evaluation_report.json"
    report_path.write_text(json.dumps(results, indent=2) + "\n", encoding="utf-8")

    matrix_path = output_dir / "confusion_matrix.csv"
    with matrix_path.open("w", newline="", encoding="utf-8") as report_file:
        writer = csv.writer(report_file)
        writer.writerow(["expected/predicted", *results["class_order"]])
        for class_name, row in zip(
            results["class_order"], results["confusion_matrix"], strict=True
        ):
            writer.writerow([class_name, *row])

    print(f"Evaluated {results['sample_count']} images")
    print(f"Accuracy: {results['accuracy']:.4f}")
    print(f"Macro F1: {results['macro_f1_score']:.4f}")
    print(f"Report: {report_path}")
    print(f"Confusion matrix: {matrix_path}")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Evaluate FreshLink's production prediction pipeline."
    )
    parser.add_argument(
        "--dataset-root",
        required=True,
        type=Path,
        help="Held-out directory containing labeled class folders.",
    )
    parser.add_argument(
        "--output-dir",
        required=True,
        type=Path,
        help="Directory for the JSON report and confusion-matrix CSV.",
    )
    args = parser.parse_args()
    write_results(evaluate_dataset(args.dataset_root), args.output_dir)


if __name__ == "__main__":
    main()
