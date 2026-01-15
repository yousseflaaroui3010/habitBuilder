package com.habitarchitect.domain.analytics

import java.time.LocalTime

/**
 * Time of day buckets for behavioral analysis.
 */
enum class TimeOfDayBucket {
    LATE_NIGHT,     // 00:00 - 04:59
    EARLY_MORNING,  // 05:00 - 07:59
    MORNING,        // 08:00 - 11:59
    AFTERNOON,      // 12:00 - 16:59
    EVENING,        // 17:00 - 20:59
    NIGHT;          // 21:00 - 23:59

    companion object {
        fun fromHour(hour: Int): TimeOfDayBucket = when (hour) {
            in 0..4 -> LATE_NIGHT
            in 5..7 -> EARLY_MORNING
            in 8..11 -> MORNING
            in 12..16 -> AFTERNOON
            in 17..20 -> EVENING
            else -> NIGHT
        }

        fun fromLocalTime(time: LocalTime): TimeOfDayBucket = fromHour(time.hour)

        fun now(): TimeOfDayBucket = fromLocalTime(LocalTime.now())
    }
}
