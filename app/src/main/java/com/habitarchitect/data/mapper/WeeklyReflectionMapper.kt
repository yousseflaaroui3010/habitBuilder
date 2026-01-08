package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.WeeklyReflectionEntity
import com.habitarchitect.domain.model.WeeklyReflection
import java.time.LocalDate

/**
 * Mapper functions between WeeklyReflection domain model and WeeklyReflectionEntity.
 */
fun WeeklyReflectionEntity.toDomain(): WeeklyReflection {
    return WeeklyReflection(
        id = id,
        userId = userId,
        weekStartDate = try { LocalDate.parse(weekStartDate) } catch (e: Exception) { LocalDate.now() },
        wentWell = wentWell,
        didntGoWell = didntGoWell,
        learned = learned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun WeeklyReflection.toEntity(): WeeklyReflectionEntity {
    return WeeklyReflectionEntity(
        id = id,
        userId = userId,
        weekStartDate = weekStartDate.toString(),
        wentWell = wentWell,
        didntGoWell = didntGoWell,
        learned = learned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
