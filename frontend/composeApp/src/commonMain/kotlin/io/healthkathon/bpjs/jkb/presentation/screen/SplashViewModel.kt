package io.healthkathon.bpjs.jkb.presentation.screen

import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

data class SplashScreenUiState(val title: String = "")
sealed interface SplashScreenIntent {
    data object NavigateToOnBoarding : SplashScreenIntent, NavigationIntent
}

@KoinViewModel
class SplashScreenViewModel :
    BaseViewModel<SplashScreenUiState, Unit>(
        initialState = SplashScreenUiState("BPJS JKB"),
        onCreate = {
            delay(100.milliseconds)
            sendIntent(SplashScreenIntent.NavigateToOnBoarding)
        }
    )
