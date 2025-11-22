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
    initialClaimId: String = "",
    initialHospitalId: String = "",
    initialDoctorId: String = "",
    initialDiagnosis: String = "",
    initialTotalCost: String = "",
    initialSymptoms: String = "",
    initialTreatment: String = "",
    initialMedications: String = "",
    initialNotes: String = ""
) {
    var claimId by mutableStateOf(initialClaimId)
    var hospitalId by mutableStateOf(initialHospitalId)
    var doctorId by mutableStateOf(initialDoctorId)
    var diagnosis by mutableStateOf(initialDiagnosis)

    private var totalCostRaw by mutableStateOf(initialTotalCost)
    var totalCostField by mutableStateOf(
        TextFieldValue(
            text = formatCurrency(initialTotalCost),
            selection = TextRange(formatCurrency(initialTotalCost).length)
        )
    )
        private set

    var symptoms by mutableStateOf(initialSymptoms)
    var treatment by mutableStateOf(initialTreatment)
    var medications by mutableStateOf(initialMedications)
    var notes by mutableStateOf(initialNotes)

    var claimIdExpanded by mutableStateOf(false)
    var hospitalExpanded by mutableStateOf(false)
    var doctorExpanded by mutableStateOf(false)
    var diagnosisExpanded by mutableStateOf(false)

    val totalCostValue: String
        get() = totalCostRaw

    val isFormValid: Boolean
        get() = claimId.isNotBlank() &&
            hospitalId.isNotBlank() &&
            doctorId.isNotBlank() &&
            diagnosis.isNotBlank() &&
            totalCostRaw.isNotBlank() &&
            symptoms.isNotBlank() &&
            treatment.isNotBlank() &&
            medications.isNotBlank()

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
        claimId = ""
        hospitalId = ""
        doctorId = ""
        diagnosis = ""
        totalCostRaw = ""
        totalCostField = TextFieldValue("")
        symptoms = ""
        treatment = ""
        medications = ""
        notes = ""
        claimIdExpanded = false
        hospitalExpanded = false
        doctorExpanded = false
        diagnosisExpanded = false
    }
}

@Composable
fun rememberNewClaimFormState(
    initialClaimId: String = "",
    initialHospitalId: String = "",
    initialDoctorId: String = "",
    initialDiagnosis: String = "",
    initialTotalCost: String = "",
    initialSymptoms: String = "",
    initialTreatment: String = "",
    initialMedications: String = "",
    initialNotes: String = ""
): NewClaimFormState = remember {
    NewClaimFormState(
        initialClaimId,
        initialHospitalId,
        initialDoctorId,
        initialDiagnosis,
        initialTotalCost,
        initialSymptoms,
        initialTreatment,
        initialMedications,
        initialNotes
    )
}
