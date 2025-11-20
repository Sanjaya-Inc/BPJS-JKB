package io.healthkathon.jkb.core.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun createHttpEngine(): HttpClientEngine {
    return CIO.create()
}
