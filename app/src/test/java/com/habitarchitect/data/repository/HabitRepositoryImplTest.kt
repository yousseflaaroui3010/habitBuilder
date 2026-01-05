package com.habitarchitect.data.repository

import app.cash.turbine.test
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.entity.HabitEntity
import com.habitarchitect.domain.model.Frequency
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HabitRepositoryImplTest {

    private lateinit var habitDao: HabitDao
    private lateinit var dailyLogDao: DailyLogDao
    private lateinit var listItemDao: ListItemDao
    private lateinit var repository: HabitRepositoryImpl

    private val testUserId = "test-user-123"
    private val testHabitId = "habit-123"

    @Before
    fun setup() {
        habitDao = mockk(relaxed = true)
        dailyLogDao = mockk(relaxed = true)
        listItemDao = mockk(relaxed = true)
        repository = HabitRepositoryImpl(habitDao, dailyLogDao, listItemDao)
    }

    @Test
    fun `getActiveHabits returns mapped habits from dao`() = runTest {
        val entity = createTestHabitEntity()
        every { habitDao.getActiveHabits(testUserId) } returns flowOf(listOf(entity))

        repository.getActiveHabits(testUserId).test {
            val habits = awaitItem()
            assertEquals(1, habits.size)
            assertEquals(testHabitId, habits[0].id)
            assertEquals("Test Habit", habits[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHabitById returns mapped habit when found`() = runTest {
        val entity = createTestHabitEntity()
        every { habitDao.getHabitById(testHabitId) } returns flowOf(entity)

        repository.getHabitById(testHabitId).test {
            val habit = awaitItem()
            assertNotNull(habit)
            assertEquals(testHabitId, habit?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHabitById returns null when not found`() = runTest {
        every { habitDao.getHabitById(testHabitId) } returns flowOf(null)

        repository.getHabitById(testHabitId).test {
            val habit = awaitItem()
            assertEquals(null, habit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createHabit inserts habit and returns id`() = runTest {
        val habit = createTestHabit()
        coEvery { habitDao.insertHabit(any()) } returns Unit

        val result = repository.createHabit(habit)

        assertTrue(result.isSuccess)
        assertEquals(testHabitId, result.getOrNull())
        coVerify { habitDao.insertHabit(any()) }
    }

    @Test
    fun `createHabit generates id when blank`() = runTest {
        val habit = createTestHabit().copy(id = "")
        coEvery { habitDao.insertHabit(any()) } returns Unit

        val result = repository.createHabit(habit)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertTrue(result.getOrNull()!!.isNotBlank())
    }

    @Test
    fun `updateHabit updates habit in dao`() = runTest {
        val habit = createTestHabit()
        coEvery { habitDao.updateHabit(any()) } returns Unit

        val result = repository.updateHabit(habit)

        assertTrue(result.isSuccess)
        coVerify { habitDao.updateHabit(any()) }
    }

    @Test
    fun `incrementStreak calls dao and returns new streak`() = runTest {
        val entity = createTestHabitEntity().copy(currentStreak = 5)
        coEvery { habitDao.incrementStreak(testHabitId, any()) } returns Unit
        coEvery { habitDao.getHabitByIdOnce(testHabitId) } returns entity

        val result = repository.incrementStreak(testHabitId)

        assertEquals(5, result)
        coVerify { habitDao.incrementStreak(testHabitId, any()) }
    }

    @Test
    fun `resetStreak calls dao`() = runTest {
        coEvery { habitDao.resetStreak(testHabitId, any()) } returns Unit

        val result = repository.resetStreak(testHabitId)

        assertTrue(result.isSuccess)
        coVerify { habitDao.resetStreak(testHabitId, any()) }
    }

    @Test
    fun `archiveHabit calls dao`() = runTest {
        coEvery { habitDao.archiveHabit(testHabitId, any()) } returns Unit

        val result = repository.archiveHabit(testHabitId)

        assertTrue(result.isSuccess)
        coVerify { habitDao.archiveHabit(testHabitId, any()) }
    }

    @Test
    fun `deleteHabit deletes related data and habit`() = runTest {
        coEvery { listItemDao.deleteAllForHabit(testHabitId) } returns Unit
        coEvery { dailyLogDao.deleteLogsForHabit(testHabitId) } returns Unit
        coEvery { habitDao.deleteHabit(testHabitId) } returns Unit

        val result = repository.deleteHabit(testHabitId)

        assertTrue(result.isSuccess)
        coVerify { listItemDao.deleteAllForHabit(testHabitId) }
        coVerify { dailyLogDao.deleteLogsForHabit(testHabitId) }
        coVerify { habitDao.deleteHabit(testHabitId) }
    }

    @Test
    fun `getActiveHabitCount returns count from dao`() = runTest {
        coEvery { habitDao.getActiveHabitCount(testUserId) } returns 5

        val result = repository.getActiveHabitCount(testUserId)

        assertEquals(5, result)
    }

    @Test
    fun `getSharedHabits returns only shared habits`() = runTest {
        val entity = createTestHabitEntity().copy(isSharedWithPartner = true)
        every { habitDao.getSharedHabits(testUserId) } returns flowOf(listOf(entity))

        repository.getSharedHabits(testUserId).test {
            val habits = awaitItem()
            assertEquals(1, habits.size)
            assertTrue(habits[0].isSharedWithPartner)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateSharingStatus calls dao`() = runTest {
        coEvery { habitDao.updateSharingStatus(testHabitId, true, any()) } returns Unit

        val result = repository.updateSharingStatus(testHabitId, true)

        assertTrue(result.isSuccess)
        coVerify { habitDao.updateSharingStatus(testHabitId, true, any()) }
    }

    private fun createTestHabitEntity() = HabitEntity(
        id = testHabitId,
        userId = testUserId,
        name = "Test Habit",
        type = "BUILD",
        category = "Health",
        templateId = null,
        iconEmoji = "🏃",
        triggerTime = null,
        triggerContext = null,
        frequency = "DAILY",
        activeDays = null,
        minimumVersion = "5 minutes",
        stackAnchor = null,
        reward = null,
        frictionStrategies = null,
        currentStreak = 0,
        longestStreak = 0,
        totalSuccessDays = 0,
        totalFailureDays = 0,
        isSharedWithPartner = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        isArchived = false
    )

    private fun createTestHabit() = Habit(
        id = testHabitId,
        userId = testUserId,
        name = "Test Habit",
        type = HabitType.BUILD,
        category = "Health",
        templateId = null,
        iconEmoji = "🏃",
        triggerTime = null,
        triggerContext = null,
        frequency = Frequency.DAILY,
        activeDays = listOf(1, 2, 3, 4, 5, 6, 7),
        minimumVersion = "5 minutes",
        stackAnchor = null,
        reward = null,
        frictionStrategies = emptyList(),
        currentStreak = 0,
        longestStreak = 0,
        totalSuccessDays = 0,
        totalFailureDays = 0,
        isSharedWithPartner = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        isArchived = false
    )
}
