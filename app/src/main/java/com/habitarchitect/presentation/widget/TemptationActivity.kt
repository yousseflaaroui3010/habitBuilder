package com.habitarchitect.presentation.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.ListItemRepository
import com.habitarchitect.presentation.screen.pause.PauseScreen
import com.habitarchitect.presentation.theme.GradientBlueDark
import com.habitarchitect.presentation.theme.GradientBlueLight
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    var currentIndex by remember { mutableIntStateOf(0) }
    var autoAdvance by remember { mutableStateOf(true) }

    // Load items when composable is first displayed
    LaunchedEffect(habitId, isBuildHabit) {
        viewModel.loadItems(habitId, isBuildHabit)
    }

    // Auto-advance flashcards every 4 seconds
    LaunchedEffect(items, currentIndex, autoAdvance) {
        if (items.isNotEmpty() && autoAdvance) {
            delay(4000)
            currentIndex = (currentIndex + 1) % items.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isBuildHabit) {
                        listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                    } else {
                        listOf(GradientBlueDark, GradientBlueLight)
                    }
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text = if (isBuildHabit) "💪 Why This Matters" else "🛑 Remember The Cost",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBuildHabit)
                    "Stay motivated. Here's why you started:"
                else
                    "Is it worth it? Think about what this costs you:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Flashcard section
            if (items.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBuildHabit)
                                "Add some reasons why this habit matters to you in the habit details."
                            else
                                "Add resistance items and costs in the habit details to remind yourself why you want to quit.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                // Flashcard with navigation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Flashcard
                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "flashcard"
                    ) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Card number
                                Text(
                                    text = "${index + 1} of ${items.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Big emoji based on type
                                Text(
                                    text = if (isBuildHabit) "✨" else "⚠️",
                                    fontSize = 48.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Content
                                Text(
                                    text = items[index].content,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }

                    // Navigation arrows
                    if (items.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    autoAdvance = false
                                    currentIndex = if (currentIndex > 0) currentIndex - 1 else items.size - 1
                                }
                            ) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    autoAdvance = false
                                    currentIndex = (currentIndex + 1) % items.size
                                }
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress dots
                if (items.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (index == currentIndex) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentIndex) Color.White
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Button(
                onClick = onStayStrong,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = if (isBuildHabit) Color(0xFF1B5E20) else GradientBlueDark
                )
            ) {
                Text(
                    text = if (isBuildHabit) "I'm Motivated! 💪" else "I'll Stay Strong 🛡️",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onFailed,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = if (isBuildHabit) "I Skipped Today" else "I Failed Today",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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
