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

    // Two-minute rule
    val minimumVersion: String?,

    // Stacking
    val stackAnchor: String?,

    // Reward (for BUILD)
    val reward: String?,

    // Friction strategies (for BREAK) - pipe-separated
    val frictionStrategies: String?,

    // Streaks
    val currentStreak: Int,
    val longestStreak: Int,
    val totalSuccessDays: Int,
    val totalFailureDays: Int,

    // Sharing
    val isSharedWithPartner: Boolean,

    // Meta
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean
)
