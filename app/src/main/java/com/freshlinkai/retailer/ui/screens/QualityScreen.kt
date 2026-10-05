package com.freshlinkai.retailer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.data.WasteRisk
import com.freshlinkai.retailer.ui.*

@Composable
fun QualityScreen(state: RetailerUiState) {
    Column(Modifier.fillMaxSize().padding(top = 10.dp)) {
        SectionTitle(
            "Quality & Waste Risk",
            "Estimated visual quality windows — not exact expiry dates"
        )
        Spacer(Modifier.height(10.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.inventory, key = { it.id }) { item ->
                Card {
                    Column(Modifier.padding(15.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Estimated quality window: ${item.estimatedQualityWindow}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            StatusPill(item.wasteRisk.label, risk = item.wasteRisk)
                        }
                        Spacer(Modifier.height(10.dp))
                        ProgressLine(item.qualityScore / 100f)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Visual quality score", style = MaterialTheme.typography.labelSmall)
                            Text("${item.qualityScore}/100", style = MaterialTheme.typography.labelSmall)
                        }
                        if (item.wasteRisk != WasteRisk.LOW) {
                            Spacer(Modifier.height(9.dp))
                            Row {
                                Icon(Icons.Default.WarningAmber, null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (item.wasteRisk == WasteRisk.HIGH)
                                        "Higher waste risk. Consider a manual promotion/markdown."
                                    else
                                        "Monitor sell-through and quality frequently.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
