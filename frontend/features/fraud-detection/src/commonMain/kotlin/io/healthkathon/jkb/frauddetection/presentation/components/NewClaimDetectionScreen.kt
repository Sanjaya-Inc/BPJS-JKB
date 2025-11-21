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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewClaimDetectionScreen(
    isLoading: Boolean,
    result: String?,
    onSubmit: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hospital by remember { mutableStateOf("") }
    var doctor by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }

    var hospitalExpanded by remember { mutableStateOf(false) }
    var doctorExpanded by remember { mutableStateOf(false) }
    var diagnosisExpanded by remember { mutableStateOf(false) }
    var actionExpanded by remember { mutableStateOf(false) }

    val hospitals = listOf(
        "RS Harapan Sehat",
        "RS Mitra Keluarga",
        "RS Siloam",
        "RS Hermina",
        "RSUD Kota"
    )

    val doctors = listOf(
        "Dr. Ahmad Wijaya, Sp.PD",
        "Dr. Siti Nurhaliza, Sp.A",
        "Dr. Budi Santoso, Sp.B",
        "Dr. Rina Kusuma, Sp.OG",
        "Dr. Hendra Gunawan, Sp.JP"
    )

    val diagnoses = listOf(
        "Diabetes Mellitus Type 2",
        "Hipertensi",
        "ISPA (Infeksi Saluran Pernapasan Akut)",
        "Gastritis",
        "Demam Berdarah Dengue",
        "Appendicitis",
        "Pneumonia"
    )

    val actions = listOf(
        "Rawat Jalan",
        "Rawat Inap",
        "Operasi",
        "Pemeriksaan Lab",
        "Konsultasi"
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

        ExposedDropdownMenuBox(
            expanded = hospitalExpanded,
            onExpandedChange = { hospitalExpanded = !hospitalExpanded && !isLoading }
        ) {
            OutlinedTextField(
                value = hospital,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rumah Sakit") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hospitalExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = hospitalExpanded,
                onDismissRequest = { hospitalExpanded = false }
            ) {
                hospitals.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            hospital = item
                            hospitalExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = doctorExpanded,
            onExpandedChange = { doctorExpanded = !doctorExpanded && !isLoading }
        ) {
            OutlinedTextField(
                value = doctor,
                onValueChange = {},
                readOnly = true,
                label = { Text("Dokter") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = doctorExpanded,
                onDismissRequest = { doctorExpanded = false }
            ) {
                doctors.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            doctor = item
                            doctorExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = diagnosisExpanded,
            onExpandedChange = { diagnosisExpanded = !diagnosisExpanded && !isLoading }
        ) {
            OutlinedTextField(
                value = diagnosis,
                onValueChange = {},
                readOnly = true,
                label = { Text("Diagnosis") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diagnosisExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = diagnosisExpanded,
                onDismissRequest = { diagnosisExpanded = false }
            ) {
                diagnoses.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            diagnosis = item
                            diagnosisExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Total Biaya") },
            placeholder = { Text("Contoh: Rp 5.000.000") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = actionExpanded,
            onExpandedChange = { actionExpanded = !actionExpanded && !isLoading }
        ) {
            OutlinedTextField(
                value = action,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tindakan") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actionExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = actionExpanded,
                onDismissRequest = { actionExpanded = false }
            ) {
                actions.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            action = item
                            actionExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSubmit(hospital, doctor, diagnosis, cost, action) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hospital.isNotBlank() && doctor.isNotBlank() &&
                diagnosis.isNotBlank() && cost.isNotBlank() &&
                action.isNotBlank() && !isLoading,
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
                MarkdownResultCard(markdown = it)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
