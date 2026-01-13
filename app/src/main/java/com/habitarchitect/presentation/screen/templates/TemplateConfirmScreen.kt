package com.habitarchitect.presentation.screen.templates

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.HabitType

/**
 * One-tap template confirmation screen.
 * Shows template details and allows instant habit creation or customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateConfirmScreen(
    onNavigateBack: () -> Unit,
    onHabitCreated: () -> Unit,
    onCustomize: (type: String, templateId: String) -> Unit,
    viewModel: TemplateConfirmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.habitCreated) {
        if (uiState.habitCreated) {
            onHabitCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Habit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        uiState.template?.let { template ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big emoji
                Text(
                    text = template.iconEmoji,
                    fontSize = 72.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Template name
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = template.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // What's included card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What's Included",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (template.type == HabitType.BUILD) {
                            template.defaultMinimumVersion?.let {
                                IncludedItem("2-Minute Version", it)
                            }
                            template.defaultStackAnchors.firstOrNull()?.let {
                                IncludedItem("Habit Stack", it)
                            }
                            IncludedItem(
                                "Attraction Items",
                                "${template.defaultAttractionItems.size} reasons to stay motivated"
                            )
                        } else {
                            IncludedItem(
                                "Resistance Items",
                                "${template.defaultResistanceItems.size} reasons to resist"
                            )
                            IncludedItem(
                                "Friction Strategies",
                                "${template.defaultFrictionStrategies.size} ways to make it harder"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons
                Button(
                    onClick = { viewModel.createHabitFromTemplate() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isCreating
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(if (uiState.isCreating) "Creating..." else "Add Habit")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onCustomize(template.type.name, template.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Customize First")
                }
            }
        } ?: run {
            // Loading or not found
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Loading template...")
            }
        }

        // Show reminder dialog for BUILD habits
        if (uiState.showReminderDialog) {
            ReminderSetupDialog(
                initialHour = uiState.reminderHour,
                initialMinute = uiState.reminderMinute,
                selectedDays = uiState.selectedDays,
                onTimeChange = { hour, minute -> viewModel.updateReminderTime(hour, minute) },
                onDayToggle = { viewModel.toggleDay(it) },
                onConfirm = { viewModel.proceedWithHabitCreation() },
                onDismiss = { viewModel.hideReminderDialog() },
                onSkip = {
                    viewModel.clearReminderTime()
                    viewModel.proceedWithHabitCreation()
                }
            )
        }
    }
}

@Composable
private fun IncludedItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1
        )
    }
}

/**
 * Improved reminder setup dialog with Material 3 TimePicker and accessible day selection.
 * Uses proper AA-compliant contrast ratios.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderSetupDialog(
    initialHour: Int,
    initialMinute: Int,
    selectedDays: List<Int>,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    onDayToggle: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var hasTimeBeenSet by remember { mutableStateOf(false) }

    // Format time for display
    val formattedTime = remember(selectedHour, selectedMinute) {
        val amPm = if (selectedHour < 12) "AM" else "PM"
        val displayHour = when {
            selectedHour == 0 -> 12
            selectedHour > 12 -> selectedHour - 12
            else -> selectedHour
        }
        String.format("%d:%02d %s", displayHour, selectedMinute, amPm)
    }

    // Time picker dialog
    if (showTimePicker) {
        TimePickerDialogContent(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            onConfirm = { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                hasTimeBeenSet = true
                onTimeChange(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Set Reminder",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "When do you want to be reminded?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Time selector button
                Surface(
                    onClick = { showTimePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (hasTimeBeenSet) formattedTime else "Select Time",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (hasTimeBeenSet)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Days section
                Text(
                    text = "Which days?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Day selection - circular buttons
                DaySelector(
                    selectedDays = selectedDays,
                    onDayToggle = onDayToggle
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Skip")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = hasTimeBeenSet && selectedDays.isNotEmpty()
                    ) {
                        Text("Set Reminder")
                    }
                }
            }
        }
    }
}

/**
 * Day selector with circular toggle buttons for better UX.
 * AA-compliant contrast for both selected and unselected states.
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
 * Individual day toggle button with proper accessibility.
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
private fun TimePickerDialogContent(
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
