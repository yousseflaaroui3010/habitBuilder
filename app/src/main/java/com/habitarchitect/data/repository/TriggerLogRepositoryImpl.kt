package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.TriggerLogDao
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.data.mapper.toEntity
import com.habitarchitect.domain.model.TriggerLog
import com.habitarchitect.domain.repository.TriggerLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of TriggerLogRepository using Room database.
 */
class TriggerLogRepositoryImpl @Inject constructor(
    private val triggerLogDao: TriggerLogDao
) : TriggerLogRepository {

    override fun getTriggerLogsForHabit(habitId: String): Flow<List<TriggerLog>> {
        return triggerLogDao.getTriggerLogsForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTriggerLogById(id: String): TriggerLog? {
        return triggerLogDao.getTriggerLogById(id)?.toDomain()
    }

    override suspend fun saveTriggerLog(triggerLog: TriggerLog): Result<String> {
        return runCatching {
            val id = triggerLog.id.ifBlank { UUID.randomUUID().toString() }
            val logWithId = triggerLog.copy(id = id)
            triggerLogDao.insertTriggerLog(logWithId.toEntity())
            id
        }
    }

    override suspend fun updateTriggerLog(triggerLog: TriggerLog): Result<Unit> {
        return runCatching {
            triggerLogDao.updateTriggerLog(triggerLog.toEntity())
        }
    }

    override suspend fun deleteTriggerLog(id: String): Result<Unit> {
        return runCatching {
            triggerLogDao.deleteTriggerLog(id)
        }
    }

    override suspend fun getUniqueTriggers(habitId: String): List<String> {
        return triggerLogDao.getUniqueTriggers(habitId)
    }

    override suspend fun getSolutionEffectiveness(habitId: String, solution: String): Int {
        val success = triggerLogDao.getSolutionSuccessCount(habitId, solution)
        val total = triggerLogDao.getSolutionTotalCount(habitId, solution)
        return if (total > 0) (success * 100) / total else 0
    }
}
