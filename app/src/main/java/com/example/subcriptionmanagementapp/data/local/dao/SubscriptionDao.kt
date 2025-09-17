package com.example.subcriptionmanagementapp.data.local.dao

import androidx.room.*
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY name ASC")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE is_active = 1 ORDER BY next_billing_date ASC")
    fun getActiveSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): Subscription?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Update suspend fun updateSubscription(subscription: Subscription)

    @Delete suspend fun deleteSubscription(subscription: Subscription)

    @Query("SELECT * FROM subscriptions WHERE category_id = :categoryId")
    fun getSubscriptionsByCategory(categoryId: Long): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE next_billing_date BETWEEN :startDate AND :endDate")
    suspend fun getSubscriptionsByBillingDateRange(
            startDate: Long,
            endDate: Long
    ): List<Subscription>

    @Query(
            "SELECT * FROM subscriptions WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'"
    )
    suspend fun searchSubscriptions(searchQuery: String): List<Subscription>

    @Query("DELETE FROM subscriptions") suspend fun clearAllSubscriptions()
}
