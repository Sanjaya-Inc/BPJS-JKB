package io.healthkathon.jkb.core.presentation.navigation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

interface NavigationIntent

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Single
class NavigationEventBus(
    private val _flow: MutableSharedFlow<NavigationIntent> = MutableSharedFlow(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : SharedFlow<NavigationIntent> by _flow,
    CoroutineScope by CoroutineScope(dispatcher) {
    fun post(event: NavigationIntent) = launch {
        _flow.emit(event)
    }
}
