package com.habitarchitect.domain.model

import java.time.LocalTime

/**
 * Domain model representing a user of the app.
 */
data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val authProvider: AuthProvider,
    val createdAt: Long,
    val lastActiveAt: Long,
    val onboardingCompleted: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val morningReminderTime: LocalTime? = LocalTime.of(7, 30),
    val eveningReminderTime: LocalTime? = LocalTime.of(21, 0)
)
