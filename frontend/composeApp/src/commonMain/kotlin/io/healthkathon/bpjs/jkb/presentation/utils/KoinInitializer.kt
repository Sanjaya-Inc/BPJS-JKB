package io.healthkathon.bpjs.jkb.presentation.utils

import io.healthkathon.bpjs.jkb.di.AppModule
import io.healthkathon.jkb.core.di.CoreModules
import io.healthkathon.jkb.core.presentation.utils.PlatformContext
import io.healthkathon.jkb.onboarding.di.OnbaordingModule
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

expect fun startKoinPlatform(context: PlatformContext?)

internal fun getKoinModules(context: PlatformContext?): List<Module> = listOf(
    CoreModules.module,
    module {
        single<PlatformContext?> { context }
    },
    AppModule.module,
    OnbaordingModule.module
)
