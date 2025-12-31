package com.habitarchitect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

/**
 * Full-screen celebration overlay shown when user hits a milestone streak.
 */
@Composable
fun MilestoneCelebration(
    streak: Int,
    onDismiss: () -> Unit
) {
    val celebrationMessage = when {
        streak >= 365 -> "INCREDIBLE! ONE YEAR!"
        streak >= 100 -> "AMAZING! 100 DAYS!"
        streak >= 90 -> "PHENOMENAL! 90 DAYS!"
        streak >= 60 -> "OUTSTANDING! 60 DAYS!"
        streak >= 30 -> "FANTASTIC! 30 DAYS!"
        streak >= 21 -> "AWESOME! 21 DAYS!"
        streak >= 14 -> "GREAT! 2 WEEKS!"
        streak >= 7 -> "NICE! 1 WEEK!"
        else -> "MILESTONE REACHED!"
    }

    val subMessage = when {
        streak >= 365 -> "You've built a life-changing habit!"
        streak >= 100 -> "This is extraordinary dedication!"
        streak >= 90 -> "You've formed a powerful habit!"
        streak >= 60 -> "Two months of consistency!"
        streak >= 30 -> "A full month! Habit is taking root!"
        streak >= 21 -> "21 days! The habit is forming!"
        streak >= 14 -> "Building momentum!"
        streak >= 7 -> "Great start! Keep going!"
        else -> "Keep up the great work!"
    }

    // Auto-dismiss after 5 seconds
    LaunchedEffect(Unit) {
        delay(5000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Confetti animation
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    position = Position.Relative(0.5, 0.3),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                ),
                Party(
                    speed = 0f,
                    maxSpeed = 20f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0x4CAF50, 0x2196F3, 0xFFC107, 0xE91E63),
                    position = Position.Relative(0.2, 0.5),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(50)
                ),
                Party(
                    speed = 0f,
                    maxSpeed = 20f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0x9C27B0, 0x00BCD4, 0xFF5722, 0x8BC34A),
                    position = Position.Relative(0.8, 0.5),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(50)
                )
            )
        )

        // Celebration card
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$streak",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "DAY STREAK",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = celebrationMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onDismiss) {
                    Text("Continue")
                }
            }
        }
    }
}
