package com.habitarchitect.domain.analytics

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Contextual information automatically attached to every analytics event.
 */
data class EventContext(
    // Temporal context
    val localHour: Int,
    val localMinute: Int,
    val dayOfWeek: Int,                    // 1 (Monday) - 7 (Sunday)
    val dayOfMonth: Int,
    val weekOfYear: Int,
    val month: Int,
    val year: Int,
    val timeOfDayBucket: TimeOfDayBucket,
    val isWeekend: Boolean,
    val isMonday: Boolean,
    val isSunday: Boolean,

    // Session context
    val sessionId: String,
    val sessionDurationMs: Long,

    // Device context
    val appVersion: String,
    val osVersion: String,
    val deviceType: String,                // phone, tablet
    val screenDensityBucket: String        // mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
) {
    companion object {
        fun create(
            sessionId: String,
            sessionDurationMs: Long,
            appVersion: String,
            osVersion: String,
            deviceType: String,
            screenDensityBucket: String,
            now: LocalDate = LocalDate.now(),
            time: LocalTime = LocalTime.now()
        ): EventContext {
            val dayOfWeek = now.dayOfWeek.value
            return EventContext(
                localHour = time.hour,
                localMinute = time.minute,
                dayOfWeek = dayOfWeek,
                dayOfMonth = now.dayOfMonth,
                weekOfYear = now.get(java.time.temporal.WeekFields.ISO.weekOfYear()),
                month = now.monthValue,
                year = now.year,
                timeOfDayBucket = TimeOfDayBucket.fromHour(time.hour),
                isWeekend = dayOfWeek == DayOfWeek.SATURDAY.value || dayOfWeek == DayOfWeek.SUNDAY.value,
                isMonday = dayOfWeek == DayOfWeek.MONDAY.value,
                isSunday = dayOfWeek == DayOfWeek.SUNDAY.value,
                sessionId = sessionId,
                sessionDurationMs = sessionDurationMs,
                appVersion = appVersion,
                osVersion = osVersion,
                deviceType = deviceType,
                screenDensityBucket = screenDensityBucket
            )
        }
    }
}
