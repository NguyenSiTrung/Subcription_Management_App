package com.example.subcriptionmanagementapp.domain.usecase.payment

import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaymentHistoryUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    operator fun invoke(id: Long): Flow<PaymentHistory?> {
        return paymentHistoryRepository.getPaymentHistoryById(id)
    }
}