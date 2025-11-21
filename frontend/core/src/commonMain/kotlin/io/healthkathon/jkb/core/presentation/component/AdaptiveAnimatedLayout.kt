package io.healthkathon.jkb.core.presentation.component
/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.healthkathon.jkb.core.presentation.preview.JKBPreview

/**
 * A layout that smoothly animates between a compact (phone) and expanded (tablet/desktop)
 * representation based on the available width.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param expandedThreshold The width threshold in Dp to switch to expanded mode. Default is 600.dp.
 * @param compactContent The composable to render when width < threshold.
 * @param expandedContent The composable to render when width >= threshold.
 */
@Composable
fun AdaptiveAnimatedLayout(
    modifier: Modifier = Modifier,
    expandedThreshold: Dp = 600.dp,
    compactContent: @Composable () -> Unit = {},
    expandedContent: @Composable () -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier) {
        // Determine mode based on available width
        val isExpanded = maxWidth >= expandedThreshold

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                // Custom transition: Fade combined with a smooth SizeTransform
                fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300)) using
                    SizeTransform { initialSize, targetSize ->
                        if (targetState) {
                            keyframes {
                                // Expand animation logic
                                IntSize(targetSize.width, initialSize.height) at 150
                                durationMillis = 300
                            }
                        } else {
                            keyframes {
                                // Collapse animation logic
                                IntSize(initialSize.width, targetSize.height) at 150
                                durationMillis = 300
                            }
                        }
                    }
            },
            label = "AdaptiveLayoutAnimation"
        ) { expanded ->
            if (expanded) {
                expandedContent()
            } else {
                compactContent()
            }
        }
    }
}

@Composable
@JKBPreview
fun AdaptiveAnimatedLayoutPreview() {
    // Simulation Wrapper to toggle sizes for preview visualization
    // In a real app, this comes from the window size.
    val isWide = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { isWide.value = !isWide.value }) {
                Text("Toggle Width Simulation")
            }

            Spacer(modifier = Modifier.size(20.dp))

            // The Component
            AdaptiveAnimatedLayout(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    // Simulate width change
                    .size(width = if (isWide.value) 700.dp else 300.dp, height = 400.dp),
                compactContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFFCDD2)) // Red tint
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Phone / Compact Mode")
                        Text("Vertical Layout")
                    }
                },
                expandedContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFC8E6C9)) // Green tint
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tablet / Desktop Mode")
                        Spacer(modifier = Modifier.size(16.dp))
                        Text("Horizontal Layout")
                    }
                }
            )
        }
    }
}
