package com.example.subcriptionmanagementapp.domain.usecase.statistics

import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class GetYearlySpendingUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    operator fun invoke(year: Int): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val startDate = calendar.timeInMillis
        
        calendar.add(Calendar.YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        
        val endDate = calendar.timeInMillis
        
        return paymentHistoryRepository.getPaymentHistoryByDateRange(startDate, endDate)
            .map { paymentHistoryList ->
                paymentHistoryList.sumOf { it.amount }
            }
    }
}