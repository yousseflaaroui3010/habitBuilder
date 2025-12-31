package com.habitarchitect.domain.model

import java.time.LocalTime

/**
 * Domain model representing a habit to build or break.
 */
data class Habit(
    val id: String,
    val userId: String,
    val name: String,
    val type: HabitType,
    val category: String? = null,
    val templateId: String? = null,
    val iconEmoji: String = "✓",

    // Scheduling
    val triggerTime: LocalTime? = null,
    val triggerContext: String? = null,
    val frequency: Frequency = Frequency.DAILY,
    val activeDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),

    // Two-minute rule (BUILD)
    val minimumVersion: String? = null,

    // Habit stacking (BUILD)
    val stackAnchor: String? = null,

    // Reward (BUILD)
    val reward: String? = null,

    // Friction strategies (BREAK)
    val frictionStrategies: List<String> = emptyList(),

    // Streaks
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalSuccessDays: Int = 0,
    val totalFailureDays: Int = 0,

    // Sharing
    val isSharedWithPartner: Boolean = false,

    // Meta
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
