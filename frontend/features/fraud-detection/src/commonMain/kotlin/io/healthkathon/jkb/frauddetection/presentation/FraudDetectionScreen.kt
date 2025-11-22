package io.healthkathon.jkb.frauddetection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.healthkathon.jkb.core.presentation.component.AdaptiveAnimatedLayout
import io.healthkathon.jkb.core.presentation.preview.JKBPreview
import io.healthkathon.jkb.core.presentation.theme.JKBTheme
import io.healthkathon.jkb.frauddetection.presentation.components.ClaimIdDetectionScreen
import io.healthkathon.jkb.frauddetection.presentation.components.NewClaimDetectionScreen
import io.healthkathon.jkb.frauddetection.presentation.components.ActorDetectionScreen
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun FraudDetectionScreen(
    modifier: Modifier = Modifier,
    viewModel: FraudDetectionViewModel = koinViewModel(),
) {
    val state = viewModel.collectAsState().value
    FraudDetectionScreenContent(
        uiState = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

@Composable
private fun FraudDetectionScreenContent(
    uiState: FraudDetectionUiState,
    onIntent: (FraudDetectionIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { paddingValues ->
        AdaptiveAnimatedLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            compactContent = {
                CompactFraudDetectionLayout(
                    uiState = uiState,
                    onIntent = onIntent
                )
            },
            expandedContent = {
                ExpandedFraudDetectionLayout(
                    uiState = uiState,
                    onIntent = onIntent
                )
            }
        )
    }
}

@Composable
private fun CompactFraudDetectionLayout(
    uiState: FraudDetectionUiState,
    onIntent: (FraudDetectionIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (uiState.currentTab) {
                FraudDetectionTab.CLAIM_ID -> ClaimIdDetectionScreen(
                    isLoading = uiState.isLoading,
                    result = uiState.result,
                    claims = uiState.filteredClaims,
                    selectedClaimId = uiState.selectedClaimId,
                    searchQuery = uiState.searchQuery,
                    isLoadingData = uiState.isLoadingData,
                    dataError = uiState.dataError,
                    feedbackGiven = uiState.feedbackGiven,
                    onIntent = onIntent
                )
                FraudDetectionTab.NEW_CLAIM -> NewClaimDetectionScreen(
                    isLoading = uiState.isLoading,
                    result = uiState.result,
                    feedbackGiven = uiState.feedbackGiven,
                    doctors = uiState.doctorsName,
                    hospitals = uiState.hospitalsName,
                    diagnoses = uiState.diagnosesDisplay,
                    diagnosesIds = uiState.diagnoses.map { it.diagnosisId }.toPersistentList(),
                    claims = uiState.claimsIds,
                    isLoadingData = uiState.isLoadingData,
                    dataError = uiState.dataError,
                    onIntent = onIntent
                )
                FraudDetectionTab.ACTOR -> ActorDetectionScreen(
                    isLoading = uiState.isLoading,
                    result = uiState.result,
                    feedbackGiven = uiState.feedbackGiven,
                    doctors = uiState.doctorsName,
                    hospitals = uiState.hospitalsName,
                    isLoadingData = uiState.isLoadingData,
                    dataError = uiState.dataError,
                    onIntent = onIntent
                )
            }
        }

        NavigationBar {
            FraudDetectionTab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = uiState.currentTab == tab,
                    onClick = { onIntent(FraudDetectionIntent.NavigateToTab(tab)) },
                    icon = {
                        Text(
                            text = tab.icon,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ExpandedFraudDetectionLayout(
    uiState: FraudDetectionUiState,
    onIntent: (FraudDetectionIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavigationRail(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            FraudDetectionTab.entries.forEach { tab ->
                NavigationRailItem(
                    selected = uiState.currentTab == tab,
                    onClick = { onIntent(FraudDetectionIntent.NavigateToTab(tab)) },
                    icon = {
                        Text(
                            text = tab.icon,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (uiState.currentTab) {
                FraudDetectionTab.CLAIM_ID -> ClaimIdDetectionScreen(
                    isLoading = uiState.isLoading,
                    result = uiState.result,
                    claims = uiState.filteredClaims,
                    selectedClaimId = uiState.selectedClaimId,
                    searchQuery = uiState.searchQuery,
                    isLoadingData = uiState.isLoadingData,
                    dataError = uiState.dataError,
                    feedbackGiven = uiState.feedbackGiven,
                    onIntent = onIntent
                )
                FraudDetectionTab.NEW_CLAIM -> NewClaimDetectionScreen(
                    isLoading = uiState.isLoading,
                    result = uiState.result,
                    feedbackGiven = uiState.feedbackGiven,
                    doctors = uiState.doctorsName,
                    hospitals = uiState.hospitalsName,
                    diagnoses = uiState.diagnosesDisplay,
                    diagnosesIds = uiState.diagnoses.map { it.diagnosisId }.toPersistentList(),
                    claims = uiState.claimsIds,
                    isLoadingData = uiState.isLoadingData,
                    dataError = uiState.dataError,
                    onIntent = onIntent
                )
                FraudDetectionTab.ACTOR -> ActorDetectionScreen(
                    isLoading = uiState.isLoading,
                    result = uiState.result,
                    feedbackGiven = uiState.feedbackGiven,
                    doctors = uiState.doctorsName,
                    hospitals = uiState.hospitalsName,
                    isLoadingData = uiState.isLoadingData,
                    dataError = uiState.dataError,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Serializable
object FraudDetection

@Composable
@JKBPreview
fun FraudDetectionScreenPreview() {
    JKBTheme {
        FraudDetectionScreenContent(
            uiState = FraudDetectionUiState(),
            onIntent = {}
        )
    }
}
