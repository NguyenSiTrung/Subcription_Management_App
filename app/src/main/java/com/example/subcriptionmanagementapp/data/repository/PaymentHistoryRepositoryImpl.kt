package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.dao.PaymentHistoryDao
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentHistoryRepositoryImpl @Inject constructor(
        private val paymentHistoryDao: PaymentHistoryDao
) : PaymentHistoryRepository {

    override fun getAllPaymentHistory(): Flow<List<PaymentHistory>> {
        return paymentHistoryDao.getAllPaymentHistory()
    }

    override suspend fun getPaymentHistoryById(id: Long): PaymentHistory? {
        return paymentHistoryDao.getPaymentHistoryById(id)
    }

    override suspend fun insertPaymentHistory(paymentHistory: PaymentHistory): Long {
        return paymentHistoryDao.insertPaymentHistory(paymentHistory)
    }

    override suspend fun updatePaymentHistory(paymentHistory: PaymentHistory) {
        paymentHistoryDao.updatePaymentHistory(paymentHistory)
    }

    override suspend fun deletePaymentHistory(paymentHistory: PaymentHistory) {
        paymentHistoryDao.deletePaymentHistory(paymentHistory)
    }

    override fun getPaymentHistoryBySubscriptionId(
            subscriptionId: Long
    ): Flow<List<PaymentHistory>> {
        return paymentHistoryDao.getPaymentHistoryBySubscriptionId(subscriptionId)
    }

    override suspend fun getPaymentHistoryByDateRange(
            startDate: Long,
            endDate: Long
    ): List<PaymentHistory> {
        return paymentHistoryDao.getPaymentHistoryByDateRange(startDate, endDate)
    }

    override suspend fun getTotalPaymentByDateRange(startDate: Long, endDate: Long): Double? {
        return paymentHistoryDao.getTotalPaymentByDateRange(startDate, endDate)
    }

    override suspend fun clearAllPaymentHistory() {
        paymentHistoryDao.clearAllPaymentHistory()
    }
}
