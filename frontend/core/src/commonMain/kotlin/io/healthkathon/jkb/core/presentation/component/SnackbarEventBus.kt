package io.healthkathon.jkb.core.presentation.component

import androidx.compose.material3.SnackbarVisuals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Single
class SnackbarEventBus(
    private val _flow: MutableSharedFlow<SnackbarVisuals> = MutableSharedFlow(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : SharedFlow<SnackbarVisuals> by _flow,
    CoroutineScope by CoroutineScope(dispatcher) {
    fun post(event: SnackbarVisuals) = launch {
        _flow.emit(event)
    }
}
