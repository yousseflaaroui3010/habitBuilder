package com.habitarchitect.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitarchitect.data.sync.SyncState

/**
 * Displays the current sync status with appropriate icon and message.
 * Shows when offline, syncing, or if there are pending changes.
 */
@Composable
fun SyncStatusIndicator(
    syncState: SyncState,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isVisible = syncState !is SyncState.Idle && syncState !is SyncState.Success

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        SyncStatusBanner(
            syncState = syncState,
            onRetryClick = onRetryClick
        )
    }
}

@Composable
private fun SyncStatusBanner(
    syncState: SyncState,
    onRetryClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (syncState) {
            is SyncState.Offline -> Color(0xFF616161) // Gray
            is SyncState.Syncing -> MaterialTheme.colorScheme.primaryContainer
            is SyncState.Error -> MaterialTheme.colorScheme.errorContainer
            is SyncState.Success -> Color(0xFF4CAF50)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "sync_bg_color"
    )

    val contentColor by animateColorAsState(
        targetValue = when (syncState) {
            is SyncState.Offline -> Color.White
            is SyncState.Syncing -> MaterialTheme.colorScheme.onPrimaryContainer
            is SyncState.Error -> MaterialTheme.colorScheme.onErrorContainer
            is SyncState.Success -> Color.White
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "sync_content_color"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SyncIcon(syncState = syncState, tint = contentColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getSyncMessage(syncState),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
            }

            // Retry button for errors
            if (syncState is SyncState.Error && syncState.retryable) {
                IconButton(
                    onClick = onRetryClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry sync",
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncIcon(syncState: SyncState, tint: Color) {
    when (syncState) {
        is SyncState.Offline -> {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Offline",
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        is SyncState.Syncing -> {
            // Animated pulsing sync icon
            val infiniteTransition = rememberInfiniteTransition(label = "sync_pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sync_alpha"
            )
            Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = "Syncing",
                tint = tint,
                modifier = Modifier
                    .size(20.dp)
                    .alpha(alpha)
            )
        }
        is SyncState.Error -> {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Sync error",
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        is SyncState.Success -> {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = "Synced",
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.CloudQueue,
                contentDescription = "Pending",
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getSyncMessage(syncState: SyncState): String {
    return when (syncState) {
        is SyncState.Idle -> "Ready"
        is SyncState.Offline -> {
            val pendingCount = syncState.pendingCount
            if (pendingCount > 0) {
                "Offline - $pendingCount change${if (pendingCount > 1) "s" else ""} pending"
            } else {
                "Offline"
            }
        }
        is SyncState.Syncing -> {
            val progress = (syncState.progress * 100).toInt()
            "Syncing... $progress%"
        }
        is SyncState.Success -> "Synced ${syncState.syncedCount} items"
        is SyncState.Error -> syncState.message
    }
}

/**
 * Compact sync status dot indicator for use in app bar.
 */
@Composable
fun SyncStatusDot(
    syncState: SyncState,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dotColor by animateColorAsState(
        targetValue = when (syncState) {
            is SyncState.Idle, is SyncState.Success -> Color(0xFF4CAF50) // Green
            is SyncState.Offline -> Color(0xFF616161) // Gray
            is SyncState.Syncing -> Color(0xFF2196F3) // Blue
            is SyncState.Error -> Color(0xFFF44336) // Red
        },
        label = "dot_color"
    )

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(dotColor)
            .clickable(onClick = onClick)
    ) {
        if (syncState is SyncState.Syncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        }
    }
}
