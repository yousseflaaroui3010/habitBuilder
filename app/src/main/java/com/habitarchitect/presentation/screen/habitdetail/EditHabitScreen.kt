package com.habitarchitect.presentation.screen.habitdetail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
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
                        contentAlignment = androidx.compose.ui.Alignment.Center
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
