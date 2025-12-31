package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing user data.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val authProvider: String,
    val createdAt: Long,
    val lastActiveAt: Long,
    val onboardingCompleted: Boolean,
    val notificationsEnabled: Boolean,
    val morningReminderTime: String?,
    val eveningReminderTime: String?
)
