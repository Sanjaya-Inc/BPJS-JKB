package io.healthkathon.jkb.core.presentation.utils

import androidx.compose.material3.SnackbarVisuals
import androidx.lifecycle.ViewModel
import io.healthkathon.jkb.core.presentation.component.SnackbarEventBus
import io.healthkathon.jkb.core.presentation.navigation.NavigationEventBus
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import org.koin.mp.KoinPlatform.getKoin
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

abstract class BaseViewModel<State : Any, SideEffect : Any>(
    initialState: State,
    onCreate: (suspend BaseViewModel<State, SideEffect>.(Syntax<State, SideEffect>) -> Unit)? = null,
    private val navigationEventBus: NavigationEventBus = getKoin().get(),
    private val snackbarEventBus: SnackbarEventBus = getKoin().get(),
) : ContainerHost<State, SideEffect>, ViewModel() {

    override val container: Container<State, SideEffect> =
        container(
            initialState = initialState,
            onCreate = {
                onCreate?.invoke(this@BaseViewModel, this)
            },
        )

    open fun onIntent(intent: Any) = Unit

    fun sendIntent(intent: Any) {
        onIntent(intent)
        when (intent) {
            is SnackbarVisuals -> snackbarEventBus.post(intent)
            is NavigationIntent -> navigationEventBus.post(intent)
        }
    }
}
