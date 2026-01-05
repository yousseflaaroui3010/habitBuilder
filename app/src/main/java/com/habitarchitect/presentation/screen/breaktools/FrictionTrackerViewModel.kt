package com.habitarchitect.presentation.screen.breaktools

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FrictionTrackerUiState(
    val habitName: String = "",
    val barriers: List<FrictionBarrier> = emptyList()
)

@HiltViewModel
class FrictionTrackerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val habitId: String = checkNotNull(savedStateHandle["habitId"])

    private val _uiState = MutableStateFlow(FrictionTrackerUiState())
    val uiState: StateFlow<FrictionTrackerUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
        loadBarriers()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            habitRepository.getHabitById(habitId).first()?.let { habit ->
                _uiState.value = _uiState.value.copy(habitName = habit.name)
            }
        }
    }

    private fun loadBarriers() {
        viewModelScope.launch {
            habitRepository.getHabitById(habitId).first()?.let { habit ->
                val barriers = habit.frictionStrategies.map { strategy ->
                    FrictionBarrier(
                        id = strategy.hashCode().toString(),
                        text = strategy,
                        isImplemented = habit.implementedFrictionStrategies.contains(strategy)
                    )
                }
                _uiState.value = _uiState.value.copy(barriers = barriers)
            }
        }
    }

    fun addBarrier(text: String) {
        viewModelScope.launch {
            val currentBarriers = _uiState.value.barriers
            val newBarrier = FrictionBarrier(
                id = UUID.randomUUID().toString(),
                text = text,
                isImplemented = false
            )
            val updatedBarriers = currentBarriers + newBarrier
            _uiState.value = _uiState.value.copy(barriers = updatedBarriers)
            saveBarriers(updatedBarriers)
        }
    }

    fun toggleBarrier(barrierId: String) {
        viewModelScope.launch {
            val updatedBarriers = _uiState.value.barriers.map { barrier ->
                if (barrier.id == barrierId) {
                    barrier.copy(isImplemented = !barrier.isImplemented)
                } else {
                    barrier
                }
            }
            _uiState.value = _uiState.value.copy(barriers = updatedBarriers)
            saveBarriers(updatedBarriers)
        }
    }

    fun deleteBarrier(barrierId: String) {
        viewModelScope.launch {
            val updatedBarriers = _uiState.value.barriers.filter { it.id != barrierId }
            _uiState.value = _uiState.value.copy(barriers = updatedBarriers)
            saveBarriers(updatedBarriers)
        }
    }

    private suspend fun saveBarriers(barriers: List<FrictionBarrier>) {
        val strategies = barriers.map { it.text }
        val implemented = barriers.filter { it.isImplemented }.map { it.text }
        habitRepository.updateFrictionStrategies(habitId, strategies, implemented)
    }
}
