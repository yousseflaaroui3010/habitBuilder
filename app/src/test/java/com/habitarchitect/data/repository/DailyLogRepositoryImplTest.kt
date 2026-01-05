package com.habitarchitect.data.repository

import app.cash.turbine.test
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.entity.DailyLogEntity
import com.habitarchitect.domain.model.DailyStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DailyLogRepositoryImplTest {

    private lateinit var dailyLogDao: DailyLogDao
    private lateinit var repository: DailyLogRepositoryImpl

    private val testHabitId = "habit-123"
    private val testDate = LocalDate.of(2026, 1, 3)

    @Before
    fun setup() {
        dailyLogDao = mockk(relaxed = true)
        repository = DailyLogRepositoryImpl(dailyLogDao)
    }

    @Test
    fun `getLogsForRange returns mapped logs`() = runTest {
        val entity = createTestLogEntity()
        every {
            dailyLogDao.getLogsForRange(testHabitId, testDate.toString(), testDate.toString())
        } returns flowOf(listOf(entity))

        repository.getLogsForRange(testHabitId, testDate, testDate).test {
            val logs = awaitItem()
            assertEquals(1, logs.size)
            assertEquals(testHabitId, logs[0].habitId)
            assertEquals(DailyStatus.SUCCESS, logs[0].status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getLogForDate returns log when found`() = runTest {
        val entity = createTestLogEntity()
        coEvery { dailyLogDao.getLogForDate(testHabitId, testDate.toString()) } returns entity

        val result = repository.getLogForDate(testHabitId, testDate)

        assertNotNull(result)
        assertEquals(testHabitId, result?.habitId)
        assertEquals(DailyStatus.SUCCESS, result?.status)
    }

    @Test
    fun `getLogForDate returns null when not found`() = runTest {
        coEvery { dailyLogDao.getLogForDate(testHabitId, testDate.toString()) } returns null

        val result = repository.getLogForDate(testHabitId, testDate)

        assertNull(result)
    }

    @Test
    fun `markStatus creates log entity with correct values`() = runTest {
        val entitySlot = slot<DailyLogEntity>()
        coEvery { dailyLogDao.upsertLog(capture(entitySlot)) } returns Unit

        val result = repository.markStatus(testHabitId, testDate, DailyStatus.SUCCESS, "Test note")

        assertTrue(result.isSuccess)
        assertEquals(testHabitId, entitySlot.captured.habitId)
        assertEquals(testDate.toString(), entitySlot.captured.date)
        assertEquals("SUCCESS", entitySlot.captured.status)
        assertEquals("Test note", entitySlot.captured.note)
    }

    @Test
    fun `markStatus with FAILURE status`() = runTest {
        val entitySlot = slot<DailyLogEntity>()
        coEvery { dailyLogDao.upsertLog(capture(entitySlot)) } returns Unit

        val result = repository.markStatus(testHabitId, testDate, DailyStatus.FAILURE, null)

        assertTrue(result.isSuccess)
        assertEquals("FAILURE", entitySlot.captured.status)
        assertNull(entitySlot.captured.note)
    }

    @Test
    fun `getLogsForHabitsOnDate returns logs for multiple habits`() = runTest {
        val habitIds = listOf("habit-1", "habit-2")
        val entities = listOf(
            createTestLogEntity().copy(habitId = "habit-1"),
            createTestLogEntity().copy(habitId = "habit-2")
        )
        coEvery { dailyLogDao.getLogsForHabitsOnDate(habitIds, testDate.toString()) } returns entities

        val result = repository.getLogsForHabitsOnDate(habitIds, testDate)

        assertEquals(2, result.size)
        assertEquals("habit-1", result[0].habitId)
        assertEquals("habit-2", result[1].habitId)
    }

    @Test
    fun `getSuccessCount returns count from dao`() = runTest {
        coEvery { dailyLogDao.getSuccessCount(testHabitId) } returns 10

        val result = repository.getSuccessCount(testHabitId)

        assertEquals(10, result)
    }

    @Test
    fun `getFailureCount returns count from dao`() = runTest {
        coEvery { dailyLogDao.getFailureCount(testHabitId) } returns 3

        val result = repository.getFailureCount(testHabitId)

        assertEquals(3, result)
    }

    @Test
    fun `getRecentLogs returns limited logs`() = runTest {
        val entities = listOf(
            createTestLogEntity(),
            createTestLogEntity().copy(date = testDate.minusDays(1).toString())
        )
        coEvery { dailyLogDao.getRecentLogs(testHabitId, 5) } returns entities

        val result = repository.getRecentLogs(testHabitId, 5)

        assertEquals(2, result.size)
        coVerify { dailyLogDao.getRecentLogs(testHabitId, 5) }
    }

    @Test
    fun `observeLogForDate emits updates`() = runTest {
        val entity = createTestLogEntity()
        every { dailyLogDao.observeLogForDate(testHabitId, testDate.toString()) } returns flowOf(entity)

        repository.observeLogForDate(testHabitId, testDate).test {
            val log = awaitItem()
            assertNotNull(log)
            assertEquals(DailyStatus.SUCCESS, log?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestLogEntity() = DailyLogEntity(
        habitId = testHabitId,
        date = testDate.toString(),
        status = "SUCCESS",
        markedAt = System.currentTimeMillis(),
        note = null
    )
}
