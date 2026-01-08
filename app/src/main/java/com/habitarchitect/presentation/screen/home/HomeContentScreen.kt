package com.habitarchitect.presentation.screen.home

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import com.habitarchitect.data.preferences.ThemeMode
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.presentation.components.HabitCard
import com.habitarchitect.presentation.components.MilestoneCelebration
import com.habitarchitect.presentation.components.StreakBreakAnimation
import com.habitarchitect.presentation.components.TodaysFocusCard
import com.habitarchitect.presentation.components.TriggerDialog
import com.habitarchitect.presentation.widget.TemptationActivity
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun HomeContentScreen(
    onNavigateToHabitDetail: (String) -> Unit,
    onNavigateToAddHabit: () -> Unit, // Kept for API compatibility - FAB in MainScreen
    onNavigateToEditHabit: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showCelebration by remember { mutableStateOf(false) }
    var celebrationStreak by remember { mutableStateOf(0) }
    var showStreakBreak by remember { mutableStateOf(false) }
    var brokenStreak by remember { mutableStateOf(0) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }
    var showTriggerDialog by remember { mutableStateOf(false) }
    var triggerHabitId by remember { mutableStateOf("") }
    var triggerHabitName by remember { mutableStateOf("") }

    // Get user info for greeting and profile picture
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "there"
    val userPhotoUrl = currentUser?.photoUrl?.toString()

    // Determine greeting based on time
    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

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
                is HomeEvent.ShowTriggerDialog -> {
                    triggerHabitId = event.habitId
                    triggerHabitName = event.habitName
                    showTriggerDialog = true
                }
            }
        }
    }

    // Select logo based on theme (use app preference, not system)
    val themeMode by viewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val systemDark = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val logoRes = if (isDarkTheme) R.drawable.logo_dark else R.drawable.logo_light

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    // Logo on left, bigger
                    Image(
                        painter = painterResource(id = logoRes),
                        contentDescription = "Habit Architect Logo",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(48.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    // Theme toggle
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to light mode" else "Switch to dark mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Profile picture on right
                    IconButton(onClick = onNavigateToProfile) {
                        if (userPhotoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(userPhotoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            )
        }
        // FAB moved to MainScreen for consistent visibility across all tabs
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Greeting
                    GreetingHeader(greeting = greeting, userName = userName)

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier.fillMaxSize(),
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
            }
            is HomeUiState.Success -> {
                val buildHabits = state.habits.filter { it.type == HabitType.BUILD }
                val breakHabits = state.habits.filter { it.type == HabitType.BREAK }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Greeting Header
                    item {
                        GreetingHeader(greeting = greeting, userName = userName)
                    }

                    // Today's Focus Card
                    item {
                        TodaysFocusCard(
                            focusText = state.todaysFocus,
                            onFocusChanged = { viewModel.updateTodaysFocus(it) }
                        )
                    }

                    // Weekly progress message
                    item {
                        Text(
                            text = "Welcome to your habits and overall progress for your week",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // BUILD Habits Section
                    if (buildHabits.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Habits to Build",
                                emoji = "🌱",
                                color = Color(0xFF4CAF50)
                            )
                        }

                        itemsIndexed(buildHabits, key = { _, habit -> "build_${habit.id}" }) { index, habit ->
                            HabitCardWithWeeklyProgress(
                                habit = habit,
                                todayStatus = state.todayStatuses[habit.id],
                                weeklyStatus = state.weeklyStatuses[habit.id],
                                onCardClick = { onNavigateToHabitDetail(habit.id) },
                                onMarkSuccess = { viewModel.markSuccess(habit.id) },
                                onMarkFailure = { viewModel.markFailure(habit.id) },
                                onTemptedClick = { viewModel.showTemptationOverlay(habit.id) },
                                onDelete = { habitToDelete = habit },
                                onEdit = { onNavigateToEditHabit(habit.id) },
                                canMoveUp = index > 0,
                                canMoveDown = index < buildHabits.size - 1,
                                onMoveUp = {
                                    if (index > 0) {
                                        val ids = buildHabits.map { it.id }.toMutableList()
                                        ids.add(index - 1, ids.removeAt(index))
                                        viewModel.reorderHabits(ids)
                                    }
                                },
                                onMoveDown = {
                                    if (index < buildHabits.size - 1) {
                                        val ids = buildHabits.map { it.id }.toMutableList()
                                        ids.add(index + 1, ids.removeAt(index))
                                        viewModel.reorderHabits(ids)
                                    }
                                }
                            )
                        }
                    }

                    // BREAK Habits Section
                    if (breakHabits.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionHeader(
                                title = "Habits to Break",
                                emoji = "🔥",
                                color = Color(0xFFE57373)
                            )
                        }

                        itemsIndexed(breakHabits, key = { _, habit -> "break_${habit.id}" }) { index, habit ->
                            HabitCardWithWeeklyProgress(
                                habit = habit,
                                todayStatus = state.todayStatuses[habit.id],
                                weeklyStatus = state.weeklyStatuses[habit.id],
                                onCardClick = { onNavigateToHabitDetail(habit.id) },
                                onMarkSuccess = { viewModel.markSuccess(habit.id) },
                                onMarkFailure = { viewModel.markFailure(habit.id) },
                                onTemptedClick = { viewModel.showTemptationOverlay(habit.id) },
                                onDelete = { habitToDelete = habit },
                                onEdit = { onNavigateToEditHabit(habit.id) },
                                canMoveUp = index > 0,
                                canMoveDown = index < breakHabits.size - 1,
                                onMoveUp = {
                                    if (index > 0) {
                                        val ids = breakHabits.map { it.id }.toMutableList()
                                        ids.add(index - 1, ids.removeAt(index))
                                        viewModel.reorderHabits(ids)
                                    }
                                },
                                onMoveDown = {
                                    if (index < breakHabits.size - 1) {
                                        val ids = breakHabits.map { it.id }.toMutableList()
                                        ids.add(index + 1, ids.removeAt(index))
                                        viewModel.reorderHabits(ids)
                                    }
                                }
                            )
                        }
                    }
                }
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

    // Trigger dialog for BREAK habits
    if (showTriggerDialog) {
        TriggerDialog(
            habitName = triggerHabitName,
            onDismiss = { showTriggerDialog = false },
            onTriggerSelected = { trigger ->
                viewModel.saveTrigger(triggerHabitId, trigger)
                showTriggerDialog = false
            }
        )
    }

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

@Composable
private fun GreetingHeader(greeting: String, userName: String) {
    Column {
        Text(
            text = "$greeting,",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    emoji: String,
    color: Color,
    subtitle: String? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitCardWithWeeklyProgress(
    habit: Habit,
    todayStatus: DailyStatus?,
    weeklyStatus: WeeklyStatus?,
    onCardClick: () -> Unit,
    onMarkSuccess: () -> Unit,
    onMarkFailure: () -> Unit,
    onTemptedClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    canMoveUp: Boolean = false,
    canMoveDown: Boolean = false,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {}
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> {
                    onDelete()
                    false
                }
                DismissValue.DismissedToEnd -> {
                    onEdit()
                    false
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
                    DismissValue.DismissedToStart -> Color(0xFFFF5252)
                    DismissValue.DismissedToEnd -> Color(0xFF4CAF50)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Reorder buttons column
                        if (canMoveUp || canMoveDown) {
                            Column(
                                modifier = Modifier.padding(end = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = onMoveUp,
                                    enabled = canMoveUp,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Move up",
                                        tint = if (canMoveUp) MaterialTheme.colorScheme.primary
                                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                }
                                IconButton(
                                    onClick = onMoveDown,
                                    enabled = canMoveDown,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Move down",
                                        tint = if (canMoveDown) MaterialTheme.colorScheme.primary
                                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            HabitCard(
                                habit = habit,
                                todayStatus = todayStatus,
                                onCardClick = onCardClick,
                                onMarkSuccess = onMarkSuccess,
                                onMarkFailure = onMarkFailure,
                                onTemptedClick = onTemptedClick
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    WeeklyProgressRow(weeklyStatus = weeklyStatus)
                }
            }
        }
    )
}

@Composable
private fun WeeklyProgressRow(weeklyStatus: WeeklyStatus?) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    val todayDayOfWeek = java.time.LocalDate.now().dayOfWeek
    val todayIndex = when (todayDayOfWeek) {
        java.time.DayOfWeek.SUNDAY -> 0
        java.time.DayOfWeek.MONDAY -> 1
        java.time.DayOfWeek.TUESDAY -> 2
        java.time.DayOfWeek.WEDNESDAY -> 3
        java.time.DayOfWeek.THURSDAY -> 4
        java.time.DayOfWeek.FRIDAY -> 5
        java.time.DayOfWeek.SATURDAY -> 6
    }

    val statuses = listOf(
        weeklyStatus?.sunday,
        weeklyStatus?.monday,
        weeklyStatus?.tuesday,
        weeklyStatus?.wednesday,
        weeklyStatus?.thursday,
        weeklyStatus?.friday,
        weeklyStatus?.saturday
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, dayLabel ->
            val isToday = index == todayIndex
            val isPast = index < todayIndex
            val status = statuses[index]

            DayCircle(
                label = dayLabel,
                isToday = isToday,
                status = status,
                isPast = isPast
            )
        }
    }
}

@Composable
private fun DayCircle(
    label: String,
    isToday: Boolean,
    status: DailyStatus?,
    isPast: Boolean
) {
    val backgroundColor = when {
        status == DailyStatus.SUCCESS -> Color(0xFF4CAF50)
        status == DailyStatus.FAILURE -> Color(0xFFE57373)
        isToday -> MaterialTheme.colorScheme.primary
        isPast -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                DailyStatus.SUCCESS -> Text("✓", color = Color.White, fontSize = 12.sp)
                DailyStatus.FAILURE -> Text("✗", color = Color.White, fontSize = 12.sp)
                else -> {}
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
