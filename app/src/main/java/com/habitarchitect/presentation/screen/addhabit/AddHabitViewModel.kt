package com.habitarchitect.presentation.screen.addhabit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.domain.repository.ListItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

data class AddHabitUiState(
    val habitType: HabitType = HabitType.BUILD,
    val currentStep: Int = 0,
    val totalSteps: Int = 5, // BUILD: 5 steps, BREAK: 6 steps

    // Current question/answer for BREAK habits (legacy flow)
    val currentQuestion: String = "",
    val currentHint: String = "",
    val currentAnswer: String = "",
    val answers: List<String> = List(6) { "" },

    // Intentions-based fields for BUILD habits
    val selectedTime: String = "",
    val selectedDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7), // All days selected by default
    val location: String = "",
    val goal: String = "",
    val startWith: String = "",
    val stackAnchor: String = "",

    // Status
    val isCreating: Boolean = false,
    val showTooManyHabitsWarning: Boolean = false,
    val currentHabitCount: Int = 0
)

private val breakQuestions = listOf(
    "What habit do you want to break?" to "Be specific about the behavior",
    "When does this usually happen?" to "Time of day, situation",
    "What triggers the urge?" to "Emotions, environments, people",
    "What does this habit cost you?" to "Be honest about the real consequences",
    "What friction can you add?" to "Make it harder to do",
    "Who could hold you accountable?" to "Optional: Share with a trusted person"
)

/**
 * ViewModel for intentions-based habit creation.
 */
