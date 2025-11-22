package io.healthkathon.jkb.frauddetection.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class ClaimIdFormState(
    initialClaimId: String = ""
) {
    var claimId by mutableStateOf(initialClaimId)

    val isFormValid: Boolean
        get() = claimId.isNotBlank()

    fun reset() {
        claimId = ""
    }
}

@Composable
fun rememberClaimIdFormState(
    initialClaimId: String = ""
): ClaimIdFormState = remember {
    ClaimIdFormState(initialClaimId)
}
