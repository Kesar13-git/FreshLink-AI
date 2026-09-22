package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScanHistoryRecord
import com.example.ui.components.DisclaimerCard
import com.example.ui.components.FreshnessScoreBar
import com.example.ui.components.getScoreColor

/**
 * 2. HOME SCREEN
 *
 * Consumer dashboard featuring:
 * - Top header with FreshLink AI brand and profile icon
 * - Friendly greeting: "Hello! Choose smarter. Buy better."
 * - Prominent "Scan Produce" hero card with camera icon and "Scan Now" button
 * - Quick action feature cards for "Compare Produce" and "Freshness Guide"
 * - Recent scans section (showing Tomato 88/100, Apple 76/100, etc.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  recentScans: List<ScanHistoryRecord>,
  onScanNowClick: () -> Unit,
  onCompareClick: () -> Unit,
  onGuideClick: () -> Unit,
  onHistoryClick: () -> Unit,
  onProfileClick: () -> Unit,
  onScanRecordClick: (ScanHistoryRecord) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Greeting Banner
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Hello!",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Choose smarter. Buy better.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Main "Scan Produce" Hero Card
      MainScanCard(onScanClick = onScanNowClick)

      Spacer(modifier = Modifier.height(16.dp))

      // Feature Quick Cards: Compare Produce & Freshness Guide
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        FeatureCard(
          title = "Compare Produce",
          subtitle = "Find the best pick",
          icon = Icons.Default.CompareArrows,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          iconTint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f),
          onClick = onCompareClick
        )
        FeatureCard(
          title = "Freshness Guide",
          subtitle = "Inspection & storage",
          icon = Icons.AutoMirrored.Outlined.MenuBook,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          iconTint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.weight(1f),
          onClick = onGuideClick
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Recent Scans Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Recent Scans",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = if (recentScans.any { !it.id.startsWith("sample-") }) "Your latest produce assessments" else "Sample records ready for model inference",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Text(
          text = "View All",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier
            .clickable(onClick = onHistoryClick)
            .padding(4.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
    }

    // Recent Scans List Items
    if (recentScans.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No recent scans yet. Tap 'Scan Now' to begin!",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(recentScans.take(3), key = { it.id }) { record ->
        RecentScanItem(
          record = record,
          onClick = { onScanRecordClick(record) }
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
      DisclaimerCard(
        text = "Visual freshness estimates only. Does not guarantee microbiological food safety or internal hidden defects.",
        isCompact = true
      )
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

/**
 * Hero card highlighting the main camera scan call-to-action.
 */
@Composable
private fun MainScanCard(onScanClick: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("main_scan_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(52.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.PhotoCamera,
              contentDescription = "Scan Camera Icon",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(28.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Scan Produce",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Analyze visible freshness and quality",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Button(
        onClick = onScanClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("scan_now_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        Icon(
          imageVector = Icons.Outlined.PhotoCamera,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Scan Now",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

/**
 * Reusable feature card for secondary quick navigation actions.
 */
@Composable
private fun FeatureCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  containerColor: Color,
  iconTint: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.size(38.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

/**
 * Scan history preview item for Home screen.
 */
@Composable
private fun RecentScanItem(
  record: ScanHistoryRecord,
  onClick: () -> Unit
) {
  val scoreColor = getScoreColor(record.freshnessScore)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = scoreColor.copy(alpha = 0.12f),
        modifier = Modifier.size(46.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = "${record.freshnessScore}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = scoreColor
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = record.produceName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = scoreColor.copy(alpha = 0.12f)
          ) {
            Text(
              text = record.qualityStage,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = scoreColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Est. window: ${record.qualityWindow}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = record.dateTimeDisplay,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "View scan detail",
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
