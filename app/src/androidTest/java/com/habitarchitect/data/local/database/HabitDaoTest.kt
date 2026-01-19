package com.habitarchitect.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.entity.HabitEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Database integration tests for HabitDao.
 * Tests CRUD operations and queries.
 */
@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var database: HabitArchitectDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HabitArchitectDatabase::class.java
        ).allowMainThreadQueries().build()

        habitDao = database.habitDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertHabit_andRetrieve_returnsHabit() = runTest {
        val habit = createTestHabit("1", "Test Habit")

        habitDao.insertHabit(habit)

        habitDao.getHabitById("1").test {
            val result = awaitItem()
            assertEquals("Test Habit", result?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getActiveHabits_returnsOnlyActiveHabits() = runTest {
        val activeHabit = createTestHabit("1", "Active Habit", isActive = true)
        val inactiveHabit = createTestHabit("2", "Inactive Habit", isActive = false)

        habitDao.insertHabit(activeHabit)
        habitDao.insertHabit(inactiveHabit)

        habitDao.getActiveHabits("user1").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Active Habit", result[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteHabit_removesHabit() = runTest {
        val habit = createTestHabit("1", "To Delete")
        habitDao.insertHabit(habit)

        habitDao.deleteHabit("1")

        habitDao.getHabitById("1").test {
            val result = awaitItem()
            assertNull(result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateStreak_updatesCorrectly() = runTest {
        val habit = createTestHabit("1", "Streak Habit", currentStreak = 0)
        habitDao.insertHabit(habit)

        habitDao.updateStreak("1", newStreak = 5, newLongestStreak = 5)

        habitDao.getHabitById("1").test {
            val result = awaitItem()
            assertEquals(5, result?.currentStreak)
            assertEquals(5, result?.longestStreak)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getHabitsByType_filtersCorrectly() = runTest {
        val buildHabit = createTestHabit("1", "Build Habit", type = "BUILD")
        val breakHabit = createTestHabit("2", "Break Habit", type = "BREAK")

        habitDao.insertHabit(buildHabit)
        habitDao.insertHabit(breakHabit)

        habitDao.getHabitsByType("user1", "BUILD").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Build Habit", result[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestHabit(
        id: String,
        name: String,
        userId: String = "user1",
        type: String = "BUILD",
        isActive: Boolean = true,
        currentStreak: Int = 0
    ) = HabitEntity(
        id = id,
        userId = userId,
        name = name,
        type = type,
        category = "test",
        iconEmoji = "✅",
        frequency = "DAILY",
        currentStreak = currentStreak,
        longestStreak = 0,
        isActive = isActive,
        isShared = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        location = null,
        goal = null,
        paperClipCount = 0,
        paperClipGoal = 30,
        isReminderEnabled = false
    )
}
