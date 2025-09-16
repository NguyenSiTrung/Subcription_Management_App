package com.example.subcriptionmanagementapp.domain.usecase.payment

import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaymentHistoryBySubscriptionIdUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    operator fun invoke(subscriptionId: Long): Flow<List<PaymentHistory>> {
        return paymentHistoryRepository.getPaymentHistoryBySubscriptionId(subscriptionId)
    }
}