package com.example.subcriptionmanagementapp.domain.usecase.statistics

import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetMonthlySpendingUseCase
@Inject
constructor(private val paymentHistoryRepository: PaymentHistoryRepository) {
    operator fun invoke(year: Int, month: Int): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)

        val endDate = calendar.timeInMillis

        return kotlinx.coroutines.flow.flow {
            val paymentHistoryList =
                    paymentHistoryRepository.getPaymentHistoryByDateRange(startDate, endDate)
            val totalSpending = paymentHistoryList.sumOf { it.amount }
            emit(totalSpending)
        }
    }
}
