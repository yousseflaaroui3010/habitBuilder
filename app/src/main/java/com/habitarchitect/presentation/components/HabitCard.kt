package com.habitarchitect.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.presentation.theme.CalendarFailure
import com.habitarchitect.presentation.theme.CalendarSuccess
import com.habitarchitect.presentation.theme.Streak

/**
 * Card component displaying a habit with actions.
 */
@Composable
fun HabitCard(
    habit: Habit,
    todayStatus: DailyStatus?,
    onCardClick: () -> Unit,
    onMarkSuccess: () -> Unit,
    onMarkFailure: () -> Unit,
    onTemptedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.iconEmoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${habit.currentStreak} day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (habit.currentStreak > 0) Streak else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status indicator
                when (todayStatus) {
                    DailyStatus.SUCCESS -> {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = CalendarSuccess,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    DailyStatus.FAILURE -> {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Failed",
                            tint = CalendarFailure,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    else -> {
                        // Show action buttons
                        Row {
                            IconButton(onClick = onMarkSuccess) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Mark success",
                                    tint = CalendarSuccess
                                )
                            }
                            IconButton(onClick = onMarkFailure) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Mark failure",
                                    tint = CalendarFailure
                                )
                            }
                        }
                    }
                }
            }

            // Show "I'm Tempted" button for BREAK habits
            if (habit.type == HabitType.BREAK && todayStatus == null) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = onTemptedClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I'm Tempted")
                }
            }
        }
    }
}
