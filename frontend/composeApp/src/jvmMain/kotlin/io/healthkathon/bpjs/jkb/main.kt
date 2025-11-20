package io.healthkathon.bpjs.jkb

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BPJS-JKB",
    ) {
        App()
    }
}
