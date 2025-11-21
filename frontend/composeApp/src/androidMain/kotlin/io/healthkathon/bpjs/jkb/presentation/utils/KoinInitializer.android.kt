package io.healthkathon.bpjs.jkb.presentation.utils

import io.healthkathon.jkb.core.presentation.utils.PlatformContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

actual fun startKoinPlatform(context: PlatformContext?) {
    context?.let {
        startKoin {
            androidContext(context.appContext)
            androidLogger()
            modules(getKoinModules(context))
        }
    }
}
