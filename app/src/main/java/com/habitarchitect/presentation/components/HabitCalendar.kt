package com.habitarchitect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitarchitect.domain.model.DailyLog
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.presentation.theme.CalendarFailure
import com.habitarchitect.presentation.theme.CalendarSuccess
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Calendar component for displaying habit streak visualization.
 * Shows a month grid with colored indicators for success/failure days.
 */
@Composable
fun HabitCalendar(
    currentMonth: YearMonth,
    dailyLogs: List<DailyLog>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val logsByDate = dailyLogs.associateBy { it.date }

    Column(modifier = modifier.fillMaxWidth()) {
        // Month navigation header
        MonthHeader(
            currentMonth = currentMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )

        // Day of week labels
        DayOfWeekLabels()

        // Calendar grid
        CalendarGrid(
            currentMonth = currentMonth,
            today = today,
            logsByDate = logsByDate,
            onDayClick = onDayClick
        )
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.Filled.ChevronLeft,
                contentDescription = "Previous month"
            )
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Next month"
            )
        }
    }
}

@Composable
private fun DayOfWeekLabels() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val daysOfWeek = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        daysOfWeek.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    today: LocalDate,
    logsByDate: Map<LocalDate, DailyLog>,
    onDayClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()

    // Calculate offset for first day (0 = Sunday, 6 = Saturday)
    val firstDayOffset = (firstDayOfMonth.dayOfWeek.value % 7)

    // Generate all days to display (including empty cells for alignment)
    val totalDays = firstDayOffset + lastDayOfMonth.dayOfMonth
    val weeks = (totalDays + 6) / 7

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (week in 0 until weeks) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    val dayIndex = week * 7 + dayOfWeek - firstDayOffset + 1

                    if (dayIndex in 1..lastDayOfMonth.dayOfMonth) {
                        val date = currentMonth.atDay(dayIndex)
                        val log = logsByDate[date]

                        CalendarDay(
                            day = dayIndex,
                            date = date,
                            isToday = date == today,
                            isFuture = date.isAfter(today),
                            status = log?.status,
                            onClick = { onDayClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty cell
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    date: LocalDate,
    isToday: Boolean,
    isFuture: Boolean,
    status: DailyStatus?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isFuture -> Color.Transparent
        status == DailyStatus.SUCCESS -> CalendarSuccess
        status == DailyStatus.FAILURE -> CalendarFailure
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    val textColor = when {
        isFuture -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        status == DailyStatus.SUCCESS -> Color.White
        status == DailyStatus.FAILURE -> Color.White
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (!isFuture) Modifier.clickable { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Compact calendar legend showing what colors mean.
 */
@Composable
fun CalendarLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = CalendarSuccess, label = "Success")
        Box(modifier = Modifier.size(24.dp))
        LegendItem(color = CalendarFailure, label = "Missed")
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
