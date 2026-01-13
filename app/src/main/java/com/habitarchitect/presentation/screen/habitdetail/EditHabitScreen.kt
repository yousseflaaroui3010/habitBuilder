package com.habitarchitect.presentation.screen.habitdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.model.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        TimePickerDialog(
            initialHour = uiState.reminderHour,
            initialMinute = uiState.reminderMinute,
            onConfirm = { hour, minute ->
                viewModel.updateReminderTime(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Habit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveHabit() }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Habit Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.iconEmoji,
                onValueChange = { viewModel.updateIcon(it) },
                label = { Text("Icon Emoji") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Enter an emoji to represent this habit") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Priority selector
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { priority ->
                    val isSelected = uiState.priority == priority
                    val backgroundColor = when {
                        isSelected && priority == Priority.HIGH -> Color(0xFFFF5252)
                        isSelected && priority == Priority.MEDIUM -> Color(0xFFFFA726)
                        isSelected && priority == Priority.LOW -> Color(0xFF66BB6A)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .clickable { viewModel.updatePriority(priority) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = priority.name,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.habit?.type == HabitType.BUILD) {
                OutlinedTextField(
                    value = uiState.minimumVersion,
                    onValueChange = { viewModel.updateMinimumVersion(it) },
                    label = { Text("Minimum Version (2-minute rule)") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("The smallest version of this habit you can do") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.stackAnchor,
                    onValueChange = { viewModel.updateStackAnchor(it) },
                    label = { Text("Habit Stack Anchor") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("After I ___, I will do this habit") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.reward,
                    onValueChange = { viewModel.updateReward(it) },
                    label = { Text("Reward") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("How you'll reward yourself after") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Reminder section
                Text(
                    text = "Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Time selector button
                TimeSelector(
                    hour = uiState.reminderHour,
                    minute = uiState.reminderMinute,
                    hasTimeSet = uiState.hasReminderSet,
                    onClick = { showTimePicker = true },
                    onClear = { viewModel.clearReminder() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Day selection
                Text(
                    text = "Reminder Days",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DaySelector(
                    selectedDays = uiState.selectedDays,
                    onDayToggle = { viewModel.toggleDay(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "To edit your ${if (uiState.habit?.type == HabitType.BREAK) "resistance" else "attraction"} list, go back and tap the list icon.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Time selector button that opens TimePicker dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSelector(
    hour: Int,
    minute: Int,
    hasTimeSet: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit
) {
    val formattedTime = remember(hour, minute) {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        String.format("%d:%02d %s", displayHour, minute, amPm)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (hasTimeSet) formattedTime else "Select Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (hasTimeSet)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (hasTimeSet) {
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onClear) {
                Text("Clear")
            }
        }
    }
}

/**
 * Day selector with circular toggle buttons.
 */
@Composable
private fun DaySelector(
    selectedDays: List<Int>,
    onDayToggle: (Int) -> Unit
) {
    val days = listOf(
        1 to "S",   // Sunday
        2 to "M",   // Monday
        3 to "T",   // Tuesday
        4 to "W",   // Wednesday
        5 to "T",   // Thursday
        6 to "F",   // Friday
        7 to "S"    // Saturday
    )

    val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, (dayNum, label) ->
            val isSelected = selectedDays.contains(dayNum)
            DayToggleButton(
                label = label,
                contentDescription = dayNames[index],
                isSelected = isSelected,
                onClick = { onDayToggle(dayNum) }
            )
        }
    }
}

/**
 * Individual day toggle button.
 */
@Composable
private fun DayToggleButton(
    label: String,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Checkbox
                this.contentDescription = "$contentDescription ${if (isSelected) "selected" else "not selected"}"
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

/**
 * Time picker dialog using Material 3 TimePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectorColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
