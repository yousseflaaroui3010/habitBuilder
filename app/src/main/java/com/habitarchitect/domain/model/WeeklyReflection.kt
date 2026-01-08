package com.habitarchitect.domain.model

import java.time.LocalDate

/**
 * Domain model representing a weekly reflection entry.
 */
data class WeeklyReflection(
    val id: String,
    val userId: String,
    val weekStartDate: LocalDate,
    val wentWell: String,
    val didntGoWell: String,
    val learned: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
