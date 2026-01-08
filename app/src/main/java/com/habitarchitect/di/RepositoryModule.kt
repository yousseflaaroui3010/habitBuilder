package com.habitarchitect.di

import com.habitarchitect.data.repository.DailyLogRepositoryImpl
import com.habitarchitect.data.repository.HabitRepositoryImpl
import com.habitarchitect.data.repository.ListItemRepositoryImpl
import com.habitarchitect.data.repository.PartnershipRepositoryImpl
import com.habitarchitect.data.repository.UserRepositoryImpl
import com.habitarchitect.data.repository.WeeklyReflectionRepositoryImpl
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import com.habitarchitect.domain.repository.ListItemRepository
import com.habitarchitect.domain.repository.PartnershipRepository
import com.habitarchitect.domain.repository.UserRepository
import com.habitarchitect.domain.repository.WeeklyReflectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository interfaces to implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(impl: DailyLogRepositoryImpl): DailyLogRepository

    @Binds
    @Singleton
    abstract fun bindListItemRepository(impl: ListItemRepositoryImpl): ListItemRepository

    @Binds
    @Singleton
    abstract fun bindPartnershipRepository(impl: PartnershipRepositoryImpl): PartnershipRepository

    @Binds
    @Singleton
    abstract fun bindWeeklyReflectionRepository(impl: WeeklyReflectionRepositoryImpl): WeeklyReflectionRepository
}
