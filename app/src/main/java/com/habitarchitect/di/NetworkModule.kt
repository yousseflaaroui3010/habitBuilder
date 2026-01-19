package com.habitarchitect.di

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.habitarchitect.BuildConfig
import com.habitarchitect.data.remote.AuthInterceptor
import com.habitarchitect.data.remote.MockApiInterceptor
import com.habitarchitect.data.remote.api.HabitArchitectApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network dependencies.
 * Provides Retrofit, OkHttp, and API interfaces.
 * Gson is provided by AppModule.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(firebaseAuth: FirebaseAuth): AuthInterceptor {
        return AuthInterceptor(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMockApiInterceptor(gson: Gson): MockApiInterceptor {
        return MockApiInterceptor(gson)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        mockApiInterceptor: MockApiInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(mockApiInterceptor) // Mock API for development
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(HabitArchitectApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitArchitectApi(retrofit: Retrofit): HabitArchitectApi {
        return retrofit.create(HabitArchitectApi::class.java)
    }
}
