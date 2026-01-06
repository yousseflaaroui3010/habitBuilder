package com.habitarchitect.presentation.screen.pause

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitarchitect.presentation.theme.GradientBlueDark
import com.habitarchitect.presentation.theme.GradientBlueLight
import kotlinx.coroutines.delay

@Composable
fun PauseScreen(
    habitName: String,
    onComplete: () -> Unit,
    onStayStrong: () -> Unit,
    pauseDuration: Int = 60,
    resistanceItems: List<String> = emptyList()
) {
    var secondsRemaining by remember { mutableIntStateOf(pauseDuration) }
    var isPaused by remember { mutableStateOf(false) }
    var currentSlideIndex by remember { mutableIntStateOf(0) }

    val progress by animateFloatAsState(
        targetValue = 1f - (secondsRemaining.toFloat() / pauseDuration),
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    // Countdown timer
    LaunchedEffect(isPaused) {
        if (!isPaused) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }
            onComplete()
        }
    }

    // Auto-rotate slides every 5 seconds
    LaunchedEffect(resistanceItems) {
        if (resistanceItems.isNotEmpty()) {
            while (true) {
                delay(5000)
                currentSlideIndex = (currentSlideIndex + 1) % resistanceItems.size
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientBlueDark, GradientBlueLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header with timer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PAUSE",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Compact timer
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(60.dp)
                ) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(60.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = secondsRemaining.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Flashcard area
            if (resistanceItems.isNotEmpty()) {
                Text(
                    text = "Remember why you want to quit:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated flashcard
                AnimatedContent(
                    targetState = currentSlideIndex,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    label = "slide",
                    modifier = Modifier.weight(1f)
                ) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${index + 1} of ${resistanceItems.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "⚠️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = resistanceItems[index],
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress dots
                if (resistanceItems.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        resistanceItems.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (index == currentSlideIndex) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentSlideIndex) Color.White
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }
            } else {
                // Fallback: show default message if no resistance items
                Spacer(modifier = Modifier.weight(0.3f))
                Text(
                    text = "You committed to reducing $habitName.",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Is doing this helping you become the person you want to be?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(0.7f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stay Strong button (visible after 15 seconds)
            if (secondsRemaining <= 45) {
                Button(
                    onClick = {
                        isPaused = true
                        onStayStrong()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "I'll Stay Strong 💪",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GradientBlueDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
