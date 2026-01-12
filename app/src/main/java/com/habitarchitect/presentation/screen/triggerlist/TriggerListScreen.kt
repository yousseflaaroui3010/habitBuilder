package com.habitarchitect.presentation.screen.triggerlist

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.TriggerLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen displaying trigger logs with solutions and effectiveness tracking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriggerListScreen(
    onNavigateBack: () -> Unit,
    viewModel: TriggerListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trigger Journal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Add trigger")
            }
        }
    ) { paddingValues ->
        if (uiState.triggerLogs.isEmpty() && !uiState.isLoading) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "No triggers logged yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Track what triggers your urges and test solutions to find what works for you.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(uiState.triggerLogs, key = { it.id }) { log ->
                    TriggerLogCard(
                        triggerLog = log,
                        onEdit = { viewModel.showEditDialog(log) },
                        onDelete = { viewModel.deleteTriggerLog(log.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Add trigger dialog
    if (uiState.showAddDialog) {
        AddTriggerDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onAdd = { trigger, solution, notes ->
                viewModel.addTriggerLog(trigger, solution, notes)
            }
        )
    }

    // Edit trigger dialog
    if (uiState.showEditDialog && uiState.editingTriggerLog != null) {
        EditTriggerDialog(
            triggerLog = uiState.editingTriggerLog!!,
            onDismiss = { viewModel.hideEditDialog() },
            onUpdate = { log, worked, alt, altWorked, notes ->
                viewModel.updateTriggerLog(log, worked, alt, altWorked, notes)
            }
        )
    }
}

@Composable
private fun TriggerLogCard(
    triggerLog: TriggerLog,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with trigger and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = triggerLog.trigger,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormat.format(Date(triggerLog.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Solution section
            if (!triggerLog.solution.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                SolutionRow(
                    label = "Solution",
                    solution = triggerLog.solution,
                    worked = triggerLog.solutionWorked
                )
            }

            // Alternative solution section
            if (!triggerLog.alternativeSolution.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                SolutionRow(
                    label = "Alternative",
                    solution = triggerLog.alternativeSolution,
                    worked = triggerLog.alternativeWorked
                )
            }

            // Notes
            if (!triggerLog.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = triggerLog.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SolutionRow(
    label: String,
    solution: String,
    worked: Boolean?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = solution,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        EffectivenessIndicator(worked = worked)
    }
}

@Composable
private fun EffectivenessIndicator(worked: Boolean?) {
    val (icon, color, label) = when (worked) {
        true -> Triple(Icons.Default.Check, MaterialTheme.colorScheme.secondary, "Worked")
        false -> Triple(Icons.Default.Close, MaterialTheme.colorScheme.error, "Didn't work")
        null -> Triple(Icons.Default.QuestionMark, Color.Gray, "Unknown")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun AddTriggerDialog(
    onDismiss: () -> Unit,
    onAdd: (trigger: String, solution: String?, notes: String?) -> Unit
) {
    var trigger by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log a Trigger") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text("What triggered the urge?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = solution,
                    onValueChange = { solution = it },
                    label = { Text("Solution to try (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(trigger, solution, notes) },
                enabled = trigger.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTriggerDialog(
    triggerLog: TriggerLog,
    onDismiss: () -> Unit,
    onUpdate: (
        log: TriggerLog,
        solutionWorked: Boolean?,
        alternativeSolution: String?,
        alternativeWorked: Boolean?,
        notes: String?
    ) -> Unit
) {
    var solutionWorked by remember { mutableStateOf(triggerLog.solutionWorked) }
    var alternativeSolution by remember { mutableStateOf(triggerLog.alternativeSolution ?: "") }
    var alternativeWorked by remember { mutableStateOf(triggerLog.alternativeWorked) }
    var notes by remember { mutableStateOf(triggerLog.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Trigger Log") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show the trigger
                Text(
                    text = "Trigger: ${triggerLog.trigger}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Solution effectiveness
                if (!triggerLog.solution.isNullOrBlank()) {
                    Column {
                        Text(
                            text = "Did \"${triggerLog.solution}\" work?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        EffectivenessChips(
                            selected = solutionWorked,
                            onSelect = { solutionWorked = it }
                        )
                    }
                }

                // Alternative solution
                OutlinedTextField(
                    value = alternativeSolution,
                    onValueChange = { alternativeSolution = it },
                    label = { Text("Alternative solution tried") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Alternative effectiveness
                if (alternativeSolution.isNotBlank()) {
                    Column {
                        Text(
                            text = "Did the alternative work?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        EffectivenessChips(
                            selected = alternativeWorked,
                            onSelect = { alternativeWorked = it }
                        )
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdate(
                        triggerLog,
                        solutionWorked,
                        alternativeSolution.takeIf { it.isNotBlank() },
                        if (alternativeSolution.isNotBlank()) alternativeWorked else null,
                        notes.takeIf { it.isNotBlank() }
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EffectivenessChips(
    selected: Boolean?,
    onSelect: (Boolean?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == true,
            onClick = { onSelect(true) },
            label = { Text("Yes") },
            leadingIcon = if (selected == true) {
                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        FilterChip(
            selected = selected == false,
            onClick = { onSelect(false) },
            label = { Text("No") },
            leadingIcon = if (selected == false) {
                { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.errorContainer
            )
        )
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text("?") },
            leadingIcon = if (selected == null) {
                { Icon(Icons.Default.QuestionMark, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null
        )
    }
}
