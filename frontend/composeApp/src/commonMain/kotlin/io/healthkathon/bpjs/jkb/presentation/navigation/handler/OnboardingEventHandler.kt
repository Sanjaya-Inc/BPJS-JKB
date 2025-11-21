/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.healthkathon.bpjs.jkb.presentation.navigation.handler

import androidx.navigation.NavController
import io.healthkathon.bpjs.jkb.presentation.navigation.NavigationEventHandler
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.menu.presentation.Menu
import io.healthkathon.jkb.onboarding.presentation.Onboarding
import io.healthkathon.jkb.onboarding.presentation.OnboardingIntent
import org.koin.core.annotation.Factory

@Factory
class OnboardingEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is OnboardingIntent.Skip || event is OnboardingIntent.Complete
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        navController.navigate(
            Menu
        ) {
            popUpTo(Onboarding) {
                inclusive = true
            }
        }
    }
}
