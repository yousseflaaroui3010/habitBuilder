package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.HabitEntity
import com.habitarchitect.domain.model.Frequency
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import java.time.LocalTime

/**
 * Mapper functions between Habit domain model and HabitEntity.
 */
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        userId = userId,
        name = name,
        type = HabitType.valueOf(type),
        category = category,
        templateId = templateId,
        iconEmoji = iconEmoji,
        triggerTime = triggerTime?.let { LocalTime.parse(it) },
        triggerContext = triggerContext,
        frequency = Frequency.valueOf(frequency),
        activeDays = activeDays?.split(",")?.map { it.toInt() } ?: listOf(1, 2, 3, 4, 5, 6, 7),
        minimumVersion = minimumVersion,
        stackAnchor = stackAnchor,
        reward = reward,
        frictionStrategies = frictionStrategies?.split("|")?.filter { it.isNotBlank() } ?: emptyList(),
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalSuccessDays = totalSuccessDays,
        totalFailureDays = totalFailureDays,
        isSharedWithPartner = isSharedWithPartner,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        userId = userId,
        name = name,
        type = type.name,
        category = category,
        templateId = templateId,
        iconEmoji = iconEmoji,
        triggerTime = triggerTime?.toString(),
        triggerContext = triggerContext,
        frequency = frequency.name,
        activeDays = activeDays.joinToString(","),
        minimumVersion = minimumVersion,
        stackAnchor = stackAnchor,
        reward = reward,
        frictionStrategies = frictionStrategies.joinToString("|"),
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalSuccessDays = totalSuccessDays,
        totalFailureDays = totalFailureDays,
        isSharedWithPartner = isSharedWithPartner,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}
