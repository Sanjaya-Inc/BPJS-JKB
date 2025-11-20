package io.healthkathon.jkb.core.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.logging.Logger
import org.koin.core.annotation.Single

@Single
class ApiHttpLogger : Logger {
    override fun log(message: String) {
        Napier.d("[$TAG] $message")
    }

    companion object {
        private const val TAG = "ApiHttpLogger"
    }
}
