package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.DisclaimerCard

/**
 * 1. SPLASH / WELCOME SCREEN
 *
 * Welcomes consumers to FreshLink AI:
 * - FreshLink AI logo / icon
 * - App title & subtitle: "Smart Fresh Produce Decision Support"
 * - Official project tagline: "From Freshness Detection to Freshness Intelligence"
 * - "Get Started" call to action opening the Home screen
 */
@Composable
fun WelcomeScreen(
  onGetStartedClick: () -> Unit
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Spacer(modifier = Modifier.height(24.dp))

        // App Brand Mark / Logo
        Surface(
          modifier = Modifier.size(100.dp),
          shape = RoundedCornerShape(24.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          shadowElevation = 4.dp
        ) {
          Box(contentAlignment = Alignment.Center) {
            Image(
              painter = painterResource(id = R.drawable.ic_app_logo),
              contentDescription = "FreshLink AI Logo",
              modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Title
        Text(
          text = "FreshLink AI",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle
        Text(
          text = "Smart Fresh Produce Decision Support",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tagline
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "“From Freshness Detection to Freshness Intelligence”",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3 Key Capabilities Pillars
        FeaturePillarRow(
          icon = Icons.Default.PhotoCamera,
          title = "Camera Freshness Analysis",
          description = "Scan visible color, texture, and post-harvest indicators"
        )
        Spacer(modifier = Modifier.height(12.dp))
        FeaturePillarRow(
          icon = Icons.Default.Psychology,
          title = "Computer Vision Ready",
          description = "Architected for Python / FastAPI deep learning models"
        )
        Spacer(modifier = Modifier.height(12.dp))
        FeaturePillarRow(
          icon = Icons.Default.Eco,
          title = "Consumer Decision Guide",
          description = "Clear quality stages and produce shelf-life guidance"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Visual estimate disclaimer
        DisclaimerCard(
          text = "FreshLink AI provides visual freshness estimates only and does not evaluate microbiological food safety.",
          isCompact = true
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // "Get Started" CTA Button
        Button(
          onClick = onGetStartedClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("get_started_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          Text(
            text = "Get Started",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Proceed to Home"
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "B.E. Computer Engineering Final-Year Project",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
      }
    }
  }
}

@Composable
private fun FeaturePillarRow(
  icon: ImageVector,
  title: String,
  description: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(42.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
        }
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
