package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getAllSubscriptions(): Flow<List<Subscription>>
    fun getActiveSubscriptions(): Flow<List<Subscription>>
    suspend fun getSubscriptionById(id: Long): Subscription?
    suspend fun insertSubscription(subscription: Subscription): Long
    suspend fun updateSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscription: Subscription)
    fun getSubscriptionsByCategory(categoryId: Long): Flow<List<Subscription>>
    suspend fun getSubscriptionsByBillingDateRange(startDate: Long, endDate: Long): List<Subscription>
    suspend fun searchSubscriptions(searchQuery: String): List<Subscription>
    suspend fun clearAllSubscriptions()
}
