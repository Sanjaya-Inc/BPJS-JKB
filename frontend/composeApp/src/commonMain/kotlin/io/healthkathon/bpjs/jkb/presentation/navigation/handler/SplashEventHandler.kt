/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.healthkathon.bpjs.jkb.presentation.navigation.handler

import androidx.navigation.NavController
import io.healthkathon.bpjs.jkb.presentation.navigation.NavigationEventHandler
import io.healthkathon.bpjs.jkb.presentation.screen.Splash
import io.healthkathon.bpjs.jkb.presentation.screen.SplashScreenIntent
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.onboarding.presentation.Onboarding
import org.koin.core.annotation.Factory

@Factory
class SplashEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is SplashScreenIntent.NavigateToOnBoarding
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            SplashScreenIntent.NavigateToOnBoarding -> navController.navigate(
                Onboarding
            ) {
                popUpTo(Splash) {
                    inclusive = true
                }
            }
        }
    }
}
