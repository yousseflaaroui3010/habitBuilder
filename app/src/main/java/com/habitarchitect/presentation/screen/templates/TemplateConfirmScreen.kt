package com.habitarchitect.presentation.screen.templates

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
                reminderTime = uiState.reminderTime,
                selectedDays = uiState.selectedDays,
                onTimeChange = { viewModel.updateReminderTime(it) },
                onDayToggle = { viewModel.toggleDay(it) },
                onConfirm = { viewModel.proceedWithHabitCreation() },
                onDismiss = { viewModel.hideReminderDialog() },
                onSkip = {
                    viewModel.updateReminderTime("")
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

@Composable
private fun ReminderSetupDialog(
    reminderTime: String,
    selectedDays: List<Int>,
    onTimeChange: (String) -> Unit,
    onDayToggle: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Set Reminder",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "When do you want to be reminded?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = onTimeChange,
                    label = { Text("Time (e.g., 8:00 AM)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Which days?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Day selection chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    days.forEachIndexed { index, day ->
                        val dayNum = index + 1 // 1=Sunday, 7=Saturday
                        FilterChip(
                            selected = selectedDays.contains(dayNum),
                            onClick = { onDayToggle(dayNum) },
                            label = { Text(day, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Skip")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        enabled = reminderTime.isNotBlank() && selectedDays.isNotEmpty()
                    ) {
                        Text("Set Reminder")
                    }
                }
            }
        }
    }
}
