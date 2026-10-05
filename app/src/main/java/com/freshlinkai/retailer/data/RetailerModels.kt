package com.freshlinkai.retailer.data

enum class QualityStatus(val label: String) {
    FRESH("Fresh"),
    GOOD("Good"),
    WATCH("Watch"),
    HIGH_RISK("High Risk")
}

enum class WasteRisk(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val stockUnits: Int,
    val unit: String,
    val currentPrice: Double,
    val qualityScore: Int,
    val qualityStatus: QualityStatus,
    val estimatedQualityWindow: String,
    val wasteRisk: WasteRisk,
    val avgDailyDemand: Double,
    val reorderPoint: Int,
    val supplier: String,
    val lastUpdated: String
)

data class DemandForecast(
    val productId: String,
    val productName: String,
    val currentStock: Int,
    val avgDailyDemand: Double,
    val forecast7Day: Int,
    val trendPercent: Int,
    val trendLabel: String
)

data class PricingRecommendation(
    val productId: String,
    val productName: String,
    val currentPrice: Double,
    val suggestedPrice: Double,
    val discountPercent: Int,
    val reason: String,
    val confidence: Int
)

data class ReplenishmentRecommendation(
    val productId: String,
    val productName: String,
    val currentStock: Int,
    val recommendedOrderQty: Int,
    val supplier: String,
    val reason: String,
    val urgency: String
)

data class WasteRiskItem(
    val productId: String,
    val productName: String,
    val stockUnits: Int,
    val estimatedQualityWindow: String,
    val risk: WasteRisk,
    val reason: String,
    val suggestedAction: String
)

data class RetailAnalytics(
    val salesUnitsToday: Int,
    val salesUnitsWeek: Int,
    val estimatedWasteUnits: Int,
    val estimatedWasteRate: Double,
    val stockTurnover: Double,
    val markdownRecovery: Double,
    val categoryPerformance: List<CategoryPerformance>,
    val weeklySales: List<Int>
)

data class CategoryPerformance(
    val category: String,
    val salesUnits: Int,
    val sharePercent: Int
)

data class RetailerDashboard(
    val storeName: String,
    val totalSkus: Int,
    val lowStockCount: Int,
    val highWasteRiskCount: Int,
    val estimatedAtRiskValue: Double,
    val todaySalesUnits: Int,
    val inventoryValue: Double
)
