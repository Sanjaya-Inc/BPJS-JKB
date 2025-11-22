package io.healthkathon.bpjs.jkb.presentation.screen

import io.healthkathon.jkb.core.domain.CoreRepository
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

data class SplashScreenUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val version: String = ""
)

sealed interface SplashScreenIntent {
    data object Init : SplashScreenIntent
    data object NavigateToOnBoarding : SplashScreenIntent, NavigationIntent
}

@KoinViewModel
class SplashScreenViewModel(
    private val coreRepository: CoreRepository
) :
    BaseViewModel<SplashScreenUiState, Unit>(
        initialState = SplashScreenUiState(),
        onCreate = {
            sendIntent(SplashScreenIntent.Init)
        }
    ) {

    override fun onIntent(intent: Any) {
        intent {
            when (intent) {
                is SplashScreenIntent.Init -> {
                    reduce { state.copy(isLoading = true) }
                    val data = coreRepository.getServiceData()
                    reduce {
                        state.copy(
                            title = data.message ?: "BPJS JKB",
                            version = data.version ?: "",
                            isLoading = false
                        )
                    }
                    delay(1000.milliseconds)
                    sendIntent(SplashScreenIntent.NavigateToOnBoarding)
                }
            }
        }
    }
}
