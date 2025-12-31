package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for resistance/attraction list item operations.
 */
interface ListItemRepository {

    fun getListItemsForHabit(habitId: String): Flow<List<ListItem>>

    fun getListItemsByType(habitId: String, type: ListItemType): Flow<List<ListItem>>

    suspend fun getListItemsByTypeOnce(habitId: String, type: ListItemType): List<ListItem>

    suspend fun addListItem(item: ListItem): Result<Unit>

    suspend fun addListItems(items: List<ListItem>): Result<Unit>

    suspend fun updateListItem(item: ListItem): Result<Unit>

    suspend fun deleteListItem(itemId: String): Result<Unit>

    suspend fun deleteAllForHabit(habitId: String): Result<Unit>

    suspend fun reorderItems(habitId: String, type: ListItemType, orderedIds: List<String>): Result<Unit>
}
