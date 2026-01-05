package com.habitarchitect.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.AuthProvider
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val memberSince: String = "",
    val accountType: String = "",
    val isGuest: Boolean = false,
    val totalHabits: Int = 0,
    val longestStreak: Int = 0,
    val totalSuccessDays: Int = 0,
    val successRate: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = firebaseAuth.currentUser ?: return

        viewModelScope.launch {
            // Get user info
            val displayName = user.displayName ?: user.email?.substringBefore("@") ?: "User"
            val email = user.email ?: "No email"
            val photoUrl = user.photoUrl?.toString()
            val isGuest = user.isAnonymous

            // Format member since date
            val creationTime = user.metadata?.creationTimestamp ?: System.currentTimeMillis()
            val memberSince = Instant.ofEpochMilli(creationTime)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM yyyy"))

            // Determine account type
            val accountType = when {
                isGuest -> "Guest Account"
                user.providerData.any { it.providerId == "google.com" } -> "Google Account"
                else -> "Email Account"
            }

            // Get habit stats
            val userId = user.uid
            val habits = habitRepository.getActiveHabits(userId).first()

            val totalHabits = habits.size
            val longestStreak = habits.maxOfOrNull { it.longestStreak } ?: 0
            val totalSuccessDays = habits.sumOf { it.totalSuccessDays }
            val totalAttempts = habits.sumOf { it.totalSuccessDays + it.totalFailureDays }
            val successRate = if (totalAttempts > 0) {
                ((totalSuccessDays.toFloat() / totalAttempts) * 100).toInt()
            } else 0

            _uiState.value = ProfileUiState(
                displayName = displayName,
                email = email,
                photoUrl = photoUrl,
                memberSince = memberSince,
                accountType = accountType,
                isGuest = isGuest,
                totalHabits = totalHabits,
                longestStreak = longestStreak,
                totalSuccessDays = totalSuccessDays,
                successRate = successRate
            )
        }
    }
}
