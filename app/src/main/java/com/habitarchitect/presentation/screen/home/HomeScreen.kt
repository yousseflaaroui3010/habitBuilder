package com.habitarchitect.presentation.screen.home

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.presentation.components.HabitCard
import com.habitarchitect.presentation.components.MilestoneCelebration
import com.habitarchitect.presentation.components.StreakBreakAnimation
import com.habitarchitect.presentation.widget.TemptationActivity
import kotlinx.coroutines.flow.collectLatest

/**
 * Home screen showing list of user's habits.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToHabitDetail: (String) -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditHabit: (String) -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showCelebration by remember { mutableStateOf(false) }
    var celebrationStreak by remember { mutableStateOf(0) }
    var showStreakBreak by remember { mutableStateOf(false) }
    var brokenStreak by remember { mutableStateOf(0) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    // Android 13+ notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled silently */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Handle events from ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeEvent.LaunchTemptationOverlay -> {
                    val intent = Intent(context, TemptationActivity::class.java).apply {
                        putExtra("habitId", event.habitId)
                    }
                    context.startActivity(intent)
                }
                is HomeEvent.ShowMilestoneCelebration -> {
                    celebrationStreak = event.streak
                    showCelebration = true
                }
                is HomeEvent.ShowStreakBreakAnimation -> {
                    brokenStreak = event.previousStreak
                    showStreakBreak = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Habits") },
                actions = {
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(Icons.Default.BarChart, contentDescription = "Dashboard")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddHabit) {
                Icon(Icons.Default.Add, contentDescription = "Add habit")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No habits yet",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start your journey by adding your first habit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is HomeUiState.Success -> {
                ReorderableHabitList(
                    habits = state.habits,
                    todayStatuses = state.todayStatuses,
                    onNavigateToHabitDetail = onNavigateToHabitDetail,
                    onMarkSuccess = { viewModel.markSuccess(it) },
                    onMarkFailure = { viewModel.markFailure(it) },
                    onTemptedClick = { viewModel.showTemptationOverlay(it) },
                    onDelete = { habit -> habitToDelete = habit },
                    onEdit = onNavigateToEditHabit,
                    onReorder = { newOrder -> viewModel.reorderHabits(newOrder) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }

    // Milestone celebration overlay
    if (showCelebration) {
        MilestoneCelebration(
            streak = celebrationStreak,
            onDismiss = { showCelebration = false }
        )
    }

    // Streak break animation overlay
    StreakBreakAnimation(
        previousStreak = brokenStreak,
        visible = showStreakBreak,
        onAnimationComplete = { showStreakBreak = false }
    )

    // Delete confirmation dialog
    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Delete Habit") },
            text = { Text("Are you sure you want to delete \"${habit.name}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHabit(habit.id)
                        habitToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableHabitCard(
    habit: Habit,
    todayStatus: com.habitarchitect.domain.model.DailyStatus?,
    onCardClick: () -> Unit,
    onMarkSuccess: () -> Unit,
    onMarkFailure: () -> Unit,
    onTemptedClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> {
                    onDelete()
                    false // Don't actually dismiss, show dialog first
                }
                DismissValue.DismissedToEnd -> {
                    onEdit()
                    false // Don't dismiss, just trigger edit
                }
                DismissValue.Default -> true
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToStart -> Color(0xFFFF5252) // Red for delete
                    DismissValue.DismissedToEnd -> Color(0xFF4CAF50) // Green for edit
                    else -> Color.Transparent
                },
                label = "swipe_color"
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Edit
                DismissDirection.EndToStart -> Icons.Default.Delete
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            HabitCard(
                habit = habit,
                todayStatus = todayStatus,
                onCardClick = onCardClick,
                onMarkSuccess = onMarkSuccess,
                onMarkFailure = onMarkFailure,
                onTemptedClick = onTemptedClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReorderableHabitList(
    habits: List<Habit>,
    todayStatuses: Map<String, com.habitarchitect.domain.model.DailyStatus>,
    onNavigateToHabitDetail: (String) -> Unit,
    onMarkSuccess: (String) -> Unit,
    onMarkFailure: (String) -> Unit,
    onTemptedClick: (String) -> Unit,
    onDelete: (Habit) -> Unit,
    onEdit: (String) -> Unit,
    onReorder: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var draggedItemIndex by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val orderedHabits = remember(habits) { habits.toMutableStateList() }

    // Sync with external changes
    LaunchedEffect(habits) {
        if (draggedItemIndex == -1) {
            orderedHabits.clear()
            orderedHabits.addAll(habits)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(orderedHabits, key = { _, habit -> habit.id }) { index, habit ->
            val isDragging = index == draggedItemIndex
            val elevation by animateDpAsState(
                targetValue = if (isDragging) 8.dp else 0.dp,
                label = "drag_elevation"
            )

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .offset {
                        IntOffset(0, if (isDragging) dragOffset.roundToInt() else 0)
                    }
                    .shadow(elevation, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedItemIndex = index
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount.y

                                // Calculate new position
                                val itemHeight = 100 // Approximate height
                                val draggedOverIndex = (index + (dragOffset / itemHeight).roundToInt())
                                    .coerceIn(0, orderedHabits.lastIndex)

                                if (draggedOverIndex != index && draggedOverIndex in orderedHabits.indices) {
                                    val movedItem = orderedHabits.removeAt(draggedItemIndex)
                                    orderedHabits.add(draggedOverIndex, movedItem)
                                    draggedItemIndex = draggedOverIndex
                                    dragOffset = 0f
                                }
                            },
                            onDragEnd = {
                                // Save new order
                                onReorder(orderedHabits.map { it.id })
                                draggedItemIndex = -1
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                draggedItemIndex = -1
                                dragOffset = 0f
                            }
                        )
                    }
            ) {
                SwipeableHabitCard(
                    habit = habit,
                    todayStatus = todayStatuses[habit.id],
                    onCardClick = { onNavigateToHabitDetail(habit.id) },
                    onMarkSuccess = { onMarkSuccess(habit.id) },
                    onMarkFailure = { onMarkFailure(habit.id) },
                    onTemptedClick = { onTemptedClick(habit.id) },
                    onDelete = { onDelete(habit) },
                    onEdit = { onEdit(habit.id) }
                )
            }
        }
    }
}
