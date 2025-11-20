package io.healthkathon.jkb.core.data.remote

import BPJS_JKB.core.BuildConfig
import org.koin.core.annotation.Single

@Single
class BaseUrlProvider {
    fun getBaseUrl(): String {
        return BuildConfig.BASE_URL
    }
}
