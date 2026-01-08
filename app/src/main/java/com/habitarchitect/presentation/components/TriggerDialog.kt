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

/**
 * Preset trigger options for quick selection.
 */
private val presetTriggers = listOf(
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
 * Dialog shown after user marks a BREAK habit as failed.
 * Asks what triggered the urge to help identify patterns.
 */
@Composable
fun TriggerDialog(
    habitName: String,
    onDismiss: () -> Unit,
    onTriggerSelected: (String) -> Unit
) {
    var selectedTrigger by remember { mutableStateOf<String?>(null) }
    var customTrigger by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

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
                presetTriggers.chunked(2).forEach { row ->
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
