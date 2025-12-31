package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for habit operations.
 */
interface HabitRepository {

    fun getActiveHabits(userId: String): Flow<List<Habit>>

    fun getHabitById(id: String): Flow<Habit?>

    suspend fun getHabitByIdOnce(id: String): Habit?

    fun getHabitsByType(userId: String, type: HabitType): Flow<List<Habit>>

    fun getSharedHabits(userId: String): Flow<List<Habit>>

    suspend fun createHabit(habit: Habit): Result<String>

    suspend fun updateHabit(habit: Habit): Result<Unit>

    suspend fun incrementStreak(habitId: String): Int

    suspend fun resetStreak(habitId: String): Result<Unit>

    suspend fun archiveHabit(habitId: String): Result<Unit>

    suspend fun updateSharingStatus(habitId: String, shared: Boolean): Result<Unit>

    suspend fun deleteHabit(habitId: String): Result<Unit>

    suspend fun getActiveHabitCount(userId: String): Int
}
