package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = FreshGreenPrimaryDark,
    onPrimary = FreshGreenOnPrimaryDark,
    primaryContainer = FreshGreenContainerDark,
    onPrimaryContainer = FreshGreenOnContainerDark,
    secondary = BotanicalSecondaryDark,
    onSecondary = BotanicalOnSecondaryDark,
    secondaryContainer = BotanicalSecondaryContainerDark,
    onSecondaryContainer = BotanicalOnSecondaryContainerDark,
    background = FreshBackgroundDark,
    onBackground = FreshOnBackgroundDark,
    surface = FreshSurfaceDark,
    onSurface = FreshOnSurfaceDark,
    surfaceVariant = FreshSurfaceVariantDark,
    onSurfaceVariant = FreshOnSurfaceVariantDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = FreshGreenPrimary,
    onPrimary = FreshGreenOnPrimary,
    primaryContainer = FreshGreenContainer,
    onPrimaryContainer = FreshGreenOnContainer,
    secondary = BotanicalSecondary,
    onSecondary = BotanicalOnSecondary,
    secondaryContainer = BotanicalSecondaryContainer,
    onSecondaryContainer = BotanicalOnSecondaryContainer,
    tertiary = HarvestTertiary,
    onTertiary = HarvestOnTertiary,
    tertiaryContainer = HarvestTertiaryContainer,
    onTertiaryContainer = HarvestOnTertiaryContainer,
    background = FreshBackground,
    onBackground = FreshOnBackground,
    surface = FreshSurface,
    onSurface = FreshOnSurface,
    surfaceVariant = FreshSurfaceVariant,
    onSurfaceVariant = FreshOnSurfaceVariant,
    outline = FreshOutline,
    outlineVariant = FreshOutlineVariant,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For brand consistency in final-year demo, prioritize the fresh produce theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
