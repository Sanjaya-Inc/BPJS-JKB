package io.healthkathon.bpjs.jkb

import androidx.compose.ui.window.ComposeUIViewController
import io.healthkathon.bpjs.jkb.presentation.utils.startKoinPlatform

fun MainViewController() = ComposeUIViewController {
    startKoinPlatform(null)
    App()
}
