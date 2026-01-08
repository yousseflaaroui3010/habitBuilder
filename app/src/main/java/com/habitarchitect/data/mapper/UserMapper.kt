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
        authProvider = try { AuthProvider.valueOf(authProvider) } catch (e: IllegalArgumentException) { AuthProvider.GOOGLE },
        createdAt = createdAt,
        lastActiveAt = lastActiveAt,
        onboardingCompleted = onboardingCompleted,
        notificationsEnabled = notificationsEnabled,
        morningReminderTime = morningReminderTime?.let {
            try { LocalTime.parse(it) } catch (e: Exception) { null }
        },
        eveningReminderTime = eveningReminderTime?.let {
            try { LocalTime.parse(it) } catch (e: Exception) { null }
        }
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
