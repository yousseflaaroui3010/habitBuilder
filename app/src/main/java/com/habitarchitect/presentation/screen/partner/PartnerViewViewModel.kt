package com.habitarchitect.presentation.screen.partner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class PartnerHabitWithStatus(
    val habit: Habit,
    val todayStatus: DailyStatus?,
    val slippedToday: Boolean
)

data class PartnerViewUiState(
    val habits: List<PartnerHabitWithStatus> = emptyList(),
    val isLoading: Boolean = true,
    val partnerName: String = "Partner",
    val hasSlipUps: Boolean = false
)

/**
 * ViewModel for viewing partner's shared habits.
 */
@HiltViewModel
class PartnerViewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val partnerId: String = savedStateHandle["partnerId"] ?: ""
    private val today = LocalDate.now()

    private val _uiState = MutableStateFlow(PartnerViewUiState())
    val uiState: StateFlow<PartnerViewUiState> = _uiState.asStateFlow()

    init {
        loadPartnerHabits()
    }

    private fun loadPartnerHabits() {
        viewModelScope.launch {
            habitRepository.getSharedHabits(partnerId).collect { habits ->
                if (habits.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        habits = emptyList(),
                        isLoading = false,
                        hasSlipUps = false
                    )
                    return@collect
                }

                val todayLogs = dailyLogRepository.getLogsForHabitsOnDate(
                    habits.map { it.id },
                    today
                )
                val statusMap = todayLogs.associate { it.habitId to it.status }

                val habitsWithStatus = habits.map { habit ->
                    val todayStatus = statusMap[habit.id]
                    PartnerHabitWithStatus(
                        habit = habit,
                        todayStatus = todayStatus,
                        slippedToday = todayStatus == DailyStatus.FAILURE
                    )
                }

                val hasSlipUps = habitsWithStatus.any { it.slippedToday }

                _uiState.value = _uiState.value.copy(
                    habits = habitsWithStatus,
                    isLoading = false,
                    hasSlipUps = hasSlipUps
                )
            }
        }
    }
}
