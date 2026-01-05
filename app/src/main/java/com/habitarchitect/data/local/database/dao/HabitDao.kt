package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for habit operations.
 */
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE userId = :userId AND isArchived = 0 ORDER BY orderIndex ASC, createdAt DESC")
    fun getActiveHabits(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: String): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabitByIdOnce(id: String): HabitEntity?

    @Query("SELECT * FROM habits WHERE userId = :userId AND type = :type AND isArchived = 0")
    fun getHabitsByType(userId: String, type: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId AND isSharedWithPartner = 1 AND isArchived = 0")
    fun getSharedHabits(userId: String): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("""
        UPDATE habits
        SET currentStreak = 0,
            totalFailureDays = totalFailureDays + 1,
            updatedAt = :timestamp
        WHERE id = :habitId
    """)
    suspend fun resetStreak(habitId: String, timestamp: Long)

    @Query("""
        UPDATE habits
        SET currentStreak = currentStreak + 1,
            longestStreak = MAX(longestStreak, currentStreak + 1),
            totalSuccessDays = totalSuccessDays + 1,
            updatedAt = :timestamp
        WHERE id = :habitId
    """)
    suspend fun incrementStreak(habitId: String, timestamp: Long)

    @Query("UPDATE habits SET isArchived = 1, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun archiveHabit(habitId: String, timestamp: Long)

    @Query("UPDATE habits SET isSharedWithPartner = :shared, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updateSharingStatus(habitId: String, shared: Boolean, timestamp: Long)

    @Query("UPDATE habits SET frictionStrategies = :strategies, implementedFrictionStrategies = :implemented, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updateFrictionStrategies(habitId: String, strategies: String, implemented: String, timestamp: Long)

    @Query("UPDATE habits SET reward = :reward, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updateReward(habitId: String, reward: String, timestamp: Long)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: String)

    @Query("DELETE FROM habits WHERE userId = :userId")
    suspend fun deleteAllHabitsForUser(userId: String)

    @Query("SELECT COUNT(*) FROM habits WHERE userId = :userId AND isArchived = 0")
    suspend fun getActiveHabitCount(userId: String): Int

    @Query("SELECT * FROM habits WHERE type = :type AND isArchived = 0 ORDER BY createdAt ASC LIMIT 1")
    suspend fun getHabitsByTypeOnce(type: String): List<HabitEntity>

    @Query("UPDATE habits SET orderIndex = :orderIndex, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updateOrderIndex(habitId: String, orderIndex: Int, timestamp: Long)

    @Query("UPDATE habits SET paperClipCount = :count, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updatePaperClipCount(habitId: String, count: Int, timestamp: Long)
}
