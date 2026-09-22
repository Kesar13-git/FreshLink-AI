package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockProduceAnalysisRepositoryImpl
import com.example.data.ProduceAnalysisRepository
import com.example.data.ProduceAnalysisRepositoryProvider
import com.example.data.ScanHistoryRepository
import com.example.model.ProduceAnalysisResult
import com.example.model.ProduceComparisonItem
import com.example.model.ScanHistoryRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel orchestrating produce freshness analysis, recent scan history,
 * and multi-item comparison data.
 */
class FreshLinkViewModel(
  val analysisRepository: ProduceAnalysisRepository = ProduceAnalysisRepositoryProvider.provideRepository()
) : ViewModel() {

  // Current or most recent analysis result
  private val _currentAnalysisResult = MutableStateFlow<ProduceAnalysisResult?>(null)
  val currentAnalysisResult: StateFlow<ProduceAnalysisResult?> = _currentAnalysisResult.asStateFlow()

  // Chronological scan history
  val historyRecords: StateFlow<List<ScanHistoryRecord>> = ScanHistoryRepository.historyFlow

  // Multi-produce comparison list with "Best Pick" computation
  val comparisonItems: StateFlow<List<ProduceComparisonItem>> =
    ScanHistoryRepository.historyFlow.map {
      ScanHistoryRepository.getComparisonItems()
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = ScanHistoryRepository.getComparisonItems()
    )

  /**
   * Called when produce analysis finishes.
   * Updates current result and automatically appends to history and comparison.
   */
  fun onNewAnalysisResult(result: ProduceAnalysisResult) {
    _currentAnalysisResult.value = result
    ScanHistoryRepository.addScanResult(result)
  }

  fun setCurrentResultFromRecord(record: ScanHistoryRecord) {
    _currentAnalysisResult.value = ProduceAnalysisResult(
      id = record.id,
      produce = record.produceName,
      freshnessScore = record.freshnessScore,
      qualityStage = record.qualityStage,
      qualityWindow = record.qualityWindow,
      reasons = record.reasons.ifEmpty {
        listOf("Visual inspection record from history log")
      },
      imageUri = record.imageUri,
      isMock = true
    )
  }
}
