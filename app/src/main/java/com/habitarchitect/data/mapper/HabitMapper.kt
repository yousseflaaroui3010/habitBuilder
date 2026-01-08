package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.HabitEntity
import com.habitarchitect.domain.model.Frequency
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.model.Priority
import java.time.LocalTime

/**
 * Mapper functions between Habit domain model and HabitEntity.
 */
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        userId = userId,
        name = name,
        type = try { HabitType.valueOf(type) } catch (e: IllegalArgumentException) { HabitType.BUILD },
        category = category,
        templateId = templateId,
        iconEmoji = iconEmoji,
        triggerTime = triggerTime?.let {
            try { LocalTime.parse(it) } catch (e: Exception) { null }
        },
        triggerContext = triggerContext,
        frequency = try { Frequency.valueOf(frequency) } catch (e: IllegalArgumentException) { Frequency.DAILY },
        activeDays = activeDays?.split(",")?.mapNotNull { it.toIntOrNull() }?.ifEmpty { null } ?: listOf(1, 2, 3, 4, 5, 6, 7),
        location = location,
        goal = goal,
        minimumVersion = minimumVersion,
        stackAnchor = stackAnchor,
        reward = reward,
        frictionStrategies = frictionStrategies?.split("|")?.filter { it.isNotBlank() } ?: emptyList(),
        implementedFrictionStrategies = implementedFrictionStrategies?.split("|")?.filter { it.isNotBlank() } ?: emptyList(),
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalSuccessDays = totalSuccessDays,
        totalFailureDays = totalFailureDays,
        paperClipCount = paperClipCount,
        paperClipGoal = paperClipGoal,
        isSharedWithPartner = isSharedWithPartner,
        orderIndex = orderIndex,
        priority = try { Priority.valueOf(priority) } catch (e: IllegalArgumentException) { Priority.MEDIUM },
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
        location = location,
        goal = goal,
        minimumVersion = minimumVersion,
        stackAnchor = stackAnchor,
        reward = reward,
        frictionStrategies = frictionStrategies.joinToString("|"),
        implementedFrictionStrategies = implementedFrictionStrategies.joinToString("|"),
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalSuccessDays = totalSuccessDays,
        totalFailureDays = totalFailureDays,
        paperClipCount = paperClipCount,
        paperClipGoal = paperClipGoal,
        isSharedWithPartner = isSharedWithPartner,
        orderIndex = orderIndex,
        priority = priority.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}
