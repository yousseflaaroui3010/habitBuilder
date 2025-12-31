package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing resistance/attraction list items.
 */
@Entity(
    tableName = "list_items",
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["type"])
    ]
)
data class ListItemEntity(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val type: String,
    val content: String,
    val orderIndex: Int,
    val isFromTemplate: Boolean,
    val createdAt: Long
)
