package com.habitarchitect.presentation.screen.home

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showCelebration by remember { mutableStateOf(false) }
    var celebrationStreak by remember { mutableStateOf(0) }
    var showStreakBreak by remember { mutableStateOf(false) }
    var brokenStreak by remember { mutableStateOf(0) }

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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.habits, key = { it.id }) { habit ->
                        HabitCard(
                            habit = habit,
                            todayStatus = state.todayStatuses[habit.id],
                            onCardClick = { onNavigateToHabitDetail(habit.id) },
                            onMarkSuccess = { viewModel.markSuccess(habit.id) },
                            onMarkFailure = { viewModel.markFailure(habit.id) },
                            onTemptedClick = { viewModel.showTemptationOverlay(habit.id) }
                        )
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
}
