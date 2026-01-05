package com.habitarchitect.presentation.screen.addhabit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class QuickAddUiState(
    val habitType: HabitType = HabitType.BUILD,
    val name: String = "",
    val emoji: String = "✨",
    val isCreating: Boolean = false,
    val habitCreated: Boolean = false,
    val showEmojiPicker: Boolean = false,
    val triggerTime: LocalTime? = null,
    val triggerTimeFormatted: String? = null,
    val triggerContext: String? = null,
    val activeDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val minimumVersion: String? = null,
    val stackAnchor: String? = null
)

@HiltViewModel
class QuickAddHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val typeString: String = savedStateHandle["type"] ?: HabitType.BUILD.name
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    private val _uiState = MutableStateFlow(QuickAddUiState())
    val uiState: StateFlow<QuickAddUiState> = _uiState.asStateFlow()

    init {
        val type = try {
            HabitType.valueOf(typeString)
        } catch (e: IllegalArgumentException) {
            HabitType.BUILD
        }
        _uiState.value = _uiState.value.copy(
            habitType = type,
            emoji = if (type == HabitType.BUILD) "✨" else "🚫"
        )
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateEmoji(emoji: String) {
        _uiState.value = _uiState.value.copy(emoji = emoji, showEmojiPicker = false)
    }

    fun toggleEmojiPicker() {
        _uiState.value = _uiState.value.copy(showEmojiPicker = !_uiState.value.showEmojiPicker)
    }

    fun updateTriggerTime(hour: Int, minute: Int) {
        val time = LocalTime.of(hour, minute)
        _uiState.value = _uiState.value.copy(
            triggerTime = time,
            triggerTimeFormatted = time.format(timeFormatter)
        )
    }

    fun updateTriggerContext(context: String) {
        _uiState.value = _uiState.value.copy(
            triggerContext = context.ifBlank { null }
        )
    }

    fun updateActiveDays(days: List<Int>) {
        _uiState.value = _uiState.value.copy(activeDays = days)
    }

    fun updateMinimumVersion(version: String) {
        _uiState.value = _uiState.value.copy(
            minimumVersion = version.ifBlank { null }
        )
    }

    fun updateStackAnchor(anchor: String) {
        _uiState.value = _uiState.value.copy(
            stackAnchor = anchor.ifBlank { null }
        )
    }

    fun createHabit() {
        val state = _uiState.value
        if (state.name.isBlank()) return

        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true)

            val habit = Habit(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = state.name.trim(),
                type = state.habitType,
                iconEmoji = state.emoji,
                triggerTime = state.triggerTime,
                triggerContext = state.triggerContext,
                activeDays = state.activeDays,
                minimumVersion = state.minimumVersion,
                stackAnchor = state.stackAnchor
            )

            habitRepository.createHabit(habit)

            _uiState.value = _uiState.value.copy(
                isCreating = false,
                habitCreated = true
            )
        }
    }
}