@HiltViewModel
class AddHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val listItemRepository: ListItemRepository
) : ViewModel() {

    private val habitTypeString: String = savedStateHandle["type"] ?: "BUILD"
    private val habitType = HabitType.valueOf(habitTypeString)

    private val _uiState = MutableStateFlow(
        AddHabitUiState(
            habitType = habitType,
            totalSteps = if (habitType == HabitType.BUILD) 5 else 6,
            currentQuestion = if (habitType == HabitType.BREAK) breakQuestions[0].first else "",
            currentHint = if (habitType == HabitType.BREAK) breakQuestions[0].second else ""
        )
    )
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    init {
        checkHabitCount()
    }

    private fun checkHabitCount() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val count = habitRepository.getActiveHabitCount(userId)
            _uiState.value = _uiState.value.copy(
                currentHabitCount = count,
                showTooManyHabitsWarning = count >= 5
            )
        }
    }

    fun dismissWarning() {
        _uiState.value = _uiState.value.copy(showTooManyHabitsWarning = false)
    }

    // For BUILD habits - Intentions-based updates
    fun updateAnswer(answer: String) {
        val currentAnswers = _uiState.value.answers.toMutableList()
        currentAnswers[_uiState.value.currentStep] = answer
        _uiState.value = _uiState.value.copy(
            currentAnswer = answer,
            answers = currentAnswers
        )
    }

    fun updateTime(time: String) {
        _uiState.value = _uiState.value.copy(selectedTime = time)
    }

    fun toggleDay(dayNumber: Int) {
        val currentDays = _uiState.value.selectedDays.toMutableList()
        if (currentDays.contains(dayNumber)) {
            currentDays.remove(dayNumber)
        } else {
            currentDays.add(dayNumber)
        }
        _uiState.value = _uiState.value.copy(selectedDays = currentDays.sorted())
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateGoal(goal: String) {
        _uiState.value = _uiState.value.copy(goal = goal)
    }

    fun updateStartWith(startWith: String) {
        _uiState.value = _uiState.value.copy(startWith = startWith)
    }

    fun updateStackAnchor(stackAnchor: String) {
        _uiState.value = _uiState.value.copy(stackAnchor = stackAnchor)
    }

    fun isCurrentStepValid(): Boolean {
        val state = _uiState.value
        return when {
            state.habitType == HabitType.BUILD -> {
                when (state.currentStep) {
                    0 -> state.currentAnswer.isNotBlank() // Habit action
                    1 -> state.selectedDays.isNotEmpty() // At least one day selected
                    2 -> state.location.isNotBlank() // Location
                    3 -> true // Goal/Start with are optional
                    4 -> true // Habit stacking is optional
                    else -> true
                }
            }
            else -> state.currentAnswer.isNotBlank() // BREAK habits use simple answer validation
        }
    }

    fun nextStep() {
        val state = _uiState.value
        val nextStep = state.currentStep + 1

        if (state.habitType == HabitType.BREAK && nextStep < breakQuestions.size) {
            _uiState.value = state.copy(
                currentStep = nextStep,
                currentQuestion = breakQuestions[nextStep].first,
                currentHint = breakQuestions[nextStep].second,
                currentAnswer = state.answers[nextStep]
            )
        } else if (nextStep < state.totalSteps) {
            _uiState.value = state.copy(currentStep = nextStep)
        }
    }

    fun previousStep() {
        val state = _uiState.value
        val prevStep = state.currentStep - 1

        if (prevStep >= 0) {
            if (state.habitType == HabitType.BREAK) {
                _uiState.value = state.copy(
                    currentStep = prevStep,
                    currentQuestion = breakQuestions[prevStep].first,
                    currentHint = breakQuestions[prevStep].second,
                    currentAnswer = state.answers[prevStep]
                )
            } else {
                _uiState.value = state.copy(currentStep = prevStep)
            }
        }
    }

    fun createHabit() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(isCreating = true)

            val habitId = UUID.randomUUID().toString()

            if (state.habitType == HabitType.BUILD) {
                // Parse time if provided
                val triggerTime = try {
                    if (state.selectedTime.isNotBlank()) {
                        parseTimeString(state.selectedTime)
                    } else null
                } catch (e: Exception) {
                    null
                }

                val habit = Habit(
                    id = habitId,
                    userId = userId,
                    name = state.currentAnswer, // The habit action from step 0
                    type = HabitType.BUILD,
                    triggerTime = triggerTime,
                    triggerContext = state.selectedTime, // Store the raw time string as context
                    activeDays = state.selectedDays,
                    location = state.location,
                    goal = state.goal,
                    minimumVersion = state.startWith.ifBlank { state.currentAnswer },
                    stackAnchor = state.stackAnchor.ifBlank { null },
                    iconEmoji = "✓"
                )

                habitRepository.createHabit(habit)
            } else {
                // BREAK habit - use legacy flow
                val answers = state.answers
                val habit = Habit(
                    id = habitId,
                    userId = userId,
                    name = answers[0],
                    type = HabitType.BREAK,
                    triggerContext = answers[1],
                    iconEmoji = "🚫"
                )

                habitRepository.createHabit(habit)

                // Create list items from step 4 answer (costs)
                val items = answers[3].split("\n")
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, content ->
                        ListItem(
                            id = UUID.randomUUID().toString(),
                            habitId = habitId,
                            type = ListItemType.RESISTANCE,
                            content = content.trim(),
                            orderIndex = index
                        )
                    }

                if (items.isNotEmpty()) {
                    listItemRepository.addListItems(items)
                }
            }

            _uiState.value = _uiState.value.copy(isCreating = false)
        }
    }

    private fun parseTimeString(timeStr: String): LocalTime? {
        // Try to parse various time formats like "8:00 AM", "8am", "08:00", etc.
        val cleanedTime = timeStr.trim().uppercase()

        return try {
            when {
                cleanedTime.contains("AM") || cleanedTime.contains("PM") -> {
                    val isPM = cleanedTime.contains("PM")
                    val timePart = cleanedTime.replace("AM", "").replace("PM", "").trim()
                    val parts = timePart.split(":")
                    var hour = parts[0].toInt()
                    val minute = if (parts.size > 1) parts[1].toInt() else 0

                    if (isPM && hour != 12) hour += 12
                    if (!isPM && hour == 12) hour = 0

                    LocalTime.of(hour, minute)
                }
                cleanedTime.contains(":") -> {
                    val parts = cleanedTime.split(":")
                    LocalTime.of(parts[0].toInt(), parts[1].toInt())
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
