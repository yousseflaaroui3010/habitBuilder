package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.ListItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for resistance/attraction list item operations.
 */
@Dao
interface ListItemDao {

    @Query("SELECT * FROM list_items WHERE habitId = :habitId ORDER BY orderIndex ASC")
    fun getListItemsForHabit(habitId: String): Flow<List<ListItemEntity>>

    @Query("SELECT * FROM list_items WHERE habitId = :habitId AND type = :type ORDER BY orderIndex ASC")
    fun getListItemsByType(habitId: String, type: String): Flow<List<ListItemEntity>>

    @Query("SELECT * FROM list_items WHERE habitId = :habitId AND type = :type ORDER BY orderIndex ASC")
    suspend fun getListItemsByTypeOnce(habitId: String, type: String): List<ListItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListItem(item: ListItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListItems(items: List<ListItemEntity>)

    @Update
    suspend fun updateListItem(item: ListItemEntity)

    @Delete
    suspend fun deleteListItem(item: ListItemEntity)

    @Query("DELETE FROM list_items WHERE id = :itemId")
    suspend fun deleteListItemById(itemId: String)

    @Query("DELETE FROM list_items WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: String)

    @Query("SELECT MAX(orderIndex) FROM list_items WHERE habitId = :habitId AND type = :type")
    suspend fun getMaxOrderIndex(habitId: String, type: String): Int?
}
