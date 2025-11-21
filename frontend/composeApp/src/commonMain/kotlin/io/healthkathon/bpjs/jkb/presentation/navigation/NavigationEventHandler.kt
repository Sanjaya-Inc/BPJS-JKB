package io.healthkathon.bpjs.jkb.presentation.navigation

import androidx.navigation.NavController
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent

abstract class NavigationEventHandler {
    abstract fun canHandle(event: NavigationIntent): Boolean
    abstract fun navigate(navController: NavController, event: NavigationIntent)
}
