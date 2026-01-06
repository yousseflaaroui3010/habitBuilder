package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitarchitect.data.local.database.entity.WeeklyReflectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyReflectionDao {

    @Query("SELECT * FROM weekly_reflections WHERE userId = :userId ORDER BY weekStartDate DESC")
    fun getReflectionsForUser(userId: String): Flow<List<WeeklyReflectionEntity>>

    @Query("SELECT * FROM weekly_reflections WHERE userId = :userId AND weekStartDate = :weekStartDate LIMIT 1")
    suspend fun getReflectionForWeek(userId: String, weekStartDate: String): WeeklyReflectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: WeeklyReflectionEntity)

    @Query("DELETE FROM weekly_reflections WHERE id = :id")
    suspend fun deleteReflection(id: String)
}
