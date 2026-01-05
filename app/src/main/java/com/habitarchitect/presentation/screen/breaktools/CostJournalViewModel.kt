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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class CostJournalUiState(
    val habitName: String = "",
    val costs: List<CostEntry> = emptyList()
)

@HiltViewModel
class CostJournalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle["habitId"] ?: ""

    private val _uiState = MutableStateFlow(CostJournalUiState())
    val uiState: StateFlow<CostJournalUiState> = _uiState.asStateFlow()

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
            }
        }
    }

    fun addCost(category: CostCategory, description: String) {
        val timestamp = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
            .format(Date())

        val newCost = CostEntry(
            id = UUID.randomUUID().toString(),
            category = category,
            description = description,
            timestamp = timestamp
        )

        _uiState.value = _uiState.value.copy(
            costs = _uiState.value.costs + newCost
        )
    }

    fun deleteCost(id: String) {
        _uiState.value = _uiState.value.copy(
            costs = _uiState.value.costs.filter { it.id != id }
        )
    }
}
