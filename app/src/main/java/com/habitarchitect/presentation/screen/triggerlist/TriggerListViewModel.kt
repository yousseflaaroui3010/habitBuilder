package com.habitarchitect.presentation.screen.triggerlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.TriggerLog
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.domain.repository.TriggerLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TriggerListUiState(
    val habitId: String = "",
    val habitName: String = "",
    val triggerLogs: List<TriggerLog> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingTriggerLog: TriggerLog? = null
)

/**
 * ViewModel for the Trigger List screen - tracks triggers with solutions and effectiveness.
 */
@HiltViewModel
class TriggerListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val triggerLogRepository: TriggerLogRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle.get<String>("habitId") ?: ""

    private val _uiState = MutableStateFlow(TriggerListUiState(habitId = habitId))
    val uiState: StateFlow<TriggerListUiState> = _uiState.asStateFlow()

    init {
        loadHabitName()
        loadTriggerLogs()
    }

    private fun loadHabitName() {
        viewModelScope.launch {
            habitRepository.getHabitById(habitId).collect { habit ->
                habit?.let {
                    _uiState.update { state -> state.copy(habitName = it.name) }
                }
            }
        }
    }

    private fun loadTriggerLogs() {
        viewModelScope.launch {
            triggerLogRepository.getTriggerLogsForHabit(habitId).collect { logs ->
                _uiState.update { state ->
                    state.copy(triggerLogs = logs, isLoading = false)
                }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showEditDialog(triggerLog: TriggerLog) {
        _uiState.update { it.copy(showEditDialog = true, editingTriggerLog = triggerLog) }
    }

    fun hideEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingTriggerLog = null) }
    }

    fun addTriggerLog(trigger: String, solution: String?, notes: String?) {
        viewModelScope.launch {
            val log = TriggerLog(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                trigger = trigger,
                solution = solution?.takeIf { it.isNotBlank() },
                notes = notes?.takeIf { it.isNotBlank() }
            )
            triggerLogRepository.saveTriggerLog(log)
            hideAddDialog()
        }
    }

    fun updateTriggerLog(
        triggerLog: TriggerLog,
        solutionWorked: Boolean?,
        alternativeSolution: String?,
        alternativeWorked: Boolean?,
        notes: String?
    ) {
        viewModelScope.launch {
            val updated = triggerLog.copy(
                solutionWorked = solutionWorked,
                alternativeSolution = alternativeSolution?.takeIf { it.isNotBlank() },
                alternativeWorked = alternativeWorked,
                notes = notes?.takeIf { it.isNotBlank() }
            )
            triggerLogRepository.updateTriggerLog(updated)
            hideEditDialog()
        }
    }

    fun deleteTriggerLog(id: String) {
        viewModelScope.launch {
            triggerLogRepository.deleteTriggerLog(id)
        }
    }
}
