package com.example.subcriptionmanagementapp.di

import com.example.subcriptionmanagementapp.data.repository.CategoryRepository
import com.example.subcriptionmanagementapp.data.repository.CategoryRepositoryImpl
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepositoryImpl
import com.example.subcriptionmanagementapp.data.repository.ReminderRepository
import com.example.subcriptionmanagementapp.data.repository.ReminderRepositoryImpl
import com.example.subcriptionmanagementapp.data.repository.SettingsRepository
import com.example.subcriptionmanagementapp.data.repository.SettingsRepositoryImpl
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepository
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindPaymentHistoryRepository(
        impl: PaymentHistoryRepositoryImpl
    ): PaymentHistoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
