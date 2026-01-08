package com.habitarchitect.presentation.screen.reflection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.WeeklyReflection
import com.habitarchitect.domain.repository.WeeklyReflectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject

data class WeeklyReflectionUiState(
    val wentWell: String = "",
    val didntGoWell: String = "",
    val learned: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class WeeklyReflectionViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val weeklyReflectionRepository: WeeklyReflectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReflectionUiState())
    val uiState: StateFlow<WeeklyReflectionUiState> = _uiState.asStateFlow()

    private val weekStartDate: LocalDate = LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    private var existingReflectionId: String? = null

    init {
        loadExistingReflection()
    }

    private fun loadExistingReflection() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val existing = weeklyReflectionRepository.getReflectionForWeek(userId, weekStartDate)
            if (existing != null) {
                existingReflectionId = existing.id
                _uiState.value = _uiState.value.copy(
                    wentWell = existing.wentWell,
                    didntGoWell = existing.didntGoWell,
                    learned = existing.learned
                )
            }
        }
    }

    fun updateWentWell(value: String) {
        _uiState.value = _uiState.value.copy(wentWell = value)
    }

    fun updateDidntGoWell(value: String) {
        _uiState.value = _uiState.value.copy(didntGoWell = value)
    }

    fun updateLearned(value: String) {
        _uiState.value = _uiState.value.copy(learned = value)
    }

    fun saveReflection() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            val now = System.currentTimeMillis()
            val existing = weeklyReflectionRepository.getReflectionForWeek(userId, weekStartDate)

            val reflection = WeeklyReflection(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                weekStartDate = weekStartDate,
                wentWell = state.wentWell,
                didntGoWell = state.didntGoWell,
                learned = state.learned,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )

            weeklyReflectionRepository.saveReflection(reflection)

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }
}
