@file:Suppress("Deprecation")

package io.healthkathon.jkb.core.presentation.preview

import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JKBPreview
