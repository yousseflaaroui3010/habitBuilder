package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.DailyLogEntity
import com.habitarchitect.domain.model.DailyLog
import com.habitarchitect.domain.model.DailyStatus
import java.time.LocalDate

/**
 * Mapper functions between DailyLog domain model and DailyLogEntity.
 */
fun DailyLogEntity.toDomain(): DailyLog {
    return DailyLog(
        habitId = habitId,
        date = LocalDate.parse(date),
        status = DailyStatus.valueOf(status),
        markedAt = markedAt,
        note = note
    )
}

fun DailyLog.toEntity(): DailyLogEntity {
    return DailyLogEntity(
        habitId = habitId,
        date = date.toString(),
        status = status.name,
        markedAt = markedAt,
        note = note
    )
}
