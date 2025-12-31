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
import java.util.UUID
import javax.inject.Inject

data class AddHabitUiState(
    val habitType: HabitType = HabitType.BUILD,
    val currentStep: Int = 0,
    val totalSteps: Int = 6,
    val currentQuestion: String = "",
    val currentHint: String = "",
    val currentAnswer: String = "",
    val answers: List<String> = List(6) { "" },
    val isCreating: Boolean = false,
    val showTooManyHabitsWarning: Boolean = false,
    val currentHabitCount: Int = 0
)

private val buildQuestions = listOf(
    "What habit do you want to build?" to "e.g., Exercise daily, Read before bed",
    "When would this habit fit best?" to "Think about your daily routine",
    "What existing routine could trigger this?" to "e.g., After I pour my morning coffee",
    "What makes this habit attractive to you?" to "List the benefits that motivate you",
    "What's the absolute minimum version?" to "e.g., Put on workout clothes, Read one page",
    "How will you reward yourself?" to "Small immediate reward after completing"
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
 * ViewModel for add habit Socratic flow.
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

    private val questions = if (habitType == HabitType.BUILD) buildQuestions else breakQuestions

    private val _uiState = MutableStateFlow(
        AddHabitUiState(
            habitType = habitType,
            currentQuestion = questions[0].first,
            currentHint = questions[0].second
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

    fun updateAnswer(answer: String) {
        val currentAnswers = _uiState.value.answers.toMutableList()
        currentAnswers[_uiState.value.currentStep] = answer
        _uiState.value = _uiState.value.copy(
            currentAnswer = answer,
            answers = currentAnswers
        )
    }

    fun nextStep() {
        val nextStep = _uiState.value.currentStep + 1
        if (nextStep < questions.size) {
            _uiState.value = _uiState.value.copy(
                currentStep = nextStep,
                currentQuestion = questions[nextStep].first,
                currentHint = questions[nextStep].second,
                currentAnswer = _uiState.value.answers[nextStep]
            )
        }
    }

    fun previousStep() {
        val prevStep = _uiState.value.currentStep - 1
        if (prevStep >= 0) {
            _uiState.value = _uiState.value.copy(
                currentStep = prevStep,
                currentQuestion = questions[prevStep].first,
                currentHint = questions[prevStep].second,
                currentAnswer = _uiState.value.answers[prevStep]
            )
        }
    }

    fun createHabit() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val answers = _uiState.value.answers

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true)

            val habitId = UUID.randomUUID().toString()
            val habit = Habit(
                id = habitId,
                userId = userId,
                name = answers[0],
                type = habitType,
                triggerContext = answers[1],
                stackAnchor = if (habitType == HabitType.BUILD) answers[2] else null,
                minimumVersion = if (habitType == HabitType.BUILD) answers[4] else null,
                reward = if (habitType == HabitType.BUILD) answers[5] else null,
                iconEmoji = if (habitType == HabitType.BUILD) "✓" else "🚫"
            )

            habitRepository.createHabit(habit)

            // Create list items from step 4 answer (costs/attractions)
            val listType = if (habitType == HabitType.BREAK) ListItemType.RESISTANCE else ListItemType.ATTRACTION
            val items = answers[3].split("\n")
                .filter { it.isNotBlank() }
                .mapIndexed { index, content ->
                    ListItem(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        type = listType,
                        content = content.trim(),
                        orderIndex = index
                    )
                }

            if (items.isNotEmpty()) {
                listItemRepository.addListItems(items)
            }

            _uiState.value = _uiState.value.copy(isCreating = false)
        }
    }
}
