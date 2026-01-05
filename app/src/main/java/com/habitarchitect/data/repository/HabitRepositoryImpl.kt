package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.data.mapper.toEntity
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of HabitRepository using Room database.
 */
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
    private val listItemDao: ListItemDao
) : HabitRepository {

    override fun getActiveHabits(userId: String): Flow<List<Habit>> {
        return habitDao.getActiveHabits(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHabitById(id: String): Flow<Habit?> {
        return habitDao.getHabitById(id).map { it?.toDomain() }
    }

    override suspend fun getHabitByIdOnce(id: String): Habit? {
        return habitDao.getHabitByIdOnce(id)?.toDomain()
    }

    override fun getHabitsByType(userId: String, type: HabitType): Flow<List<Habit>> {
        return habitDao.getHabitsByType(userId, type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSharedHabits(userId: String): Flow<List<Habit>> {
        return habitDao.getSharedHabits(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createHabit(habit: Habit): Result<String> {
        return runCatching {
            val id = habit.id.ifBlank { UUID.randomUUID().toString() }
            val habitWithId = habit.copy(id = id)
            habitDao.insertHabit(habitWithId.toEntity())
            id
        }
    }

    override suspend fun updateHabit(habit: Habit): Result<Unit> {
        return runCatching {
            habitDao.updateHabit(habit.copy(updatedAt = System.currentTimeMillis()).toEntity())
        }
    }

    override suspend fun incrementStreak(habitId: String): Int {
        habitDao.incrementStreak(habitId, System.currentTimeMillis())
        return habitDao.getHabitByIdOnce(habitId)?.currentStreak ?: 0
    }

    override suspend fun resetStreak(habitId: String): Result<Unit> {
        return runCatching {
            habitDao.resetStreak(habitId, System.currentTimeMillis())
        }
    }

    override suspend fun archiveHabit(habitId: String): Result<Unit> {
        return runCatching {
            habitDao.archiveHabit(habitId, System.currentTimeMillis())
        }
    }

    override suspend fun updateSharingStatus(habitId: String, shared: Boolean): Result<Unit> {
        return runCatching {
            habitDao.updateSharingStatus(habitId, shared, System.currentTimeMillis())
        }
    }

    override suspend fun updateFrictionStrategies(habitId: String, strategies: List<String>, implemented: List<String>): Result<Unit> {
        return runCatching {
            val strategiesString = strategies.joinToString("|")
            val implementedString = implemented.joinToString("|")
            habitDao.updateFrictionStrategies(habitId, strategiesString, implementedString, System.currentTimeMillis())
        }
    }

    override suspend fun updateHabitReward(habitId: String, reward: String): Result<Unit> {
        return runCatching {
            habitDao.updateReward(habitId, reward, System.currentTimeMillis())
        }
    }

    override suspend fun deleteHabit(habitId: String): Result<Unit> {
        return runCatching {
            listItemDao.deleteAllForHabit(habitId)
            dailyLogDao.deleteLogsForHabit(habitId)
            habitDao.deleteHabit(habitId)
        }
    }

    override suspend fun getActiveHabitCount(userId: String): Int {
        return habitDao.getActiveHabitCount(userId)
    }

    override suspend fun reorderHabits(habitIds: List<String>): Result<Unit> {
        return runCatching {
            val timestamp = System.currentTimeMillis()
            habitIds.forEachIndexed { index, habitId ->
                habitDao.updateOrderIndex(habitId, index, timestamp)
            }
        }
    }
}
