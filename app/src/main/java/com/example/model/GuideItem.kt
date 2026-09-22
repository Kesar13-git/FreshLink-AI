package com.example.model

/**
 * Data model for consumer produce freshness & quality selection guides.
 */
data class FreshnessGuideItem(
  val id: String,
  val produceName: String,
  val category: String, // Fruit, Vegetable, Leafy Green
  val positiveSigns: List<String>,
  val warningSigns: List<String>,
  val ripenessStages: List<String>,
  val storageAdvice: String,
  val idealTemperature: String,
  val typicalShelfLife: String
)
