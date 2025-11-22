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
                    coreRepository.runCatching {
                        getServiceData()
                    }.onFailure {
                        reduce {
                            state.copy(
                                title = "BPJS JKB",
                                version = "Error getting version",
                                isLoading = false
                            )
                        }
                    }.onSuccess { response ->
                        reduce {
                            state.copy(
                                title = response.message ?: "BPJS JKB",
                                version = response.version ?: "",
                                isLoading = false
                            )
                        }
                        delay(1000.milliseconds)
                    }
                    sendIntent(SplashScreenIntent.NavigateToOnBoarding)
                }
            }
        }
    }
}
