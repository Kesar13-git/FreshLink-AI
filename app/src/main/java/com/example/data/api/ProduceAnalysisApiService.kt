package com.example.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * ============================================================================
 * FASTAPI RETROFIT INTERFACE
 * ============================================================================
 *
 * WHERE THE API REQUEST IS MADE:
 * This interface defines the contract for calling the Python/FastAPI backend:
 *
 * POST /api/v1/analyze
 * Content-Type: multipart/form-data
 *
 * Parameters:
 * - produce: Selected produce category (e.g. "Tomato", "Apple", "Banana", "Spinach")
 * - image: Multipart image file part
 * ============================================================================
 */
interface ProduceAnalysisApiService {

  @Multipart
  @POST("api/v1/analyze")
  suspend fun analyzeProduce(
    @Part("produce") produce: RequestBody,
    @Part image: MultipartBody.Part
  ): FastApiAnalyzeResponse
}
