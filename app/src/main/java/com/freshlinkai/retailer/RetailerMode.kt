package com.freshlinkai.retailer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.freshlinkai.retailer.data.MockRetailerRepository
import com.freshlinkai.retailer.ui.RetailerModeScreen as RetailerContentScreen
import com.freshlinkai.retailer.ui.RetailerViewModel

/**
 * Public entry point for the existing FreshLink AI app.
 *
 * Call this from the host app's Consumer/Retailer mode navigation.
 */
@Composable
fun RetailerModeEntry(
    onExitRetailerMode: () -> Unit = {}
) {
    val repository = remember { MockRetailerRepository() }
    val vm: RetailerViewModel = viewModel(
        factory = RetailerViewModel.factory(repository)
    )

    RetailerContentScreen(
        viewModel = vm,
        onExitRetailerMode = onExitRetailerMode
    )
}

/**
 * Optional internal navigation wrapper.
 *
 * Use this only if the host app wants the Retailer feature to own its
 * navigation. If the host already owns navigation, use RetailerModeScreen().
 */
@Composable
fun RetailerModeNavHost(
    onExitRetailerMode: () -> Unit = {}
) {
    val navController = rememberNavController()
    val repository = remember { MockRetailerRepository() }
    val vm: RetailerViewModel = viewModel(
        factory = RetailerViewModel.factory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = "retailer/home"
    ) {
        composable("retailer/home") {
            RetailerContentScreen(
                viewModel = vm,
                onExitRetailerMode = onExitRetailerMode
            )
        }
    }
}
