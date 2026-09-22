package com.example.data

import android.content.Context
import com.example.config.AnalysisMode
import com.example.config.AppConfig
import com.example.data.api.FastApiProduceAnalysisRepositoryImpl

/**
 * ============================================================================
 * PRODUCE ANALYSIS REPOSITORY PROVIDER
 * ============================================================================
 *
 * ARCHITECTURAL FLOW:
 * UI (Composable Screens)
 *   ↓
 * ProduceAnalysisRepository (Abstraction)
 *   ↓
 * [MockProduceAnalysisRepositoryImpl] OR [FastApiProduceAnalysisRepositoryImpl]
 *   ↓
 * FastAPI Backend (http://10.0.2.2:8000/api/v1/analyze)
 *   ↓
 * Computer Vision / AI Model
 *
 * HOW MOCK MODE CAN LATER BE SWITCHED TO BACKEND MODE:
 * 1. Open [com.example.config.AppConfig].
 * 2. Set [AppConfig.CURRENT_ANALYSIS_MODE] = [AnalysisMode.BACKEND].
 * 3. Verify [AppConfig.BACKEND_BASE_URL] points to Raina's live FastAPI endpoint.
 *
 * Zero UI changes are required because the presentation layer only depends
 * on the [ProduceAnalysisRepository] interface.
 * ============================================================================
 */
object ProduceAnalysisRepositoryProvider {

  /**
   * Provides the active [ProduceAnalysisRepository] according to [AppConfig.CURRENT_ANALYSIS_MODE].
   *
   * By default, returns [MockProduceAnalysisRepositoryImpl] for prototype demonstration.
   */
  fun provideRepository(context: Context? = null): ProduceAnalysisRepository {
    return when (AppConfig.CURRENT_ANALYSIS_MODE) {
      AnalysisMode.MOCK -> MockProduceAnalysisRepositoryImpl()
      AnalysisMode.BACKEND -> FastApiProduceAnalysisRepositoryImpl(context = context)
    }
  }
}
