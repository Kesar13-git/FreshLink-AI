package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProduceComparisonItem
import com.example.ui.components.DisclaimerCard
import com.example.ui.components.getScoreColor

/**
 * 5. COMPARE PRODUCE SCREEN
 *
 * Compares multiple produce items to assist consumers in selecting the best option:
 * - Shows produce name, freshness score, quality stage, and quality window
 * - Highlights "Best Pick" based on highest visual quality score
 * - Prominent disclaimer: Recommendation based on visual quality score, not a food safety guarantee
 * - Seamlessly integrates newly scanned items alongside sample data
 */
@Composable
fun CompareScreen(
  items: List<ProduceComparisonItem>,
  onScanMoreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bestPickItem = items.firstOrNull { it.isBestPick }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    item {
      // Screen Header
      Text(
        text = "Compare Produce",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Compare visible freshness metrics and shelf-life across items to make smart purchase decisions.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      // "Best Pick" Featured Banner
      if (bestPickItem != null) {
        BestPickHeroBanner(bestPick = bestPickItem)
        Spacer(modifier = Modifier.height(20.dp))
      }

      // Comparison Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Produce Comparison (${items.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )

        Button(
          onClick = onScanMoreClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          modifier = Modifier.testTag("compare_scan_more_button")
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Scan to Add",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
    }

    // Comparison List Cards
    items(items, key = { it.id }) { item ->
      ComparisonProduceCard(item = item)
      Spacer(modifier = Modifier.height(12.dp))
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Mandatory recommendation & safety disclaimer
      DisclaimerCard(
        text = "The 'Best Pick' recommendation is strictly based on visible exterior quality and post-harvest indicators. It is not a microbiological food-safety guarantee.",
        isCompact = false
      )
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

/**
 * Hero banner highlighting the "Best Pick" item with highest freshness score.
 */
@Composable
private fun BestPickHeroBanner(bestPick: ProduceComparisonItem) {
  val scoreColor = getScoreColor(bestPick.freshnessScore)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("best_pick_banner"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    ),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "BEST PICK",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary,
              letterSpacing = 0.5.sp
            )
          }
        }

        Text(
          text = "${bestPick.freshnessScore}/100",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = scoreColor
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = bestPick.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "Quality: ${bestPick.qualityStage}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
          text = "Window: ${bestPick.qualityWindow}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = bestPick.visualNote,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
      )
    }
  }
}

/**
 * Individual produce comparison card with score meter and quality breakdown.
 */
@Composable
private fun ComparisonProduceCard(item: ProduceComparisonItem) {
  val scoreColor = getScoreColor(item.freshnessScore)

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(
      width = if (item.isBestPick) 1.5.dp else 1.dp,
      color = if (item.isBestPick) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (item.isBestPick) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "Best",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Text(
          text = "${item.freshnessScore}/100",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = scoreColor
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Freshness Progress Bar
      LinearProgressIndicator(
        progress = { item.freshnessScore / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = scoreColor,
        trackColor = scoreColor.copy(alpha = 0.15f)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Quality Stage and Window Tags
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Stage: ${item.qualityStage}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Window: ${item.qualityWindow}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      if (item.reasons.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Key reason: ${item.reasons.first()}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
