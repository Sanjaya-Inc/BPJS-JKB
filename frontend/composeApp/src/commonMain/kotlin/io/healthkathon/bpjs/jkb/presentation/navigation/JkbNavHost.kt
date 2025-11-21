package io.healthkathon.bpjs.jkb.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.healthkathon.bpjs.jkb.presentation.screen.Splash
import io.healthkathon.bpjs.jkb.presentation.screen.SplashScreen
import io.healthkathon.jkb.core.presentation.component.rememberSnackBarHostState
import io.healthkathon.jkb.core.presentation.theme.LocalNavController
import io.healthkathon.jkb.core.presentation.theme.LocalSnackBarHost
import io.healthkathon.jkb.frauddetection.presentation.FraudDetection
import io.healthkathon.jkb.frauddetection.presentation.FraudDetectionScreen
import io.healthkathon.jkb.menu.presentation.Menu
import io.healthkathon.jkb.menu.presentation.MenuScreen
import io.healthkathon.jkb.onboarding.presentation.Onboarding
import io.healthkathon.jkb.onboarding.presentation.OnboardingScreen

@Composable
fun JkbNavHost() {
    val navController = rememberNavController()
    val snackBarHostState = rememberSnackBarHostState()
    NavigationEventBusHandler(navController)

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackBarHost provides snackBarHostState
    ) {
        NavHost(navController = navController, startDestination = Splash) {
            composable<Splash> { SplashScreen() }
            composable<Onboarding> { OnboardingScreen() }
            composable<Menu> { MenuScreen() }
            composable<FraudDetection> { FraudDetectionScreen() }
        }
    }
}
