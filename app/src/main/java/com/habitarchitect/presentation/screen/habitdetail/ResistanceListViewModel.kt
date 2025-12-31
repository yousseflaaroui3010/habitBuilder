package com.habitarchitect.presentation.screen.habitdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.domain.repository.ListItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ResistanceListUiState(
    val items: List<ListItem> = emptyList(),
    val listType: String = "RESISTANCE",
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false
)

/**
 * ViewModel for resistance/attraction list screen.
 */
@HiltViewModel
class ResistanceListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val listItemRepository: ListItemRepository
) : ViewModel() {

    private val habitId: String = savedStateHandle["habitId"] ?: ""

    private val _uiState = MutableStateFlow(ResistanceListUiState())
    val uiState: StateFlow<ResistanceListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val habit = habitRepository.getHabitById(habitId).first()
            val listType = if (habit?.type == HabitType.BREAK) ListItemType.RESISTANCE else ListItemType.ATTRACTION

            _uiState.value = _uiState.value.copy(listType = listType.name)

            listItemRepository.getListItemsByType(habitId, listType).collect { items ->
                _uiState.value = _uiState.value.copy(
                    items = items,
                    isLoading = false
                )
            }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun addItem(content: String) {
        viewModelScope.launch {
            val type = ListItemType.valueOf(_uiState.value.listType)
            val item = ListItem(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                type = type,
                content = content,
                orderIndex = _uiState.value.items.size
            )
            listItemRepository.addListItem(item)
            hideAddDialog()
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            listItemRepository.deleteListItem(itemId)
        }
    }
}
