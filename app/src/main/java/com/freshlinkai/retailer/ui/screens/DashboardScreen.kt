package com.freshlinkai.retailer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.data.WasteRisk
import com.freshlinkai.retailer.ui.MetricCard
import com.freshlinkai.retailer.ui.RetailerUiState
import com.freshlinkai.retailer.ui.SectionTitle
import com.freshlinkai.retailer.ui.StatusPill
import com.freshlinkai.retailer.ui.money

@Composable
fun DashboardScreen(
    state: RetailerUiState
) {
    val d = state.dashboard ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Store header
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Good evening 👋",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = d.storeName,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Retail overview • sample data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // First row of metrics
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            MetricCard(
                title = "SKUs",
                value = d.totalSkus.toString(),
                supporting = "Tracked products",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Low stock",
                value = d.lowStockCount.toString(),
                supporting = "Need attention",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Second row of metrics
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            MetricCard(
                title = "Waste risk",
                value = d.highWasteRiskCount.toString(),
                supporting = "High-risk items",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Today",
                value = d.todaySalesUnits.toString(),
                supporting = "Units sold",
                icon = {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Attention section
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Attention needed",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "• ${d.lowStockCount} products are at/below their reorder point."
                )

                Text(
                    text = "• ${d.highWasteRiskCount} products have high waste risk."
                )

                Text(
                    text = "• ${money(d.estimatedAtRiskValue)} estimated inventory value is at risk."
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Recommendations are decision support only; review before taking action.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick insights
        SectionTitle(
            title = "Quick insights"
        )

        state.pricing
            .take(3)
            .forEach { item ->

                Card(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = item.productName,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = item.reason,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        StatusPill(
                            text = "${item.discountPercent}% suggested",
                            risk = WasteRisk.HIGH
                        )
                    }
                }
            }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}