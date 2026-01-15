package com.habitarchitect.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AnalyticsEventTest {

    private fun createTestContext() = EventContext.create(
        sessionId = "test-session",
        sessionDurationMs = 5000,
        appVersion = "1.0.0",
        osVersion = "14",
        deviceType = "phone",
        screenDensityBucket = "xxhdpi",
        now = LocalDate.of(2024, 3, 13),
        time = LocalTime.of(14, 30)
    )

    @Test
    fun `event creates with correct default values`() {
        val context = createTestContext()
        val event = AnalyticsEvent(
            eventName = AnalyticsEvents.Habit.CREATED,
            anonymousUserId = "anon-123",
            context = context
        )

        assertNotNull(event.eventId)
        assertEquals(AnalyticsEvents.Habit.CREATED, event.eventName)
        assertEquals("anon-123", event.anonymousUserId)
        assertTrue(event.properties.isEmpty())
        assertEquals(ConsentTier.ESSENTIAL, event.requiredConsentTier)
        assertTrue(event.timestamp > 0)
    }

    @Test
    fun `event stores properties correctly`() {
        val context = createTestContext()
        val properties = mapOf(
            EventProperties.HABIT_TYPE to "BUILD",
            EventProperties.HABIT_CATEGORY to "health",
            EventProperties.FROM_TEMPLATE to true
        )

        val event = AnalyticsEvent(
            eventName = AnalyticsEvents.Habit.CREATED,
            anonymousUserId = "anon-123",
            properties = properties,
            context = context
        )

        assertEquals(3, event.properties.size)
        assertEquals("BUILD", event.properties[EventProperties.HABIT_TYPE])
        assertEquals("health", event.properties[EventProperties.HABIT_CATEGORY])
        assertEquals(true, event.properties[EventProperties.FROM_TEMPLATE])
    }

    @Test
    fun `builder creates event correctly`() {
        val context = createTestContext()

        val event = AnalyticsEvent.builder(AnalyticsEvents.Completion.MARKED_SUCCESS)
            .userId("anon-456")
            .property(EventProperties.HABIT_TYPE, "BUILD")
            .property(EventProperties.CURRENT_STREAK, 7)
            .property(EventProperties.SUCCESS_RATE_7D, 0.85)
            .context(context)
            .consentTier(ConsentTier.ENHANCED)
            .build()

        assertEquals(AnalyticsEvents.Completion.MARKED_SUCCESS, event.eventName)
        assertEquals("anon-456", event.anonymousUserId)
        assertEquals("BUILD", event.properties[EventProperties.HABIT_TYPE])
        assertEquals(7, event.properties[EventProperties.CURRENT_STREAK])
        assertEquals(0.85, event.properties[EventProperties.SUCCESS_RATE_7D])
        assertEquals(ConsentTier.ENHANCED, event.requiredConsentTier)
    }

    @Test
    fun `builder accepts properties map`() {
        val context = createTestContext()
        val props = mapOf(
            "key1" to "value1",
            "key2" to 42
        )

        val event = AnalyticsEvent.builder(AnalyticsEvents.Navigation.SCREEN_VIEWED)
            .userId("anon-789")
            .properties(props)
            .property("key3", true)
            .context(context)
            .build()

        assertEquals(3, event.properties.size)
        assertEquals("value1", event.properties["key1"])
        assertEquals(42, event.properties["key2"])
        assertEquals(true, event.properties["key3"])
    }

    @Test(expected = IllegalArgumentException::class)
    fun `builder throws when context not set`() {
        AnalyticsEvent.builder(AnalyticsEvents.Habit.CREATED)
            .userId("anon-123")
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `builder throws when userId not set`() {
        val context = createTestContext()
        AnalyticsEvent.builder(AnalyticsEvents.Habit.CREATED)
            .context(context)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `builder throws when userId is blank`() {
        val context = createTestContext()
        AnalyticsEvent.builder(AnalyticsEvents.Habit.CREATED)
            .userId("")
            .context(context)
            .build()
    }

    @Test
    fun `consent tiers have correct ordering`() {
        assertTrue(ConsentTier.ESSENTIAL.ordinal < ConsentTier.ENHANCED.ordinal)
        assertTrue(ConsentTier.ENHANCED.ordinal < ConsentTier.RESEARCH.ordinal)
        assertTrue(ConsentTier.RESEARCH.ordinal < ConsentTier.PERSONAL.ordinal)
    }

    @Test
    fun `event IDs are unique`() {
        val context = createTestContext()
        val events = (1..100).map {
            AnalyticsEvent(
                eventName = "test",
                anonymousUserId = "user",
                context = context
            )
        }

        val uniqueIds = events.map { it.eventId }.toSet()
        assertEquals(100, uniqueIds.size)
    }
}
