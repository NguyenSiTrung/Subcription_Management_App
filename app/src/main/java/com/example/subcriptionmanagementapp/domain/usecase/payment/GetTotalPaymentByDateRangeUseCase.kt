package com.example.subcriptionmanagementapp.domain.usecase.payment

import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import javax.inject.Inject

class GetTotalPaymentByDateRangeUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    suspend operator fun invoke(startDate: Long, endDate: Long): Double? {
        return paymentHistoryRepository.getTotalPaymentByDateRange(startDate, endDate)
    }
}