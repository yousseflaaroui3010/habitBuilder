package com.habitarchitect.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaperClipJar(
    currentCount: Int,
    goalCount: Int,
    modifier: Modifier = Modifier,
    isBuildHabit: Boolean = true
) {
    val progress = (currentCount.toFloat() / goalCount.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    val progressColor = if (isBuildHabit) Color(0xFF4CAF50) else Color(0xFFE57373)
    val backgroundColor = if (isBuildHabit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paper Clip Jar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (currentCount >= goalCount) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Goal reached!",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Visual progress tracker",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Jar visualization
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
                    .clip(JarShape())
                    .border(2.dp, progressColor, JarShape())
                    .background(backgroundColor),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Fill level
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((120.dp * animatedProgress))
                        .background(progressColor.copy(alpha = 0.7f))
                )

                // Paper clips (dots representing clips)
                PaperClipsGrid(
                    count = currentCount.coerceAtMost(goalCount),
                    maxCount = goalCount,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress text
            Text(
                text = "$currentCount / $goalCount",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )

            Text(
                text = if (currentCount >= goalCount)
                    "Goal reached!"
                else
                    "${goalCount - currentCount} more to go",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PaperClipsGrid(
    count: Int,
    maxCount: Int,
    color: Color
) {
    val rows = 5
    val cols = 4
    val displayCount = count.coerceAtMost(rows * cols)

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until cols) {
                    val index = row * cols + col
                    val isFilled = index < displayCount

                    val animatedColor by animateColorAsState(
                        targetValue = if (isFilled) Color.White else Color.Transparent,
                        label = "clip_$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(animatedColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun JarShape() = RoundedCornerShape(
    topStart = 8.dp,
    topEnd = 8.dp,
    bottomStart = 20.dp,
    bottomEnd = 20.dp
)

@Composable
fun WeeklyPaperClipProgress(
    weeklySuccesses: Int,
    weeklyGoal: Int = 7,
    modifier: Modifier = Modifier,
    isBuildHabit: Boolean = true
) {
    val progress = (weeklySuccesses.toFloat() / weeklyGoal).coerceIn(0f, 1f)
    val progressColor = if (isBuildHabit) Color(0xFF4CAF50) else Color(0xFFE57373)
    val backgroundColor = if (isBuildHabit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$weeklySuccesses / $weeklyGoal",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Paper clip row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0 until weeklyGoal) {
                    PaperClipIcon(
                        isFilled = i < weeklySuccesses,
                        color = progressColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PaperClipIcon(
    isFilled: Boolean,
    color: Color
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isFilled) color else color.copy(alpha = 0.2f),
        label = "clip_color"
    )

    // Simple circle representation of a paper clip
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(animatedColor),
        contentAlignment = Alignment.Center
    ) {
        if (isFilled) {
            Text(
                text = "~",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
