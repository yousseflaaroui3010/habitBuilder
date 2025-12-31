package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.data.mapper.toEntity
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import com.habitarchitect.domain.repository.ListItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ListItemRepository using Room database.
 */
class ListItemRepositoryImpl @Inject constructor(
    private val listItemDao: ListItemDao
) : ListItemRepository {

    override fun getListItemsForHabit(habitId: String): Flow<List<ListItem>> {
        return listItemDao.getListItemsForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getListItemsByType(habitId: String, type: ListItemType): Flow<List<ListItem>> {
        return listItemDao.getListItemsByType(habitId, type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getListItemsByTypeOnce(habitId: String, type: ListItemType): List<ListItem> {
        return listItemDao.getListItemsByTypeOnce(habitId, type.name).map { it.toDomain() }
    }

    override suspend fun addListItem(item: ListItem): Result<Unit> {
        return runCatching {
            val maxIndex = listItemDao.getMaxOrderIndex(item.habitId, item.type.name) ?: -1
            val itemWithOrder = item.copy(orderIndex = maxIndex + 1)
            listItemDao.insertListItem(itemWithOrder.toEntity())
        }
    }

    override suspend fun addListItems(items: List<ListItem>): Result<Unit> {
        return runCatching {
            listItemDao.insertListItems(items.map { it.toEntity() })
        }
    }

    override suspend fun updateListItem(item: ListItem): Result<Unit> {
        return runCatching {
            listItemDao.updateListItem(item.toEntity())
        }
    }

    override suspend fun deleteListItem(itemId: String): Result<Unit> {
        return runCatching {
            listItemDao.deleteListItemById(itemId)
        }
    }

    override suspend fun deleteAllForHabit(habitId: String): Result<Unit> {
        return runCatching {
            listItemDao.deleteAllForHabit(habitId)
        }
    }

    override suspend fun reorderItems(
        habitId: String,
        type: ListItemType,
        orderedIds: List<String>
    ): Result<Unit> {
        return runCatching {
            val items = listItemDao.getListItemsByTypeOnce(habitId, type.name)
            orderedIds.forEachIndexed { index, id ->
                items.find { it.id == id }?.let { item ->
                    listItemDao.updateListItem(item.copy(orderIndex = index))
                }
            }
        }
    }
}
