package io.healthkathon.bpjs.jkb.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.healthkathon.jkb.core.presentation.preview.JKBPreview
import io.healthkathon.jkb.core.presentation.theme.JKBTheme
import io.healthkathon.jkb.core.presentation.utils.CoreResources
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashScreenViewModel = koinViewModel(),
) {
    val state = viewModel.collectAsState().value
    SplashScreenContent(uiState = state, modifier = modifier)
}

@Composable
private fun SplashScreenContent(
    uiState: SplashScreenUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        CoreResources.appLogo
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(155.dp)
                )
                AnimatedContent(uiState.isLoading) { loading ->
                    when (loading) {
                        true -> CircularProgressIndicator()
                        false -> Column {
                            Text(
                                uiState.title,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                uiState.version,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Serializable
object Splash

@Composable
@JKBPreview
fun SplashScreenPreview() {
    JKBTheme {
        SplashScreenContent(
            modifier = Modifier.background(Color.White),
            uiState = SplashScreenUiState()
        )
    }
}
