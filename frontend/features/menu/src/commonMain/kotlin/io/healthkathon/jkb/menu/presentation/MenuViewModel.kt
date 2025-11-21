package io.healthkathon.jkb.menu.presentation

import androidx.compose.ui.graphics.Color
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

data class MenuUiState(
    val userName: String = "Admin",
    val menuItems: PersistentList<MenuItem> = persistentListOf(
        MenuItem(
            id = "fraud-detection",
            title = "Fraud Detection",
            description = "Analisis dan deteksi pola fraud pada klaim BPJS secara real-time",
            emoji = "🔍",
            gradientColors = persistentListOf(
                Color(0xFF1F4FAB),
                Color(0xFF3E68C5)
            )
        ),
        MenuItem(
            id = "chatbot",
            title = "AI Chatbot",
            description = "Konsultasi dengan AI untuk investigasi fraud dan analisis mendalam",
            emoji = "🤖",
            gradientColors = persistentListOf(
                Color(0xFF7E368A),
                Color(0xFF994FA4)
            )
        )
    )
)

data class MenuItem(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val gradientColors: PersistentList<Color>
)

sealed interface MenuIntent {
    data class NavigateToFeature(val menuItem: MenuItem) : MenuIntent, NavigationIntent
    data object NavigateToProfile : MenuIntent, NavigationIntent
}

@KoinViewModel
class MenuViewModel : BaseViewModel<MenuUiState, Unit>(
    initialState = MenuUiState()
) {
    override fun onIntent(intent: Any) {
        when (intent) {
            is MenuIntent.NavigateToFeature -> navigateToFeature(intent.menuItem)
            is MenuIntent.NavigateToProfile -> navigateToProfile()
        }
    }

    private fun navigateToFeature(menuItem: MenuItem) {
        sendIntent(MenuIntent.NavigateToFeature(menuItem))
    }

    private fun navigateToProfile() {
        sendIntent(MenuIntent.NavigateToProfile)
    }
}
