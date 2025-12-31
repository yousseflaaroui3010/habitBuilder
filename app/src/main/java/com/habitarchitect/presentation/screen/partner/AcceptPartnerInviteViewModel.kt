package com.habitarchitect.presentation.screen.partner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.Partnership
import com.habitarchitect.domain.model.PartnershipStatus
import com.habitarchitect.domain.repository.PartnershipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AcceptInviteUiState {
    object Loading : AcceptInviteUiState()
    data class Found(val partnership: Partnership) : AcceptInviteUiState()
    object Expired : AcceptInviteUiState()
    object NotFound : AcceptInviteUiState()
    object AlreadyAccepted : AcceptInviteUiState()
    object OwnInvite : AcceptInviteUiState()
    object Accepting : AcceptInviteUiState()
    object Success : AcceptInviteUiState()
    data class Error(val message: String) : AcceptInviteUiState()
}

@HiltViewModel
class AcceptPartnerInviteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val partnershipRepository: PartnershipRepository
) : ViewModel() {

    private val inviteCode: String = savedStateHandle["inviteCode"] ?: ""

    private val _uiState = MutableStateFlow<AcceptInviteUiState>(AcceptInviteUiState.Loading)
    val uiState: StateFlow<AcceptInviteUiState> = _uiState.asStateFlow()

    init {
        loadInvite()
    }

    private fun loadInvite() {
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null) {
            _uiState.value = AcceptInviteUiState.Error("Please sign in first")
            return
        }

        viewModelScope.launch {
            _uiState.value = AcceptInviteUiState.Loading

            val partnership = partnershipRepository.getPartnershipByInviteCode(inviteCode)

            _uiState.value = when {
                partnership == null -> AcceptInviteUiState.NotFound
                partnership.ownerId == currentUserId -> AcceptInviteUiState.OwnInvite
                partnership.status == PartnershipStatus.ACTIVE -> AcceptInviteUiState.AlreadyAccepted
                partnership.status == PartnershipStatus.REVOKED -> AcceptInviteUiState.NotFound
                partnership.inviteExpiresAt != null && partnership.inviteExpiresAt < System.currentTimeMillis() -> AcceptInviteUiState.Expired
                else -> AcceptInviteUiState.Found(partnership)
            }
        }
    }

    fun acceptInvite() {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = AcceptInviteUiState.Accepting

            partnershipRepository.acceptPartnership(inviteCode, currentUserId)
                .onSuccess {
                    _uiState.value = AcceptInviteUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = AcceptInviteUiState.Error(e.message ?: "Failed to accept invite")
                }
        }
    }
}
