package com.freshlinkai.retailer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.ui.*

@Composable
fun InventoryScreen(state: RetailerUiState) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All") + state.inventory.map { it.category }.distinct()
    val filtered = state.inventory.filter {
        (query.isBlank() || it.name.contains(query, ignoreCase = true)) &&
        (selectedCategory == "All" || it.category == selectedCategory)
    }

    Column(Modifier.fillMaxSize().padding(top = 10.dp)) {
        SectionTitle("Inventory Management", "Live stock position and quality status")
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search products") }
        )

        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            categories.take(4).forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.substringBefore(" ")) }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            items(filtered, key = { it.id }) { item ->
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text("${item.stockUnits} ${item.unit} • ${item.supplier}", style = MaterialTheme.typography.bodySmall)
                            }
                            StatusPill(item.qualityStatus.label, quality = item.qualityStatus)
                        }
                        Spacer(Modifier.height(10.dp))
                        ProgressLine(item.qualityScore / 100f)
                        Spacer(Modifier.height(7.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Quality ${item.qualityScore}/100", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "Estimated quality window: ${item.estimatedQualityWindow}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "₹${item.currentPrice.toInt()}/${item.unit} • Reorder point: ${item.reorderPoint} ${item.unit}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
