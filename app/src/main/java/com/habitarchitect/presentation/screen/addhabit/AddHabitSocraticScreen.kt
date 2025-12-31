package com.habitarchitect.presentation.screen.addhabit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Socratic flow for habit creation - guided questions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSocraticScreen(
    habitType: String,
    onNavigateBack: () -> Unit,
    onHabitCreated: () -> Unit,
    viewModel: AddHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Warning dialog for too many habits
    if (uiState.showTooManyHabitsWarning) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissWarning() },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Consider Focusing") },
            text = {
                Text(
                    "You currently have ${uiState.currentHabitCount} active habits. " +
                    "Research shows that tracking too many habits at once reduces success rates. " +
                    "Consider mastering your current habits before adding new ones.\n\n" +
                    "Are you sure you want to continue?"
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissWarning() }) {
                    Text("Continue Anyway")
                }
            },
            dismissButton = {
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step ${uiState.currentStep + 1} of ${uiState.totalSteps}") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep > 0) {
                            viewModel.previousStep()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = (uiState.currentStep + 1).toFloat() / uiState.totalSteps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question
            Text(
                text = uiState.currentQuestion,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.currentHint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Answer input
            OutlinedTextField(
                value = uiState.currentAnswer,
                onValueChange = { viewModel.updateAnswer(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your answer") },
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Navigation buttons
            Button(
                onClick = {
                    if (uiState.currentStep < uiState.totalSteps - 1) {
                        viewModel.nextStep()
                    } else {
                        viewModel.createHabit()
                        onHabitCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.currentAnswer.isNotBlank()
            ) {
                Text(if (uiState.currentStep < uiState.totalSteps - 1) "Next" else "Create Habit")
            }

            if (uiState.currentStep < uiState.totalSteps - 1) {
                TextButton(
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip")
                }
            }
        }
    }
}
