package com.example.data

import com.example.model.ProduceAnalysisResult

/**
 * Repository interface for produce freshness and quality analysis.
 *
 * ARCHITECTURE DESIGN NOTE:
 * This interface decouples the UI layer from the AI inference engine.
 * Currently backed by [MockProduceAnalysisRepositoryImpl] for prototype demonstration.
 * In production, switch to a FastAPI / Retrofit implementation without touching any Composable screens.
 */
interface ProduceAnalysisRepository {
  /**
   * Analyzes an image of fresh produce to estimate visual freshness, quality stage, and window.
   *
   * @param imageUri Optional local URI of captured photo or gallery selection
   * @param produceHint Optional produce hint or manual category if selected by user
   * @return [Result] enclosing [ProduceAnalysisResult]
   */
  suspend fun analyzeProduce(
    imageUri: String?,
    produceHint: String? = null
  ): Result<ProduceAnalysisResult>
}
