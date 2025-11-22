package io.healthkathon.jkb.onboarding.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.healthkathon.jkb.core.presentation.component.AdaptiveAnimatedLayout
import io.healthkathon.jkb.core.presentation.preview.JKBPreview
import io.healthkathon.jkb.core.presentation.theme.JKBTheme
import io.healthkathon.jkb.core.presentation.utils.CoreResources
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state = viewModel.collectAsState().value
    OnboardingScreenContent(
        uiState = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

@Composable
private fun OnboardingScreenContent(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { paddingValues ->
        AdaptiveAnimatedLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            compactContent = {
                CompactOnboardingLayout(
                    uiState = uiState,
                    onIntent = onIntent
                )
            },
            expandedContent = {
                ExpandedOnboardingLayout(
                    uiState = uiState,
                    onIntent = onIntent
                )
            }
        )
    }
}

@Composable
private fun CompactOnboardingLayout(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = uiState.currentPage < uiState.pages.size - 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextButton(onClick = { onIntent(OnboardingIntent.Skip) }) {
                    Text(stringResource(CoreResources.ctaSkip))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OnboardingPageContent(
            page = uiState.pages[uiState.currentPage],
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        PageIndicator(
            pageCount = uiState.pages.size,
            currentPage = uiState.currentPage,
            onPageClick = { page -> onIntent(OnboardingIntent.GoToPage(page)) },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OnboardingActions(
            currentPage = uiState.currentPage,
            totalPages = uiState.pages.size,
            onNextPage = { onIntent(OnboardingIntent.NextPage) },
            onComplete = { onIntent(OnboardingIntent.Complete) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        )
    }
}

@Composable
private fun ExpandedOnboardingLayout(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            OnboardingPageContent(
                page = uiState.pages[uiState.currentPage],
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = uiState.currentPage < uiState.pages.size - 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onIntent(OnboardingIntent.Skip) }) {
                        Text(stringResource(CoreResources.ctaSkip))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = uiState.pages[uiState.currentPage].title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.pages[uiState.currentPage].description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            PageIndicator(
                pageCount = uiState.pages.size,
                currentPage = uiState.currentPage,
                onPageClick = { page -> onIntent(OnboardingIntent.GoToPage(page)) },
                modifier = Modifier.padding(vertical = 24.dp)
            )

            OnboardingActions(
                currentPage = uiState.currentPage,
                totalPages = uiState.pages.size,
                onNextPage = { onIntent(OnboardingIntent.NextPage) },
                onComplete = { onIntent(OnboardingIntent.Complete) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OnboardingIllustration(
            emoji = getEmojiForPage(page.imageResource),
            gradientColors = getGradientForPage(page.imageResource),
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun OnboardingIllustration(
    emoji: String,
    gradientColors: PersistentList<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = MaterialTheme.typography.displayLarge.fontSize * 2
            )
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    onPageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = tween(300)
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            )

            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun OnboardingActions(
    currentPage: Int,
    totalPages: Int,
    onNextPage: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLastPage = currentPage == totalPages - 1

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLastPage) {
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(CoreResources.ctaContinue))
            }
        } else {
            FilledTonalButton(
                onClick = onNextPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(CoreResources.ctaContinue))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(CoreResources.icArrowForward),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun getEmojiForPage(imageResource: String): String {
    return when (imageResource) {
        "onboarding_1" -> "👋"
        "onboarding_2" -> "🏥"
        "onboarding_3" -> "💳"
        else -> "✨"
    }
}

@Composable
private fun getGradientForPage(imageResource: String): PersistentList<Color> {
    return when (imageResource) {
        "onboarding_1" -> persistentListOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
        "onboarding_2" -> persistentListOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer
        )
        "onboarding_3" -> persistentListOf(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.primaryContainer
        )
        else -> persistentListOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Serializable
object Onboarding

@Composable
@JKBPreview
fun OnboardingScreenPreview() {
    JKBTheme {
        OnboardingScreenContent(
            uiState = OnboardingUiState(),
            onIntent = {}
        )
    }
}
