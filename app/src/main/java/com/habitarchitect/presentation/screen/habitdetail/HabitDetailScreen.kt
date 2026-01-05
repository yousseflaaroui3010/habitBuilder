package com.habitarchitect.presentation.screen.habitdetail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.presentation.components.CalendarLegend
import com.habitarchitect.presentation.components.HabitCalendar
import com.habitarchitect.presentation.components.WeeklyPaperClipProgress

/**
 * Detailed view of a habit with calendar and stats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    onNavigateToResistanceList: () -> Unit,
    onNavigateToEditHabit: () -> Unit = {},
    onNavigateToCueElimination: () -> Unit = {},
    onNavigateToCostJournal: () -> Unit = {},
    onNavigateToTemptationBundle: () -> Unit = {},
    onNavigateToFrictionTracker: () -> Unit = {},
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    // Navigate back when habit is deleted
    LaunchedEffect(uiState.habitDeleted) {
        if (uiState.habitDeleted) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.habit?.name ?: "Habit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToResistanceList) {
                        Icon(Icons.Default.List, contentDescription = "View list")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Habit") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToEditHabit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (uiState.habit?.isSharedWithPartner == true)
                                            "Stop Sharing"
                                        else
                                            "Share with Partner"
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    viewModel.toggleShareWithPartner()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Archive Habit") },
                                onClick = {
                                    showMenu = false
                                    viewModel.archiveHabit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Archive, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Habit") },
                                onClick = {
                                    showMenu = false
                                    viewModel.showDeleteConfirmation()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
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
            uiState.habit?.let { habit ->
                // Emoji and name
                Text(
                    text = habit.iconEmoji,
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${habit.currentStreak} Day Streak",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Longest: ${habit.longestStreak} days | Total: ${habit.totalSuccessDays} successes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Calendar
                Text(
                    text = "Progress Calendar",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                HabitCalendar(
                    currentMonth = uiState.currentMonth,
                    dailyLogs = uiState.monthLogs,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onDayClick = { /* Could show day details */ }
                )

                CalendarLegend()

                Spacer(modifier = Modifier.height(16.dp))

                // Paper Clip Jar Progress
                WeeklyPaperClipProgress(
                    weeklySuccesses = uiState.weeklySuccessCount,
                    weeklyGoal = 7,
                    isBuildHabit = habit.type == HabitType.BUILD
                )

                Spacer(modifier = Modifier.height(24.dp))

                // View resistance/attraction list
                TextButton(
                    onClick = onNavigateToResistanceList,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (habit.type.name == "BREAK") "View Resistance List"
                        else "View Attraction List"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Atomic Habits Tools Section
                Text(
                    text = "Atomic Habits Tools",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (habit.type == HabitType.BREAK) {
                    // Break habit tools - inversions of the 4 laws
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ToolCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.VisibilityOff,
                            title = "Make It Invisible",
                            subtitle = "Cue Elimination",
                            color = Color(0xFFE57373),
                            onClick = onNavigateToCueElimination
                        )
                        ToolCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.ThumbDown,
                            title = "Make It Unattractive",
                            subtitle = "Cost Journal",
                            color = Color(0xFFE57373),
                            onClick = onNavigateToCostJournal
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ToolCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Block,
                            title = "Make It Difficult",
                            subtitle = "Friction Tracker",
                            color = Color(0xFFE57373),
                            onClick = onNavigateToFrictionTracker
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                } else {
                    // Build habit tools - the 4 laws
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ToolCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Link,
                            title = "Make It Attractive",
                            subtitle = "Temptation Bundle",
                            color = Color(0xFF4CAF50),
                            onClick = onNavigateToTemptationBundle
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text("Delete Habit?") },
            text = {
                Text("This will permanently delete \"${uiState.habit?.name}\" and all its history. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteHabit() }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteConfirmation() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
