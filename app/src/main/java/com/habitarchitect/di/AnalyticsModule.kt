package com.habitarchitect.di

import com.habitarchitect.data.analytics.AnalyticsSDK
import com.habitarchitect.data.analytics.HabitAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing analytics dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsSDK(habitAnalytics: HabitAnalytics): AnalyticsSDK
}
