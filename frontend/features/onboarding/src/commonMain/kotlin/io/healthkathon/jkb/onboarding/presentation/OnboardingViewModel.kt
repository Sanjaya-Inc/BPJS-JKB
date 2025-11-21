package io.healthkathon.jkb.onboarding.presentation

import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import org.koin.android.annotation.KoinViewModel

data class OnboardingUiState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            title = "Selamat Datang di BPJS JKB",
            description = "Sistem internal deteksi fraud berbasis AI untuk melindungi integritas " +
                "program jaminan kesehatan",
            imageResource = "onboarding_1"
        ),
        OnboardingPage(
            title = "Chatbot AI Cerdas",
            description = "Analisis potensi fraud secara real-time dengan bantuan chatbot AI yang " +
                "dapat mendeteksi pola mencurigakan",
            imageResource = "onboarding_2"
        ),
        OnboardingPage(
            title = "Monitoring & Pelaporan",
            description = "Pantau aktivitas mencurigakan dan buat laporan komprehensif untuk " +
                "tindakan preventif",
            imageResource = "onboarding_3"
        )
    )
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageResource: String
)

sealed interface OnboardingIntent {
    data object NextPage : OnboardingIntent
    data object PreviousPage : OnboardingIntent
    data class GoToPage(val page: Int) : OnboardingIntent
    data object Skip : OnboardingIntent, NavigationIntent
    data object Complete : OnboardingIntent, NavigationIntent
}

@KoinViewModel
class OnboardingViewModel : BaseViewModel<OnboardingUiState, Unit>(
    initialState = OnboardingUiState()
) {
    override fun onIntent(intent: Any) {
        when (intent) {
            is OnboardingIntent.NextPage -> nextPage()
            is OnboardingIntent.PreviousPage -> previousPage()
            is OnboardingIntent.GoToPage -> goToPage(intent.page)
            is OnboardingIntent.Skip -> skip()
            is OnboardingIntent.Complete -> complete()
        }
    }

    private fun nextPage() = intent {
        val currentState = state
        if (currentState.currentPage < currentState.pages.size - 1) {
            reduce { state.copy(currentPage = currentState.currentPage + 1) }
        } else {
            sendIntent(OnboardingIntent.Complete)
        }
    }

    private fun previousPage() = intent {
        val currentState = state
        if (currentState.currentPage > 0) {
            reduce { state.copy(currentPage = currentState.currentPage - 1) }
        }
    }

    private fun goToPage(page: Int) = intent {
        reduce { state.copy(currentPage = page) }
    }

    private fun skip() {
        sendIntent(OnboardingIntent.Skip)
    }

    private fun complete() {
        sendIntent(OnboardingIntent.Complete)
    }
}
