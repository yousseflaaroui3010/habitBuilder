package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.TriggerLogEntity
import com.habitarchitect.domain.model.TriggerLog

/**
 * Mapper functions between TriggerLog domain model and TriggerLogEntity.
 */
fun TriggerLogEntity.toDomain(): TriggerLog {
    return TriggerLog(
        id = id,
        habitId = habitId,
        trigger = trigger,
        timestamp = timestamp,
        solution = solution,
        solutionWorked = solutionWorked,
        alternativeSolution = alternativeSolution,
        alternativeWorked = alternativeWorked,
        notes = notes
    )
}

fun TriggerLog.toEntity(): TriggerLogEntity {
    return TriggerLogEntity(
        id = id,
        habitId = habitId,
        trigger = trigger,
        timestamp = timestamp,
        solution = solution,
        solutionWorked = solutionWorked,
        alternativeSolution = alternativeSolution,
        alternativeWorked = alternativeWorked,
        notes = notes
    )
}
