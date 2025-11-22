package io.healthkathon.jkb.frauddetection.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.healthkathon.jkb.frauddetection.presentation.FraudDetectionIntent
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewClaimDetectionScreen(
    isLoading: Boolean,
    result: String?,
    doctors: PersistentList<String>,
    hospitals: PersistentList<String>,
    isLoadingData: Boolean,
    dataError: String?,
    onIntent: (FraudDetectionIntent) -> Unit,
    modifier: Modifier = Modifier,
    feedbackGiven: Boolean = false,
) {
    val formState = rememberNewClaimFormState()

    val diagnoses = listOf(
        "Diabetes Mellitus Type 2",
        "Hipertensi",
        "ISPA (Infeksi Saluran Pernapasan Akut)",
        "Gastritis",
        "Demam Berdarah Dengue",
        "Appendicitis",
        "Pneumonia"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        IllustrationBox(
            emoji = "📝",
            gradientColors = persistentListOf(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.tertiaryContainer
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Deteksi Fraud Klaim Baru",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Isi data klaim baru untuk menganalisis potensi fraud sebelum diproses",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (dataError != null) {
            Text(
                text = dataError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = formState.claimId,
            onValueChange = { formState.claimId = it },
            label = { Text("Claim ID") },
            placeholder = { Text("Contoh: CLM-2025-001234") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = formState.hospitalExpanded,
            onExpandedChange = {
                if (!formState.hospitalExpanded && hospitals.isEmpty() && !isLoadingData) {
                    onIntent(FraudDetectionIntent.LoadHospitals)
                }
                formState.hospitalExpanded = !formState.hospitalExpanded && !isLoading && !isLoadingData
            }
        ) {
            OutlinedTextField(
                value = formState.hospitalId,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hospital ID") },
                placeholder = {
                    Text(
                        if (isLoadingData) {
                            "Memuat data..."
                        } else {
                            "Pilih Hospital ID"
                        }
                    )
                },
                trailingIcon = {
                    if (isLoadingData && hospitals.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = formState.hospitalExpanded)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                enabled = !isLoading && !isLoadingData && hospitals.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                isError = dataError != null
            )
            ExposedDropdownMenu(
                expanded = formState.hospitalExpanded,
                onDismissRequest = { formState.hospitalExpanded = false }
            ) {
                if (hospitals.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Tidak ada data") },
                        onClick = { }
                    )
                } else {
                    hospitals.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                formState.hospitalId = item
                                formState.hospitalExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = formState.doctorExpanded,
            onExpandedChange = {
                if (!formState.doctorExpanded && doctors.isEmpty() && !isLoadingData) {
                    onIntent(FraudDetectionIntent.LoadDoctors)
                }
                formState.doctorExpanded = !formState.doctorExpanded && !isLoading && !isLoadingData
            }
        ) {
            OutlinedTextField(
                value = formState.doctorId,
                onValueChange = {},
                readOnly = true,
                label = { Text("Doctor ID") },
                placeholder = {
                    Text(
                        if (isLoadingData) {
                            "Memuat data..."
                        } else {
                            "Pilih Doctor ID"
                        }
                    )
                },
                trailingIcon = {
                    if (isLoadingData && doctors.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = formState.doctorExpanded)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                enabled = !isLoading && !isLoadingData && doctors.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                isError = dataError != null
            )
            ExposedDropdownMenu(
                expanded = formState.doctorExpanded,
                onDismissRequest = { formState.doctorExpanded = false }
            ) {
                if (doctors.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Tidak ada data") },
                        onClick = { }
                    )
                } else {
                    doctors.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                formState.doctorId = item
                                formState.doctorExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = formState.diagnosisExpanded,
            onExpandedChange = { formState.diagnosisExpanded = !formState.diagnosisExpanded && !isLoading }
        ) {
            OutlinedTextField(
                value = formState.diagnosis,
                onValueChange = {},
                readOnly = true,
                label = { Text("Diagnosis") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formState.diagnosisExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = formState.diagnosisExpanded,
                onDismissRequest = { formState.diagnosisExpanded = false }
            ) {
                diagnoses.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            formState.diagnosis = item
                            formState.diagnosisExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.totalCostField,
            onValueChange = { formState.updateTotalCost(it) },
            label = { Text("Total Cost") },
            placeholder = { Text("Contoh: Rp 5.000.000") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Medical Resume",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.symptoms,
            onValueChange = { formState.symptoms = it },
            label = { Text("Symptoms") },
            placeholder = { Text("Contoh: Demam tinggi, batuk, sakit kepala") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            minLines = 2,
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.treatment,
            onValueChange = { formState.treatment = it },
            label = { Text("Treatment") },
            placeholder = { Text("Contoh: Rawat inap, observasi 24 jam") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            minLines = 2,
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.medications,
            onValueChange = { formState.medications = it },
            label = { Text("Medications") },
            placeholder = { Text("Contoh: Paracetamol 500mg, Amoxicillin 250mg") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            minLines = 2,
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.notes,
            onValueChange = { formState.notes = it },
            label = { Text("Additional Notes") },
            placeholder = { Text("Catatan tambahan tentang kondisi pasien") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onIntent(
                    FraudDetectionIntent.SubmitNewClaim(
                        formState.claimId,
                        formState.hospitalId,
                        formState.doctorId,
                        formState.diagnosis,
                        formState.totalCostValue,
                        formState.symptoms,
                        formState.treatment,
                        formState.medications,
                        formState.notes
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.isFormValid && !isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Menganalisis...")
            } else {
                Text("📝 Analisis Klaim Baru")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(visible = result != null) {
            result?.let {
                MarkdownResultCard(
                    markdown = it,
                    feedbackGiven = feedbackGiven,
                    onFeedback = { isLike ->
                        onIntent(FraudDetectionIntent.SubmitFeedback(isLike))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
