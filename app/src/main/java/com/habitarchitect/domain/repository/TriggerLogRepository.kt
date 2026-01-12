package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.TriggerLog
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for trigger log operations.
 */
interface TriggerLogRepository {
    fun getTriggerLogsForHabit(habitId: String): Flow<List<TriggerLog>>
    suspend fun getTriggerLogById(id: String): TriggerLog?
    suspend fun saveTriggerLog(triggerLog: TriggerLog): Result<String>
    suspend fun updateTriggerLog(triggerLog: TriggerLog): Result<Unit>
    suspend fun deleteTriggerLog(id: String): Result<Unit>
    suspend fun getUniqueTriggers(habitId: String): List<String>
    suspend fun getSolutionEffectiveness(habitId: String, solution: String): Int
}
