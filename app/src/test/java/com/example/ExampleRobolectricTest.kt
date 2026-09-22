package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.config.AnalysisMode
import com.example.config.AppConfig
import com.example.data.MockProduceAnalysisRepositoryImpl
import com.example.data.ProduceAnalysisRepositoryProvider
import com.example.data.ScanHistoryRepository
import com.example.data.api.FastApiAnalyzeResponse
import com.example.data.api.FastApiProduceAnalysisRepositoryImpl
import com.example.data.api.ProduceAnalysisApiService
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.net.ConnectException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Before
  fun setUp() {
    ScanHistoryRepository.clearUserScans()
    AppConfig.CURRENT_ANALYSIS_MODE = AnalysisMode.MOCK
  }

  @After
  fun tearDown() {
    AppConfig.CURRENT_ANALYSIS_MODE = AnalysisMode.MOCK
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FreshLink AI", appName)
  }

  @Test
  fun `backend base url is centralized and analysis mode defaults to MOCK`() {
    assertEquals("http://10.0.2.2:8000", AppConfig.BACKEND_BASE_URL)
    assertEquals(AnalysisMode.MOCK, AppConfig.CURRENT_ANALYSIS_MODE)

    val defaultRepo = ProduceAnalysisRepositoryProvider.provideRepository()
    assertTrue(defaultRepo is MockProduceAnalysisRepositoryImpl)

    AppConfig.CURRENT_ANALYSIS_MODE = AnalysisMode.BACKEND
    val backendRepo = ProduceAnalysisRepositoryProvider.provideRepository()
    assertTrue(backendRepo is FastApiProduceAnalysisRepositoryImpl)
  }

  @Test
  fun `analysis fails without image in mock repository`() = runBlocking {
    val repo = MockProduceAnalysisRepositoryImpl()
    val result = repo.analyzeProduce(imageUri = null, produceHint = "Tomato")
    assertTrue(result.isFailure)
    assertEquals(
      "Please capture or select a produce image before analyzing.",
      result.exceptionOrNull()?.message
    )
  }

  @Test
  fun `analysis returns correct mock values for each category`() = runBlocking {
    val repo = MockProduceAnalysisRepositoryImpl()
    val testImageUri = "content://media/external/images/media/1"

    // 1. Tomato
    val tomato = repo.analyzeProduce(testImageUri, "Tomato").getOrThrow()
    assertEquals("Tomato", tomato.produce)
    assertEquals(88, tomato.freshnessScore)
    assertEquals("Fresh", tomato.qualityStage)
    assertEquals("3–4 days", tomato.qualityWindow)
    assertEquals(listOf("Good visible colour", "Low visible damage", "Firm appearance"), tomato.reasons)

    // 2. Apple
    val apple = repo.analyzeProduce(testImageUri, "Apple").getOrThrow()
    assertEquals("Apple", apple.produce)
    assertEquals(76, apple.freshnessScore)
    assertEquals("Good", apple.qualityStage)
    assertEquals("5–7 days", apple.qualityWindow)
    assertEquals(listOf("Solid colour", "Minor surface blemishes", "No major visible damage"), apple.reasons)

    // 3. Banana
    val banana = repo.analyzeProduce(testImageUri, "Banana").getOrThrow()
    assertEquals("Banana", banana.produce)
    assertEquals(82, banana.freshnessScore)
    assertEquals("Ripe", banana.qualityStage)
    assertEquals("2–3 days", banana.qualityWindow)
    assertEquals(listOf("Appropriate yellow colour", "Moderate ripeness", "Limited dark areas"), banana.reasons)

    // 4. Spinach
    val spinach = repo.analyzeProduce(testImageUri, "Spinach").getOrThrow()
    assertEquals("Spinach", spinach.produce)
    assertEquals(70, spinach.freshnessScore)
    assertEquals("Good", spinach.qualityStage)
    assertEquals("1–2 days", spinach.qualityWindow)
    assertEquals(listOf("Mostly green leaves", "Minor wilting", "No major visible damage"), spinach.reasons)
  }

  @Test
  fun `fastapi repository maps successful response correctly`() = runBlocking {
    val fakeApiService = object : ProduceAnalysisApiService {
      override suspend fun analyzeProduce(
        produce: RequestBody,
        image: MultipartBody.Part
      ): FastApiAnalyzeResponse {
        return FastApiAnalyzeResponse(
          produce = "Tomato",
          freshnessScore = 88,
          qualityStage = "Fresh",
          qualityWindow = "3-4 days",
          reasons = listOf("Good visible colour", "Low visible damage")
        )
      }
    }

    val repo = FastApiProduceAnalysisRepositoryImpl(apiService = fakeApiService)
    val result = repo.analyzeProduce("content://media/1", "Tomato").getOrThrow()

    assertEquals("Tomato", result.produce)
    assertEquals(88, result.freshnessScore)
    assertEquals("Fresh", result.qualityStage)
    assertEquals("3-4 days", result.qualityWindow)
    assertEquals(listOf("Good visible colour", "Low visible damage"), result.reasons)
    assertFalse(result.isMock)
  }

  @Test
  fun `fastapi repository returns friendly error when backend is unavailable`() = runBlocking {
    val unavailableApiService = object : ProduceAnalysisApiService {
      override suspend fun analyzeProduce(
        produce: RequestBody,
        image: MultipartBody.Part
      ): FastApiAnalyzeResponse {
        throw ConnectException("Failed to connect to /10.0.2.2:8000")
      }
    }

    val repo = FastApiProduceAnalysisRepositoryImpl(apiService = unavailableApiService)
    val result = repo.analyzeProduce("content://media/1", "Tomato")

    assertTrue(result.isFailure)
    assertEquals(
      "Freshness analysis service is currently unavailable. Please try again.",
      result.exceptionOrNull()?.message
    )
  }

  @Test
  fun `fastapi repository fails and does not fabricate data on invalid response`() = runBlocking {
    val invalidDataApiService = object : ProduceAnalysisApiService {
      override suspend fun analyzeProduce(
        produce: RequestBody,
        image: MultipartBody.Part
      ): FastApiAnalyzeResponse {
        // Missing required fields
        return FastApiAnalyzeResponse(
          produce = null,
          freshnessScore = null,
          qualityStage = null,
          qualityWindow = null
        )
      }
    }

    val repo = FastApiProduceAnalysisRepositoryImpl(apiService = invalidDataApiService)
    val result = repo.analyzeProduce("content://media/1", "Tomato")

    assertTrue(result.isFailure)
    assertEquals(
      "Freshness analysis service returned incomplete or invalid data.",
      result.exceptionOrNull()?.message
    )
  }

  @Test
  fun `initial scan history shows sample records only when no user scans exist`() {
    assertFalse(ScanHistoryRepository.hasUserScans())
    val currentHistory = ScanHistoryRepository.historyFlow.value
    assertEquals(3, currentHistory.size)
    assertEquals("Tomato", currentHistory[0].produceName)
    assertEquals("Apple", currentHistory[1].produceName)
    assertEquals("Banana", currentHistory[2].produceName)
  }

  @Test
  fun `scanning items dynamically adds to history with newest at top and replaces sample records`() = runBlocking {
    val repo = MockProduceAnalysisRepositoryImpl()
    val testImageUri = "content://media/external/images/media/test"

    // 1. Scan Tomato (88)
    val tomatoResult = repo.analyzeProduce(testImageUri, "Tomato").getOrThrow()
    ScanHistoryRepository.addScanResult(tomatoResult)

    assertTrue(ScanHistoryRepository.hasUserScans())
    var history = ScanHistoryRepository.historyFlow.value
    assertEquals(1, history.size)
    assertEquals("Tomato", history[0].produceName)
    assertEquals(88, history[0].freshnessScore)
    assertEquals("Fresh", history[0].qualityStage)
    assertEquals("3–4 days", history[0].qualityWindow)
    assertTrue(history[0].dateTimeDisplay.isNotEmpty())

    // 2. Scan Apple (76)
    val appleResult = repo.analyzeProduce(testImageUri, "Apple").getOrThrow()
    ScanHistoryRepository.addScanResult(appleResult)

    history = ScanHistoryRepository.historyFlow.value
    assertEquals(2, history.size)
    assertEquals("Apple", history[0].produceName)
    assertEquals(76, history[0].freshnessScore)
    assertEquals("Good", history[0].qualityStage)
    assertEquals("5–7 days", history[0].qualityWindow)
    assertEquals("Tomato", history[1].produceName)
    assertEquals(88, history[1].freshnessScore)

    // 3. Scan Banana (82)
    val bananaResult = repo.analyzeProduce(testImageUri, "Banana").getOrThrow()
    ScanHistoryRepository.addScanResult(bananaResult)

    history = ScanHistoryRepository.historyFlow.value
    assertEquals(3, history.size)
    assertEquals("Banana", history[0].produceName)
    assertEquals(82, history[0].freshnessScore)
    assertEquals("Ripe", history[0].qualityStage)
    assertEquals("2–3 days", history[0].qualityWindow)

    assertEquals("Apple", history[1].produceName)
    assertEquals(76, history[1].freshnessScore)

    assertEquals("Tomato", history[2].produceName)
    assertEquals(88, history[2].freshnessScore)

    // Verify comparison items update and Best Pick is Tomato (88)
    val comparisonItems = ScanHistoryRepository.getComparisonItems()
    assertEquals(3, comparisonItems.size)
    val bestPick = comparisonItems.firstOrNull { it.isBestPick }
    assertEquals("Tomato", bestPick?.name)
    assertEquals(88, bestPick?.freshnessScore)
  }
}
