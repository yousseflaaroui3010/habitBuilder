package com.habitarchitect.presentation.screen.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.data.preferences.ThemeMode

/**
 * Settings screen with notification, account, and partner options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPartners: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showMorningTimePicker by remember { mutableStateOf(false) }
    var showEveningTimePicker by remember { mutableStateOf(false) }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShareExport -> {
                    val intent = viewModel.getShareIntent(event.uri)
                    context.startActivity(android.content.Intent.createChooser(intent, "Export Data"))
                }
                is SettingsEvent.ExportError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Parse current times
    val morningParts = uiState.morningTime.split(":")
    val eveningParts = uiState.eveningTime.split(":")
    val morningHour = morningParts.getOrNull(0)?.toIntOrNull() ?: 7
    val morningMinute = morningParts.getOrNull(1)?.toIntOrNull() ?: 30
    val eveningHour = eveningParts.getOrNull(0)?.toIntOrNull() ?: 21
    val eveningMinute = eveningParts.getOrNull(1)?.toIntOrNull() ?: 0

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This will permanently delete all your data. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount()
                        showDeleteDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.signOut()
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showMorningTimePicker) {
        TimePickerDialog(
            title = "Morning Reminder",
            initialHour = morningHour,
            initialMinute = morningMinute,
            onConfirm = { hour, minute ->
                viewModel.updateMorningTime(hour, minute)
                showMorningTimePicker = false
            },
            onDismiss = { showMorningTimePicker = false }
        )
    }

    if (showEveningTimePicker) {
        TimePickerDialog(
            title = "Evening Check-in",
            initialHour = eveningHour,
            initialMinute = eveningMinute,
            onConfirm = { hour, minute ->
                viewModel.updateEveningTime(hour, minute)
                showEveningTimePicker = false
            },
            onDismiss = { showEveningTimePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        ) {
            // Notifications section
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Enable Notifications") },
                supportingContent = { Text("Morning and evening reminders") },
                leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )
                }
            )

            if (uiState.notificationsEnabled) {
                ListItem(
                    headlineContent = { Text("Morning Reminder") },
                    supportingContent = { Text(uiState.morningTime) },
                    leadingContent = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.clickable { showMorningTimePicker = true }
                )

                ListItem(
                    headlineContent = { Text("Evening Check-in") },
                    supportingContent = { Text(uiState.eveningTime) },
                    leadingContent = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.clickable { showEveningTimePicker = true }
                )
            }

            Divider()

            // Appearance section
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = {
                    Text(
                        when (uiState.themeMode) {
                            ThemeMode.SYSTEM -> "Follow system"
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                        }
                    )
                },
                leadingContent = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                trailingContent = {
                    ThemeSelector(
                        currentMode = uiState.themeMode,
                        onModeSelected = { viewModel.setThemeMode(it) }
                    )
                }
            )

            Divider()

            // Account section
            Text(
                text = "Account",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Accountability Partners") },
                supportingContent = { Text("Manage who can see your habits") },
                leadingContent = { Icon(Icons.Default.People, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToPartners() }
            )

            ListItem(
                headlineContent = { Text("Export Data") },
                supportingContent = { Text("Download all your data as JSON") },
                leadingContent = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                trailingContent = {
                    if (uiState.isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                },
                modifier = Modifier.clickable(enabled = !uiState.isExporting) {
                    viewModel.exportData()
                }
            )

            ListItem(
                headlineContent = { Text("Sign Out") },
                leadingContent = { Icon(Icons.Filled.ExitToApp, contentDescription = null) },
                modifier = Modifier.clickable { showSignOutDialog = true }
            )

            ListItem(
                headlineContent = { Text("Delete Account") },
                supportingContent = { Text("Permanently delete all data") },
                leadingContent = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.clickable { showDeleteDialog = true }
            )
        }
    }
}

/**
 * Dialog for picking a time with Material 3 TimePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
            ) {
                Text("OK")
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
private fun ThemeSelector(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material3.DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        ThemeMode.entries.forEach { mode ->
            androidx.compose.material3.DropdownMenuItem(
                text = {
                    Text(
                        when (mode) {
                            ThemeMode.SYSTEM -> "Follow system"
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                        }
                    )
                },
                onClick = {
                    onModeSelected(mode)
                    expanded = false
                },
                leadingIcon = if (mode == currentMode) {
                    { Icon(Icons.Default.Schedule, contentDescription = null) }
                } else null
            )
        }
    }

    TextButton(onClick = { expanded = true }) {
        Text("Change")
    }
}
