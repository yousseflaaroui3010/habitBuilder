package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.DailyLog
import com.habitarchitect.domain.model.DailyStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for daily log operations.
 */
interface DailyLogRepository {

    fun getLogsForRange(
        habitId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DailyLog>>

    suspend fun getLogsForRangeOnce(
        habitId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyLog>

    suspend fun getLogForDate(habitId: String, date: LocalDate): DailyLog?

    fun observeLogForDate(habitId: String, date: LocalDate): Flow<DailyLog?>

    suspend fun markStatus(habitId: String, date: LocalDate, status: DailyStatus, note: String? = null): Result<Unit>

    suspend fun getLogsForHabitsOnDate(habitIds: List<String>, date: LocalDate): List<DailyLog>

    suspend fun getRecentLogs(habitId: String, limit: Int): List<DailyLog>

    suspend fun getSuccessCount(habitId: String): Int

    suspend fun getFailureCount(habitId: String): Int
}
