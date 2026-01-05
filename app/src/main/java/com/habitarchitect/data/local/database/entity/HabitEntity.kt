package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing habit data.
 */
@Entity(
    tableName = "habits",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["type"]),
        Index(value = ["isArchived"])
    ]
)
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val type: String,
    val category: String?,
    val templateId: String?,
    val iconEmoji: String,

    // Scheduling
    val triggerTime: String?,
    val triggerContext: String?,
    val frequency: String,
    val activeDays: String?,

    // Intentions-based creation
    val location: String?,
    val goal: String?,

    // Two-minute rule - Start with
    val minimumVersion: String?,

    // Stacking
    val stackAnchor: String?,

    // Reward (for BUILD)
    val reward: String?,

    // Friction strategies (for BREAK) - pipe-separated
    val frictionStrategies: String?,
    val implementedFrictionStrategies: String?,

    // Streaks
    val currentStreak: Int,
    val longestStreak: Int,
    val totalSuccessDays: Int,
    val totalFailureDays: Int,

    // Sharing
    val isSharedWithPartner: Boolean,

    // Ordering & Priority
    val orderIndex: Int = 0,
    val priority: String = "MEDIUM",

    // Meta
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean
)
