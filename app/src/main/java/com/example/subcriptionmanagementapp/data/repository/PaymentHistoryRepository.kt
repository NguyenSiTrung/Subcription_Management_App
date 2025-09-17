package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import kotlinx.coroutines.flow.Flow

interface PaymentHistoryRepository {
    fun getAllPaymentHistory(): Flow<List<PaymentHistory>>
    suspend fun getPaymentHistoryById(id: Long): PaymentHistory?
    suspend fun insertPaymentHistory(paymentHistory: PaymentHistory): Long
    suspend fun updatePaymentHistory(paymentHistory: PaymentHistory)
    suspend fun deletePaymentHistory(paymentHistory: PaymentHistory)
    fun getPaymentHistoryBySubscriptionId(subscriptionId: Long): Flow<List<PaymentHistory>>
    suspend fun getPaymentHistoryByDateRange(startDate: Long, endDate: Long): List<PaymentHistory>
    suspend fun getTotalPaymentByDateRange(startDate: Long, endDate: Long): Double?
    suspend fun clearAllPaymentHistory()
}