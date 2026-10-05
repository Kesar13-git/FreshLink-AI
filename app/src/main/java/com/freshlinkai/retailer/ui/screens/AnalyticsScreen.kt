package com.freshlinkai.retailer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.ui.*

@Composable
fun AnalyticsScreen(state: RetailerUiState) {
    val a = state.analytics ?: return

    Column(
        Modifier.fillMaxSize().verticalScroll(androidx.compose.foundation.rememberScrollState()).padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle("Retail Analytics", "Basic operating metrics from sample data")

        Row(Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Today", a.salesUnitsToday.toString(), "Units sold", { Icon(Icons.Default.BarChart, null) }, Modifier.weight(1f))
            MetricCard("7 days", a.salesUnitsWeek.toString(), "Units sold", { Icon(Icons.Default.BarChart, null) }, Modifier.weight(1f))
        }

        Row(Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Waste", "${a.estimatedWasteRate}%", "Estimated rate", { Icon(Icons.Default.Delete, null) }, Modifier.weight(1f))
            MetricCard("Turnover", "${a.stockTurnover}×", "Stock turnover", { Icon(Icons.Default.Refresh, null) }, Modifier.weight(1f))
        }

        Card(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(15.dp)) {
                Text("Weekly unit sales", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                MiniBarChart(a.weeklySales)
            }
        }

        Card(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(15.dp)) {
                Text("Category performance", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                a.categoryPerformance.forEach {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(it.category)
                        Text("${it.salesUnits} units • ${it.sharePercent}%")
                    }
                    Spacer(Modifier.height(7.dp))
                }
            }
        }

        Card(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(15.dp)) {
                Text("Markdown recovery", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${a.markdownRecovery}% of marked-down inventory value recovered in this sample.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
