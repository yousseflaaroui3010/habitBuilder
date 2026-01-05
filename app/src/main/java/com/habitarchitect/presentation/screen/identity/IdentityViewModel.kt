package com.habitarchitect.presentation.screen.identity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class IdentityUiState(
    val isLoading: Boolean = true,
    val identities: List<IdentityItem> = emptyList(),
    val todayVotes: Int = 0,
    val todayBuildVotes: Int = 0,
    val todayBreakVotes: Int = 0
)

@HiltViewModel
class IdentityViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdentityUiState())
    val uiState: StateFlow<IdentityUiState> = _uiState.asStateFlow()

    private val today = LocalDate.now()

    init {
        loadIdentities()
    }

    private fun loadIdentities() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            habitRepository.getActiveHabits(userId).collect { habits ->
                if (habits.isEmpty()) {
                    _uiState.value = IdentityUiState(isLoading = false)
                    return@collect
                }

                // Get today's logs
                val todayLogs = dailyLogRepository.getLogsForHabitsOnDate(
                    habits.map { it.id },
                    today
                )

                val successLogs = todayLogs.filter { it.status == DailyStatus.SUCCESS }

                // Map habits to identity items
                val identities = habits.map { habit ->
                    val log = todayLogs.find { it.habitId == habit.id }
                    val votedToday = log?.status == DailyStatus.SUCCESS

                    IdentityItem(
                        habitId = habit.id,
                        emoji = habit.iconEmoji,
                        identity = habitToIdentity(habit),
                        habitType = habit.type,
                        votedToday = votedToday,
                        todayAction = if (votedToday) getActionDescription(habit) else null
                    )
                }

                // Count votes
                val buildVotes = successLogs.count { log ->
                    habits.find { it.id == log.habitId }?.type == HabitType.BUILD
                }
                val breakVotes = successLogs.count { log ->
                    habits.find { it.id == log.habitId }?.type == HabitType.BREAK
                }

                _uiState.value = IdentityUiState(
                    isLoading = false,
                    identities = identities,
                    todayVotes = successLogs.size,
                    todayBuildVotes = buildVotes,
                    todayBreakVotes = breakVotes
                )
            }
        }
    }

    private fun habitToIdentity(habit: Habit): String {
        // Convert habit name to identity statement
        val name = habit.name.lowercase()

        return when {
            // Exercise related
            name.contains("exercise") || name.contains("workout") || name.contains("gym") ||
                    name.contains("run") || name.contains("pushup") -> "an athlete"

            // Reading
            name.contains("read") || name.contains("book") -> "a reader"

            // Writing
            name.contains("write") || name.contains("journal") || name.contains("blog") -> "a writer"

            // Meditation
            name.contains("meditat") || name.contains("mindful") -> "a mindful person"

            // Coding/Development
            name.contains("code") || name.contains("program") || name.contains("develop") -> "a developer"

            // Learning
            name.contains("learn") || name.contains("study") || name.contains("course") -> "a lifelong learner"

            // Health
            name.contains("water") || name.contains("vegetable") || name.contains("healthy") ||
                    name.contains("sleep") -> "a healthy person"

            // Break habits - convert to positive identity
            habit.type == HabitType.BREAK -> when {
                name.contains("smoke") || name.contains("smoking") -> "smoke-free"
                name.contains("drink") || name.contains("alcohol") -> "sober"
                name.contains("snack") || name.contains("junk") || name.contains("sugar") -> "a healthy eater"
                name.contains("phone") || name.contains("screen") || name.contains("scroll") -> "digitally mindful"
                name.contains("procrastinat") -> "productive"
                else -> "disciplined"
            }

            // Default
            else -> "the person I want to be"
        }
    }

    private fun getActionDescription(habit: Habit): String {
        val name = habit.name
        return when (habit.type) {
            HabitType.BUILD -> "Completed $name"
            HabitType.BREAK -> "Resisted $name"
        }
    }
}
