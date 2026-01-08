package com.habitarchitect.presentation.screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.HabitType
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit,
    showBackButton: Boolean = true,
    onNavigateToWeeklyReflection: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            if (showBackButton) {
                TopAppBar(
                    title = { Text("Progress Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.totalHabits == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No habits yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add some habits to track your progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title when shown from bottom nav
                if (!showBackButton) {
                    item {
                        Text(
                            text = "Your Progress",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Summary cards
                item {
                    SummarySection(uiState)
                }

                // Success rate
                item {
                    SuccessRateCard(uiState.overallSuccessRate)
                }

                // Charts section - side by side, larger and clickable
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Pie Chart for habit types
                        HabitTypePieChart(
                            buildCount = uiState.buildHabits,
                            breakCount = uiState.breakHabits,
                            modifier = Modifier.weight(1f),
                            onClick = { /* TODO: Navigate to habits list */ }
                        )

                        // Success/Failure pie chart
                        SuccessFailurePieChart(
                            successDays = uiState.habitProgress.sumOf { it.successDays },
                            failureDays = uiState.habitProgress.sumOf { it.totalDays - it.successDays },
                            modifier = Modifier.weight(1f),
                            onClick = { /* TODO: Show detailed stats */ }
                        )
                    }
                }

                // Weekly bar chart
                item {
                    WeeklyProgressBarChart(
                        habitProgress = uiState.habitProgress
                    )
                }

                // Weekly Reflection Card
                item {
                    WeeklyReflectionCard(
                        reflection = uiState.weeklyReflection,
                        onClick = onNavigateToWeeklyReflection
                    )
                }
            }
        }
    }
}

@Composable
private fun SummarySection(uiState: DashboardUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = uiState.totalHabits.toString(),
            label = "Total Habits",
            emoji = "📋"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = uiState.totalStreakDays.toString(),
            label = "Total Streak",
            emoji = "🔥"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = uiState.longestStreak.toString(),
            label = "Best Streak",
            emoji = "🏆"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    emoji: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SuccessRateCard(successRate: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Overall Success Rate",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = successRate,
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(successRate * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun HabitProgressCard(progress: HabitProgress) {
    val isBreakHabit = progress.habit.type == HabitType.BREAK

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progress.habit.iconEmoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = progress.habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isBreakHabit) "BREAK" else "BUILD",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isBreakHabit) Color(0xFFE57373) else Color(0xFF81C784)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "🔥 ${progress.habit.currentStreak}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Last 7 days visualization
            Text(
                text = "Last 7 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val today = LocalDate.now()
                val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }

                progress.last7Days.forEachIndexed { index, status ->
                    val date = last7Days.getOrNull(index)
                    val dayLabel = date?.dayOfWeek?.getDisplayName(TextStyle.NARROW, Locale.getDefault()) ?: ""
                    DayIndicator(
                        dayLabel = dayLabel,
                        status = status
                    )
                }
            }

            if (progress.totalDays > 0) {
                Spacer(modifier = Modifier.height(12.dp))

                val habitSuccessRate = progress.successDays.toFloat() / progress.totalDays

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = habitSuccessRate,
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isBreakHabit) Color(0xFFE57373) else Color(0xFF81C784),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(habitSuccessRate * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DayIndicator(dayLabel: String, status: Boolean?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when (status) {
                        true -> Color(0xFF81C784) // Green for success
                        false -> Color(0xFFE57373) // Red for failure
                        null -> MaterialTheme.colorScheme.surfaceVariant // Gray for no data
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                true -> Text("✓", color = Color.White, fontSize = 14.sp)
                false -> Text("✗", color = Color.White, fontSize = 14.sp)
                null -> {}
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dayLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeeklyReflectionCard(
    reflection: WeeklyReflectionSummary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9C27B0).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weekly Reflection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                    Text(
                        text = if (reflection.hasReflection) "Tap to edit" else "Reflect on your progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0)
                )
            }

            // Show summary if reflection exists
            if (reflection.hasReflection) {
                Spacer(modifier = Modifier.height(12.dp))

                // Went well summary
                if (reflection.wentWell.isNotBlank()) {
                    ReflectionSummaryItem(
                        emoji = "👍",
                        label = "Went well",
                        text = reflection.wentWell
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Didn't go well summary
                if (reflection.didntGoWell.isNotBlank()) {
                    ReflectionSummaryItem(
                        emoji = "👎",
                        label = "Struggled with",
                        text = reflection.didntGoWell
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Learned summary
                if (reflection.learned.isNotBlank()) {
                    ReflectionSummaryItem(
                        emoji = "🎓",
                        label = "Learned",
                        text = reflection.learned
                    )
                }
            }
        }
    }
}

@Composable
private fun ReflectionSummaryItem(
    emoji: String,
    label: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text.take(100) + if (text.length > 100) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitTypePieChart(
    buildCount: Int,
    breakCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val total = buildCount + breakCount
    val buildColor = Color(0xFF81C784) // Green
    val breakColor = Color(0xFFE57373) // Red

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Habit Types",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (total > 0) {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val buildAngle = (buildCount.toFloat() / total) * 360f

                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 20.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2

                        // Break arc (background)
                        drawArc(
                            color = breakColor,
                            startAngle = -90f + buildAngle,
                            sweepAngle = 360f - buildAngle,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        // Build arc
                        if (buildCount > 0) {
                            drawArc(
                                color = buildColor,
                                startAngle = -90f,
                                sweepAngle = buildAngle,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = total.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "habits",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LegendItem(color = buildColor, label = "Build: $buildCount")
                    LegendItem(color = breakColor, label = "Break: $breakCount")
                }
            } else {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessFailurePieChart(
    successDays: Int,
    failureDays: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val total = successDays + failureDays
    val successColor = Color(0xFF4CAF50) // Brighter green
    val failureColor = Color(0xFFFF5252) // Brighter red

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Success Rate",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (total > 0) {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val successAngle = (successDays.toFloat() / total) * 360f

                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 20.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2

                        // Failure arc (background)
                        drawArc(
                            color = failureColor,
                            startAngle = -90f + successAngle,
                            sweepAngle = 360f - successAngle,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        // Success arc
                        if (successDays > 0) {
                            drawArc(
                                color = successColor,
                                startAngle = -90f,
                                sweepAngle = successAngle,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val percentage = if (total > 0) (successDays * 100 / total) else 0
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "success",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LegendItem(color = successColor, label = "Success: $successDays")
                    LegendItem(color = failureColor, label = "Failed: $failureDays")
                }
            } else {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeeklyProgressBarChart(
    habitProgress: List<HabitProgress>
) {
    val today = LocalDate.now()
    val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }

    // Calculate daily success rates
    val dailyStats = last7Days.map { date ->
        val dayIndex = 6 - java.time.temporal.ChronoUnit.DAYS.between(date, today).toInt()
        var successCount = 0
        var totalCount = 0

        habitProgress.forEach { progress ->
            val status = progress.last7Days.getOrNull(dayIndex)
            if (status != null) {
                totalCount++
                if (status) successCount++
            }
        }

        Triple(
            date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            successCount,
            totalCount
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyStats.forEach { (dayLabel, success, total) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bar
                        val maxHeight = 80.dp
                        val barHeight = if (total > 0) {
                            maxHeight * (success.toFloat() / total)
                        } else {
                            0.dp
                        }

                        val barColor = when {
                            total == 0 -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            success == total -> Color(0xFF4CAF50) // All success - green
                            success > total / 2 -> Color(0xFFFFC107) // More than half - yellow
                            else -> Color(0xFFFF5252) // Less than half - red
                        }

                        // Background bar
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(maxHeight)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Filled bar
                            if (total > 0) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(barColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Count label
                        Text(
                            text = if (total > 0) "$success/$total" else "-",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Day label
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
