package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FreshnessGuideRepository
import com.example.model.FreshnessGuideItem
import com.example.ui.components.DisclaimerCard
import com.example.ui.theme.ScoreHighGreen
import com.example.ui.theme.ScoreLowRed

/**
 * 6. FRESHNESS GUIDE SCREEN
 *
 * Comprehensive consumer guide for selecting and storing fresh produce:
 * - Specific checklists for Tomato, Apple, Banana, Spinach, Bell Pepper
 * - Positive inspection signs vs warning signs
 * - Ripeness stage indicators
 * - Post-harvest storage temperature and humidity advice
 * - Expandable cards for intuitive mobile scanning
 */
@Composable
fun GuideScreen(
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }
  val categories = listOf("All", "Fruiting Vegetable", "Pome Fruit", "Tropical Fruit", "Leafy Vegetable")

  val guideList = FreshnessGuideRepository.guideItems.filter {
    selectedCategory == "All" || it.category == selectedCategory
  }

  // Set of expanded item IDs, default expanding first 2
  var expandedIds by remember { mutableStateOf(setOf("tomato", "apple")) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    item {
      // Screen Header
      Text(
        text = "Freshness Guide",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Practical sensory inspection tips and storage science for everyday grocery shoppers.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Category Filter Carousel
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { category ->
          val selected = selectedCategory == category
          FilterChip(
            selected = selected,
            onClick = { selectedCategory = category },
            label = { Text(text = category) },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Guide Items List
    items(guideList, key = { it.id }) { item ->
      val isExpanded = expandedIds.contains(item.id)
      ExpandableGuideCard(
        item = item,
        isExpanded = isExpanded,
        onToggleExpand = {
          expandedIds = if (isExpanded) {
            expandedIds - item.id
          } else {
            expandedIds + item.id
          }
        }
      )
      Spacer(modifier = Modifier.height(12.dp))
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      DisclaimerCard(
        text = "Always inspect food thoroughly before cooking or eating. FreshLink AI guidelines are educational best practices for commercial grade produce.",
        isCompact = true
      )
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

/**
 * Expandable card displaying detailed inspection indicators and storage guidelines.
 */
@Composable
private fun ExpandableGuideCard(
  item: FreshnessGuideItem,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onToggleExpand)
      .testTag("guide_card_${item.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Produce name, category tag, shelf-life and expand icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.produceName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = item.category,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "• Shelf-life: ${item.typicalShelfLife}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.size(34.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse guide" else "Expand guide",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Quick preview summary if collapsed
      if (!isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Key tip: ${item.positiveSigns.firstOrNull() ?: ""}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Expanded Content
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
      ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
          // What to Look For (Positive signs)
          Text(
            text = "What to Look For",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = ScoreHighGreen
          )
          Spacer(modifier = Modifier.height(6.dp))
          item.positiveSigns.forEach { sign ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ScoreHighGreen,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 2.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = sign,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // What to Avoid (Warning signs)
          Text(
            text = "What to Avoid",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = ScoreLowRed
          )
          Spacer(modifier = Modifier.height(6.dp))
          item.warningSigns.forEach { sign ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = ScoreLowRed,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 2.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = sign,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Ripeness Stages
          Text(
            text = "Ripeness Stages",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          item.ripenessStages.forEach { stage ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = stage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Storage Guidance Banner
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Inventory2,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Storage Guidance (${item.idealTemperature})",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = item.storageAdvice,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
