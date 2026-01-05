package com.habitarchitect.presentation.screen.habitdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Screen for viewing and editing resistance/attraction list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResistanceListScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    viewModel: ResistanceListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val title = if (uiState.listType == "RESISTANCE") "Resistance List" else "Attraction List"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.items, key = { it.id }) { item ->
                val itemIndex = uiState.items.indexOf(item)
                val isFirst = itemIndex == 0
                val isLast = itemIndex == uiState.items.size - 1

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showEditDialog(item) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reorder buttons
                        Column {
                            IconButton(
                                onClick = { viewModel.moveItemUp(item.id) },
                                enabled = !isFirst
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move up",
                                    tint = if (isFirst)
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = { viewModel.moveItemDown(item.id) },
                                enabled = !isLast
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move down",
                                    tint = if (isLast)
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Text(
                            text = item.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        Row {
                            IconButton(onClick = { viewModel.showEditDialog(item) }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { viewModel.deleteItem(item.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showDialog) {
        val isEditing = uiState.editingItem != null
        AlertDialog(
            onDismissRequest = { viewModel.hideDialog() },
            title = {
                Text(if (isEditing) "Edit Item" else "Add Item")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.dialogText,
                        onValueChange = { viewModel.updateDialogText(it) },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.saveItem() },
                    enabled = uiState.dialogText.isNotBlank()
                ) {
                    Text(if (isEditing) "Save" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
