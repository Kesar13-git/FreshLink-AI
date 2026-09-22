package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ScoreHighGreen
import com.example.ui.theme.ScoreLowRed
import com.example.ui.theme.ScoreModerateAmber

fun getScoreColor(score: Int): Color {
  return when {
    score >= 80 -> ScoreHighGreen
    score >= 65 -> ScoreModerateAmber
    else -> ScoreLowRed
  }
}

/**
 * Circular visual gauge displaying the freshness score (0-100) with animated arc.
 */
@Composable
fun FreshnessCircularGauge(
  score: Int,
  modifier: Modifier = Modifier,
  size: Dp = 130.dp,
  strokeWidth: Dp = 12.dp,
  qualityStage: String? = null
) {
  val animatedProgress by animateFloatAsState(
    targetValue = (score.coerceIn(0, 100) / 100f),
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "score_arc_anim"
  )

  val color = getScoreColor(score)

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(size)
    ) {
      Canvas(modifier = Modifier.size(size)) {
        val trackWidth = strokeWidth.toPx()
        // Background track
        drawCircle(
          color = color.copy(alpha = 0.15f),
          radius = (size.toPx() - trackWidth) / 2,
          style = Stroke(width = trackWidth, cap = StrokeCap.Round)
        )
        // Score progress sweep
        drawArc(
          color = color,
          startAngle = -90f,
          sweepAngle = 360f * animatedProgress,
          useCenter = false,
          style = Stroke(width = trackWidth, cap = StrokeCap.Round)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "$score",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 34.sp
          )
          Text(
            text = "/100",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
          )
        }
        Text(
          text = "Freshness",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (!qualityStage.isNullOrBlank()) {
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text(
          text = qualityStage,
          color = color,
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
        )
      }
    }
  }
}

/**
 * Compact linear freshness bar for cards and list items.
 */
@Composable
fun FreshnessScoreBar(
  score: Int,
  modifier: Modifier = Modifier
) {
  val color = getScoreColor(score)

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Freshness Score",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "$score/100",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { score / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = color.copy(alpha = 0.15f)
    )
  }
}
