package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_reflections",
    indices = [Index(value = ["userId", "weekStartDate"], unique = true)]
)
data class WeeklyReflectionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val weekStartDate: String,
    val wentWell: String,
    val didntGoWell: String,
    val learned: String,
    val createdAt: Long,
    val updatedAt: Long
)
