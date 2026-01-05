package com.habitarchitect.presentation.screen.breaktools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

// Common trigger suggestions for different habits
private val triggerSuggestions = mapOf(
    "smoking" to listOf(
        "Remove cigarettes from home",
        "Avoid the smoking area at work",
        "Unfollow smoking-related content online",
        "Remove lighters and ashtrays",
        "Avoid coffee breaks with smokers"
    ),
    "phone" to listOf(
        "Move phone charger outside bedroom",
        "Turn off non-essential notifications",
        "Delete social media apps",
        "Use grayscale mode",
        "Set screen time limits"
    ),
    "snacking" to listOf(
        "Don't buy junk food",
        "Keep healthy snacks visible",
        "Avoid the snack aisle",
        "Don't eat in front of TV",
        "Meal prep to avoid hunger"
    ),
    "default" to listOf(
        "Identify your trigger locations",
        "Remove visual cues from your environment",
        "Change your routine to avoid triggers",
        "Tell others about your goal",
        "Create physical barriers"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CueEliminationScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    viewModel: CueEliminationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newCue by remember { mutableStateOf("") }
    var showAddField by remember { mutableStateOf(false) }

    // Get suggestions based on habit name
    val suggestions = remember(uiState.habitName) {
        val habitLower = uiState.habitName.lowercase()
        when {
            habitLower.contains("smok") -> triggerSuggestions["smoking"]!!
            habitLower.contains("phone") || habitLower.contains("screen") || habitLower.contains("scroll") ->
                triggerSuggestions["phone"]!!
            habitLower.contains("snack") || habitLower.contains("eat") || habitLower.contains("food") ->
                triggerSuggestions["snacking"]!!
            else -> triggerSuggestions["default"]!!
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cue Elimination") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddField = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add cue")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE57373).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFFE57373)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Make It Invisible",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Remove cues from your environment. The most practical way to eliminate a bad habit is to reduce exposure to the cue that causes it.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Current habit
            item {
                Text(
                    text = "Breaking: ${uiState.habitName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Add new cue field
            if (showAddField) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = newCue,
                                onValueChange = { newCue = it },
                                label = { Text("New trigger to eliminate") },
                                placeholder = { Text("e.g., Remove phone from bedroom") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        if (newCue.isNotBlank()) {
                                            viewModel.addCue(newCue)
                                            newCue = ""
                                            showAddField = false
                                        }
                                    }
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }
            }

            // Your checklist
            item {
                Text(
                    text = "Your Elimination Checklist",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.cues.isEmpty()) {
                item {
                    Text(
                        text = "No items yet. Add triggers to eliminate from your environment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(uiState.cues) { cue ->
                CueChecklistItem(
                    text = cue.text,
                    isCompleted = cue.isCompleted,
                    onToggle = { viewModel.toggleCue(cue.id) },
                    onDelete = { viewModel.deleteCue(cue.id) }
                )
            }

            // Suggestions section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Suggested Actions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(suggestions) { suggestion ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.addCue(suggestion) }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add to checklist",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun CueChecklistItem(
    text: String,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                color = if (isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

data class CueItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)
