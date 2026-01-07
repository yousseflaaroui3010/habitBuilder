package com.habitarchitect.presentation.screen.breaktools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

enum class CostCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val emoji: String
) {
    TIME("Time", Icons.Default.Schedule, Color(0xFF2196F3), "⏰"),
    MONEY("Money", Icons.Default.AttachMoney, Color(0xFF4CAF50), "💰"),
    HEALTH("Health", Icons.Default.Favorite, Color(0xFFE91E63), "❤️"),
    FOCUS("Focus", Icons.Default.Psychology, Color(0xFF9C27B0), "🧠"),
    RELATIONSHIPS("Family", Icons.Default.FamilyRestroom, Color(0xFFFF9800), "👨‍👩‍👧"),
    DISCIPLINE("Discipline", Icons.Default.ThumbDown, Color(0xFF795548), "💪"),
    WORK("Work", Icons.Default.Work, Color(0xFF607D8B), "💼"),
    OTHER("Other", Icons.Default.Edit, Color(0xFF9E9E9E), "✏️")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CostJournalScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    viewModel: CostJournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var showCustomInput by remember { mutableStateOf(false) }
    var newCostText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cost Visibility Journal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add cost")
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
                                Icons.Default.ThumbDown,
                                contentDescription = null,
                                tint = Color(0xFFE57373)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Make It Unattractive",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Track the true costs of your bad habit. Reframe your mindset by seeing the negative consequences clearly - time lost, money spent, health impacted, and more.",
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

            // Summary stats
            if (uiState.costs.isNotEmpty()) {
                item {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CostCategory.entries.forEach { category ->
                            val count = uiState.costs.count { it.category == category }
                            if (count > 0) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = category.color.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(category.emoji, fontSize = 20.sp)
                                        Text(
                                            count.toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = category.color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Your costs
            item {
                Text(
                    text = "Your Cost Journal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.costs.isEmpty()) {
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
                            Text(
                                text = "No costs recorded yet",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start tracking what this habit is truly costing you",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(uiState.costs) { cost ->
                CostEntryCard(
                    cost = cost,
                    onDelete = { viewModel.deleteCost(cost.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add cost bottom sheet
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddSheet = false
                showCustomInput = false
                newCostText = ""
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "What does this habit cost you?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap a category to add it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category buttons - FlowRow for proper wrapping
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CostCategory.entries.forEach { category ->
                        CategoryChip(
                            category = category,
                            isSelected = false,
                            onClick = {
                                if (category == CostCategory.OTHER) {
                                    showCustomInput = true
                                } else {
                                    // One-tap add for predefined categories
                                    viewModel.addCost(category, category.displayName)
                                    showAddSheet = false
                                }
                            }
                        )
                    }
                }

                // Custom input for "Other" category
                if (showCustomInput) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newCostText,
                        onValueChange = { newCostText = it },
                        label = { Text("Describe the cost") },
                        placeholder = { Text("e.g., Prayer time, Sleep quality, Work focus...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showCustomInput = false
                            newCostText = ""
                        }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                if (newCostText.isNotBlank()) {
                                    viewModel.addCost(CostCategory.OTHER, newCostText)
                                    showAddSheet = false
                                    showCustomInput = false
                                    newCostText = ""
                                }
                            },
                            enabled = newCostText.isNotBlank()
                        ) {
                            Text("Add")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChip(
    category: CostCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                category.color.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category.emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                category.displayName,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CostEntryCard(
    cost: CostEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cost.category.color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(cost.category.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cost.category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = cost.category.color,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = cost.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = cost.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

data class CostEntry(
    val id: String,
    val category: CostCategory,
    val description: String,
    val timestamp: String
)
