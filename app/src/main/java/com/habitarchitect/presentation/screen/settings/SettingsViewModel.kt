package com.habitarchitect.presentation.screen.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.data.preferences.ThemeMode
import com.habitarchitect.data.preferences.ThemePreferences
import com.habitarchitect.domain.repository.UserRepository
import com.habitarchitect.service.export.DataExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val morningTime: String = "07:30",
    val eveningTime: String = "21:00",
    val userEmail: String = "",
    val isExporting: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

sealed class SettingsEvent {
    data class ShareExport(val uri: Uri) : SettingsEvent()
    data class ExportError(val message: String) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val dataExportService: DataExportService,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    private val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        loadSettings()
        loadThemeMode()
    }

    private fun loadThemeMode() {
        viewModelScope.launch {
            themePreferences.themeMode.collect { mode ->
                _uiState.value = _uiState.value.copy(themeMode = mode)
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.let { user ->
                _uiState.value = SettingsUiState(
                    notificationsEnabled = user.notificationsEnabled,
                    morningTime = user.morningReminderTime?.toString() ?: "07:30",
                    eveningTime = user.eveningReminderTime?.toString() ?: "21:00",
                    userEmail = user.email
                )
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                userRepository.updateNotificationsEnabled(userId, enabled)
                _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
            }
        }
    }

    fun updateMorningTime(hour: Int, minute: Int) = updateReminderTime(hour, minute, isMorning = true)

    fun updateEveningTime(hour: Int, minute: Int) = updateReminderTime(hour, minute, isMorning = false)

    private fun updateReminderTime(hour: Int, minute: Int, isMorning: Boolean) {
        currentUserId?.let { userId ->
            val timeString = "%02d:%02d".format(hour, minute)
            viewModelScope.launch {
                if (isMorning) {
                    userRepository.updateReminderTimes(userId, timeString, null)
                    _uiState.value = _uiState.value.copy(morningTime = timeString)
                } else {
                    userRepository.updateReminderTimes(userId, null, timeString)
                    _uiState.value = _uiState.value.copy(eveningTime = timeString)
                }
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                userRepository.deleteAllUserData(userId)
                firebaseAuth.currentUser?.delete()
            }
        }
    }

    fun exportData() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isExporting = true)
                val uri = dataExportService.exportUserData(userId)
                _uiState.value = _uiState.value.copy(isExporting = false)
                if (uri != null) {
                    _events.emit(SettingsEvent.ShareExport(uri))
                } else {
                    _events.emit(SettingsEvent.ExportError("Failed to export data"))
                }
            }
        }
    }

    fun getShareIntent(uri: Uri) = dataExportService.createShareIntent(uri)
}
