package io.healthkathon.jkb.core.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpEngine(): HttpClientEngine {
    return OkHttp.create()
}
