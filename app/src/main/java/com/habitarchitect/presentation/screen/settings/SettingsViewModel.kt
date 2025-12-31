package com.habitarchitect.presentation.screen.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    val isExporting: Boolean = false
)

sealed class SettingsEvent {
    data class ShareExport(val uri: Uri) : SettingsEvent()
    data class ExportError(val message: String) : SettingsEvent()
}

/**
 * ViewModel for settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val dataExportService: DataExportService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            user?.let {
                _uiState.value = SettingsUiState(
                    notificationsEnabled = it.notificationsEnabled,
                    morningTime = it.morningReminderTime?.toString() ?: "07:30",
                    eveningTime = it.eveningReminderTime?.toString() ?: "21:00",
                    userEmail = it.email
                )
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.updateNotificationsEnabled(userId, enabled)
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        }
    }

    fun updateMorningTime(hour: Int, minute: Int) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val timeString = String.format("%02d:%02d", hour, minute)
        viewModelScope.launch {
            userRepository.updateReminderTimes(userId, timeString, null)
            _uiState.value = _uiState.value.copy(morningTime = timeString)
        }
    }

    fun updateEveningTime(hour: Int, minute: Int) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val timeString = String.format("%02d:%02d", hour, minute)
        viewModelScope.launch {
            userRepository.updateReminderTimes(userId, null, timeString)
            _uiState.value = _uiState.value.copy(eveningTime = timeString)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.deleteAllUserData(userId)
            firebaseAuth.currentUser?.delete()
        }
    }

    fun exportData() {
        val userId = firebaseAuth.currentUser?.uid ?: return
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

    fun getShareIntent(uri: android.net.Uri) = dataExportService.createShareIntent(uri)
}
