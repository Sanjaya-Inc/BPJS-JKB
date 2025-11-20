package io.healthkathon.jkb.core.presentation.theme

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController: ProvidableCompositionLocal<NavController?> =
    compositionLocalOf { null }

val LocalSnackBarHost: ProvidableCompositionLocal<SnackbarHostState?> =
    compositionLocalOf { null }
