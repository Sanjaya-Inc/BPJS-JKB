/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.healthkathon.jkb.core.data.remote

import kotlinx.coroutines.delay
import kotlin.random.Random

object ApiConfig {
    const val USE_MOCK_API = true

    private val networkDelayRange = 500L..1500L
    private val longOperationDelayRange = 2000L..3000L

    suspend fun applyNetworkDelay() {
        if (USE_MOCK_API) {
            val delayMs = Random.nextLong(networkDelayRange.first, networkDelayRange.last + 1)
            delay(delayMs)
        }
    }

    suspend fun applyLongOperationDelay() {
        if (USE_MOCK_API) {
            val delayMs = Random.nextLong(longOperationDelayRange.first, longOperationDelayRange.last + 1)
            delay(delayMs)
        }
    }
}
