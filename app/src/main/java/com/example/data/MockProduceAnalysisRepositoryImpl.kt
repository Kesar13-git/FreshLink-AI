package com.example.data

import com.example.model.ProduceAnalysisResult
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Mock implementation of [ProduceAnalysisRepository] for prototype demonstration.
 *
 * NOTE: For now, we are NOT using a real AI model.
 * These values are ONLY demonstration/mock values. They are not presented as predictions
 * from a real AI model.
 *
 * Later this will be swapped with FastAPIProduceAnalysisRepository connecting to
 * the Python/FastAPI computer vision microservice.
 */
class MockProduceAnalysisRepositoryImpl : ProduceAnalysisRepository {

  override suspend fun analyzeProduce(
    imageUri: String?,
    produceHint: String?
  ): Result<ProduceAnalysisResult> {
    // Validate image presence
    if (imageUri.isNullOrBlank()) {
      return Result.failure(
        IllegalArgumentException("Please capture or select a produce image before analyzing.")
      )
    }

    // Simulate network latency for demonstration (approx. 1.0s)
    delay(1000)

    val targetProduce = produceHint?.trim()?.takeIf { it.isNotBlank() } ?: "Tomato"

    val result = when (targetProduce.lowercase()) {
      "tomato" -> ProduceAnalysisResult(
        id = UUID.randomUUID().toString(),
        produce = "Tomato",
        freshnessScore = 88,
        qualityStage = "Fresh",
        qualityWindow = "3–4 days",
        reasons = listOf(
          "Good visible colour",
          "Low visible damage",
          "Firm appearance"
        ),
        imageUri = imageUri,
        isMock = true
      )

      "apple" -> ProduceAnalysisResult(
        id = UUID.randomUUID().toString(),
        produce = "Apple",
        freshnessScore = 76,
        qualityStage = "Good",
        qualityWindow = "5–7 days",
        reasons = listOf(
          "Solid colour",
          "Minor surface blemishes",
          "No major visible damage"
        ),
        imageUri = imageUri,
        isMock = true
      )

      "banana" -> ProduceAnalysisResult(
        id = UUID.randomUUID().toString(),
        produce = "Banana",
        freshnessScore = 82,
        qualityStage = "Ripe",
        qualityWindow = "2–3 days",
        reasons = listOf(
          "Appropriate yellow colour",
          "Moderate ripeness",
          "Limited dark areas"
        ),
        imageUri = imageUri,
        isMock = true
      )

      "spinach" -> ProduceAnalysisResult(
        id = UUID.randomUUID().toString(),
        produce = "Spinach",
        freshnessScore = 70,
        qualityStage = "Good",
        qualityWindow = "1–2 days",
        reasons = listOf(
          "Mostly green leaves",
          "Minor wilting",
          "No major visible damage"
        ),
        imageUri = imageUri,
        isMock = true
      )

      else -> ProduceAnalysisResult(
        id = UUID.randomUUID().toString(),
        produce = targetProduce.replaceFirstChar { it.uppercase() },
        freshnessScore = 80,
        qualityStage = "Good",
        qualityWindow = "3–4 days",
        reasons = listOf(
          "Standard visible colour",
          "Low surface damage",
          "Typical post-harvest condition"
        ),
        imageUri = imageUri,
        isMock = true
      )
    }

    return Result.success(result)
  }
}
