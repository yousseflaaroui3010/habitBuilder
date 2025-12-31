package com.habitarchitect.presentation.screen.templates

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.habitarchitect.data.HabitTemplates
import com.habitarchitect.domain.model.HabitTemplate
import com.habitarchitect.domain.model.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class TemplateBrowserUiState(
    val templates: List<HabitTemplate> = emptyList(),
    val habitType: HabitType = HabitType.BUILD
)

/**
 * ViewModel for template browser screen.
 */
@HiltViewModel
class TemplateBrowserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitTypeString: String = savedStateHandle["type"] ?: "BUILD"
    private val habitType = HabitType.valueOf(habitTypeString)

    private val _uiState = MutableStateFlow(TemplateBrowserUiState())
    val uiState: StateFlow<TemplateBrowserUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        val templates = if (habitType == HabitType.BUILD) {
            HabitTemplates.buildTemplates
        } else {
            HabitTemplates.breakTemplates
        }

        _uiState.value = TemplateBrowserUiState(
            templates = templates,
            habitType = habitType
        )
    }
}
