package com.habitarchitect.di

import com.google.gson.Gson
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.PendingOperationDao
import com.habitarchitect.data.remote.api.HabitArchitectApi
import com.habitarchitect.data.sync.NetworkMonitor
import com.habitarchitect.data.sync.OfflineQueue
import com.habitarchitect.data.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for sync-related dependencies.
 * Provides SyncManager, OfflineQueue, and NetworkMonitor.
 */
@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideOfflineQueue(
        pendingOperationDao: PendingOperationDao,
        gson: Gson
    ): OfflineQueue {
        return OfflineQueue(pendingOperationDao, gson)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        api: HabitArchitectApi,
        habitDao: HabitDao,
        dailyLogDao: DailyLogDao,
        offlineQueue: OfflineQueue,
        networkMonitor: NetworkMonitor,
        gson: Gson
    ): SyncManager {
        return SyncManager(api, habitDao, dailyLogDao, offlineQueue, networkMonitor, gson)
    }
}
