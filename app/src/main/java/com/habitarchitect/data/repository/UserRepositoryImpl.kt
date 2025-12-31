package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.dao.PartnershipDao
import com.habitarchitect.data.local.database.dao.UserDao
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.data.mapper.toEntity
import com.habitarchitect.domain.model.User
import com.habitarchitect.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

/**
 * Implementation of UserRepository using Room database.
 */
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
    private val listItemDao: ListItemDao,
    private val partnershipDao: PartnershipDao
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toDomain() }
    }

    override fun getUserById(id: String): Flow<User?> {
        return userDao.getUserById(id).map { it?.toDomain() }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return runCatching {
            userDao.insertUser(user.toEntity())
        }
    }

    override suspend fun updateLastActive(userId: String): Result<Unit> {
        return runCatching {
            userDao.updateLastActive(userId, System.currentTimeMillis())
        }
    }

    override suspend fun markOnboardingCompleted(userId: String): Result<Unit> {
        return runCatching {
            userDao.markOnboardingCompleted(userId)
        }
    }

    override suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean): Result<Unit> {
        return runCatching {
            userDao.updateNotificationsEnabled(userId, enabled)
        }
    }

    override suspend fun updateMorningReminderTime(userId: String, time: LocalTime?): Result<Unit> {
        return runCatching {
            userDao.updateMorningReminderTime(userId, time?.toString())
        }
    }

    override suspend fun updateEveningReminderTime(userId: String, time: LocalTime?): Result<Unit> {
        return runCatching {
            userDao.updateEveningReminderTime(userId, time?.toString())
        }
    }

    override suspend fun updateReminderTimes(userId: String, morningTime: String?, eveningTime: String?): Result<Unit> {
        return runCatching {
            morningTime?.let { userDao.updateMorningReminderTime(userId, it) }
            eveningTime?.let { userDao.updateEveningReminderTime(userId, it) }
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return runCatching {
            userDao.deleteUser(userId)
        }
    }

    override suspend fun deleteAllUserData(userId: String): Result<Unit> {
        return runCatching {
            dailyLogDao.deleteLogsForUser(userId)
            habitDao.deleteAllHabitsForUser(userId)
            partnershipDao.deletePartnershipsForUser(userId)
            userDao.deleteUser(userId)
        }
    }
}
