package io.healthkathon.jkb.core.presentation.utils

import kotlin.coroutines.cancellation.CancellationException

inline fun <T> Result<T>.onFailureRethrowCancellation(action: (Throwable) -> Unit): Result<T> {
    return onFailure { throwable ->
        if (throwable is CancellationException) throw throwable
        action(throwable)
    }
}

inline fun <T> Result<T>.getOrElseRethrowCancellation(onFailure: (Throwable) -> T): T {
    return fold(
        onSuccess = { it },
        onFailure = { throwable ->
            if (throwable is CancellationException) throw throwable
            onFailure(throwable)
        }
    )
}

fun <T> Result<T>.getOrNullRethrowCancellation(): T? {
    return fold(
        onSuccess = { it },
        onFailure = { throwable ->
            if (throwable is CancellationException) throw throwable
            null
        }
    )
}
