package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.entity.DailyLogEntity
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.domain.model.DailyLog
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.repository.DailyLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementation of DailyLogRepository using Room database.
 */
class DailyLogRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao
) : DailyLogRepository {

    override fun getLogsForRange(
        habitId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DailyLog>> {
        return dailyLogDao.getLogsForRange(
            habitId = habitId,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLogForDate(habitId: String, date: LocalDate): DailyLog? {
        return dailyLogDao.getLogForDate(habitId, date.toString())?.toDomain()
    }

    override fun observeLogForDate(habitId: String, date: LocalDate): Flow<DailyLog?> {
        return dailyLogDao.observeLogForDate(habitId, date.toString()).map { it?.toDomain() }
    }

    override suspend fun markStatus(
        habitId: String,
        date: LocalDate,
        status: DailyStatus,
        note: String?
    ): Result<Unit> {
        return runCatching {
            val entity = DailyLogEntity(
                habitId = habitId,
                date = date.toString(),
                status = status.name,
                markedAt = System.currentTimeMillis(),
                note = note
            )
            dailyLogDao.upsertLog(entity)
        }
    }

    override suspend fun getLogsForHabitsOnDate(
        habitIds: List<String>,
        date: LocalDate
    ): List<DailyLog> {
        return dailyLogDao.getLogsForHabitsOnDate(habitIds, date.toString())
            .map { it.toDomain() }
    }

    override suspend fun getRecentLogs(habitId: String, limit: Int): List<DailyLog> {
        return dailyLogDao.getRecentLogs(habitId, limit).map { it.toDomain() }
    }

    override suspend fun getSuccessCount(habitId: String): Int {
        return dailyLogDao.getSuccessCount(habitId)
    }

    override suspend fun getFailureCount(habitId: String): Int {
        return dailyLogDao.getFailureCount(habitId)
    }
}
