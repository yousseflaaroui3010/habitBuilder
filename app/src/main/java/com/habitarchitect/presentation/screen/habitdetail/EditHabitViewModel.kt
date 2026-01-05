package com.habitarchitect.presentation.screen.habitdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.Priority
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditHabitUiState(
    val habit: Habit? = null,
    val name: String = "",
    val iconEmoji: String = "",
    val minimumVersion: String = "",
    val stackAnchor: String = "",
    val reward: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
)

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle["habitId"] ?: ""

    private val _uiState = MutableStateFlow(EditHabitUiState())
    val uiState: StateFlow<EditHabitUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habit = habitRepository.getHabitById(habitId).first()
            habit?.let {
                _uiState.value = EditHabitUiState(
                    habit = it,
                    name = it.name,
                    iconEmoji = it.iconEmoji,
                    minimumVersion = it.minimumVersion ?: "",
                    stackAnchor = it.stackAnchor ?: "",
                    reward = it.reward ?: "",
                    priority = it.priority,
                    isLoading = false
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateIcon(icon: String) {
        _uiState.value = _uiState.value.copy(iconEmoji = icon)
    }

    fun updateMinimumVersion(value: String) {
        _uiState.value = _uiState.value.copy(minimumVersion = value)
    }

    fun updateStackAnchor(value: String) {
        _uiState.value = _uiState.value.copy(stackAnchor = value)
    }

    fun updateReward(value: String) {
        _uiState.value = _uiState.value.copy(reward = value)
    }

    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun saveHabit() {
        val currentHabit = _uiState.value.habit ?: return
        val state = _uiState.value

        if (state.name.isBlank()) return

        viewModelScope.launch {
            val updatedHabit = currentHabit.copy(
                name = state.name.trim(),
                iconEmoji = state.iconEmoji.ifBlank { currentHabit.iconEmoji },
                minimumVersion = state.minimumVersion.ifBlank { null },
                stackAnchor = state.stackAnchor.ifBlank { null },
                reward = state.reward.ifBlank { null },
                priority = state.priority,
                updatedAt = System.currentTimeMillis()
            )
            habitRepository.updateHabit(updatedHabit)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
