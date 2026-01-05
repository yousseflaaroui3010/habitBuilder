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
import java.util.UUID
import javax.inject.Inject

data class TemplateConfirmUiState(
    val template: HabitTemplate? = null,
    val isCreating: Boolean = false,
    val habitCreated: Boolean = false
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

    fun createHabitFromTemplate() {
        val template = _uiState.value.template ?: return
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true)

            val habitId = UUID.randomUUID().toString()

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
                frictionStrategies = template.defaultFrictionStrategies
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
}
