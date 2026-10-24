from pathlib import Path

from inference import predict, UnsupportedProduceError


# ============================================================
# FRESHLINK AI - ALL PRODUCE INFERENCE TEST
# ============================================================

PROJECT_ROOT = Path(__file__).resolve().parent.parent

SUPPORTED_PRODUCE = [
    "Banana",
    "Bittermelon",
    "Cucumber",
    "Eggplant",
    "Orange",
    "Papaya",
    "Pineapple",
    "Tomato"
]

TEST_ROOT = PROJECT_ROOT.parent / "FreshLink_AI" / "dataset_split" / "test"

print("=" * 70)
print("FRESHLINK AI - SUPPORTED PRODUCE TEST")
print("=" * 70)

passed = 0
failed = 0

for produce in SUPPORTED_PRODUCE:

    # Use a Fresh image for the integration test
    image_dir = TEST_ROOT / produce / "Fresh"

    image_files = [
        p for p in image_dir.iterdir()
        if p.suffix.lower() in [
            ".jpg",
            ".jpeg",
            ".png",
            ".bmp",
            ".webp"
        ]
    ]

    if not image_files:
        print(f"\n❌ {produce}: No test image found")
        failed += 1
        continue

    image_path = image_files[0]

    try:

        with open(image_path, "rb") as file:
            image_bytes = file.read()

        result = predict(
            image_bytes=image_bytes,
            produce_hint=produce
        )

        # Verify exact response contract
        expected_keys = {
            "produce",
            "freshness_score",
            "quality_stage",
            "quality_window",
            "reasons"
        }

        if set(result.keys()) != expected_keys:
            raise ValueError(
                f"Incorrect response keys: {result.keys()}"
            )

        if not isinstance(result["freshness_score"], int):
            raise ValueError(
                "freshness_score is not an integer"
            )

        if not 0 <= result["freshness_score"] <= 100:
            raise ValueError(
                "freshness_score outside 0-100"
            )

        if not isinstance(result["reasons"], list):
            raise ValueError(
                "reasons is not a list"
            )

        print(f"\n✅ {produce}")
        print(f"   Image: {image_path.name}")
        print(f"   Score: {result['freshness_score']}")
        print(f"   Stage: {result['quality_stage']}")
        print(f"   Window: {result['quality_window']}")
        print(f"   Reasons: {result['reasons']}")

        passed += 1

    except Exception as error:

        print(f"\n❌ {produce}")
        print(f"   Error: {error}")

        failed += 1


# ============================================================
# UNSUPPORTED PRODUCE TEST
# ============================================================

print("\n" + "-" * 70)
print("UNSUPPORTED PRODUCE TEST")
print("-" * 70)

try:

    # Reuse a valid image but request unsupported Apple
    tomato_dir = TEST_ROOT / "Tomato" / "Fresh"

    image_path = next(
        p for p in tomato_dir.iterdir()
        if p.suffix.lower() in [
            ".jpg",
            ".jpeg",
            ".png",
            ".bmp",
            ".webp"
        ]
    )

    with open(image_path, "rb") as file:
        image_bytes = file.read()

    predict(
        image_bytes=image_bytes,
        produce_hint="Apple"
    )

    print("❌ Apple: Unsupported produce was NOT rejected")
    failed += 1

except UnsupportedProduceError as error:

    print("✅ Apple: Correctly rejected")
    print(f"   Message: {error}")

    passed += 1


# ============================================================
# FINAL RESULT
# ============================================================

print("\n" + "=" * 70)
print("INTEGRATION TEST SUMMARY")
print("=" * 70)

print(f"Passed: {passed}")
print(f"Failed: {failed}")

if failed == 0:
    print("\n✅ ALL INFERENCE TESTS PASSED")
else:
    print("\n⚠️ SOME TESTS FAILED")

print("=" * 70)
