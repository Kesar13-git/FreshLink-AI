package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.ProduceAnalysisRepository
import com.example.model.ProduceAnalysisResult
import com.example.ui.components.DisclaimerCard
import kotlinx.coroutines.launch

/**
 * 3. SCAN PRODUCE SCREEN
 *
 * Captures or selects produce imagery for quality assessment:
 * - Direct camera photo capture via [ActivityResultContracts.TakePicturePreview]
 * - Gallery selection via [ActivityResultContracts.PickVisualMedia]
 * - Real-time image preview and state storage
 * - Image validation: prevents analysis if no image is selected/captured
 * - Produce category selector: Tomato, Apple, Banana, Spinach
 * - Realistic mock analysis service integration via [ProduceAnalysisRepository]
 */
@Composable
fun ScanScreen(
  analysisRepository: ProduceAnalysisRepository,
  onAnalysisSuccess: (ProduceAnalysisResult) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val scrollState = rememberScrollState()

  // Image State
  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var selectedProduceTag by remember { mutableStateOf("Tomato") }
  var isAnalyzing by remember { mutableStateOf(false) }
  var validationErrorMessage by remember { mutableStateOf<String?>(null) }

  // Supported sample produce categories as explicitly requested
  val produceOptions = listOf("Tomato", "Apple", "Banana", "Spinach")

  // Camera Launcher
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      capturedBitmap = bitmap
      selectedImageUri = null
      validationErrorMessage = null // Clear any prior error
    }
  }

  // Camera Permission Launcher
  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      cameraLauncher.launch()
    } else {
      Toast.makeText(context, "Camera permission needed to take photos", Toast.LENGTH_SHORT).show()
    }
  }

  // Photo Picker Launcher (zero-permission modern Android Photo Picker)
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      selectedImageUri = uri
      capturedBitmap = null
      validationErrorMessage = null // Clear any prior error
    }
  }

  val hasImage = selectedImageUri != null || capturedBitmap != null

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Screen Title & Subtitle
    Text(
      text = "Produce Scanner",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "Capture or select fresh produce to estimate visual shelf-life and quality stage.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Image Preview / Viewfinder Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(4f / 3f)
        .testTag("scan_viewfinder_card"),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      border = BorderStroke(
        width = 1.5.dp,
        color = if (hasImage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
      )
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        when {
          capturedBitmap != null -> {
            Image(
              bitmap = capturedBitmap!!.asImageBitmap(),
              contentDescription = "Captured produce image",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            // Clear image button overlay
            IconButton(
              onClick = {
                capturedBitmap = null
              },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape)
                .size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove photo",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          selectedImageUri != null -> {
            AsyncImage(
              model = selectedImageUri,
              contentDescription = "Selected produce from gallery",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            // Clear image button overlay
            IconButton(
              onClick = {
                selectedImageUri = null
              },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape)
                .size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove photo",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          else -> {
            // Empty State Viewfinder with scan guides
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(24.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Scan viewfinder icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "No produce image selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Take a photo or choose an existing picture from your device gallery.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Image Input Buttons: Open Camera vs Select Gallery
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = {
          cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        },
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("open_camera_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(
          imageVector = Icons.Outlined.PhotoCamera,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Take Photo")
      }

      OutlinedButton(
        onClick = {
          galleryLauncher.launch(
            androidx.activity.result.PickVisualMediaRequest(
              ActivityResultContracts.PickVisualMedia.ImageOnly
            )
          )
        },
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("open_gallery_button"),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
      ) {
        Icon(
          imageVector = Icons.Outlined.Collections,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "From Gallery", color = MaterialTheme.colorScheme.primary)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Produce Type Tag Selection (passed into mock analysis service)
    Text(
      text = "Produce Category",
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = "Select produce category to inspect tailored quality factors.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(produceOptions) { produce ->
        val selected = selectedProduceTag == produce
        FilterChip(
          selected = selected,
          onClick = { selectedProduceTag = produce },
          label = { Text(text = produce) },
          leadingIcon = if (selected) {
            {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
            }
          } else null,
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          shape = RoundedCornerShape(20.dp)
        )
      }
    }

    // Validation Error Message Banner if triggered
    if (validationErrorMessage != null) {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("scan_validation_error"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error icon",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = validationErrorMessage!!,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // "Analyze Produce" Primary Action Button
    // Enabled ONLY when an image has been captured or selected and not analyzing
    Button(
      onClick = {
        // Strict Image Validation Check
        if (!hasImage) {
          validationErrorMessage = "Please capture or select a produce image before analyzing."
          Toast.makeText(
            context,
            "Please capture or select a produce image before analyzing.",
            Toast.LENGTH_SHORT
          ).show()
          return@Button
        }

        if (!isAnalyzing) {
          isAnalyzing = true
          validationErrorMessage = null

          val imageIdentifier = selectedImageUri?.toString()
            ?: "camera://captured_photo_${System.currentTimeMillis()}"

          coroutineScope.launch {
            val result = analysisRepository.analyzeProduce(
              imageUri = imageIdentifier,
              produceHint = selectedProduceTag
            )
            isAnalyzing = false
            result.onSuccess { analysisResult ->
              onAnalysisSuccess(analysisResult)
            }.onFailure { error ->
              val msg = error.localizedMessage ?: "Analysis error. Please retry."
              validationErrorMessage = msg
              Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
          }
        }
      },
      enabled = hasImage && !isAnalyzing,
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("analyze_produce_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
      )
    ) {
      if (isAnalyzing) {
        CircularProgressIndicator(
          color = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(22.dp),
          strokeWidth = 2.5.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "Analyzing Produce...",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
      } else {
        Icon(
          imageVector = Icons.Default.Psychology,
          contentDescription = null,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (hasImage) "Analyze Produce" else "Select Image to Analyze",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Transparency Note regarding Mock Analysis
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Eco,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Prototype Demonstration: Values are simulated mock records for college project presentation, architected to connect with our Python/FastAPI CV backend.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Safety disclaimer
    DisclaimerCard(isCompact = true)

    Spacer(modifier = Modifier.height(24.dp))
  }
}
