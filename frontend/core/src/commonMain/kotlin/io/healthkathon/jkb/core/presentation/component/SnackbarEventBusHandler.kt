package io.healthkathon.jkb.core.presentation.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import org.koin.compose.koinInject

@Composable
fun rememberSnackBarHostState(
    eventBus: SnackbarEventBus = koinInject()
): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        eventBus.collect {
            snackbarHostState.showSnackbar(it)
        }
    }
    return snackbarHostState
}

data class DefaultSnackBarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false
) : SnackbarVisuals
