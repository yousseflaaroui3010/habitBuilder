package com.habitarchitect.presentation.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HabitProgress(
    val habit: Habit,
    val successDays: Int,
    val totalDays: Int,
    val last7Days: List<Boolean?> // true = success, false = failure, null = no data
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalHabits: Int = 0,
    val buildHabits: Int = 0,
    val breakHabits: Int = 0,
    val totalStreakDays: Int = 0,
    val longestStreak: Int = 0,
    val overallSuccessRate: Float = 0f,
    val habitProgress: List<HabitProgress> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            habitRepository.getActiveHabits(userId).collect { habits ->
                if (habits.isEmpty()) {
                    _uiState.value = DashboardUiState(isLoading = false)
                    return@collect
                }

                val buildHabits = habits.count { it.type == HabitType.BUILD }
                val breakHabits = habits.count { it.type == HabitType.BREAK }
                val totalStreakDays = habits.sumOf { it.currentStreak }
                val longestStreak = habits.maxOfOrNull { it.longestStreak } ?: 0

                // Calculate overall success rate
                val totalSuccess = habits.sumOf { it.totalSuccessDays }
                val totalFailure = habits.sumOf { it.totalFailureDays }
                val totalAttempts = totalSuccess + totalFailure
                val successRate = if (totalAttempts > 0) {
                    totalSuccess.toFloat() / totalAttempts
                } else 0f

                // Get last 7 days progress for each habit
                val today = LocalDate.now()
                val last7Days = (0..6).map { today.minusDays(it.toLong()) }.reversed()

                val habitProgressList = habits.map { habit ->
                    val logs = try {
                        dailyLogRepository.getRecentLogs(habit.id, 7)
                    } catch (e: Exception) {
                        emptyList()
                    }

                    val last7DaysStatus = last7Days.map { date ->
                        val log = logs.find { l -> l.date == date }
                        when (log?.status) {
                            com.habitarchitect.domain.model.DailyStatus.SUCCESS -> true
                            com.habitarchitect.domain.model.DailyStatus.FAILURE -> false
                            else -> null
                        }
                    }

                    HabitProgress(
                        habit = habit,
                        successDays = habit.totalSuccessDays,
                        totalDays = habit.totalSuccessDays + habit.totalFailureDays,
                        last7Days = last7DaysStatus
                    )
                }

                _uiState.value = DashboardUiState(
                    isLoading = false,
                    totalHabits = habits.size,
                    buildHabits = buildHabits,
                    breakHabits = breakHabits,
                    totalStreakDays = totalStreakDays,
                    longestStreak = longestStreak,
                    overallSuccessRate = successRate,
                    habitProgress = habitProgressList
                )
            }
        }
    }
}
