package com.habitarchitect.presentation.screen.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArchivedHabitsUiState(
    val isLoading: Boolean = true,
    val habits: List<Habit> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for archived habits screen - manages restore and delete operations.
 */
@HiltViewModel
class ArchivedHabitsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchivedHabitsUiState())
    val uiState: StateFlow<ArchivedHabitsUiState> = _uiState.asStateFlow()

    init {
        loadArchivedHabits()
    }

    private fun loadArchivedHabits() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            habitRepository.getArchivedHabits(userId).collect { habits ->
                _uiState.value = ArchivedHabitsUiState(
                    isLoading = false,
                    habits = habits
                )
            }
        }
    }

    fun restoreHabit(habitId: String) {
        viewModelScope.launch {
            habitRepository.restoreHabit(habitId)
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId)
        }
    }
}
