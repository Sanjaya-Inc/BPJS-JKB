package io.healthkathon.bpjs.jkb

import androidx.compose.runtime.Composable
import io.healthkathon.bpjs.jkb.presentation.navigation.JkbNavHost
import io.healthkathon.jkb.core.presentation.preview.JKBPreview
import io.healthkathon.jkb.core.presentation.theme.JKBTheme

@Composable
@JKBPreview
fun App() {
    JKBTheme {
        JkbNavHost()
    }
}
