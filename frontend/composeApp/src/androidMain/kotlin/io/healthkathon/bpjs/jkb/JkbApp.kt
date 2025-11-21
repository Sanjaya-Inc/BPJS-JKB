package io.healthkathon.bpjs.jkb

import android.app.Application
import io.healthkathon.bpjs.jkb.presentation.utils.startKoinPlatform
import io.healthkathon.jkb.core.presentation.utils.PlatformContext

class JkbApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoinPlatform(PlatformContext(this))
    }
}
