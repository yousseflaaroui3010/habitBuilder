package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for user operations.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET onboardingCompleted = 1 WHERE id = :userId")
    suspend fun markOnboardingCompleted(userId: String)

    @Query("UPDATE users SET lastActiveAt = :timestamp WHERE id = :userId")
    suspend fun updateLastActive(userId: String, timestamp: Long)

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :userId")
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean)

    @Query("UPDATE users SET morningReminderTime = :time WHERE id = :userId")
    suspend fun updateMorningReminderTime(userId: String, time: String?)

    @Query("UPDATE users SET eveningReminderTime = :time WHERE id = :userId")
    suspend fun updateEveningReminderTime(userId: String, time: String?)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
