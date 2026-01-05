package com.habitarchitect.presentation.screen.breaktools

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CueEliminationUiState(
    val habitName: String = "",
    val cues: List<CueItem> = emptyList()
)

@HiltViewModel
class CueEliminationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle["habitId"] ?: ""

    private val _uiState = MutableStateFlow(CueEliminationUiState())
    val uiState: StateFlow<CueEliminationUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habit = habitRepository.getHabitByIdOnce(habitId)
            if (habit != null) {
                _uiState.value = _uiState.value.copy(
                    habitName = habit.name
                )
                // Load existing friction strategies as cues
                val existingCues = habit.frictionStrategies.map { strategy ->
                    CueItem(
                        id = UUID.randomUUID().toString(),
                        text = strategy,
                        isCompleted = habit.implementedFrictionStrategies.contains(strategy)
                    )
                }
                _uiState.value = _uiState.value.copy(cues = existingCues)
            }
        }
    }

    fun addCue(text: String) {
        val newCue = CueItem(
            id = UUID.randomUUID().toString(),
            text = text,
            isCompleted = false
        )
        _uiState.value = _uiState.value.copy(
            cues = _uiState.value.cues + newCue
        )
        saveCues()
    }

    fun toggleCue(id: String) {
        _uiState.value = _uiState.value.copy(
            cues = _uiState.value.cues.map { cue ->
                if (cue.id == id) cue.copy(isCompleted = !cue.isCompleted)
                else cue
            }
        )
        saveCues()
    }

    fun deleteCue(id: String) {
        _uiState.value = _uiState.value.copy(
            cues = _uiState.value.cues.filter { it.id != id }
        )
        saveCues()
    }

    private fun saveCues() {
        viewModelScope.launch {
            val strategies = _uiState.value.cues.map { it.text }
            val implemented = _uiState.value.cues.filter { it.isCompleted }.map { it.text }
            habitRepository.updateFrictionStrategies(habitId, strategies, implemented)
        }
    }
}
