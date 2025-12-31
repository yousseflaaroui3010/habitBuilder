package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitarchitect.data.local.database.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for daily log operations.
 */
@Dao
interface DailyLogDao {

    @Query("""
        SELECT * FROM daily_logs
        WHERE habitId = :habitId
        AND date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
    """)
    fun getLogsForRange(
        habitId: String,
        startDate: String,
        endDate: String
    ): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getLogForDate(habitId: String, date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    fun observeLogForDate(habitId: String, date: String): Flow<DailyLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: DailyLogEntity)

    @Query("SELECT * FROM daily_logs WHERE habitId IN (:habitIds) AND date = :date")
    suspend fun getLogsForHabitsOnDate(habitIds: List<String>, date: String): List<DailyLogEntity>

    @Query("""
        SELECT * FROM daily_logs
        WHERE habitId = :habitId
        ORDER BY date DESC
        LIMIT :limit
    """)
    suspend fun getRecentLogs(habitId: String, limit: Int): List<DailyLogEntity>

    @Query("DELETE FROM daily_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: String)

    @Query("DELETE FROM daily_logs WHERE habitId IN (SELECT id FROM habits WHERE userId = :userId)")
    suspend fun deleteLogsForUser(userId: String)

    @Query("SELECT COUNT(*) FROM daily_logs WHERE habitId = :habitId AND status = 'SUCCESS'")
    suspend fun getSuccessCount(habitId: String): Int

    @Query("SELECT COUNT(*) FROM daily_logs WHERE habitId = :habitId AND status = 'FAILURE'")
    suspend fun getFailureCount(habitId: String): Int

    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId ORDER BY date DESC")
    fun getLogsForHabit(habitId: String): Flow<List<DailyLogEntity>>
}
