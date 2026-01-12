package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing trigger occurrences with solutions and effectiveness.
 * Tracks: trigger, when it happened, solution taken, and whether it worked.
 */
@Entity(
    tableName = "trigger_logs",
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["timestamp"])
    ]
)
data class TriggerLogEntity(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val trigger: String,
    val timestamp: Long,
    val solution: String?,
    val solutionWorked: Boolean?,
    val alternativeSolution: String?,
    val alternativeWorked: Boolean?,
    val notes: String?
)
