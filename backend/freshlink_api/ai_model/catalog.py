"""Produce categories supported by the trained freshness model."""

PRODUCE_NAMES = {
    "banana": "Banana",
    "bittermelon": "Bittermelon",
    "cucumber": "Cucumber",
    "eggplant": "Eggplant",
    "orange": "Orange",
    "papaya": "Papaya",
    "pineapple": "Pineapple",
    "tomato": "Tomato",
}


class UnsupportedProduceError(ValueError):
    """Raised when the model does not support the requested produce."""


def normalize_produce(produce_hint: str) -> str:
    if not isinstance(produce_hint, str):
        raise UnsupportedProduceError("Produce category must be a string.")

    produce = produce_hint.strip().casefold()
    if produce not in PRODUCE_NAMES:
        supported = ", ".join(sorted(PRODUCE_NAMES.values()))
        raise UnsupportedProduceError(
            f"Unsupported produce: '{produce_hint}'. Supported produce: {supported}"
        )

    return produce
