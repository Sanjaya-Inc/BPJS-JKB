package io.healthkathon.bpjs.jkb

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.healthkathon.bpjs.jkb.presentation.utils.startKoinPlatform

fun main() = application {
    startKoinPlatform(null)

    Window(
        onCloseRequest = ::exitApplication,
        title = "BPJS-JKB",
    ) {
        App()
    }
}
