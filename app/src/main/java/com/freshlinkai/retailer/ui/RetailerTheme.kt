package com.freshlinkai.retailer.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RetailerGreen = Color(0xFF37D35A)
private val RetailerGreenDark = Color(0xFF149B39)
private val RetailerBackground = Color(0xFF061007)
private val RetailerSurface = Color(0xFF0B170C)
private val RetailerSurface2 = Color(0xFF102112)
private val RetailerText = Color(0xFFF1F7F1)
private val RetailerMuted = Color(0xFF9EAE9F)

private val RetailerColors = darkColorScheme(
    primary = RetailerGreen,
    onPrimary = Color(0xFF001F08),
    primaryContainer = RetailerGreenDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF8BEA9A),
    background = RetailerBackground,
    onBackground = RetailerText,
    surface = RetailerSurface,
    onSurface = RetailerText,
    surfaceVariant = RetailerSurface2,
    onSurfaceVariant = RetailerMuted
)

@Composable
fun FreshLinkRetailerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RetailerColors,
        content = content
    )
}
