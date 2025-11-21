/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.healthkathon.bpjs.jkb.presentation.navigation.handler

import androidx.navigation.NavController
import io.healthkathon.bpjs.jkb.presentation.navigation.NavigationEventHandler
import io.healthkathon.jkb.core.presentation.navigation.NavigationIntent
import io.healthkathon.jkb.frauddetection.presentation.FraudDetection
import io.healthkathon.jkb.menu.presentation.MenuIntent
import org.koin.core.annotation.Factory

@Factory
class MenuEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is MenuIntent.NavigateToFeature || event is MenuIntent.NavigateToProfile
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        if (event is MenuIntent.NavigateToFeature) {
            when (event.menuItem.id) {
                "fraud-detection" -> navController.navigate(
                    FraudDetection
                )
            }
        }
    }
}
