package com.habitarchitect.presentation.screen.reflection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.data.local.database.dao.WeeklyReflectionDao
import com.habitarchitect.data.local.database.entity.WeeklyReflectionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private val weeklyReflectionDao: WeeklyReflectionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReflectionUiState())
    val uiState: StateFlow<WeeklyReflectionUiState> = _uiState.asStateFlow()

    private val weekStartDate: String = LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        .format(DateTimeFormatter.ISO_DATE)

    init {
        loadExistingReflection()
    }

    private fun loadExistingReflection() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val existing = weeklyReflectionDao.getReflectionForWeek(userId, weekStartDate)
            if (existing != null) {
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

            val existing = weeklyReflectionDao.getReflectionForWeek(userId, weekStartDate)
            val now = System.currentTimeMillis()

            val reflection = WeeklyReflectionEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                weekStartDate = weekStartDate,
                wentWell = state.wentWell,
                didntGoWell = state.didntGoWell,
                learned = state.learned,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )

            weeklyReflectionDao.insertReflection(reflection)

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }
}
