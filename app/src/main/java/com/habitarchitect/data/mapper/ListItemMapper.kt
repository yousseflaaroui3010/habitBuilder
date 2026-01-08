package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.ListItemEntity
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType

/**
 * Mapper functions between ListItem domain model and ListItemEntity.
 */
fun ListItemEntity.toDomain(): ListItem {
    return ListItem(
        id = id,
        habitId = habitId,
        type = try { ListItemType.valueOf(type) } catch (e: IllegalArgumentException) { ListItemType.RESISTANCE },
        content = content,
        orderIndex = orderIndex,
        isFromTemplate = isFromTemplate,
        createdAt = createdAt
    )
}

fun ListItem.toEntity(): ListItemEntity {
    return ListItemEntity(
        id = id,
        habitId = habitId,
        type = type.name,
        content = content,
        orderIndex = orderIndex,
        isFromTemplate = isFromTemplate,
        createdAt = createdAt
    )
}
