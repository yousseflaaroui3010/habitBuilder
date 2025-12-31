package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * Room entity for storing daily habit log entries.
 */
@Entity(
    tableName = "daily_logs",
    primaryKeys = ["habitId", "date"],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["date"])
    ]
)
data class DailyLogEntity(
    val habitId: String,
    val date: String,
    val status: String,
    val markedAt: Long,
    val note: String?
)
