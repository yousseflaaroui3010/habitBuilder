package com.habitarchitect.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.Partnership
import com.habitarchitect.domain.repository.PartnershipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val partnershipRepository: PartnershipRepository
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
            }
        }
    }

    fun revokePartnership(partnershipId: String) {
        viewModelScope.launch {
            partnershipRepository.revokePartnership(partnershipId)
        }
    }
}
