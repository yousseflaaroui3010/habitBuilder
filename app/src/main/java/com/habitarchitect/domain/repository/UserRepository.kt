package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Repository interface for user operations.
 */
interface UserRepository {

    fun getCurrentUser(): Flow<User?>

    fun getUserById(id: String): Flow<User?>

    suspend fun saveUser(user: User): Result<Unit>

    suspend fun updateLastActive(userId: String): Result<Unit>

    suspend fun markOnboardingCompleted(userId: String): Result<Unit>

    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean): Result<Unit>

    suspend fun updateMorningReminderTime(userId: String, time: LocalTime?): Result<Unit>

    suspend fun updateEveningReminderTime(userId: String, time: LocalTime?): Result<Unit>

    suspend fun updateReminderTimes(userId: String, morningTime: String?, eveningTime: String?): Result<Unit>

    suspend fun deleteUser(userId: String): Result<Unit>

    suspend fun deleteAllUserData(userId: String): Result<Unit>
}
