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
    val showDialog: Boolean = false,
    val editingItem: ListItem? = null,
    val dialogText: String = ""
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
        _uiState.value = _uiState.value.copy(
            showDialog = true,
            editingItem = null,
            dialogText = ""
        )
    }

    fun showEditDialog(item: ListItem) {
        _uiState.value = _uiState.value.copy(
            showDialog = true,
            editingItem = item,
            dialogText = item.content
        )
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(
            showDialog = false,
            editingItem = null,
            dialogText = ""
        )
    }

    fun updateDialogText(text: String) {
        _uiState.value = _uiState.value.copy(dialogText = text)
    }

    fun saveItem() {
        val content = _uiState.value.dialogText.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            val editingItem = _uiState.value.editingItem
            if (editingItem != null) {
                listItemRepository.updateListItem(editingItem.copy(content = content))
            } else {
                val type = ListItemType.valueOf(_uiState.value.listType)
                val item = ListItem(
                    id = UUID.randomUUID().toString(),
                    habitId = habitId,
                    type = type,
                    content = content,
                    orderIndex = _uiState.value.items.size
                )
                listItemRepository.addListItem(item)
            }
            hideDialog()
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            listItemRepository.deleteListItem(itemId)
        }
    }

    fun moveItemUp(itemId: String) {
        val items = _uiState.value.items.toMutableList()
        val index = items.indexOfFirst { it.id == itemId }
        if (index > 0) {
            val item = items.removeAt(index)
            items.add(index - 1, item)
            saveOrder(items.map { it.id })
        }
    }

    fun moveItemDown(itemId: String) {
        val items = _uiState.value.items.toMutableList()
        val index = items.indexOfFirst { it.id == itemId }
        if (index < items.size - 1) {
            val item = items.removeAt(index)
            items.add(index + 1, item)
            saveOrder(items.map { it.id })
        }
    }

    private fun saveOrder(orderedIds: List<String>) {
        viewModelScope.launch {
            val type = ListItemType.valueOf(_uiState.value.listType)
            listItemRepository.reorderItems(habitId, type, orderedIds)
        }
    }
}
