package com.habitarchitect.data.analytics

import com.google.gson.Gson
import com.habitarchitect.data.local.database.dao.AnalyticsDao
import com.habitarchitect.data.local.database.entity.AnalyticsEventEntity
import com.habitarchitect.domain.analytics.AnalyticsEvent
import com.habitarchitect.domain.analytics.ConsentTier
import com.habitarchitect.domain.analytics.EventContext
import com.habitarchitect.domain.analytics.TimeOfDayBucket
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AnalyticsLocalStorageTest {

    private lateinit var analyticsDao: AnalyticsDao
    private lateinit var gson: Gson
    private lateinit var storage: AnalyticsLocalStorage

    @Before
    fun setup() {
        analyticsDao = mockk(relaxed = true)
        gson = Gson()
        storage = AnalyticsLocalStorage(analyticsDao, gson)
    }

    private fun createTestContext() = EventContext(
        localHour = 14,
        localMinute = 30,
        dayOfWeek = 3,
        dayOfMonth = 13,
        weekOfYear = 11,
        month = 3,
        year = 2024,
        timeOfDayBucket = TimeOfDayBucket.AFTERNOON,
        isWeekend = false,
        isMonday = false,
        isSunday = false,
        sessionId = "test-session",
        sessionDurationMs = 5000,
        appVersion = "1.0.0",
        osVersion = "14",
        deviceType = "phone",
        screenDensityBucket = "xxhdpi"
    )

    private fun createTestEvent(
        eventName: String = "test_event",
        properties: Map<String, Any> = emptyMap(),
        consentTier: ConsentTier = ConsentTier.ESSENTIAL
    ) = AnalyticsEvent(
        eventId = "event-123",
        eventName = eventName,
        timestamp = 1710000000000,
        anonymousUserId = "anon-user-456",
        properties = properties,
        context = createTestContext(),
        requiredConsentTier = consentTier
    )

    @Test
    fun `store converts event to entity and inserts`() = runTest {
        val event = createTestEvent(
            eventName = "habit_created",
            properties = mapOf("habit_type" to "BUILD", "category" to "health")
        )

        val entitySlot = slot<AnalyticsEventEntity>()
        coEvery { analyticsDao.insertEvent(capture(entitySlot)) } returns Unit

        storage.store(event)

        coVerify { analyticsDao.insertEvent(any()) }
        val captured = entitySlot.captured

        assertEquals("event-123", captured.eventId)
        assertEquals("habit_created", captured.eventName)
        assertEquals(1710000000000, captured.timestamp)
        assertEquals("anon-user-456", captured.anonymousUserId)
        assertEquals(ConsentTier.ESSENTIAL.ordinal, captured.consentTier)
        assertEquals(false, captured.synced)
        assertEquals(0, captured.syncAttempts)

        // Verify properties are serialized
        assertNotNull(captured.propertiesJson)
        assert(captured.propertiesJson.contains("BUILD"))
        assert(captured.propertiesJson.contains("health"))
    }

    @Test
    fun `storeBatch inserts multiple events`() = runTest {
        val events = listOf(
            createTestEvent(eventName = "event_1"),
            createTestEvent(eventName = "event_2"),
            createTestEvent(eventName = "event_3")
        )

        val entitiesSlot = slot<List<AnalyticsEventEntity>>()
        coEvery { analyticsDao.insertEvents(capture(entitiesSlot)) } returns Unit

        storage.storeBatch(events)

        coVerify { analyticsDao.insertEvents(any()) }
        assertEquals(3, entitiesSlot.captured.size)
    }

    @Test
    fun `getUnsyncedEvents returns domain events`() = runTest {
        val entity = AnalyticsEventEntity(
            eventId = "entity-789",
            eventName = "test_event",
            timestamp = 1710000000000,
            anonymousUserId = "anon-123",
            propertiesJson = """{"key": "value"}""",
            contextJson = gson.toJson(createTestContext()),
            consentTier = ConsentTier.ENHANCED.ordinal,
            synced = false,
            syncAttempts = 0,
            lastSyncAttempt = null
        )

        coEvery { analyticsDao.getUnsyncedEvents(any(), any()) } returns listOf(entity)

        val events = storage.getUnsyncedEvents()

        assertEquals(1, events.size)
        assertEquals("entity-789", events[0].eventId)
        assertEquals("test_event", events[0].eventName)
        assertEquals(ConsentTier.ENHANCED, events[0].requiredConsentTier)
        assertEquals("value", events[0].properties["key"])
    }

    @Test
    fun `getPendingCount returns dao count`() = runTest {
        coEvery { analyticsDao.getUnsyncedCount() } returns 42

        val count = storage.getPendingCount()

        assertEquals(42, count)
        coVerify { analyticsDao.getUnsyncedCount() }
    }

    @Test
    fun `markAsSynced delegates to dao`() = runTest {
        val eventIds = listOf("id1", "id2", "id3")

        storage.markAsSynced(eventIds)

        coVerify { analyticsDao.markAsSynced(eventIds) }
    }

    @Test
    fun `recordSyncFailure increments attempts`() = runTest {
        val eventIds = listOf("id1", "id2")

        storage.recordSyncFailure(eventIds)

        coVerify { analyticsDao.incrementSyncAttempts(eventIds, any()) }
    }

    @Test
    fun `cleanupSyncedEvents deletes old events`() = runTest {
        storage.cleanupSyncedEvents()

        coVerify { analyticsDao.deleteSyncedEventsBefore(any()) }
    }

    @Test
    fun `handleConsentDowngrade deletes higher tier events`() = runTest {
        storage.handleConsentDowngrade(ConsentTier.ENHANCED)

        coVerify { analyticsDao.deleteEventsAboveConsentTier(ConsentTier.ENHANCED.ordinal) }
    }

    @Test
    fun `deleteAllData clears all events`() = runTest {
        storage.deleteAllData()

        coVerify { analyticsDao.deleteAllEvents() }
    }

    @Test
    fun `event with PERSONAL consent tier has correct ordinal`() = runTest {
        val event = createTestEvent(consentTier = ConsentTier.PERSONAL)
        val entitySlot = slot<AnalyticsEventEntity>()
        coEvery { analyticsDao.insertEvent(capture(entitySlot)) } returns Unit

        storage.store(event)

        assertEquals(ConsentTier.PERSONAL.ordinal, entitySlot.captured.consentTier)
    }
}
