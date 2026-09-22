package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Expected JSON response structure from FastAPI endpoint: POST /api/v1/analyze
 *
 * Example FastAPI payload:
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
@JsonClass(generateAdapter = true)
data class FastApiAnalyzeResponse(
  @Json(name = "produce") val produce: String? = null,
  @Json(name = "freshness_score") val freshnessScore: Int? = null,
  @Json(name = "quality_stage") val qualityStage: String? = null,
  @Json(name = "quality_window") val qualityWindow: String? = null,
  @Json(name = "reasons") val reasons: List<String>? = null
)
