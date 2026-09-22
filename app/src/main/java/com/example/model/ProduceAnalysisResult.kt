package com.example.model

import java.util.UUID

/**
 * Core result data model for fresh produce quality assessment.
 *
 * Designed to directly map to the future Python/FastAPI computer vision response:
 * {
 *   "produce": "Tomato",
 *   "freshness_score": 88,
 *   "quality_stage": "Fresh",
 *   "quality_window": "3-4 days",
 *   "reasons": [
 *     "Good visible colour",
 *     "Low visible damage"
 *   ]
 * }
 */
data class ProduceAnalysisResult(
  val id: String = UUID.randomUUID().toString(),
  val produce: String,
  val freshnessScore: Int,
  val qualityStage: String,
  val qualityWindow: String,
  val reasons: List<String>,
  val timestamp: Long = System.currentTimeMillis(),
  val imageUri: String? = null,
  val isMock: Boolean = true,
  val disclaimer: String = "Visual estimate only. Actual quality may vary depending on storage and internal condition."
)
