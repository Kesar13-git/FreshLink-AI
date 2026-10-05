package com.freshlinkai.retailer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.ui.*

@Composable
fun PricingScreen(
    state: RetailerUiState,
    onMessage: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(top = 10.dp)) {
        SectionTitle(
            "Dynamic Pricing",
            "Recommendations only — retailer approval is required"
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.pricing, key = { it.productId }) { item ->
                Card {
                    Column(Modifier.padding(15.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(item.productName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Current price: ${money(item.currentPrice)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            StatusPill("${item.discountPercent}% off")
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Suggested price", style = MaterialTheme.typography.labelMedium)
                            Text(
                                money(item.suggestedPrice),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(7.dp))
                        Text(item.reason, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Recommendation confidence: ${item.confidence}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                onMessage("Demo: recommendation reviewed. Actual price was not changed.")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Review recommendation")
                        }
                    }
                }
            }
        }
    }
}
