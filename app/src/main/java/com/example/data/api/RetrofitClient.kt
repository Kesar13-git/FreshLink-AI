package com.example.data.api

import com.example.config.AppConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit and OkHttpClient builder.
 *
 * Configured with:
 * - [AppConfig.BACKEND_BASE_URL] (centralized URL configuration)
 * - Moshi JSON converter with [KotlinJsonAdapterFactory]
 * - Logging interceptor for debugging network communication
 * - Safe timeouts (15s connect, 30s read/write) for computer-vision inference
 */
object RetrofitClient {

  private val moshi: Moshi by lazy {
    Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()
  }

  private val okHttpClient: OkHttpClient by lazy {
    val logging = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    OkHttpClient.Builder()
      .connectTimeout(15, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .addInterceptor(logging)
      .build()
  }

  val produceAnalysisApiService: ProduceAnalysisApiService by lazy {
    val normalizedBaseUrl = if (AppConfig.BACKEND_BASE_URL.endsWith("/")) {
      AppConfig.BACKEND_BASE_URL
    } else {
      "${AppConfig.BACKEND_BASE_URL}/"
    }

    Retrofit.Builder()
      .baseUrl(normalizedBaseUrl)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(ProduceAnalysisApiService::class.java)
  }
}
