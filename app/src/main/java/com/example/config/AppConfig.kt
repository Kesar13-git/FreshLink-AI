package com.example.config

/**
 * ============================================================================
 * CENTRALIZED APPLICATION & BACKEND CONFIGURATION
 * ============================================================================
 *
 * 1. WHERE THE BACKEND URL IS CONFIGURED:
 *    [BACKEND_BASE_URL] below holds the root address for the FastAPI microservice.
 *
 *    DEVELOPMENT PLACEHOLDER:
 *    "http://10.0.2.2:8000" refers to the host machine's localhost from within
 *    the standard Android Emulator.
 *
 *    IMPORTANT NOTE FOR RAINA & BACKEND TEAM:
 *    This is only a development placeholder. Replace this value with the actual
 *    server address when Raina's FastAPI backend is deployed or hosted
 *    (e.g., "https://api.freshlink.example.com" or "http://192.168.1.100:8000").
 *
 * 2. HOW MOCK MODE CAN LATER BE SWITCHED TO BACKEND MODE:
 *    Change [CURRENT_ANALYSIS_MODE] below from [AnalysisMode.MOCK] to [AnalysisMode.BACKEND].
 *    The UI screens and ViewModel require zero modifications because both implementations
 *    conform strictly to [com.example.data.ProduceAnalysisRepository].
 *
 * 3. CURRENT STATE:
 *    Kept in [AnalysisMode.MOCK] by default so local development and prototype
 *    demonstrations continue working reliably until the FastAPI service is live.
 * ============================================================================
 */
object AppConfig {

  /**
   * Centralized backend base URL configuration.
   * NOTE: Development placeholder for host localhost via Android emulator.
   * Must be replaced with the actual backend address when Raina's FastAPI server is available.
   */
  const val BACKEND_BASE_URL = "http://10.0.2.2:8000"

  /**
   * Current analysis mode:
   * - [AnalysisMode.MOCK]: Offline deterministic mock results for prototype demonstration.
   * - [AnalysisMode.BACKEND]: Live multipart HTTP calls to FastAPI /api/v1/analyze.
   */
  var CURRENT_ANALYSIS_MODE: AnalysisMode = AnalysisMode.MOCK
}

/**
 * Analysis mode enumeration.
 */
enum class AnalysisMode {
  MOCK,
  BACKEND
}
