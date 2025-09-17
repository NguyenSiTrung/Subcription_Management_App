package com.example.subcriptionmanagementapp.domain.usecase.payment

import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPaymentHistoryUseCase
@Inject
constructor(private val paymentHistoryRepository: PaymentHistoryRepository) {
    operator fun invoke(id: Long): Flow<PaymentHistory?> {
        return flow { emit(paymentHistoryRepository.getPaymentHistoryById(id)) }
    }
}
