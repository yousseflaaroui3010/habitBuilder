package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.UserEntity
import com.habitarchitect.domain.model.AuthProvider
import com.habitarchitect.domain.model.User
import java.time.LocalTime

/**
 * Mapper functions between User domain model and UserEntity.
 */
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        authProvider = AuthProvider.valueOf(authProvider),
        createdAt = createdAt,
        lastActiveAt = lastActiveAt,
        onboardingCompleted = onboardingCompleted,
        notificationsEnabled = notificationsEnabled,
        morningReminderTime = morningReminderTime?.let { LocalTime.parse(it) },
        eveningReminderTime = eveningReminderTime?.let { LocalTime.parse(it) }
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        authProvider = authProvider.name,
        createdAt = createdAt,
        lastActiveAt = lastActiveAt,
        onboardingCompleted = onboardingCompleted,
        notificationsEnabled = notificationsEnabled,
        morningReminderTime = morningReminderTime?.toString(),
        eveningReminderTime = eveningReminderTime?.toString()
    )
}
