package com.habitarchitect.presentation.screen.reminders

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

data class RemindersUiState(
    val habits: List<Habit> = emptyList(),
    val enabledReminders: Map<String, Boolean> = emptyMap()
)

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            habitRepository.getActiveHabits(userId).collect { habits ->
                // Filter habits that have trigger times
                val habitsWithReminders = habits.filter { it.triggerTime != null }

                // Use actual isReminderEnabled from habit
                val enabledMap = habitsWithReminders.associate { it.id to it.isReminderEnabled }

                _uiState.value = RemindersUiState(
                    habits = habitsWithReminders,
                    enabledReminders = enabledMap
                )
            }
        }
    }

    fun toggleReminder(habitId: String) {
        val habit = _uiState.value.habits.find { it.id == habitId } ?: return
        val newEnabledState = !habit.isReminderEnabled

        // Update UI state immediately
        _uiState.value = _uiState.value.copy(
            enabledReminders = _uiState.value.enabledReminders + (habitId to newEnabledState)
        )

        // Update habit and schedule/cancel alarms
        viewModelScope.launch {
            habitRepository.updateHabit(
                habit.copy(isReminderEnabled = newEnabledState)
            )
        }
    }
}
