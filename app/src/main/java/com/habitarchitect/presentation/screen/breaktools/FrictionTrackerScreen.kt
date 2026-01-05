package com.habitarchitect.presentation.screen.breaktools

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val frictionSuggestions = mapOf(
    "phone" to listOf(
        "Delete social media apps",
        "Enable grayscale mode",
        "Set app time limits",
        "Move phone charger to another room",
        "Use a physical alarm clock instead",
        "Turn off all notifications",
        "Use website blockers"
    ),
    "smoking" to listOf(
        "Don't carry cigarettes with you",
        "Leave wallet at home when going for walks",
        "Remove all lighters from home",
        "Avoid stores that sell cigarettes",
        "Tell friends not to offer you cigarettes"
    ),
    "snacking" to listOf(
        "Don't buy junk food at all",
        "Keep snacks in hard-to-reach places",
        "Use smaller plates and bowls",
        "Pre-portion snacks into small bags",
        "Keep healthy snacks visible instead"
    ),
    "default" to listOf(
        "Add a 10-second delay before the action",
        "Put physical barriers in place",
        "Remove easy access to triggers",
        "Tell someone about your commitment",
        "Create a commitment contract",
        "Use blocking apps or tools",
        "Change your environment"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrictionTrackerScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    viewModel: FrictionTrackerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newBarrier by remember { mutableStateOf("") }
    var showAddField by remember { mutableStateOf(false) }

    val suggestions = remember(uiState.habitName) {
        val habitLower = uiState.habitName.lowercase()
        when {
            habitLower.contains("phone") || habitLower.contains("screen") || habitLower.contains("scroll") ||
                    habitLower.contains("social") || habitLower.contains("instagram") || habitLower.contains("tiktok") ->
                frictionSuggestions["phone"]!!
            habitLower.contains("smok") || habitLower.contains("cigarette") || habitLower.contains("vape") ->
                frictionSuggestions["smoking"]!!
            habitLower.contains("snack") || habitLower.contains("eat") || habitLower.contains("food") ||
                    habitLower.contains("junk") || habitLower.contains("sugar") ->
                frictionSuggestions["snacking"]!!
            else -> frictionSuggestions["default"]!!
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friction Tracker") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddField = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add barrier")
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
                                Icons.Default.Block,
                                contentDescription = null,
                                tint = Color(0xFFE57373)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Make It Difficult",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add friction between you and the bad habit. The more steps required to perform the habit, the less likely you are to do it.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Breaking: ${uiState.habitName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

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
                                value = newBarrier,
                                onValueChange = { newBarrier = it },
                                label = { Text("New friction/barrier") },
                                placeholder = { Text("e.g., Delete the app from my phone") },
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
                                        if (newBarrier.isNotBlank()) {
                                            viewModel.addBarrier(newBarrier)
                                            newBarrier = ""
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

            item {
                Text(
                    text = "Your Friction Barriers",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.barriers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No barriers added yet",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add friction to make the bad habit harder to do",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(uiState.barriers) { barrier ->
                BarrierItem(
                    text = barrier.text,
                    isImplemented = barrier.isImplemented,
                    onToggle = { viewModel.toggleBarrier(barrier.id) },
                    onDelete = { viewModel.deleteBarrier(barrier.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Suggested Barriers",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(suggestions.filter { suggestion ->
                uiState.barriers.none { it.text == suggestion }
            }) { suggestion ->
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
                        Icon(
                            Icons.Default.Block,
                            contentDescription = null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.addBarrier(suggestion) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add to list",
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
private fun BarrierItem(
    text: String,
    isImplemented: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isImplemented)
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
                checked = isImplemented,
                onCheckedChange = { onToggle() }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (isImplemented) TextDecoration.LineThrough else null,
                    color = if (isImplemented)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                if (isImplemented) {
                    Text(
                        text = "Barrier active",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
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

data class FrictionBarrier(
    val id: String,
    val text: String,
    val isImplemented: Boolean = false
)
