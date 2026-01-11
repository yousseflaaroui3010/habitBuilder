package com.habitarchitect.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitarchitect.data.HabitTemplates

/**
 * Default fallback triggers when no habit-specific ones are available.
 */
private val defaultTriggers = listOf(
    "I got bored" to "😐",
    "Stressed/anxious" to "😰",
    "Tired/exhausted" to "😴",
    "Needed dopamine" to "⚡",
    "Social pressure" to "👥",
    "Saw a cue/trigger" to "👁️"
)

/**
 * Maps trigger text to appropriate emoji based on keywords.
 */
private fun getTriggerEmoji(trigger: String): String {
    val lower = trigger.lowercase()
    return when {
        lower.contains("bored") || lower.contains("nothing to do") -> "😐"
        lower.contains("stress") || lower.contains("anxious") -> "😰"
        lower.contains("tired") || lower.contains("sleep") || lower.contains("exhausted") -> "😴"
        lower.contains("phone") || lower.contains("scroll") || lower.contains("screen") -> "📱"
        lower.contains("social") || lower.contains("friend") || lower.contains("peer") -> "👥"
        lower.contains("lonely") || lower.contains("rejected") -> "💔"
        lower.contains("night") || lower.contains("alone") || lower.contains("dark") -> "🌙"
        lower.contains("food") || lower.contains("meal") || lower.contains("hungry") -> "🍕"
        lower.contains("alcohol") || lower.contains("drink") -> "🍺"
        lower.contains("coffee") || lower.contains("morning") -> "☕"
        lower.contains("work") || lower.contains("meeting") -> "💼"
        lower.contains("fear") || lower.contains("overwhelm") -> "😨"
        lower.contains("fomo") || lower.contains("missing") -> "📲"
        lower.contains("weekend") || lower.contains("friday") -> "🎉"
        lower.contains("mistake") || lower.contains("fail") -> "❌"
        lower.contains("compare") || lower.contains("other") -> "👀"
        lower.contains("critic") -> "🗣️"
        lower.contains("deadline") || lower.contains("urgent") -> "⏰"
        lower.contains("tv") || lower.contains("watch") -> "📺"
        lower.contains("wait") -> "⏳"
        lower.contains("saw") || lower.contains("content") || lower.contains("ad") -> "👁️"
        else -> "💭"
    }
}

/**
 * Get triggers for a specific habit template.
 */
fun getTriggersForTemplate(templateId: String?): List<Pair<String, String>> {
    if (templateId == null) return defaultTriggers

    val template = HabitTemplates.breakTemplates.find { it.id == templateId }
    val triggers = template?.defaultTriggerContexts

    return if (!triggers.isNullOrEmpty()) {
        triggers.map { it to getTriggerEmoji(it) }
    } else {
        defaultTriggers
    }
}

/**
 * Dialog shown after user marks a BREAK habit as failed.
 * Displays habit-specific triggers based on template, with custom input option.
 */
@Composable
fun TriggerDialog(
    habitName: String,
    templateId: String? = null,
    onDismiss: () -> Unit,
    onTriggerSelected: (String) -> Unit
) {
    var selectedTrigger by remember { mutableStateOf<String?>(null) }
    var customTrigger by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    // Get habit-specific triggers or fallback to defaults
    val triggers = remember(templateId) { getTriggersForTemplate(templateId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "What triggered the urge?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This helps identify patterns",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Habit-specific triggers in a grid (2 columns)
                triggers.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { (trigger, emoji) ->
                            TriggerChip(
                                text = trigger,
                                emoji = emoji,
                                isSelected = selectedTrigger == trigger,
                                onClick = {
                                    selectedTrigger = trigger
                                    showCustomInput = false
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty space if odd number
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom trigger option
                if (showCustomInput) {
                    OutlinedTextField(
                        value = customTrigger,
                        onValueChange = {
                            customTrigger = it
                            selectedTrigger = null
                        },
                        label = { Text("Describe the trigger") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    TextButton(
                        onClick = {
                            showCustomInput = true
                            selectedTrigger = null
                        }
                    ) {
                        Text("+ Add custom trigger")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trigger = if (showCustomInput && customTrigger.isNotBlank()) {
                        customTrigger
                    } else {
                        selectedTrigger
                    }
                    if (trigger != null) {
                        onTriggerSelected(trigger)
                    }
                    onDismiss()
                },
                enabled = selectedTrigger != null || (showCustomInput && customTrigger.isNotBlank())
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip")
            }
        }
    )
}

@Composable
private fun TriggerChip(
    text: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
