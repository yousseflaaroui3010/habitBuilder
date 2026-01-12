package com.habitarchitect.domain.model

/**
 * Domain model for a trigger log entry.
 * Tracks when a trigger occurred, what solution was tried, and if it worked.
 */
data class TriggerLog(
    val id: String = "",
    val habitId: String,
    val trigger: String,
    val timestamp: Long = System.currentTimeMillis(),
    val solution: String? = null,
    val solutionWorked: Boolean? = null,
    val alternativeSolution: String? = null,
    val alternativeWorked: Boolean? = null,
    val notes: String? = null
)
