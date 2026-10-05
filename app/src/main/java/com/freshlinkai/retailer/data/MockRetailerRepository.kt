package com.freshlinkai.retailer.data

interface RetailerRepository {
    suspend fun getDashboard(): RetailerDashboard
    suspend fun getInventory(): List<InventoryItem>
    suspend fun getDemandForecast(): List<DemandForecast>
    suspend fun getPricingRecommendations(): List<PricingRecommendation>
    suspend fun getReplenishmentRecommendations(): List<ReplenishmentRecommendation>
    suspend fun getWasteRisks(): List<WasteRiskItem>
    suspend fun getAnalytics(): RetailAnalytics
}

class MockRetailerRepository : RetailerRepository {

    private val inventory = listOf(
        InventoryItem(
            "tomato", "Tomato", "Fruit & Vegetable", 42, "kg", 48.0, 88,
            QualityStatus.FRESH, "3–4 days", WasteRisk.LOW, 9.0, 24,
            "GreenFarm Foods", "Today, 10:24 AM"
        ),
        InventoryItem(
            "apple", "Apple", "Fruit", 18, "kg", 120.0, 76,
            QualityStatus.GOOD, "4–7 days", WasteRisk.MEDIUM, 5.0, 20,
            "Fresh Valley", "Today, 09:48 AM"
        ),
        InventoryItem(
            "banana", "Banana", "Tropical Fruit", 13, "kg", 55.0, 82,
            QualityStatus.FRESH, "2–3 days", WasteRisk.MEDIUM, 5.5, 18,
            "Fresh Valley", "Today, 10:02 AM"
        ),
        InventoryItem(
            "spinach", "Spinach", "Leafy Green", 7, "kg", 70.0, 69,
            QualityStatus.WATCH, "1–2 days", WasteRisk.HIGH, 3.5, 10,
            "GreenFarm Foods", "Today, 09:30 AM"
        ),
        InventoryItem(
            "pepper", "Bell Pepper", "Fruit & Vegetable", 22, "kg", 95.0, 84,
            QualityStatus.GOOD, "4–6 days", WasteRisk.LOW, 3.0, 12,
            "GreenFarm Foods", "Yesterday, 06:15 PM"
        ),
        InventoryItem(
            "potato", "Potato", "Root Vegetable", 64, "kg", 38.0, 92,
            QualityStatus.FRESH, "8–12 days", WasteRisk.LOW, 7.0, 25,
            "Daily Harvest", "Yesterday, 05:45 PM"
        ),
        InventoryItem(
            "mango", "Mango", "Fruit", 9, "kg", 140.0, 64,
            QualityStatus.WATCH, "1–2 days", WasteRisk.HIGH, 4.0, 12,
            "Orchard Link", "Today, 08:50 AM"
        ),
        InventoryItem(
            "carrot", "Carrot", "Root Vegetable", 31, "kg", 52.0, 89,
            QualityStatus.FRESH, "6–9 days", WasteRisk.LOW, 4.0, 15,
            "Daily Harvest", "Yesterday, 05:10 PM"
        )
    )

    override suspend fun getDashboard() = RetailerDashboard(
        storeName = "FreshLink Market",
        totalSkus = inventory.size,
        lowStockCount = inventory.count { it.stockUnits <= it.reorderPoint },
        highWasteRiskCount = inventory.count { it.wasteRisk == WasteRisk.HIGH },
        estimatedAtRiskValue = 9 * 140.0 + 7 * 70.0,
        todaySalesUnits = 146,
        inventoryValue = inventory.sumOf { it.stockUnits * it.currentPrice }
    )

    override suspend fun getInventory() = inventory

    override suspend fun getDemandForecast() = inventory.map {
        val forecast = (it.avgDailyDemand * 7).toInt()
        DemandForecast(
            it.id, it.name, it.stockUnits, it.avgDailyDemand, forecast,
            when {
                it.avgDailyDemand >= 6 -> 18
                it.avgDailyDemand >= 4 -> 9
                else -> -4
            },
            when {
                it.avgDailyDemand >= 6 -> "Rising"
                it.avgDailyDemand >= 4 -> "Stable"
                else -> "Softening"
            }
        )
    }

    override suspend fun getPricingRecommendations() = inventory
        .filter { it.wasteRisk != WasteRisk.LOW || it.qualityScore < 80 }
        .map {
            val discount = when (it.wasteRisk) {
                WasteRisk.HIGH -> 20
                WasteRisk.MEDIUM -> 10
                WasteRisk.LOW -> 0
            }
            val suggested = it.currentPrice * (1 - discount / 100.0)
            PricingRecommendation(
                it.id, it.name, it.currentPrice,
                suggested, discount,
                if (it.wasteRisk == WasteRisk.HIGH)
                    "Short estimated quality window with higher waste risk."
                else
                    "Quality is still saleable; a modest markdown may improve sell-through.",
                it.qualityScore
            )
        }

    override suspend fun getReplenishmentRecommendations() = inventory
        .filter { it.stockUnits <= it.reorderPoint || it.stockUnits < it.avgDailyDemand * 3 }
        .map {
            val target = (it.avgDailyDemand * 7 + it.reorderPoint).toInt()
            ReplenishmentRecommendation(
                it.id,
                it.name,
                it.stockUnits,
                (target - it.stockUnits).coerceAtLeast(0),
                it.supplier,
                if (it.stockUnits <= it.reorderPoint)
                    "Stock is at or below the reorder point."
                else
                    "Projected 7-day demand may reduce safety stock.",
                if (it.stockUnits <= it.reorderPoint) "High" else "Plan"
            )
        }

    override suspend fun getWasteRisks() = inventory
        .filter { it.wasteRisk != WasteRisk.LOW }
        .map {
            WasteRiskItem(
                it.id, it.name, it.stockUnits, it.estimatedQualityWindow,
                it.wasteRisk,
                when (it.wasteRisk) {
                    WasteRisk.HIGH -> "Quality window is short relative to current stock."
                    WasteRisk.MEDIUM -> "Sell-through should be monitored."
                    WasteRisk.LOW -> "Low current risk."
                },
                when (it.wasteRisk) {
                    WasteRisk.HIGH -> "Consider a manual markdown or promotion."
                    WasteRisk.MEDIUM -> "Monitor daily demand and quality."
                    WasteRisk.LOW -> "No action suggested."
                }
            )
        }

    override suspend fun getAnalytics() = RetailAnalytics(
        salesUnitsToday = 146,
        salesUnitsWeek = 912,
        estimatedWasteUnits = 31,
        estimatedWasteRate = 3.4,
        stockTurnover = 4.8,
        markdownRecovery = 72.0,
        categoryPerformance = listOf(
            CategoryPerformance("Fruit & Veg", 392, 43),
            CategoryPerformance("Fruit", 226, 25),
            CategoryPerformance("Root Veg", 178, 20),
            CategoryPerformance("Leafy Green", 116, 12)
        ),
        weeklySales = listOf(104, 121, 116, 137, 129, 159, 146)
    )
}
