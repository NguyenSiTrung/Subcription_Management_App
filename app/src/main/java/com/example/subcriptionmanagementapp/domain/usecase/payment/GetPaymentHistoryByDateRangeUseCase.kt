package com.example.subcriptionmanagementapp.domain.usecase.payment

import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import javax.inject.Inject

class GetPaymentHistoryByDateRangeUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    suspend operator fun invoke(startDate: Long, endDate: Long): List<PaymentHistory> {
        return paymentHistoryRepository.getPaymentHistoryByDateRange(startDate, endDate)
    }
}