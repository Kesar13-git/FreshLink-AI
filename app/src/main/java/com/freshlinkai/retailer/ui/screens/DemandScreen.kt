package com.freshlinkai.retailer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.ui.*

@Composable
fun DemandScreen(state: RetailerUiState) {
    Column(Modifier.fillMaxSize().padding(top = 10.dp)) {
        SectionTitle("Demand Forecasting", "Mock 7-day demand projection")
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.demand, key = { it.productId }) { item ->
                Card {
                    Column(Modifier.padding(15.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(item.productName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Avg. daily demand: ${"%.1f".format(item.avgDailyDemand)} units",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Current stock: ${item.currentStock}")
                            Text("7-day forecast: ${item.forecast7Day}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                        Spacer(Modifier.height(7.dp))
                        Text(
                            "${item.trendLabel} • ${if (item.trendPercent >= 0) "+" else ""}${item.trendPercent}% trend",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
