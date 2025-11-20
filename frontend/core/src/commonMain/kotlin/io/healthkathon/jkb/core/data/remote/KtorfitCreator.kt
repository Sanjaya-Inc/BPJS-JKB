package io.healthkathon.jkb.core.data.remote

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class KtorfitCreator(
    private val baseUrlProvider: BaseUrlProvider,
    private val apiHttpLogger: ApiHttpLogger,
    private val json: Json
) {
    fun create(): Ktorfit {
        return Ktorfit.Builder()
            .httpClient(createHttpEngine()) {
                install(ContentNegotiation) {
                    json(json, contentType = ContentType.Application.Json)
                }
                expectSuccess = true
                install(Logging) {
                    logger = apiHttpLogger
                    level = LogLevel.ALL
                }
            }
            .baseUrl(baseUrlProvider.getBaseUrl())
            .build()
    }
}

expect fun createHttpEngine(): HttpClientEngine
