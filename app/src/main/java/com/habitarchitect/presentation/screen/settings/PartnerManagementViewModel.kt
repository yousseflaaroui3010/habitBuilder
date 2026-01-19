package com.habitarchitect.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.data.analytics.AnalyticsTracker
import com.habitarchitect.domain.model.Partnership
import com.habitarchitect.domain.repository.PartnershipRepository
import com.habitarchitect.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartnerManagementUiState(
    val partnerships: List<Partnership> = emptyList(),
    val isLoading: Boolean = true,
    val pendingInviteCode: String? = null
)

/**
 * ViewModel for partner management screen.
 */
@HiltViewModel
class PartnerManagementViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val partnershipRepository: PartnershipRepository,
    private val userRepository: UserRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartnerManagementUiState())
    val uiState: StateFlow<PartnerManagementUiState> = _uiState.asStateFlow()

    init {
        loadPartnerships()
    }

    private fun loadPartnerships() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            partnershipRepository.getPartnershipsForUser(userId).collect { partnerships ->
                _uiState.value = _uiState.value.copy(
                    partnerships = partnerships,
                    isLoading = false
                )
            }
        }
    }

    fun createInvite() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = partnershipRepository.createPartnership(userId)
            result.onSuccess { partnership ->
                _uiState.value = _uiState.value.copy(
                    pendingInviteCode = partnership.inviteCode
                )
                // Track partner invited
                val user = userRepository.getUserById(userId).first()
                val daysSinceSignup = user?.let {
                    val createdAtDate = java.time.Instant.ofEpochMilli(it.createdAt)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    ChronoUnit.DAYS.between(createdAtDate, LocalDate.now()).toInt()
                } ?: 0
                analyticsTracker.trackPartnerInvited(daysSinceSignup)
            }
        }
    }

    fun revokePartnership(partnershipId: String) {
        viewModelScope.launch {
            partnershipRepository.revokePartnership(partnershipId)
        }
    }
}
