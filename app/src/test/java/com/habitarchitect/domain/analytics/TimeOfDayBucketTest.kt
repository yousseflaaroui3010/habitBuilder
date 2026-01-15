package com.habitarchitect.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class TimeOfDayBucketTest {

    @Test
    fun `fromHour returns LATE_NIGHT for hours 0-4`() {
        assertEquals(TimeOfDayBucket.LATE_NIGHT, TimeOfDayBucket.fromHour(0))
        assertEquals(TimeOfDayBucket.LATE_NIGHT, TimeOfDayBucket.fromHour(2))
        assertEquals(TimeOfDayBucket.LATE_NIGHT, TimeOfDayBucket.fromHour(4))
    }

    @Test
    fun `fromHour returns EARLY_MORNING for hours 5-7`() {
        assertEquals(TimeOfDayBucket.EARLY_MORNING, TimeOfDayBucket.fromHour(5))
        assertEquals(TimeOfDayBucket.EARLY_MORNING, TimeOfDayBucket.fromHour(6))
        assertEquals(TimeOfDayBucket.EARLY_MORNING, TimeOfDayBucket.fromHour(7))
    }

    @Test
    fun `fromHour returns MORNING for hours 8-11`() {
        assertEquals(TimeOfDayBucket.MORNING, TimeOfDayBucket.fromHour(8))
        assertEquals(TimeOfDayBucket.MORNING, TimeOfDayBucket.fromHour(10))
        assertEquals(TimeOfDayBucket.MORNING, TimeOfDayBucket.fromHour(11))
    }

    @Test
    fun `fromHour returns AFTERNOON for hours 12-16`() {
        assertEquals(TimeOfDayBucket.AFTERNOON, TimeOfDayBucket.fromHour(12))
        assertEquals(TimeOfDayBucket.AFTERNOON, TimeOfDayBucket.fromHour(14))
        assertEquals(TimeOfDayBucket.AFTERNOON, TimeOfDayBucket.fromHour(16))
    }

    @Test
    fun `fromHour returns EVENING for hours 17-20`() {
        assertEquals(TimeOfDayBucket.EVENING, TimeOfDayBucket.fromHour(17))
        assertEquals(TimeOfDayBucket.EVENING, TimeOfDayBucket.fromHour(19))
        assertEquals(TimeOfDayBucket.EVENING, TimeOfDayBucket.fromHour(20))
    }

    @Test
    fun `fromHour returns NIGHT for hours 21-23`() {
        assertEquals(TimeOfDayBucket.NIGHT, TimeOfDayBucket.fromHour(21))
        assertEquals(TimeOfDayBucket.NIGHT, TimeOfDayBucket.fromHour(22))
        assertEquals(TimeOfDayBucket.NIGHT, TimeOfDayBucket.fromHour(23))
    }

    @Test
    fun `fromLocalTime maps correctly`() {
        assertEquals(TimeOfDayBucket.LATE_NIGHT, TimeOfDayBucket.fromLocalTime(LocalTime.of(3, 30)))
        assertEquals(TimeOfDayBucket.MORNING, TimeOfDayBucket.fromLocalTime(LocalTime.of(9, 15)))
        assertEquals(TimeOfDayBucket.EVENING, TimeOfDayBucket.fromLocalTime(LocalTime.of(18, 45)))
    }
}
