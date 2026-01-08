package com.habitarchitect.presentation.screen.pause

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    pauseDuration: Int = 30, // Changed from 60 to 30 seconds
    resistanceItems: List<String> = emptyList()
) {
    var secondsRemaining by remember { mutableIntStateOf(pauseDuration) }
    var isPaused by remember { mutableStateOf(false) }
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    var isManualNavigation by remember { mutableStateOf(false) }

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

    // Auto-rotate slides every 5 seconds (only when not manually navigating)
    LaunchedEffect(resistanceItems, isManualNavigation) {
        if (resistanceItems.isNotEmpty() && !isManualNavigation) {
            while (true) {
                delay(5000)
                if (!isManualNavigation) {
                    currentSlideIndex = (currentSlideIndex + 1) % resistanceItems.size
                }
            }
        }
    }

    // Reset manual navigation flag after 10 seconds of inactivity
    LaunchedEffect(isManualNavigation) {
        if (isManualNavigation) {
            delay(10000)
            isManualNavigation = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Deep indigo
                        Color(0xFF3949AB), // Lighter indigo
                        Color(0xFF5C6BC0)  // Even lighter
                    )
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
            Spacer(modifier = Modifier.height(32.dp))

            // Header with timer - improved design
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Breathing indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF81C784)) // Green
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "BREATHE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = 6.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timer - larger and more prominent
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(100.dp),
                    color = Color(0xFF81C784), // Green progress
                    trackColor = Color.White.copy(alpha = 0.15f),
                    strokeWidth = 6.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = secondsRemaining.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "sec",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Flashcard area with navigation
            if (resistanceItems.isNotEmpty()) {
                Text(
                    text = "Remember why you're doing this:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Card with navigation arrows
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Left arrow
                    IconButton(
                        onClick = {
                            isManualNavigation = true
                            currentSlideIndex = if (currentSlideIndex > 0) {
                                currentSlideIndex - 1
                            } else {
                                resistanceItems.size - 1
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Animated flashcard - improved design
                    AnimatedContent(
                        targetState = currentSlideIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                        slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                        slideOutHorizontally { it } + fadeOut()
                            }
                        },
                        label = "slide",
                        modifier = Modifier.weight(1f)
                    ) { index ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Card number badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE8EAF6))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${index + 1} / ${resistanceItems.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF3949AB),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                // Icon based on content
                                Text(
                                    text = getIconForContent(resistanceItems[index]),
                                    fontSize = 44.sp
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = resistanceItems[index],
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF1A1A1A),
                                    lineHeight = 28.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right arrow
                    IconButton(
                        onClick = {
                            isManualNavigation = true
                            currentSlideIndex = (currentSlideIndex + 1) % resistanceItems.size
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress dots - clickable
                if (resistanceItems.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        resistanceItems.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                                    .size(if (index == currentSlideIndex) 12.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentSlideIndex) Color.White
                                        else Color.White.copy(alpha = 0.35f)
                                    )
                                    .clickable {
                                        isManualNavigation = true
                                        currentSlideIndex = index
                                    }
                            )
                        }
                    }
                }
            } else {
                // Fallback: show default message if no resistance items
                Spacer(modifier = Modifier.weight(0.3f))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🧘",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You committed to reducing $habitName.",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Is doing this helping you become the person you want to be?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.7f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stay Strong button (visible after 10 seconds for 30s timer)
            if (secondsRemaining <= 20) {
                Button(
                    onClick = {
                        isPaused = true
                        onStayStrong()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784) // Green
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "I'll Stay Strong 💪",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Returns an appropriate emoji based on content keywords.
 */
private fun getIconForContent(content: String): String {
    val lowerContent = content.lowercase()
    return when {
        lowerContent.contains("health") || lowerContent.contains("body") -> "💪"
        lowerContent.contains("money") || lowerContent.contains("cost") || lowerContent.contains("$") -> "💰"
        lowerContent.contains("time") || lowerContent.contains("wast") -> "⏰"
        lowerContent.contains("family") || lowerContent.contains("relationship") -> "👨‍👩‍👧‍👦"
        lowerContent.contains("work") || lowerContent.contains("career") || lowerContent.contains("job") -> "💼"
        lowerContent.contains("sleep") || lowerContent.contains("tired") -> "😴"
        lowerContent.contains("stress") || lowerContent.contains("anxiety") -> "😰"
        lowerContent.contains("focus") || lowerContent.contains("productive") -> "🎯"
        lowerContent.contains("happy") || lowerContent.contains("joy") -> "😊"
        else -> "💡"
    }
}
