package com.habitarchitect.presentation.screen.bundle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TemptationBundleUiState(
    val habitName: String = "",
    val bundleReward: String = "",
    val enforceBundle: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class TemptationBundleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle["habitId"] ?: ""

    private val _uiState = MutableStateFlow(TemptationBundleUiState())
    val uiState: StateFlow<TemptationBundleUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habit = habitRepository.getHabitByIdOnce(habitId)
            if (habit != null) {
                _uiState.value = _uiState.value.copy(
                    habitName = habit.name,
                    bundleReward = habit.reward ?: ""
                )
            }
        }
    }

    fun saveBundle(habitId: String, needToDo: String, wantToDo: String, enforce: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            // Update the habit with the bundle reward
            habitRepository.updateHabitReward(habitId, wantToDo)

            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }
}
