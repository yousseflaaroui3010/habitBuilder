package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.TriggerLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for trigger log operations - tracks trigger occurrences and solution effectiveness.
 */
@Dao
interface TriggerLogDao {

    @Query("SELECT * FROM trigger_logs WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getTriggerLogsForHabit(habitId: String): Flow<List<TriggerLogEntity>>

    @Query("SELECT * FROM trigger_logs WHERE id = :id")
    suspend fun getTriggerLogById(id: String): TriggerLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriggerLog(triggerLog: TriggerLogEntity)

    @Update
    suspend fun updateTriggerLog(triggerLog: TriggerLogEntity)

    @Query("DELETE FROM trigger_logs WHERE id = :id")
    suspend fun deleteTriggerLog(id: String)

    @Query("DELETE FROM trigger_logs WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: String)

    // Get unique triggers for a habit (for pattern analysis)
    @Query("SELECT DISTINCT trigger FROM trigger_logs WHERE habitId = :habitId")
    suspend fun getUniqueTriggers(habitId: String): List<String>

    // Get success rate for a specific solution
    @Query("""
        SELECT COUNT(*) FROM trigger_logs
        WHERE habitId = :habitId
        AND solution = :solution
        AND solutionWorked = 1
    """)
    suspend fun getSolutionSuccessCount(habitId: String, solution: String): Int

    @Query("""
        SELECT COUNT(*) FROM trigger_logs
        WHERE habitId = :habitId
        AND solution = :solution
        AND solutionWorked IS NOT NULL
    """)
    suspend fun getSolutionTotalCount(habitId: String, solution: String): Int
}
