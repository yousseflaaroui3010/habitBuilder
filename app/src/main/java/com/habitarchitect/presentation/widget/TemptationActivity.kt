package com.habitarchitect.presentation.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.ListItemRepository
import com.habitarchitect.presentation.screen.pause.PauseScreen
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Overlay activity showing resistance list when user is tempted.
 * For break habits, shows a PAUSE screen first.
 */
@AndroidEntryPoint
class TemptationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val habitId = intent.getStringExtra("habitId") ?: ""
        val habitName = intent.getStringExtra("habitName") ?: "this habit"
        val isBuildHabit = intent.getBooleanExtra("isBuildHabit", false)

        setContent {
            HabitArchitectTheme {
                TemptationFlow(
                    habitId = habitId,
                    habitName = habitName,
                    isBuildHabit = isBuildHabit,
                    onComplete = { finish() }
                )
            }
        }
    }
}

@Composable
fun TemptationFlow(
    habitId: String,
    habitName: String,
    isBuildHabit: Boolean,
    onComplete: () -> Unit
) {
    var showPauseScreen by remember { mutableStateOf(!isBuildHabit) }
    var pauseCompleted by remember { mutableStateOf(false) }

    if (showPauseScreen && !pauseCompleted) {
        // Show PAUSE screen for break habits
        PauseScreen(
            habitName = habitName,
            onComplete = {
                pauseCompleted = true
                showPauseScreen = false
            },
            onStayStrong = {
                onComplete()
            },
            pauseDuration = 60
        )
    } else {
        // Show temptation overlay
        TemptationOverlay(
            habitId = habitId,
            isBuildHabit = isBuildHabit,
            onStayStrong = onComplete,
            onFailed = onComplete
        )
    }
}

@Composable
fun TemptationOverlay(
    habitId: String,
    isBuildHabit: Boolean = false,
    onStayStrong: () -> Unit,
    onFailed: () -> Unit,
    viewModel: TemptationViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()

    // Load items when composable is first displayed
    androidx.compose.runtime.LaunchedEffect(habitId, isBuildHabit) {
        viewModel.loadItems(habitId, isBuildHabit)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isBuildHabit) "Why This Matters" else "Remember Why You Started",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBuildHabit)
                    "Here's why you want to build this habit:"
                else
                    "Here's why you want to break this habit:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (items.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBuildHabit)
                            "No attraction items yet.\nAdd reasons why this habit matters to you."
                        else
                            "No resistance items yet.\nAdd reasons why you want to quit.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(items) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${index + 1}. ${item.content}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStayStrong,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isBuildHabit) "I'm Motivated!" else "I'll Stay Strong")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onFailed,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(if (isBuildHabit) "I Skipped Today" else "I Failed Today")
            }
        }
    }
}

@HiltViewModel
class TemptationViewModel @Inject constructor(
    private val listItemRepository: ListItemRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items.asStateFlow()

    fun loadItems(habitId: String, isBuildHabit: Boolean) {
        viewModelScope.launch {
            val itemType = if (isBuildHabit) ListItemType.ATTRACTION else ListItemType.RESISTANCE
            listItemRepository.getListItemsByType(habitId, itemType).collect {
                _items.value = it
            }
        }
    }
}
