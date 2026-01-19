package com.habitarchitect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitarchitect.data.remote.dto.SyncConflict
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Resolution strategies for sync conflicts.
 */
enum class ConflictResolution {
    KEEP_LOCAL,  // Use local (device) version
    KEEP_REMOTE, // Use remote (server) version
    KEEP_NEWEST  // Keep whichever is newer
}

/**
 * Dialog for resolving sync conflicts between local and remote data.
 */
@Composable
fun ConflictResolutionDialog(
    conflict: SyncConflict,
    entityName: String,
    onResolve: (ConflictResolution) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedResolution by remember { mutableStateOf<ConflictResolution?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync Conflict")
            }
        },
        text = {
            Column {
                Text(
                    text = "\"$entityName\" has been modified both on this device and the server.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Version comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    VersionCard(
                        icon = Icons.Default.PhoneAndroid,
                        label = "This Device",
                        timestamp = conflict.localVersion,
                        isSelected = selectedResolution == ConflictResolution.KEEP_LOCAL,
                        onClick = { selectedResolution = ConflictResolution.KEEP_LOCAL },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    VersionCard(
                        icon = Icons.Default.Cloud,
                        label = "Server",
                        timestamp = conflict.serverVersion,
                        isSelected = selectedResolution == ConflictResolution.KEEP_REMOTE,
                        onClick = { selectedResolution = ConflictResolution.KEEP_REMOTE },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Auto-resolve option
                AutoResolveOption(
                    isSelected = selectedResolution == ConflictResolution.KEEP_NEWEST,
                    onClick = { selectedResolution = ConflictResolution.KEEP_NEWEST }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedResolution?.let { onResolve(it) }
                },
                enabled = selectedResolution != null
            ) {
                Text("Resolve")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun VersionCard(
    icon: ImageVector,
    label: String,
    timestamp: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }

    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateFormat.format(Date(timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AutoResolveOption(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Keep newest version",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Automatically use whichever was modified most recently",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Dialog shown when multiple conflicts need resolution.
 * Offers bulk resolution options.
 */
@Composable
fun BulkConflictResolutionDialog(
    conflictCount: Int,
    onResolveAll: (ConflictResolution) -> Unit,
    onResolveIndividually: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("$conflictCount Sync Conflicts")
        },
        text = {
            Column {
                Text(
                    text = "Some items have been modified both on this device and the server. How would you like to resolve these conflicts?",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                BulkResolutionButton(
                    icon = Icons.Default.PhoneAndroid,
                    text = "Keep all local versions",
                    description = "Use this device's data for all conflicts",
                    onClick = { onResolveAll(ConflictResolution.KEEP_LOCAL) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BulkResolutionButton(
                    icon = Icons.Default.Cloud,
                    text = "Keep all server versions",
                    description = "Use server data for all conflicts",
                    onClick = { onResolveAll(ConflictResolution.KEEP_REMOTE) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BulkResolutionButton(
                    icon = Icons.Default.SwapHoriz,
                    text = "Keep newest versions",
                    description = "Use the most recently modified version for each",
                    onClick = { onResolveAll(ConflictResolution.KEEP_NEWEST) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onResolveIndividually) {
                Text("Resolve individually")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        }
    )
}

@Composable
private fun BulkResolutionButton(
    icon: ImageVector,
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
