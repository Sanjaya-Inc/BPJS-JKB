@file:Suppress("Deprecation")

package io.healthkathon.jkb.core.presentation.preview

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * A versatile PreviewParameterProvider that can feed any sequence of values.
 *
 * Example usage:
 *   @JKBPreview
 *   @Composable
 *   fun MyPreview(
 *       @PreviewParameter(GenericPreviewProvider::class) isActive: Boolean
 *   ) {
 *       MyComposable(isActive = isActive)
 *   }
 */
open class GenericPreviewProvider<T>(
    items: Sequence<T>
) : PreviewParameterProvider<T> {
    override val values: Sequence<T> = items
}
