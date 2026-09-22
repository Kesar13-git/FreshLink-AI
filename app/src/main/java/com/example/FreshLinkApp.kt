package com.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CompareScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.WelcomeScreen

/**
 * Navigation destination definitions for FreshLink AI.
 */
sealed class Screen(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  data object Welcome : Screen("welcome", "Welcome", Icons.Default.Home, Icons.Outlined.Home)
  data object Home : Screen("home", "Home", Icons.Default.Home, Icons.Outlined.Home)
  data object Scan : Screen("scan", "Scan", Icons.Default.PhotoCamera, Icons.Outlined.PhotoCamera)
  data object Compare : Screen("compare", "Compare", Icons.Default.CompareArrows, Icons.Outlined.CompareArrows)
  data object Guide : Screen("guide", "Guide", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook)
  data object History : Screen("history", "History", Icons.Default.History, Icons.Outlined.History)
  data object Result : Screen("result", "Result", Icons.Default.PhotoCamera, Icons.Outlined.PhotoCamera)
  data object Profile : Screen("profile", "Profile", Icons.Default.Person, Icons.Outlined.Person)
}

val bottomNavItems = listOf(
  Screen.Home,
  Screen.Scan,
  Screen.Compare,
  Screen.Guide,
  Screen.History
)

/**
 * Main application composable setting up Navigation Compose, TopAppBar,
 * and BottomNavigationBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshLinkApp(
  viewModel: FreshLinkViewModel = viewModel()
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val historyRecords by viewModel.historyRecords.collectAsStateWithLifecycle()
  val comparisonItems by viewModel.comparisonItems.collectAsStateWithLifecycle()
  val currentResult by viewModel.currentAnalysisResult.collectAsStateWithLifecycle()

  val showBottomBar = currentRoute in bottomNavItems.map { it.route }
  val showTopBar = currentRoute == Screen.Home.route

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      if (showTopBar) {
        TopAppBar(
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = null,
                modifier = Modifier
                  .size(32.dp)
                  .clip(RoundedCornerShape(8.dp))
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "FreshLink AI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.5.sp
              )
            }
          },
          actions = {
            IconButton(
              onClick = { navController.navigate(Screen.Profile.route) },
              modifier = Modifier.testTag("top_profile_button")
            ) {
              Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Open Profile and Settings",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
          )
        )
      }
    },
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 6.dp,
          modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
          bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
              selected = selected,
              onClick = {
                if (currentRoute != screen.route) {
                  navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              icon = {
                Icon(
                  imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                  contentDescription = screen.label
                )
              },
              label = {
                Text(
                  text = screen.label,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.testTag("nav_${screen.route}")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Welcome.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Splash / Welcome Screen
      composable(Screen.Welcome.route) {
        WelcomeScreen(
          onGetStartedClick = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Welcome.route) { inclusive = true }
            }
          }
        )
      }

      // 2. Home Screen
      composable(Screen.Home.route) {
        HomeScreen(
          recentScans = historyRecords,
          onScanNowClick = { navController.navigate(Screen.Scan.route) },
          onCompareClick = { navController.navigate(Screen.Compare.route) },
          onGuideClick = { navController.navigate(Screen.Guide.route) },
          onHistoryClick = { navController.navigate(Screen.History.route) },
          onProfileClick = { navController.navigate(Screen.Profile.route) },
          onScanRecordClick = { record ->
            viewModel.setCurrentResultFromRecord(record)
            navController.navigate(Screen.Result.route)
          }
        )
      }

      // 3. Scan Produce Screen
      composable(Screen.Scan.route) {
        ScanScreen(
          analysisRepository = viewModel.analysisRepository,
          onAnalysisSuccess = { result ->
            viewModel.onNewAnalysisResult(result)
            navController.navigate(Screen.Result.route)
          }
        )
      }

      // 4. Result Screen
      composable(Screen.Result.route) {
        currentResult?.let { result ->
          ResultScreen(
            result = result,
            onCompareClick = { navController.navigate(Screen.Compare.route) },
            onScanAgainClick = { navController.navigate(Screen.Scan.route) },
            onBackClick = { navController.navigateUp() }
          )
        } ?: run {
          // Fallback if no result exists yet
          navController.navigate(Screen.Home.route)
        }
      }

      // 5. Compare Produce Screen
      composable(Screen.Compare.route) {
        CompareScreen(
          items = comparisonItems,
          onScanMoreClick = { navController.navigate(Screen.Scan.route) }
        )
      }

      // 6. Freshness Guide Screen
      composable(Screen.Guide.route) {
        GuideScreen()
      }

      // 7. History Screen
      composable(Screen.History.route) {
        HistoryScreen(
          historyRecords = historyRecords,
          onScanNewClick = { navController.navigate(Screen.Scan.route) }
        )
      }

      // 8. Profile & Settings Screen
      composable(Screen.Profile.route) {
        ProfileScreen(
          onBackClick = { navController.navigateUp() }
        )
      }
    }
  }
}
