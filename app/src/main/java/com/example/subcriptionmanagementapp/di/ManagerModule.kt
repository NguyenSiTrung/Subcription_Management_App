package com.example.subcriptionmanagementapp.di

import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {
    // CurrencyRateManager is already configured as @Singleton with @Inject constructor
    // so no explicit binding needed - Hilt will handle it automatically
}
