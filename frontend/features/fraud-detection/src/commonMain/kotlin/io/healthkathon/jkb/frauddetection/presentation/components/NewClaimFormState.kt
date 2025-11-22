package io.healthkathon.jkb.frauddetection.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Stable
class NewClaimFormState(
    initialHospitalId: String = "",
    initialDoctorId: String = "",
    initialDiagnosisId: String = "",
    initialTotalCost: String = "",
    initialPrimaryProcedure: String = "",
    initialSecondaryProcedure: String = "",
    initialDiagnosisText: String = ""
) {
    var hospitalId by mutableStateOf(initialHospitalId)
    var doctorId by mutableStateOf(initialDoctorId)
    var diagnosisId by mutableStateOf(initialDiagnosisId)

    private var totalCostRaw by mutableStateOf(initialTotalCost)
    var totalCostField by mutableStateOf(
        TextFieldValue(
            text = formatCurrency(initialTotalCost),
            selection = TextRange(formatCurrency(initialTotalCost).length)
        )
    )
        private set

    var primaryProcedure by mutableStateOf(initialPrimaryProcedure)
    var secondaryProcedure by mutableStateOf(initialSecondaryProcedure)
    var diagnosisText by mutableStateOf(initialDiagnosisText)

    var hospitalExpanded by mutableStateOf(false)
    var doctorExpanded by mutableStateOf(false)

    val totalCostValue: String
        get() = totalCostRaw

    val isFormValid: Boolean
        get() = hospitalId.isNotBlank() &&
            doctorId.isNotBlank() &&
            diagnosisId.isNotBlank() &&
            totalCostRaw.isNotBlank() &&
            primaryProcedure.isNotBlank() &&
            secondaryProcedure.isNotBlank() &&
            diagnosisText.isNotBlank()

    fun updateTotalCost(newValue: TextFieldValue) {
        val digitsOnly = newValue.text.filter { it.isDigit() }

        if (digitsOnly == totalCostRaw) {
            totalCostField = newValue
            return
        }

        totalCostRaw = digitsOnly
        val formatted = formatCurrency(digitsOnly)

        val oldDigitCount = totalCostField.text.count { it.isDigit() }
        val newDigitCount = digitsOnly.length
        val digitDiff = newDigitCount - oldDigitCount

        val oldCursorPos = newValue.selection.start
        val digitsBeforeCursor = newValue.text.take(oldCursorPos).count { it.isDigit() }

        var newCursorPos = 0
        var digitsSeen = 0
        for (i in formatted.indices) {
            if (formatted[i].isDigit()) {
                digitsSeen++
                if (digitsSeen >= digitsBeforeCursor + digitDiff) {
                    newCursorPos = i + 1
                    break
                }
            }
        }

        if (newCursorPos == 0) newCursorPos = formatted.length

        totalCostField = TextFieldValue(
            text = formatted,
            selection = TextRange(newCursorPos)
        )
    }

    private fun formatCurrency(value: String): String {
        if (value.isBlank()) return ""
        val number = value.toLongOrNull() ?: return value
        val formatted = number.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        return "Rp $formatted"
    }

    fun reset() {
        hospitalId = ""
        doctorId = ""
        diagnosisId = ""
        totalCostRaw = ""
        totalCostField = TextFieldValue("")
        primaryProcedure = ""
        secondaryProcedure = ""
        diagnosisText = ""
        hospitalExpanded = false
        doctorExpanded = false
    }
}

@Composable
fun rememberNewClaimFormState(
    initialHospitalId: String = "",
    initialDoctorId: String = "",
    initialDiagnosisId: String = "",
    initialTotalCost: String = "",
    initialPrimaryProcedure: String = "",
    initialSecondaryProcedure: String = "",
    initialDiagnosisText: String = ""
): NewClaimFormState = remember {
    NewClaimFormState(
        initialHospitalId,
        initialDoctorId,
        initialDiagnosisId,
        initialTotalCost,
        initialPrimaryProcedure,
        initialSecondaryProcedure,
        initialDiagnosisText
    )
}
