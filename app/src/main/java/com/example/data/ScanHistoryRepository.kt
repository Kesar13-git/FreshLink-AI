package com.example.data

import com.example.model.ProduceAnalysisResult
import com.example.model.ProduceComparisonItem
import com.example.model.ScanHistoryRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Repository for managing scan history records and comparison items.
 *
 * Requirements:
 * 1. Automatically creates a scan-history record when produce analysis completes.
 * 2. Each record contains produce name, freshness score, quality stage, quality window, and date/time.
 * 3. Stores records locally in-memory for the current application session.
 * 4. Displays actual saved records in Scan History.
 * 5. New scans appear at the top of the history list (LIFO).
 * 6. Persists across navigation between screens during the app session.
 * 7. Keeps initial sample/mock records ONLY when no user-generated scans exist yet.
 * 8. Easily decoupled for future connection to backend/database through Raina's module.
 */
object ScanHistoryRepository {

  private val fullDateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
  private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

  // =========================================================================
  // SECTION: Sample / Mock Records
  // Used strictly as fallback placeholders when the user has not performed
  // any scans in the session yet. These can be cleanly removed once Raina's
  // cloud database module is integrated.
  // =========================================================================
  val sampleRecords: List<ScanHistoryRecord> = listOf(
    ScanHistoryRecord(
      id = "sample-tomato-1",
      produceName = "Tomato",
      freshnessScore = 88,
      qualityStage = "Fresh",
      qualityWindow = "3–4 days",
      dateTimeDisplay = "Sample Record • 10:24 AM",
      reasons = listOf("Good visible colour", "Low visible damage", "Firm appearance")
    ),
    ScanHistoryRecord(
      id = "sample-apple-2",
      produceName = "Apple",
      freshnessScore = 76,
      qualityStage = "Good",
      qualityWindow = "5–7 days",
      dateTimeDisplay = "Sample Record • 04:15 PM",
      reasons = listOf("Solid colour", "Minor surface blemishes", "No major visible damage")
    ),
    ScanHistoryRecord(
      id = "sample-banana-3",
      produceName = "Banana",
      freshnessScore = 82,
      qualityStage = "Ripe",
      qualityWindow = "2–3 days",
      dateTimeDisplay = "Sample Record • 02:40 PM",
      reasons = listOf("Appropriate yellow colour", "Moderate ripeness", "Limited dark areas")
    )
  )

  // =========================================================================
  // In-memory session store of user-generated scans
  // =========================================================================
  private val _userScans = mutableListOf<ScanHistoryRecord>()

  // Reactive StateFlow: emits user scans if available, otherwise sample fallback
  private val _historyFlow = MutableStateFlow<List<ScanHistoryRecord>>(sampleRecords)
  val historyFlow: StateFlow<List<ScanHistoryRecord>> = _historyFlow.asStateFlow()

  /**
   * Returns true if the user has performed any scans during this app session.
   */
  fun hasUserScans(): Boolean = synchronized(this) {
    _userScans.isNotEmpty()
  }

  /**
   * Returns the list of actual user-generated scan records.
   */
  fun getUserScans(): List<ScanHistoryRecord> = synchronized(this) {
    _userScans.toList()
  }

  /**
   * Automatically creates a history record from a successful produce analysis result
   * and prepends it to the top of the history list.
   */
  @Synchronized
  fun addScanResult(result: ProduceAnalysisResult) {
    val formattedDate = formatTimestamp(result.timestamp)

    val newRecord = ScanHistoryRecord(
      id = result.id,
      produceName = result.produce,
      freshnessScore = result.freshnessScore,
      qualityStage = result.qualityStage,
      qualityWindow = result.qualityWindow,
      dateTimeDisplay = formattedDate,
      reasons = result.reasons,
      imageUri = result.imageUri
    )

    // Prepend to top of the list so newest scans appear first
    _userScans.add(0, newRecord)

    // Update the public flow with the actual user scans
    _historyFlow.value = _userScans.toList()
  }

  /**
   * Clears user scans (useful for testing or resetting session state).
   */
  @Synchronized
  fun clearUserScans() {
    _userScans.clear()
    _historyFlow.value = sampleRecords
  }

  /**
   * Generates comparison items from active scan records.
   * If user has scanned items, compares actual user scans; otherwise compares sample records.
   * Automatically designates "Best Pick" based on the highest freshness score.
   */
  fun getComparisonItems(): List<ProduceComparisonItem> {
    val current = _historyFlow.value
    if (current.isEmpty()) return emptyList()

    val maxScore = current.maxOfOrNull { it.freshnessScore } ?: 0

    return current.map { item ->
      ProduceComparisonItem(
        id = item.id,
        name = item.produceName,
        freshnessScore = item.freshnessScore,
        qualityStage = item.qualityStage,
        qualityWindow = item.qualityWindow,
        scanDateFormatted = item.dateTimeDisplay,
        reasons = item.reasons,
        isBestPick = item.freshnessScore == maxScore,
        visualNote = when {
          item.freshnessScore >= 85 -> "Excellent overall visual freshness with long shelf window."
          item.freshnessScore >= 75 -> "Good commercial retail quality; suitable for consumption this week."
          else -> "Shorter remaining shelf-life. Best utilized promptly."
        }
      )
    }
  }

  private fun formatTimestamp(timestamp: Long): String {
    val now = Calendar.getInstance()
    val scanTime = Calendar.getInstance().apply { timeInMillis = timestamp }

    return if (now.get(Calendar.YEAR) == scanTime.get(Calendar.YEAR) &&
      now.get(Calendar.DAY_OF_YEAR) == scanTime.get(Calendar.DAY_OF_YEAR)
    ) {
      "Today, " + timeFormat.format(Date(timestamp))
    } else {
      fullDateFormat.format(Date(timestamp))
    }
  }
}
