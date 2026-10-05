package com.freshlinkai.retailer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.freshlinkai.retailer.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RetailerUiState(
    val loading: Boolean = true,
    val dashboard: RetailerDashboard? = null,
    val inventory: List<InventoryItem> = emptyList(),
    val demand: List<DemandForecast> = emptyList(),
    val pricing: List<PricingRecommendation> = emptyList(),
    val replenishment: List<ReplenishmentRecommendation> = emptyList(),
    val wasteRisks: List<WasteRiskItem> = emptyList(),
    val analytics: RetailAnalytics? = null,
    val message: String? = null
)

class RetailerViewModel(
    private val repository: RetailerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetailerUiState())
    val uiState: StateFlow<RetailerUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            _uiState.value = RetailerUiState(
                loading = false,
                dashboard = repository.getDashboard(),
                inventory = repository.getInventory(),
                demand = repository.getDemandForecast(),
                pricing = repository.getPricingRecommendations(),
                replenishment = repository.getReplenishmentRecommendations(),
                wasteRisks = repository.getWasteRisks(),
                analytics = repository.getAnalytics()
            )
        }
    }

    fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    companion object {
        fun factory(repository: RetailerRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RetailerViewModel(repository) as T
                }
            }
    }
}
