package com.example.model

/**
 * Data model for produce comparison records and historical scans.
 */
data class ProduceComparisonItem(
  val id: String,
  val name: String,
  val freshnessScore: Int,
  val qualityStage: String,
  val qualityWindow: String,
  val scanDateFormatted: String,
  val reasons: List<String>,
  val isBestPick: Boolean = false,
  val visualNote: String = ""
)

/**
 * Data model for historical scan entries.
 */
data class ScanHistoryRecord(
  val id: String,
  val produceName: String,
  val freshnessScore: Int,
  val qualityStage: String,
  val qualityWindow: String,
  val dateTimeDisplay: String,
  val reasons: List<String> = emptyList(),
  val imageUri: String? = null
)
