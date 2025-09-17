package com.example.subcriptionmanagementapp.domain.usecase.statistics

import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MonthlySpending(val year: Int, val month: Int, val amount: Double)

class GetMonthlySpendingTrendUseCase
@Inject
constructor(private val paymentHistoryRepository: PaymentHistoryRepository) {
    operator fun invoke(year: Int): Flow<List<MonthlySpending>> {
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

        return kotlinx.coroutines.flow.flow {
            val paymentHistoryList =
                    paymentHistoryRepository.getPaymentHistoryByDateRange(startDate, endDate)
            val monthlySpendingMap = mutableMapOf<Int, Double>()

            // Initialize all months with 0 spending
            for (month in 0..11) {
                monthlySpendingMap[month] = 0.0
            }

            // Calculate spending for each month
            paymentHistoryList.forEach { payment ->
                val paymentCalendar = Calendar.getInstance()
                paymentCalendar.timeInMillis = payment.paymentDate

                if (paymentCalendar.get(Calendar.YEAR) == year) {
                    val month = paymentCalendar.get(Calendar.MONTH)
                    val currentAmount = monthlySpendingMap[month] ?: 0.0
                    monthlySpendingMap[month] = currentAmount + payment.amount
                }
            }

            // Convert to list of MonthlySpending
            val result =
                    monthlySpendingMap
                            .map { (month, amount) -> MonthlySpending(year, month, amount) }
                            .sortedBy { it.month }
            emit(result)
        }
    }
}
