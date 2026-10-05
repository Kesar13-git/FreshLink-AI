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
fun ReplenishmentScreen(
    state: RetailerUiState,
    onMessage: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(top = 10.dp)) {
        SectionTitle("Replenishment", "Suggested order quantities based on mock demand")
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.replenishment, key = { it.productId }) { item ->
                Card {
                    Column(Modifier.padding(15.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(item.productName, style = MaterialTheme.typography.titleMedium)
                                Text("Supplier: ${item.supplier}", style = MaterialTheme.typography.bodySmall)
                            }
                            StatusPill(item.urgency)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Current stock: ${item.currentStock}")
                            Text(
                                "Order: ${item.recommendedOrderQty}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(7.dp))
                        Text(item.reason, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { onMessage("Demo order prepared for ${item.productName}. No real order was placed.") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Prepare order")
                        }
                    }
                }
            }
        }
    }
}
