package io.healthkathon.jkb.frauddetection.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.healthkathon.jkb.frauddetection.presentation.ActorType
import io.healthkathon.jkb.frauddetection.presentation.FraudDetectionIntent
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActorDetectionScreen(
    isLoading: Boolean,
    result: String?,
    doctors: PersistentList<String>,
    hospitals: PersistentList<String>,
    isLoadingData: Boolean,
    dataError: String?,
    onIntent: (FraudDetectionIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val formState = rememberActorAnalysisFormState()
    val actorList = if (formState.selectedActorType == ActorType.DOCTOR) doctors else hospitals
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
            emoji = "👤",
            gradientColors = persistentListOf(
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.primaryContainer
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Analisis Fraud Berdasarkan Aktor",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Analisis pola fraud dari dokter atau rumah sakit tertentu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Pilih Tipe Aktor",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = formState.selectedActorType == ActorType.DOCTOR,
                onClick = {
                    formState.changeActorType(ActorType.DOCTOR)
                    if (doctors.isEmpty() && !isLoadingData) {
                        onIntent(FraudDetectionIntent.LoadDoctors)
                    }
                },
                label = { Text("👨‍⚕️ Dokter") },
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilterChip(
                selected = formState.selectedActorType == ActorType.HOSPITAL,
                onClick = {
                    formState.changeActorType(ActorType.HOSPITAL)
                    if (hospitals.isEmpty() && !isLoadingData) {
                        onIntent(FraudDetectionIntent.LoadHospitals)
                    }
                },
                label = { Text("🏥 Rumah Sakit") },
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (dataError != null) {
            Text(
                text = dataError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        ExposedDropdownMenuBox(
            expanded = formState.actorExpanded,
            onExpandedChange = {
                if (!formState.actorExpanded && actorList.isEmpty() && !isLoadingData) {
                    if (formState.selectedActorType == ActorType.DOCTOR) {
                        onIntent(FraudDetectionIntent.LoadDoctors)
                    } else {
                        onIntent(FraudDetectionIntent.LoadHospitals)
                    }
                }
                formState.actorExpanded = !formState.actorExpanded && !isLoading && !isLoadingData
            }
        ) {
            OutlinedTextField(
                value = formState.selectedActor,
                onValueChange = {},
                readOnly = true,
                label = { Text(formState.selectedActorType.displayName) },
                placeholder = {
                    Text(
                        if (isLoadingData) {
                            "Memuat data..."
                        } else {
                            "Pilih ${formState.selectedActorType.displayName}"
                        }
                    )
                },
                trailingIcon = {
                    if (isLoadingData) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = formState.actorExpanded)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                enabled = !isLoading && !isLoadingData && actorList.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                isError = dataError != null
            )
            ExposedDropdownMenu(
                expanded = formState.actorExpanded,
                onDismissRequest = { formState.actorExpanded = false }
            ) {
                if (actorList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Tidak ada data") },
                        onClick = { }
                    )
                } else {
                    actorList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                formState.selectedActor = item
                                formState.actorExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onIntent(
                    FraudDetectionIntent.SubmitActorAnalysis(
                        formState.selectedActorType,
                        formState.selectedActor
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
                Text("👤 Analisis Aktor")
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
