package io.healthkathon.bpjs.jkb

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.healthkathon.bpjs.jkb.presentation.utils.startKoinPlatform

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoinPlatform(null)
    
    ComposeViewport {
        App()
    }
}