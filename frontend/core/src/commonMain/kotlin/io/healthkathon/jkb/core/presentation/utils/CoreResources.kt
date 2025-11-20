package io.healthkathon.jkb.core.presentation.utils

import bpjs_jkb.core.generated.resources.Res
import bpjs_jkb.core.generated.resources.cta_continue
import bpjs_jkb.core.generated.resources.cta_skip
import bpjs_jkb.core.generated.resources.ic_arrow_forward
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

object CoreResources {
    val ctaContinue: StringResource
        get() = Res.string.cta_continue

    val ctaSkip: StringResource
        get() = Res.string.cta_skip

    val icArrowForward: DrawableResource
        get() = Res.drawable.ic_arrow_forward
}
