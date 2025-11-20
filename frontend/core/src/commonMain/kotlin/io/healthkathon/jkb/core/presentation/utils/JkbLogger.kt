package io.healthkathon.jkb.core.presentation.utils

import io.github.aakira.napier.Napier

object JkbLogger {
    fun i(tag: String, message: String) {
        Napier.i("[$tag]: $message")
    }

    fun e(tag: String, message: String) {
        Napier.e("[$tag]: $message")
    }
}
