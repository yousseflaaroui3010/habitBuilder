package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.WeeklyReflectionDao
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.data.mapper.toEntity
import com.habitarchitect.domain.model.WeeklyReflection
import com.habitarchitect.domain.repository.WeeklyReflectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementation of WeeklyReflectionRepository using Room database.
 */
class WeeklyReflectionRepositoryImpl @Inject constructor(
    private val weeklyReflectionDao: WeeklyReflectionDao
) : WeeklyReflectionRepository {

    override fun getReflectionsForUser(userId: String): Flow<List<WeeklyReflection>> {
        return weeklyReflectionDao.getReflectionsForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReflectionForWeek(userId: String, weekStartDate: LocalDate): WeeklyReflection? {
        return weeklyReflectionDao.getReflectionForWeek(userId, weekStartDate.toString())?.toDomain()
    }

    override suspend fun saveReflection(reflection: WeeklyReflection): Result<Unit> {
        return runCatching {
            weeklyReflectionDao.insertReflection(reflection.toEntity())
        }
    }

    override suspend fun deleteReflection(id: String): Result<Unit> {
        return runCatching {
            weeklyReflectionDao.deleteReflection(id)
        }
    }
}
