package com.example

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.remember
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

import com.freshlinkai.retailer.data.MockRetailerRepository
import com.freshlinkai.retailer.ui.RetailerModeScreen
import com.freshlinkai.retailer.ui.RetailerViewModel


/**
 * Navigation destination definitions for FreshLink AI.
 */
sealed class Screen(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {

  data object Welcome : Screen(
    "welcome",
    "Welcome",
    Icons.Default.Home,
    Icons.Outlined.Home
  )

  data object Home : Screen(
    "home",
    "Home",
    Icons.Default.Home,
    Icons.Outlined.Home
  )

  data object Scan : Screen(
    "scan",
    "Scan",
    Icons.Default.PhotoCamera,
    Icons.Outlined.PhotoCamera
  )

  data object Compare : Screen(
    "compare",
    "Compare",
    Icons.Default.CompareArrows,
    Icons.Outlined.CompareArrows
  )

  data object Guide : Screen(
    "guide",
    "Guide",
    Icons.AutoMirrored.Filled.MenuBook,
    Icons.AutoMirrored.Outlined.MenuBook
  )

  data object History : Screen(
    "history",
    "History",
    Icons.Default.History,
    Icons.Outlined.History
  )

  data object Result : Screen(
    "result",
    "Result",
    Icons.Default.PhotoCamera,
    Icons.Outlined.PhotoCamera
  )

  data object Profile : Screen(
    "profile",
    "Profile",
    Icons.Default.Person,
    Icons.Outlined.Person
  )

  /**
   * Retailer Mode is intentionally not added
   * to the visible bottom navigation.
   */
  data object Retailer : Screen(
    "retailer",
    "Retailer",
    Icons.Default.Person,
    Icons.Outlined.Person
  )
}


/**
 * Consumer bottom navigation items.
 *
 * Retailer Mode is NOT included here so the
 * existing Consumer UI remains unchanged.
 */
val bottomNavItems = listOf(
  Screen.Home,
  Screen.Scan,
  Screen.Compare,
  Screen.Guide,
  Screen.History
)


/**
 * Main application composable.
 *
 * Consumer UI remains unchanged.
 *
 * Retailer Mode is integrated as a separate
 * navigation destination without adding a new
 * visible button to the existing UI.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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


  /*
   * Bottom navigation is shown only for Consumer screens.
   */
  val showBottomBar = currentRoute in bottomNavItems.map { it.route }


  /*
   * Top bar is shown only on Consumer Home,
   * exactly as before.
   */
  val showTopBar = currentRoute == Screen.Home.route


  Scaffold(

    contentWindowInsets = WindowInsets.safeDrawing,

    /*
     * Existing Consumer Top Bar
     */
    topBar = {

      if (showTopBar) {

        TopAppBar(

          title = {

            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {

              Image(
                painter = painterResource(
                  id = R.drawable.ic_app_logo
                ),
                contentDescription = null,
                modifier = Modifier
                  .size(32.dp)
                  .clip(
                    RoundedCornerShape(8.dp)
                  )
              )

              Spacer(
                modifier = Modifier.width(10.dp)
              )

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

            /*
             * NORMAL TAP
             * ----------------
             * Opens Profile exactly as before.
             *
             * LONG PRESS
             * ----------------
             * Opens Retailer Mode.
             *
             * The UI itself does not change.
             */
            IconButton(
              onClick = {
                navController.navigate(
                  Screen.Profile.route
                )
              },

              modifier = Modifier
                .testTag("top_profile_button")
                .combinedClickable(
                  onClick = {
                    navController.navigate(
                      Screen.Profile.route
                    )
                  },
                  onLongClick = {
                    navController.navigate(
                      Screen.Retailer.route
                    )
                  }
                )
            ) {

              Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription =
                  "Open Profile and Settings",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
              )
            }
          },

          colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
              MaterialTheme.colorScheme.background
          )
        )
      }
    },


    /*
     * Existing Consumer Bottom Navigation
     *
     * Retailer Mode is deliberately NOT included.
     */
    bottomBar = {

      if (showBottomBar) {

        NavigationBar(

          containerColor =
            MaterialTheme.colorScheme.surface,

          tonalElevation = 6.dp,

          modifier = Modifier
            .windowInsetsPadding(
              WindowInsets.navigationBars
            )

        ) {

          bottomNavItems.forEach { screen ->

            val selected =
              currentRoute == screen.route

            NavigationBarItem(

              selected = selected,

              onClick = {

                if (currentRoute != screen.route) {

                  navController.navigate(
                    screen.route
                  ) {

                    popUpTo(
                      navController.graph
                        .findStartDestination()
                        .id
                    ) {
                      saveState = true
                    }

                    launchSingleTop = true

                    restoreState = true
                  }
                }
              },

              icon = {

                Icon(
                  imageVector =
                    if (selected) {
                      screen.selectedIcon
                    } else {
                      screen.unselectedIcon
                    },

                  contentDescription =
                    screen.label
                )
              },

              label = {

                Text(
                  text = screen.label,
                  fontWeight =
                    if (selected) {
                      FontWeight.Bold
                    } else {
                      FontWeight.Normal
                    }
                )
              },

              colors =
                NavigationBarItemDefaults.colors(

                  selectedIconColor =
                    MaterialTheme.colorScheme.primary,

                  selectedTextColor =
                    MaterialTheme.colorScheme.primary,

                  indicatorColor =
                    MaterialTheme.colorScheme.primaryContainer,

                  unselectedIconColor =
                    MaterialTheme.colorScheme.onSurfaceVariant,

                  unselectedTextColor =
                    MaterialTheme.colorScheme.onSurfaceVariant
                ),

              modifier =
                Modifier.testTag(
                  "nav_${screen.route}"
                )
            )
          }
        }
      }
    }

  ) { innerPadding ->


    /*
     * Main Navigation
     */
    NavHost(

      navController = navController,

      startDestination =
        Screen.Welcome.route,

      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)

    ) {


      /*
       * 1. Welcome Screen
       */
      composable(Screen.Welcome.route) {

        WelcomeScreen(

          onGetStartedClick = {

            navController.navigate(
              Screen.Home.route
            ) {

              popUpTo(
                Screen.Welcome.route
              ) {
                inclusive = true
              }
            }
          }
        )
      }


      /*
       * 2. Consumer Home Screen
       */
      composable(Screen.Home.route) {

        HomeScreen(

          recentScans = historyRecords,

          onScanNowClick = {
            navController.navigate(
              Screen.Scan.route
            )
          },

          onCompareClick = {
            navController.navigate(
              Screen.Compare.route
            )
          },

          onGuideClick = {
            navController.navigate(
              Screen.Guide.route
            )
          },

          onHistoryClick = {
            navController.navigate(
              Screen.History.route
            )
          },

          onProfileClick = {
            navController.navigate(
              Screen.Profile.route
            )
          },

          onScanRecordClick = { record ->

            viewModel.setCurrentResultFromRecord(
              record
            )

            navController.navigate(
              Screen.Result.route
            )
          }
        )
      }


      /*
       * 3. Scan Produce Screen
       */
      composable(Screen.Scan.route) {

        ScanScreen(

          analysisRepository =
            viewModel.analysisRepository,

          onAnalysisSuccess = { result ->

            viewModel.onNewAnalysisResult(
              result
            )

            navController.navigate(
              Screen.Result.route
            )
          }
        )
      }


      /*
       * 4. Result Screen
       */
      composable(Screen.Result.route) {

        currentResult?.let { result ->

          ResultScreen(

            result = result,

            onCompareClick = {
              navController.navigate(
                Screen.Compare.route
              )
            },

            onScanAgainClick = {
              navController.navigate(
                Screen.Scan.route
              )
            },

            onBackClick = {
              navController.navigateUp()
            }
          )

        } ?: run {

          navController.navigate(
            Screen.Home.route
          )
        }
      }


      /*
       * 5. Compare Produce Screen
       */
      composable(Screen.Compare.route) {

        CompareScreen(

          items = comparisonItems,

          onScanMoreClick = {
            navController.navigate(
              Screen.Scan.route
            )
          }
        )
      }


      /*
       * 6. Freshness Guide Screen
       */
      composable(Screen.Guide.route) {

        GuideScreen()
      }


      /*
       * 7. History Screen
       */
      composable(Screen.History.route) {

        HistoryScreen(

          historyRecords = historyRecords,

          onScanNewClick = {
            navController.navigate(
              Screen.Scan.route
            )
          }
        )
      }


      /*
       * 8. Profile & Settings Screen
       */
      composable(Screen.Profile.route) {

        ProfileScreen(

          onBackClick = {
            navController.navigateUp()
          },

          onRetailerModeClick = {

            navController.navigate(
              Screen.Retailer.route
            )
          }
        )
      }


      /*
       * 9. RETAILER MODE
       *
       * This is Raina's module.
       *
       * It is NOT added to the Consumer
       * bottom navigation, so the existing
       * Consumer UI stays unchanged.
       */
      composable(Screen.Retailer.route) {

        val repository = remember {
          MockRetailerRepository()
        }

        val retailerViewModel: RetailerViewModel =
          viewModel(
            factory =
              RetailerViewModel.factory(
                repository
              )
          )

        RetailerModeScreen(

          viewModel = retailerViewModel,

          onExitRetailerMode = {

            navController.navigate(
              Screen.Home.route
            ) {

              popUpTo(
                Screen.Home.route
              ) {
                inclusive = false
              }

              launchSingleTop = true
            }
          }
        )
      }
    }
  }
}