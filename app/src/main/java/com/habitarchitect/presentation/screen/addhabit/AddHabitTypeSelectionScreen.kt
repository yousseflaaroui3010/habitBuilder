package com.habitarchitect.presentation.screen.addhabit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.presentation.theme.CalendarSuccess
import com.habitarchitect.presentation.theme.Error

/**
 * Screen for selecting habit type (BUILD or BREAK).
 * Now goes directly to templates when a type is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun AddHabitTypeSelectionScreen(
    onNavigateBack: () -> Unit,
    onSelectType: (HabitType) -> Unit, // Kept for API compatibility
    onBrowseTemplates: (HabitType) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Habit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "What would you like to do?",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BUILD habit card - goes to templates
            HabitTypeCard(
                emoji = "🎯",
                title = "Build a Habit",
                description = "Start doing something good",
                containerColor = CalendarSuccess.copy(alpha = 0.1f),
                onClick = { onBrowseTemplates(HabitType.BUILD) }
            )

            // BREAK habit card - goes to templates
            HabitTypeCard(
                emoji = "🚫",
                title = "Break a Habit",
                description = "Stop doing something harmful",
                containerColor = Error.copy(alpha = 0.1f),
                onClick = { onBrowseTemplates(HabitType.BREAK) }
            )
        }
    }
}

@Composable
private fun HabitTypeCard(
    emoji: String,
    title: String,
    description: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
