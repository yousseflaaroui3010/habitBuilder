package com.habitarchitect.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Full-screen overlay animation that shows when a streak is broken.
 * The counter animates down from the previous streak to 0.
 */
@Composable
fun StreakBreakAnimation(
    previousStreak: Int,
    visible: Boolean,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        var displayedStreak by remember { mutableIntStateOf(previousStreak) }
        val progress = remember { Animatable(0f) }

        LaunchedEffect(previousStreak) {
            // Reset the animation
            displayedStreak = previousStreak
            progress.snapTo(0f)

            // Wait a moment before starting the countdown
            delay(500)

            // Animate from 0 to 1 over time proportional to streak size
            val animationDuration = (previousStreak * 150).coerceIn(500, 3000)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearEasing
                )
            )

            // Hold at 0 for a moment
            delay(800)
            onAnimationComplete()
        }

        // Update displayed streak based on progress
        LaunchedEffect(progress.value) {
            val newStreak = (previousStreak * (1 - progress.value)).toInt()
            displayedStreak = newStreak
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Streak Broken",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = displayedStreak.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (displayedStreak == 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.White
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (displayedStreak == 0) {
                    Text(
                        text = "Don't give up. Every day is a new chance.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
