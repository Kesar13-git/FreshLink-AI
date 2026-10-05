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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Profile / Settings Screen
 *
 * The Consumer UI remains unchanged.
 *
 * Retailer Mode is opened through the existing
 * "Switch to Retailer Mode" button.
 *
 * The actual navigation is controlled by the
 * parent FreshLinkApp navigation graph.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  onBackClick: () -> Unit,
  onRetailerModeClick: () -> Unit,
  modifier: Modifier = Modifier
) {

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {

    /*
     * Profile Top App Bar
     */
    TopAppBar(
      title = {
        Text(
          text = "Profile & Settings",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      },

      navigationIcon = {
        IconButton(
          onClick = onBackClick
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Navigate back"
          )
        }
      },

      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background
      )
    )


    /*
     * Scrollable Profile Content
     */
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(
          horizontal = 16.dp,
          vertical = 8.dp
        )
    ) {


      /*
       * USER PROFILE HEADER
       */
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outlineVariant
        )
      ) {

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {

          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(54.dp)
          ) {

            Box(
              contentAlignment = Alignment.Center
            ) {

              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User profile icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
              )
            }
          }


          Spacer(
            modifier = Modifier.width(16.dp)
          )


          Column(
            modifier = Modifier.weight(1f)
          ) {

            Text(
              text = "Consumer Demo User",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
              modifier = Modifier.height(2.dp)
            )

            Text(
              text = "Active Mode: Consumer Decision Support",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }


      Spacer(
        modifier = Modifier.height(16.dp)
      )


      /*
       * RETAILER MODE
       *
       * IMPORTANT:
       * The appearance of this section is unchanged.
       *
       * Only the button action has changed.
       *
       * Previously:
       *     showRetailerDialog = true
       *
       * Now:
       *     onRetailerModeClick()
       *
       * This sends the user to Raina's actual
       * Retailer Mode dashboard.
       */
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("retailer_mode_card"),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),

        border = BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outlineVariant
        )
      ) {

        Column(
          modifier = Modifier.padding(16.dp)
        ) {

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {

            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.secondaryContainer,
              modifier = Modifier.size(42.dp)
            ) {

              Box(
                contentAlignment = Alignment.Center
              ) {

                Icon(
                  imageVector = Icons.Default.Store,
                  contentDescription = "Retailer mode icon",
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(22.dp)
                )
              }
            }


            Spacer(
              modifier = Modifier.width(14.dp)
            )


            Column(
              modifier = Modifier.weight(1f)
            ) {

              Text(
                text = "Retailer Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(
                modifier = Modifier.height(2.dp)
              )

              Text(
                text = "Shelf-life batches & dynamic pricing module",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }


          Spacer(
            modifier = Modifier.height(14.dp)
          )


          /*
           * RETAILER MODE BUTTON
           *
           * UI IS UNCHANGED.
           *
           * Only the click behavior is changed.
           */
          OutlinedButton(
            onClick = {
              onRetailerModeClick()
            },

            modifier = Modifier
              .fillMaxWidth()
              .testTag("open_retailer_mode_button"),

            shape = RoundedCornerShape(10.dp),

            border = BorderStroke(
              1.dp,
              MaterialTheme.colorScheme.secondary
            )
          ) {

            Text(
              text = "Switch to Retailer Mode",
              color = MaterialTheme.colorScheme.secondary,
              fontWeight = FontWeight.SemiBold
            )

            Spacer(
              modifier = Modifier.width(6.dp)
            )

            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }


      Spacer(
        modifier = Modifier.height(16.dp)
      )


      /*
       * PROJECT INFORMATION
       */
      Text(
        text = "Project Information",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(
        modifier = Modifier.height(8.dp)
      )


      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outlineVariant
        )
      ) {

        Column(
          modifier = Modifier.padding(16.dp)
        ) {

          InfoRow(
            label = "Project Title",
            value = "FreshLink AI: Smart Shelf-Life & Dynamic Pricing System"
          )

          Spacer(
            modifier = Modifier.height(10.dp)
          )

          InfoRow(
            label = "Academic Scope",
            value = "Final-Year B.E. Computer Engineering Project"
          )

          Spacer(
            modifier = Modifier.height(10.dp)
          )

          InfoRow(
            label = "Application Edition",
            value = "Consumer Client (Version 1.0)"
          )

          Spacer(
            modifier = Modifier.height(10.dp)
          )

          InfoRow(
            label = "Tagline",
            value = "From Freshness Detection to Freshness Intelligence"
          )
        }
      }


      Spacer(
        modifier = Modifier.height(16.dp)
      )


      /*
       * ABOUT FRESHLINK AI
       */
      Text(
        text = "About FreshLink AI",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(
        modifier = Modifier.height(8.dp)
      )


      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outlineVariant
        )
      ) {

        Column(
          modifier = Modifier.padding(16.dp)
        ) {

          Text(
            text = "Purpose & System Architecture",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(
            modifier = Modifier.height(6.dp)
          )

          Text(
            text = "FreshLink AI empowers consumers—especially inexperienced grocery shoppers—to evaluate visible surface freshness and quality when buying fresh fruits and vegetables. The client is engineered to communicate with a companion Python/FastAPI computer vision microservice for automated inference.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
          )
        }
      }


      Spacer(
        modifier = Modifier.height(16.dp)
      )


      /*
       * IMPORTANT OPERATIONAL DISCLAIMER
       */
      Text(
        text = "Important Operational Disclaimer",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(
        modifier = Modifier.height(8.dp)
      )


      Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
          containerColor =
            MaterialTheme.colorScheme.errorContainer
              .copy(alpha = 0.2f)
        ),

        border = BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
        )
      ) {

        Column(
          modifier = Modifier.padding(16.dp)
        ) {

          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {

            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(20.dp)
            )

            Spacer(
              modifier = Modifier.width(8.dp)
            )

            Text(
              text = "NOT A FOOD-SAFETY DETECTOR",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.error
            )
          }


          Spacer(
            modifier = Modifier.height(6.dp)
          )


          Text(
            text = "Never claim or assume that a food item is safe to eat based solely on an image. FreshLink AI describes its output strictly as a visual freshness and quality estimate. It cannot evaluate microbiological safety, bacterial pathogens, or internal decay hidden beneath the surface.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
          )
        }
      }


      Spacer(
        modifier = Modifier.height(24.dp)
      )
    }
  }
}


/*
 * Reusable information row.
 */
@Composable
private fun InfoRow(
  label: String,
  value: String
) {

  Column(
    modifier = Modifier.fillMaxWidth()
  ) {

    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(
      modifier = Modifier.height(2.dp)
    )

    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}