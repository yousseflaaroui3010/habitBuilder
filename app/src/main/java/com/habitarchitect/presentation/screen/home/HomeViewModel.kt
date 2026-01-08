package com.habitarchitect.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.data.preferences.AppPreferences
import com.habitarchitect.data.preferences.ThemeMode
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.domain.repository.ListItemRepository
import com.habitarchitect.service.notification.AlarmScheduler
import java.util.UUID
import com.habitarchitect.service.sound.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class WeeklyStatus(
    val sunday: DailyStatus? = null,
    val monday: DailyStatus? = null,
    val tuesday: DailyStatus? = null,
    val wednesday: DailyStatus? = null,
    val thursday: DailyStatus? = null,
    val friday: DailyStatus? = null,
    val saturday: DailyStatus? = null
)

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(
        val habits: List<Habit>,
        val todayStatuses: Map<String, DailyStatus>,
        val weeklyStatuses: Map<String, WeeklyStatus> = emptyMap(),
        val todaysFocus: String = ""
    ) : HomeUiState()
}

sealed class HomeEvent {
    data class LaunchTemptationOverlay(val habitId: String) : HomeEvent()
    data class ShowMilestoneCelebration(val streak: Int) : HomeEvent()
    data class ShowStreakBreakAnimation(val previousStreak: Int) : HomeEvent()
    data class ShowTriggerDialog(val habitId: String, val habitName: String) : HomeEvent()
}

/**
 * ViewModel for home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val listItemRepository: ListItemRepository,
    private val soundManager: SoundManager,
    private val appPreferences: AppPreferences,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    // Expose theme mode for UI
    val themeMode = appPreferences.themeMode

    private val today = LocalDate.now()

    // Milestone streak values that trigger celebration
    private val milestoneStreaks = setOf(7, 14, 21, 30, 60, 90, 100, 365)

    init {
        loadHabits()
        loadTodaysFocus()
    }

    private var currentFocus: String = ""

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

                    // Load weekly status for each habit
                    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                    val weeklyStatusMap = habits.associate { habit ->
                        val weekLogs = dailyLogRepository.getLogsForRangeOnce(
                            habit.id, startOfWeek, endOfWeek
                        )
                        val logsByDay = weekLogs.associateBy { it.date.dayOfWeek }

                        habit.id to WeeklyStatus(
                            sunday = logsByDay[DayOfWeek.SUNDAY]?.status,
                            monday = logsByDay[DayOfWeek.MONDAY]?.status,
                            tuesday = logsByDay[DayOfWeek.TUESDAY]?.status,
                            wednesday = logsByDay[DayOfWeek.WEDNESDAY]?.status,
                            thursday = logsByDay[DayOfWeek.THURSDAY]?.status,
                            friday = logsByDay[DayOfWeek.FRIDAY]?.status,
                            saturday = logsByDay[DayOfWeek.SATURDAY]?.status
                        )
                    }

                    _uiState.value = HomeUiState.Success(
                        habits = habits,
                        todayStatuses = statusMap,
                        weeklyStatuses = weeklyStatusMap,
                        todaysFocus = currentFocus
                    )
                }
            }
        }
    }

    private fun loadTodaysFocus() {
        viewModelScope.launch {
            appPreferences.todaysFocus.collect { focus ->
                currentFocus = focus
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(todaysFocus = focus)
                }
            }
        }
    }

    fun updateTodaysFocus(focus: String) {
        viewModelScope.launch {
            appPreferences.setTodaysFocus(focus)
        }
    }

    fun markSuccess(habitId: String) {
        viewModelScope.launch {
            dailyLogRepository.markStatus(habitId, today, DailyStatus.SUCCESS)
            val newStreak = habitRepository.incrementStreak(habitId)

            // Add 1 paper clip for success
            habitRepository.addPaperClip(habitId)

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

            // PUNISHMENT: Remove 2 paper clips for failure
            habitRepository.removePaperClips(habitId, 2)

            soundManager.playStreakBreakSound()

            // Show animation if there was a streak to break
            if (previousStreak > 0) {
                _events.emit(HomeEvent.ShowStreakBreakAnimation(previousStreak))
            }

            // For BREAK habits, ask what triggered the failure
            if (habit?.type == HabitType.BREAK) {
                _events.emit(HomeEvent.ShowTriggerDialog(habitId, habit.name))
                // Schedule supportive notification 1 hour later
                alarmScheduler.schedulePostFailureReminder(habitId, habit.name)
            }

            // Reload to update UI
            loadHabits()
        }
    }

    fun saveTrigger(habitId: String, trigger: String) {
        viewModelScope.launch {
            val listItem = ListItem(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                type = ListItemType.TRIGGER,
                content = trigger,
                orderIndex = 0
            )
            listItemRepository.addListItem(listItem)
        }
    }

    fun showTemptationOverlay(habitId: String) {
        viewModelScope.launch {
            _events.emit(HomeEvent.LaunchTemptationOverlay(habitId))
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId)
        }
    }

    fun archiveHabit(habitId: String) {
        viewModelScope.launch {
            habitRepository.archiveHabit(habitId)
        }
    }

    fun reorderHabits(habitIds: List<String>) {
        viewModelScope.launch {
            habitRepository.reorderHabits(habitIds)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val currentMode = appPreferences.themeMode.first()
            val newMode = when (currentMode) {
                ThemeMode.SYSTEM -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.DARK
            }
            appPreferences.setThemeMode(newMode)
        }
    }
}
