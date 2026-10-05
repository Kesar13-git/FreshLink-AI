package com.freshlinkai.retailer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.ui.screens.*

enum class RetailerTab(
    val title: String,
    val icon: @Composable () -> Unit
) {
    DASHBOARD("Dashboard", { Icon(Icons.Default.Dashboard, null) }),
    INVENTORY("Inventory", { Icon(Icons.Default.Inventory2, null) }),
    QUALITY("Quality", { Icon(Icons.Default.Verified, null) }),
    DEMAND("Demand", { Icon(Icons.Default.TrendingUp, null) }),
    PRICING("Pricing", { Icon(Icons.Default.LocalOffer, null) }),
    REPLENISHMENT("Orders", { Icon(Icons.Default.ShoppingCart, null) }),
    ANALYTICS("Analytics", { Icon(Icons.Default.BarChart, null) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailerModeScreen(
    viewModel: RetailerViewModel,
    onExitRetailerMode: () -> Unit = {}
) {
    FreshLinkRetailerTheme {
        val state by viewModel.uiState.collectAsState()
        var selectedTab by remember { mutableStateOf(RetailerTab.DASHBOARD) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("FreshLink AI", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Retailer Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onExitRetailerMode) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::refresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    val tabs = RetailerTab.values().take(5)
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = tab.icon,
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        ) { padding ->
            if (state.loading) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    RetailerTab.DASHBOARD -> DashboardScreen(
                        state = state
                    )
                    RetailerTab.INVENTORY -> InventoryScreen(state)
                    RetailerTab.QUALITY -> QualityScreen(state)
                    RetailerTab.DEMAND -> DemandScreen(state)
                    RetailerTab.PRICING -> PricingScreen(
                        state = state,
                        onMessage = viewModel::showMessage
                    )
                    RetailerTab.REPLENISHMENT -> ReplenishmentScreen(
                        state = state,
                        onMessage = viewModel::showMessage
                    )
                    RetailerTab.ANALYTICS -> AnalyticsScreen(state)
                }
            }

            state.message?.let { message ->
                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(1800)
                    viewModel.clearMessage()
                }
                SnackbarHost(
                    hostState = remember { SnackbarHostState() },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}
