import json
from pathlib import Path

import pytest

from freshlink_api.ai_model.evaluation import (
    _classification_metrics,
    discover_samples,
    evaluate_dataset,
    write_results,
)


def test_discover_samples_maps_dataset_folder_labels(tmp_path: Path) -> None:
    folder_names = (
        "Fresh Banana(1-4)",
        "Semi fresh Banana(4-7)",
        "Rotten Banana(7-13)",
    )
    for folder_name in folder_names:
        folder = tmp_path / folder_name
        folder.mkdir()
        (folder / "sample.jpeg").touch()

    samples = discover_samples(tmp_path)

    assert [sample.expected_stage for sample in samples] == [
        "Fresh",
        "Poor",
        "Moderate",
    ]
    assert all(sample.produce == "Banana" for sample in samples)


def test_discover_samples_rejects_unknown_folder_names(tmp_path: Path) -> None:
    (tmp_path / "unknown").mkdir()

    with pytest.raises(ValueError, match="Cannot determine freshness label"):
        discover_samples(tmp_path)


def test_classification_metrics_include_per_class_and_confusion_matrix() -> None:
    results = _classification_metrics(
        ["Fresh", "Moderate", "Poor", "Poor"],
        ["Fresh", "Poor", "Poor", "Moderate"],
    )

    assert results["sample_count"] == 4
    assert results["accuracy"] == 0.5
    assert results["confusion_matrix"] == [
        [1, 0, 0],
        [0, 0, 1],
        [0, 1, 1],
    ]
    assert results["per_class"]["Moderate"]["support"] == 1


def test_evaluation_runs_inference_and_writes_reproducible_outputs(
    tmp_path: Path,
) -> None:
    dataset_root = tmp_path / "dataset"
    folder_names = (
        "Fresh Banana",
        "Semi fresh Banana",
        "Rotten Banana",
    )
    for folder_name in folder_names:
        folder = dataset_root / folder_name
        folder.mkdir(parents=True)
        (folder / "sample.jpeg").write_bytes(b"image")

    predicted_stages = iter(["Fresh", "Poor", "Moderate"])

    def predictor(image_bytes: bytes, produce_hint: str) -> dict[str, str]:
        assert image_bytes == b"image"
        assert produce_hint == "Banana"
        return {"quality_stage": next(predicted_stages)}

    results = evaluate_dataset(dataset_root, predictor=predictor)
    output_dir = tmp_path / "results"
    write_results(results, output_dir)

    report = json.loads((output_dir / "evaluation_report.json").read_text(encoding="utf-8"))
    confusion_matrix = (output_dir / "confusion_matrix.csv").read_text(encoding="utf-8")
    assert report["accuracy"] == 1.0
    assert report["sample_count"] == 3
    assert report["per_produce"]["Banana"]["accuracy"] == 1.0
    assert "expected/predicted,Fresh,Moderate,Poor" in confusion_matrix
