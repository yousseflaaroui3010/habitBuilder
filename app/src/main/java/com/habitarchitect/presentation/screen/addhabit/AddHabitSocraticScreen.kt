package com.habitarchitect.presentation.screen.addhabit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.HabitType

/**
 * Intentions-based habit creation flow.
 * Format: "I will [habit] at [time] in [location]"
 */
@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddHabitSocraticScreen(
    habitType: String,
    onNavigateBack: () -> Unit,
    onHabitCreated: () -> Unit,
    viewModel: AddHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBuildHabit = uiState.habitType == HabitType.BUILD

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
                title = {
                    Text(
                        if (isBuildHabit) "Create Intention" else "Break Bad Habit"
                    )
                },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = (uiState.currentStep + 1).toFloat() / uiState.totalSteps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Step ${uiState.currentStep + 1} of ${uiState.totalSteps}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Different content based on habit type and step
            if (isBuildHabit) {
                BuildHabitContent(
                    uiState = uiState,
                    onUpdateAnswer = { viewModel.updateAnswer(it) },
                    onUpdateLocation = { viewModel.updateLocation(it) },
                    onUpdateGoal = { viewModel.updateGoal(it) },
                    onUpdateStartWith = { viewModel.updateStartWith(it) },
                    onUpdateStackAnchor = { viewModel.updateStackAnchor(it) }
                )
            } else {
                BreakHabitContent(
                    uiState = uiState,
                    onUpdateAnswer = { viewModel.updateAnswer(it) }
                )
            }

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
                enabled = viewModel.isCurrentStepValid()
            ) {
                Text(if (uiState.currentStep < uiState.totalSteps - 1) "Next" else "Create Intention")
            }

            // Skip button for optional steps (Goal/Start and Habit Stacking)
            if (uiState.currentStep in 2..3 && isBuildHabit) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BuildHabitContent(
    uiState: AddHabitUiState,
    onUpdateAnswer: (String) -> Unit,
    onUpdateLocation: (String) -> Unit,
    onUpdateGoal: (String) -> Unit,
    onUpdateStartWith: (String) -> Unit,
    onUpdateStackAnchor: (String) -> Unit
) {
    // Steps: 0=Intention, 1=Location, 2=Goal/Start, 3=HabitStacking
    // Time/Days removed - set via reminder popup after creation or edit screen
    when (uiState.currentStep) {
        0 -> IntentionStep(
            habitAction = uiState.currentAnswer,
            onHabitActionChange = onUpdateAnswer
        )
        1 -> LocationStep(
            location = uiState.location,
            onLocationChange = onUpdateLocation
        )
        2 -> GoalAndStartStep(
            goal = uiState.goal,
            startWith = uiState.startWith,
            onGoalChange = onUpdateGoal,
            onStartWithChange = onUpdateStartWith
        )
        3 -> HabitStackingStep(
            stackAnchor = uiState.stackAnchor,
            habitAction = uiState.currentAnswer,
            onStackAnchorChange = onUpdateStackAnchor
        )
    }
}

@Composable
private fun BreakHabitContent(
    uiState: AddHabitUiState,
    onUpdateAnswer: (String) -> Unit
) {
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

    OutlinedTextField(
        value = uiState.currentAnswer,
        onValueChange = onUpdateAnswer,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Your answer") },
        minLines = 3
    )
}

@Composable
private fun IntentionStep(
    habitAction: String,
    onHabitActionChange: (String) -> Unit
) {
    // Intention header
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Create Your Intention",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"People who make a specific plan for when and where they will perform a new habit are more likely to follow through.\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // "I will" section
    Text(
        text = "I will...",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = habitAction,
        onValueChange = onHabitActionChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Meditate for 2 minutes") },
        label = { Text("Your habit") },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Examples
    Text(
        text = "Examples:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    val examples = listOf(
        "Read one page",
        "Do 5 push-ups",
        "Write for 10 minutes",
        "Practice a language for 5 mins"
    )
    examples.forEach { example ->
        ExampleChip(
            text = example,
            onClick = { onHabitActionChange(example) }
        )
    }
}

@Composable
private fun LocationStep(
    location: String,
    onLocationChange: (String) -> Unit
) {
    Text(
        text = "in...",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = location,
        onValueChange = onLocationChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("my living room") },
        label = { Text("Location") },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Preview of full intention
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Intention:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"I will [habit] at [time] in $location\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Location suggestions
    Text(
        text = "Common locations:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    val locations = listOf(
        "my bedroom",
        "my living room",
        "my office",
        "the kitchen",
        "the gym"
    )
    locations.forEach { loc ->
        ExampleChip(
            text = loc,
            onClick = { onLocationChange(loc) }
        )
    }
}

@Composable
private fun GoalAndStartStep(
    goal: String,
    startWith: String,
    onGoalChange: (String) -> Unit,
    onStartWithChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "The 2-Minute Rule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"A habit must be established before it can be improved.\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Goal section
    Text(
        text = "Goal:",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = goal,
        onValueChange = onGoalChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Read 30 pages before bed") },
        label = { Text("What's your ultimate goal?") },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Start with section
    Text(
        text = "Start with:",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Scale it down to 2 minutes or less",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = startWith,
        onValueChange = onStartWithChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Read one page") },
        label = { Text("2-minute version") },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Examples
    Text(
        text = "Examples of scaling down:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ScaleDownExample("Run 5K", "Put on running shoes")
        ScaleDownExample("Study for 3 hours", "Open your notes")
        ScaleDownExample("Meditate 20 mins", "Sit in meditation posture")
    }
}

@Composable
private fun HabitStackingStep(
    stackAnchor: String,
    habitAction: String,
    onStackAnchorChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Habit Stacking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"After [CURRENT HABIT], I will [NEW HABIT].\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "After...",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = stackAnchor,
        onValueChange = onStackAnchorChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("I pour my morning coffee") },
        label = { Text("Current habit or routine") },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Preview of habit stack
    AnimatedVisibility(
        visible = stackAnchor.isNotBlank(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Habit Stack:",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "After $stackAnchor, I will $habitAction",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Stack anchor suggestions
    Text(
        text = "Common anchors:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(8.dp))

    val anchors = listOf(
        "I pour my morning coffee",
        "I finish breakfast",
        "I get home from work",
        "I sit down for lunch",
        "I brush my teeth"
    )
    anchors.forEach { anchor ->
        ExampleChip(
            text = anchor,
            onClick = { onStackAnchorChange(anchor) }
        )
    }
}

@Composable
private fun ExampleChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Text(
            text = "• $text",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DayChip(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.take(1),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun ScaleDownExample(
    goal: String,
    startWith: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goal,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "→",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = startWith,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}
