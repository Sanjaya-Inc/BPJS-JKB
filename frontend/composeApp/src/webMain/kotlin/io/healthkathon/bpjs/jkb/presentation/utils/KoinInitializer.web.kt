package io.healthkathon.bpjs.jkb.presentation.utils

import io.healthkathon.jkb.core.presentation.utils.PlatformContext
import org.koin.core.context.startKoin

actual fun startKoinPlatform(context: PlatformContext?) {
    startKoin {
        modules(getKoinModules(context))
    }
}
