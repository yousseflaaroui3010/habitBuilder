package com.habitarchitect.domain.model

import java.time.LocalDate

/**
 * Domain model representing a single day's status for a habit.
 */
data class DailyLog(
    val habitId: String,
    val date: LocalDate,
    val status: DailyStatus,
    val markedAt: Long,
    val note: String? = null
)
