package com.habitarchitect.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class EventContextTest {

    @Test
    fun `create sets correct temporal fields for weekday`() {
        val date = LocalDate.of(2024, 3, 13) // Wednesday
        val time = LocalTime.of(14, 30)

        val context = EventContext.create(
            sessionId = "session-123",
            sessionDurationMs = 5000,
            appVersion = "1.0.0",
            osVersion = "14",
            deviceType = "phone",
            screenDensityBucket = "xxhdpi",
            now = date,
            time = time
        )

        assertEquals(14, context.localHour)
        assertEquals(30, context.localMinute)
        assertEquals(3, context.dayOfWeek) // Wednesday
        assertEquals(13, context.dayOfMonth)
        assertEquals(3, context.month)
        assertEquals(2024, context.year)
        assertEquals(TimeOfDayBucket.AFTERNOON, context.timeOfDayBucket)
        assertFalse(context.isWeekend)
        assertFalse(context.isMonday)
        assertFalse(context.isSunday)
    }

    @Test
    fun `create identifies weekend correctly for Saturday`() {
        val date = LocalDate.of(2024, 3, 16) // Saturday
        val time = LocalTime.of(10, 0)

        val context = EventContext.create(
            sessionId = "session-123",
            sessionDurationMs = 5000,
            appVersion = "1.0.0",
            osVersion = "14",
            deviceType = "phone",
            screenDensityBucket = "xxhdpi",
            now = date,
            time = time
        )

        assertEquals(6, context.dayOfWeek) // Saturday
        assertTrue(context.isWeekend)
        assertFalse(context.isMonday)
        assertFalse(context.isSunday)
    }

    @Test
    fun `create identifies weekend correctly for Sunday`() {
        val date = LocalDate.of(2024, 3, 17) // Sunday
        val time = LocalTime.of(10, 0)

        val context = EventContext.create(
            sessionId = "session-123",
            sessionDurationMs = 5000,
            appVersion = "1.0.0",
            osVersion = "14",
            deviceType = "phone",
            screenDensityBucket = "xxhdpi",
            now = date,
            time = time
        )

        assertEquals(7, context.dayOfWeek) // Sunday
        assertTrue(context.isWeekend)
        assertFalse(context.isMonday)
        assertTrue(context.isSunday)
    }

    @Test
    fun `create identifies Monday correctly`() {
        val date = LocalDate.of(2024, 3, 18) // Monday
        val time = LocalTime.of(9, 0)

        val context = EventContext.create(
            sessionId = "session-123",
            sessionDurationMs = 5000,
            appVersion = "1.0.0",
            osVersion = "14",
            deviceType = "phone",
            screenDensityBucket = "xxhdpi",
            now = date,
            time = time
        )

        assertEquals(1, context.dayOfWeek) // Monday
        assertFalse(context.isWeekend)
        assertTrue(context.isMonday)
        assertFalse(context.isSunday)
    }

    @Test
    fun `create preserves session and device context`() {
        val context = EventContext.create(
            sessionId = "my-session-456",
            sessionDurationMs = 12345,
            appVersion = "2.1.0",
            osVersion = "13",
            deviceType = "tablet",
            screenDensityBucket = "hdpi",
            now = LocalDate.of(2024, 1, 1),
            time = LocalTime.of(12, 0)
        )

        assertEquals("my-session-456", context.sessionId)
        assertEquals(12345, context.sessionDurationMs)
        assertEquals("2.1.0", context.appVersion)
        assertEquals("13", context.osVersion)
        assertEquals("tablet", context.deviceType)
        assertEquals("hdpi", context.screenDensityBucket)
    }

    @Test
    fun `create calculates week of year correctly`() {
        // First week of 2024
        val date1 = LocalDate.of(2024, 1, 3)
        val context1 = EventContext.create(
            sessionId = "s", sessionDurationMs = 0,
            appVersion = "1.0", osVersion = "14",
            deviceType = "phone", screenDensityBucket = "xxhdpi",
            now = date1, time = LocalTime.NOON
        )
        assertEquals(1, context1.weekOfYear)

        // Mid year
        val date2 = LocalDate.of(2024, 6, 15)
        val context2 = EventContext.create(
            sessionId = "s", sessionDurationMs = 0,
            appVersion = "1.0", osVersion = "14",
            deviceType = "phone", screenDensityBucket = "xxhdpi",
            now = date2, time = LocalTime.NOON
        )
        assertEquals(24, context2.weekOfYear)
    }
}
