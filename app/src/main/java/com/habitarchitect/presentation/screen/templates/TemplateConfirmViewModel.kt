package com.habitarchitect.presentation.screen.templates

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.data.HabitTemplates
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitTemplate
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.domain.repository.ListItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

data class TemplateConfirmUiState(
    val template: HabitTemplate? = null,
    val isCreating: Boolean = false,
    val habitCreated: Boolean = false,
    val showReminderDialog: Boolean = false,
    val reminderTime: String = "",
    val selectedDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7)
)

@HiltViewModel
class TemplateConfirmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val listItemRepository: ListItemRepository
) : ViewModel() {

    private val templateId: String = savedStateHandle["templateId"] ?: ""

    private val _uiState = MutableStateFlow(TemplateConfirmUiState())
    val uiState: StateFlow<TemplateConfirmUiState> = _uiState.asStateFlow()

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        val template = HabitTemplates.allTemplates.find { it.id == templateId }
        _uiState.value = _uiState.value.copy(template = template)
    }

    fun showReminderDialog() {
        _uiState.value = _uiState.value.copy(showReminderDialog = true)
    }

    fun hideReminderDialog() {
        _uiState.value = _uiState.value.copy(showReminderDialog = false)
    }

    fun updateReminderTime(time: String) {
        _uiState.value = _uiState.value.copy(reminderTime = time)
    }

    fun toggleDay(day: Int) {
        val currentDays = _uiState.value.selectedDays
        val newDays = if (currentDays.contains(day)) {
            currentDays - day
        } else {
            (currentDays + day).sorted()
        }
        _uiState.value = _uiState.value.copy(selectedDays = newDays)
    }

    fun createHabitFromTemplate() {
        val template = _uiState.value.template ?: return
        // For BUILD habits, show reminder dialog first
        if (template.type == HabitType.BUILD) {
            showReminderDialog()
        } else {
            proceedWithHabitCreation()
        }
    }

    fun proceedWithHabitCreation() {
        val template = _uiState.value.template ?: return
        val userId = firebaseAuth.currentUser?.uid ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, showReminderDialog = false)

            val habitId = UUID.randomUUID().toString()

            // Parse trigger time if provided
            val triggerTime = if (state.reminderTime.isNotBlank()) {
                parseTimeString(state.reminderTime)
            } else null

            // Create habit with all template defaults
            val habit = Habit(
                id = habitId,
                userId = userId,
                name = template.name,
                type = template.type,
                category = template.category,
                templateId = template.id,
                iconEmoji = template.iconEmoji,
                minimumVersion = template.defaultMinimumVersion,
                stackAnchor = template.defaultStackAnchors.firstOrNull(),
                reward = template.defaultRewards.firstOrNull(),
                frictionStrategies = template.defaultFrictionStrategies,
                triggerTime = triggerTime,
                triggerContext = state.reminderTime,
                activeDays = state.selectedDays,
                isReminderEnabled = triggerTime != null
            )

            habitRepository.createHabit(habit)

            // Create list items based on template type
            val listItems = if (template.type == HabitType.BREAK) {
                template.defaultResistanceItems.mapIndexed { index, content ->
                    ListItem(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        type = ListItemType.RESISTANCE,
                        content = content,
                        orderIndex = index,
                        isFromTemplate = true
                    )
                }
            } else {
                template.defaultAttractionItems.mapIndexed { index, content ->
                    ListItem(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        type = ListItemType.ATTRACTION,
                        content = content,
                        orderIndex = index,
                        isFromTemplate = true
                    )
                }
            }

            if (listItems.isNotEmpty()) {
                listItemRepository.addListItems(listItems)
            }

            _uiState.value = _uiState.value.copy(
                isCreating = false,
                habitCreated = true
            )
        }
    }

    private fun parseTimeString(timeStr: String): LocalTime? {
        val cleanedTime = timeStr.trim().uppercase()

        return try {
            when {
                cleanedTime.contains("AM") || cleanedTime.contains("PM") -> {
                    val isPM = cleanedTime.contains("PM")
                    val timePart = cleanedTime.replace("AM", "").replace("PM", "").trim()
                    val parts = timePart.split(":")
                    var hour = parts[0].toInt()
                    val minute = if (parts.size > 1) parts[1].toInt() else 0

                    if (isPM && hour != 12) hour += 12
                    if (!isPM && hour == 12) hour = 0

                    LocalTime.of(hour, minute)
                }
                cleanedTime.contains(":") -> {
                    val parts = cleanedTime.split(":")
                    LocalTime.of(parts[0].toInt(), parts[1].toInt())
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
