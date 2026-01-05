package com.habitarchitect.presentation.screen.addhabit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.data.HabitTemplates
import com.habitarchitect.domain.model.Habit
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
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class QuickAddUiState(
    val habitType: HabitType = HabitType.BUILD,
    val name: String = "",
    val emoji: String = "✨",
    val isCreating: Boolean = false,
    val habitCreated: Boolean = false,
    val showEmojiPicker: Boolean = false,
    val triggerTime: LocalTime? = null,
    val triggerTimeFormatted: String? = null,
    val triggerContext: String? = null,
    val activeDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val minimumVersion: String? = null,
    val stackAnchor: String? = null,
    val templateId: String? = null,
    val frictionStrategies: List<String> = emptyList(),
    val resistanceItems: List<String> = emptyList(),
    val attractionItems: List<String> = emptyList()
)

@HiltViewModel
class QuickAddHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val listItemRepository: ListItemRepository
) : ViewModel() {

    private val typeString: String = savedStateHandle["type"] ?: HabitType.BUILD.name
    private val templateId: String? = savedStateHandle["templateId"]
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    private val _uiState = MutableStateFlow(QuickAddUiState())
    val uiState: StateFlow<QuickAddUiState> = _uiState.asStateFlow()

    init {
        val type = try {
            HabitType.valueOf(typeString)
        } catch (e: IllegalArgumentException) {
            HabitType.BUILD
        }

        // Check if we have a template to pre-fill
        val template = templateId?.let { id ->
            HabitTemplates.allTemplates.find { it.id == id }
        }

        if (template != null) {
            // Pre-fill from template
            _uiState.value = QuickAddUiState(
                habitType = type,
                name = template.name,
                emoji = template.iconEmoji,
                templateId = template.id,
                minimumVersion = template.defaultMinimumVersion,
                stackAnchor = template.defaultStackAnchors.firstOrNull(),
                frictionStrategies = template.defaultFrictionStrategies,
                resistanceItems = template.defaultResistanceItems,
                attractionItems = template.defaultAttractionItems
            )
        } else {
            _uiState.value = _uiState.value.copy(
                habitType = type,
                emoji = if (type == HabitType.BUILD) "✨" else "🚫"
            )
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateEmoji(emoji: String) {
        _uiState.value = _uiState.value.copy(emoji = emoji, showEmojiPicker = false)
    }

    fun toggleEmojiPicker() {
        _uiState.value = _uiState.value.copy(showEmojiPicker = !_uiState.value.showEmojiPicker)
    }

    fun updateTriggerTime(hour: Int, minute: Int) {
        val time = LocalTime.of(hour, minute)
        _uiState.value = _uiState.value.copy(
            triggerTime = time,
            triggerTimeFormatted = time.format(timeFormatter)
        )
    }

    fun updateTriggerContext(context: String) {
        _uiState.value = _uiState.value.copy(
            triggerContext = context.ifBlank { null }
        )
    }

    fun updateActiveDays(days: List<Int>) {
        _uiState.value = _uiState.value.copy(activeDays = days)
    }

    fun updateMinimumVersion(version: String) {
        _uiState.value = _uiState.value.copy(
            minimumVersion = version.ifBlank { null }
        )
    }

    fun updateStackAnchor(anchor: String) {
        _uiState.value = _uiState.value.copy(
            stackAnchor = anchor.ifBlank { null }
        )
    }

    fun createHabit() {
        val state = _uiState.value
        if (state.name.isBlank()) return

        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true)

            val habitId = UUID.randomUUID().toString()

            val habit = Habit(
                id = habitId,
                userId = userId,
                name = state.name.trim(),
                type = state.habitType,
                iconEmoji = state.emoji,
                triggerTime = state.triggerTime,
                triggerContext = state.triggerContext,
                activeDays = state.activeDays,
                minimumVersion = state.minimumVersion,
                stackAnchor = state.stackAnchor,
                templateId = state.templateId,
                frictionStrategies = state.frictionStrategies
            )

            habitRepository.createHabit(habit)

            // Save resistance/attraction items from template
            if (state.habitType == HabitType.BREAK && state.resistanceItems.isNotEmpty()) {
                val items = state.resistanceItems.mapIndexed { index, content ->
                    ListItem(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        type = ListItemType.RESISTANCE,
                        content = content,
                        orderIndex = index,
                        isFromTemplate = true
                    )
                }
                listItemRepository.addListItems(items)
            } else if (state.habitType == HabitType.BUILD && state.attractionItems.isNotEmpty()) {
                val items = state.attractionItems.mapIndexed { index, content ->
                    ListItem(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        type = ListItemType.ATTRACTION,
                        content = content,
                        orderIndex = index,
                        isFromTemplate = true
                    )
                }
                listItemRepository.addListItems(items)
            }

            _uiState.value = _uiState.value.copy(
                isCreating = false,
                habitCreated = true
            )
        }
    }
}
