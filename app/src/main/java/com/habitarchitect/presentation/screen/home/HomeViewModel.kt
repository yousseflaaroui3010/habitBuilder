package com.habitarchitect.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.service.sound.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(
        val habits: List<Habit>,
        val todayStatuses: Map<String, DailyStatus>
    ) : HomeUiState()
}

sealed class HomeEvent {
    data class LaunchTemptationOverlay(val habitId: String) : HomeEvent()
    data class ShowMilestoneCelebration(val streak: Int) : HomeEvent()
    data class ShowStreakBreakAnimation(val previousStreak: Int) : HomeEvent()
}

/**
 * ViewModel for home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private val today = LocalDate.now()

    // Milestone streak values that trigger celebration
    private val milestoneStreaks = setOf(7, 14, 21, 30, 60, 90, 100, 365)

    init {
        loadHabits()
    }

    private fun loadHabits() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            habitRepository.getActiveHabits(userId).collect { habits ->
                if (habits.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    val todayLogs = dailyLogRepository.getLogsForHabitsOnDate(
                        habits.map { it.id },
                        today
                    )
                    val statusMap = todayLogs.associate { it.habitId to it.status }
                    _uiState.value = HomeUiState.Success(
                        habits = habits,
                        todayStatuses = statusMap
                    )
                }
            }
        }
    }

    fun markSuccess(habitId: String) {
        viewModelScope.launch {
            dailyLogRepository.markStatus(habitId, today, DailyStatus.SUCCESS)
            val newStreak = habitRepository.incrementStreak(habitId)

            // Check for milestone celebration
            if (newStreak in milestoneStreaks) {
                soundManager.playMilestoneSound()
                _events.emit(HomeEvent.ShowMilestoneCelebration(newStreak))
            } else {
                soundManager.playSuccessSound()
            }

            // Reload to update UI
            loadHabits()
        }
    }

    fun markFailure(habitId: String) {
        viewModelScope.launch {
            // Get current streak before resetting
            val habit = habitRepository.getHabitByIdOnce(habitId)
            val previousStreak = habit?.currentStreak ?: 0

            dailyLogRepository.markStatus(habitId, today, DailyStatus.FAILURE)
            habitRepository.resetStreak(habitId)
            soundManager.playStreakBreakSound()

            // Show animation if there was a streak to break
            if (previousStreak > 0) {
                _events.emit(HomeEvent.ShowStreakBreakAnimation(previousStreak))
            }

            // Reload to update UI
            loadHabits()
        }
    }

    fun showTemptationOverlay(habitId: String) {
        viewModelScope.launch {
            _events.emit(HomeEvent.LaunchTemptationOverlay(habitId))
        }
    }
}
