package com.habitarchitect.presentation.screen.habitdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.DailyLog
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class HabitDetailUiState(
    val habit: Habit? = null,
    val monthLogs: List<DailyLog> = emptyList(),
    val currentMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true,
    val showDeleteConfirmation: Boolean = false,
    val habitDeleted: Boolean = false,
    val weeklySuccessCount: Int = 0
)

/**
 * ViewModel for habit detail screen.
 */
@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle["habitId"] ?: ""

    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
        loadMonthLogs()
        loadWeeklyProgress()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            habitRepository.getHabitById(habitId).collect { habit ->
                _uiState.value = _uiState.value.copy(
                    habit = habit,
                    isLoading = false
                )
            }
        }
    }

    private fun loadMonthLogs() {
        viewModelScope.launch {
            val month = _uiState.value.currentMonth
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()

            dailyLogRepository.getLogsForRange(habitId, startDate, endDate).collect { logs ->
                _uiState.value = _uiState.value.copy(monthLogs = logs)
            }
        }
    }

    private fun loadWeeklyProgress() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

            dailyLogRepository.getLogsForRange(habitId, startOfWeek, endOfWeek).collect { logs ->
                val successCount = logs.count { it.status == DailyStatus.SUCCESS }
                _uiState.value = _uiState.value.copy(weeklySuccessCount = successCount)
            }
        }
    }

    fun navigateToMonth(month: YearMonth) {
        _uiState.value = _uiState.value.copy(currentMonth = month)
        loadMonthLogs()
    }

    fun previousMonth() {
        navigateToMonth(_uiState.value.currentMonth.minusMonths(1))
    }

    fun nextMonth() {
        navigateToMonth(_uiState.value.currentMonth.plusMonths(1))
    }

    fun showDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = true)
    }

    fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
    }

    fun deleteHabit() {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId)
            _uiState.value = _uiState.value.copy(
                showDeleteConfirmation = false,
                habitDeleted = true
            )
        }
    }

    fun archiveHabit() {
        viewModelScope.launch {
            habitRepository.archiveHabit(habitId)
            _uiState.value = _uiState.value.copy(habitDeleted = true)
        }
    }

    fun toggleShareWithPartner() {
        viewModelScope.launch {
            val currentShared = _uiState.value.habit?.isSharedWithPartner ?: false
            habitRepository.updateSharingStatus(habitId, !currentShared)
        }
    }
}
