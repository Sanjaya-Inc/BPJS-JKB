package io.healthkathon.jkb.core.data.remote

import BPJS_JKB.core.BuildConfig
import org.koin.core.annotation.Single

@Single
class RemoteApiConfigProvider {
    fun getBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    fun isMock(): Boolean {
        return BuildConfig.API_MOCKED
    }
}
