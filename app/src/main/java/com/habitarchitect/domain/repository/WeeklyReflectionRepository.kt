package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.WeeklyReflection
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for weekly reflection operations.
 */
interface WeeklyReflectionRepository {

    fun getReflectionsForUser(userId: String): Flow<List<WeeklyReflection>>

    suspend fun getReflectionForWeek(userId: String, weekStartDate: LocalDate): WeeklyReflection?

    suspend fun saveReflection(reflection: WeeklyReflection): Result<Unit>

    suspend fun deleteReflection(id: String): Result<Unit>
}
