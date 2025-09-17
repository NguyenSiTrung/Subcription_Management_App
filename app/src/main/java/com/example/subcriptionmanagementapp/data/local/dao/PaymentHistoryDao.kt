package com.example.subcriptionmanagementapp.data.local.dao

import androidx.room.*
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentHistoryDao {
    @Query("SELECT * FROM payment_history ORDER BY payment_date DESC")
    fun getAllPaymentHistory(): Flow<List<PaymentHistory>>

    @Query("SELECT * FROM payment_history WHERE id = :id")
    suspend fun getPaymentHistoryById(id: Long): PaymentHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentHistory(paymentHistory: PaymentHistory): Long

    @Update suspend fun updatePaymentHistory(paymentHistory: PaymentHistory)

    @Delete suspend fun deletePaymentHistory(paymentHistory: PaymentHistory)

    @Query("SELECT * FROM payment_history WHERE subscription_id = :subscriptionId")
    fun getPaymentHistoryBySubscriptionId(subscriptionId: Long): Flow<List<PaymentHistory>>

    @Query("SELECT * FROM payment_history WHERE payment_date BETWEEN :startDate AND :endDate")
    suspend fun getPaymentHistoryByDateRange(startDate: Long, endDate: Long): List<PaymentHistory>

    @Query(
            "SELECT SUM(amount) FROM payment_history WHERE payment_date BETWEEN :startDate AND :endDate"
    )
    suspend fun getTotalPaymentByDateRange(startDate: Long, endDate: Long): Double?

    @Query("DELETE FROM payment_history") suspend fun clearAllPaymentHistory()
}
