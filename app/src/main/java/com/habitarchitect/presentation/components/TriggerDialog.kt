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
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * Default/fallback trigger options when no template-specific ones exist.
 */
private val defaultTriggers = listOf(
    "I got bored" to "😐",
    "Phone was nearby" to "📱",
    "Needed dopamine" to "⚡",
    "Stressed/anxious" to "😰",
    "Social pressure" to "👥",
    "Saw a cue/trigger" to "👁️",
    "Tired/exhausted" to "😴",
    "Emotional eating" to "🍕"
)

/**
 * Get triggers for a specific template, or fallback to defaults.
 */
private fun getTriggersForTemplate(templateId: String?): List<Pair<String, String>> {
    if (templateId == null) return defaultTriggers

    val template = HabitTemplates.breakTemplates.find { it.id == templateId }
    val triggerContexts = template?.defaultTriggerContexts ?: emptyList()

    if (triggerContexts.isEmpty()) return defaultTriggers

    // Parse "text|emoji" format
    return triggerContexts.mapNotNull { encoded ->
        val parts = encoded.split("|")
        if (parts.size == 2) {
            parts[0] to parts[1]
        } else null
    }.ifEmpty { defaultTriggers }
}

/**
 * Dialog shown after user marks a BREAK habit as failed.
 * Asks what triggered the urge to help identify patterns.
 * Shows habit-specific triggers if templateId is provided.
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
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Preset triggers in a grid
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
        modifier = modifier
            .clickable { onClick() },
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
