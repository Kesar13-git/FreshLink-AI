package com.freshlinkai.retailer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freshlinkai.retailer.data.QualityStatus
import com.freshlinkai.retailer.data.WasteRisk
import kotlin.math.roundToInt

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    supporting: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = .13f)
                ) {
                    Box(Modifier.padding(8.dp)) { icon() }
                }
                Spacer(Modifier.width(9.dp))
                Text(title, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                supporting,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusPill(text: String, quality: QualityStatus? = null, risk: WasteRisk? = null) {
    val color = when {
        risk == WasteRisk.HIGH || quality == QualityStatus.HIGH_RISK -> Color(0xFFFF7676)
        risk == WasteRisk.MEDIUM || quality == QualityStatus.WATCH -> Color(0xFFFFC857)
        quality == QualityStatus.GOOD -> Color(0xFF8BEA9A)
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(
        color = color.copy(alpha = .13f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProgressLine(value: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { value.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(7.dp),
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun MiniBarChart(
    values: List<Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(110.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val max = (values.maxOrNull() ?: 1).toFloat()
        values.forEach { value ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((70 * (value / max)).coerceAtLeast(8f).dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(value.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

fun money(value: Double): String = "₹${value.roundToInt()}"
