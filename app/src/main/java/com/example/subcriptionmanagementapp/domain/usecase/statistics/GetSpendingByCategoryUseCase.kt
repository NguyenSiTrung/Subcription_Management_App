package com.example.subcriptionmanagementapp.domain.usecase.statistics

import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.repository.CategoryRepository
import com.example.subcriptionmanagementapp.data.repository.PaymentHistoryRepository
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

data class CategorySpending(
    val category: Category,
    val amount: Double
)

class GetSpendingByCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<CategorySpending>> {
        val categoriesFlow = categoryRepository.getAllCategories()
        val subscriptionsFlow = subscriptionRepository.getAllSubscriptions()
        val paymentHistoryFlow = paymentHistoryRepository.getPaymentHistoryByDateRange(startDate, endDate)
        
        return combine(categoriesFlow, subscriptionsFlow, paymentHistoryFlow) { categories, subscriptions, paymentHistoryList ->
            val categorySpendingMap = mutableMapOf<Long, Double>()
            
            // Initialize all categories with 0 spending
            categories.forEach { category ->
                categorySpendingMap[category.id] = 0.0
            }
            
            // Calculate spending for each category
            paymentHistoryList.forEach { payment ->
                val subscription = subscriptions.find { it.id == payment.subscriptionId }
                if (subscription != null) {
                    val categoryId = subscription.categoryId ?: 0L // 0 for no category
                    val currentAmount = categorySpendingMap[categoryId] ?: 0.0
                    categorySpendingMap[categoryId] = currentAmount + payment.amount
                }
            }
            
            // Convert to list of CategorySpending
            categorySpendingMap.map { (categoryId, amount) ->
                val category = categories.find { it.id == categoryId }
                if (category != null) {
                    CategorySpending(category, amount)
                } else {
                    // Create a default category for subscriptions without a category
                    CategorySpending(Category(0, "Other", null, true), amount)
                }
            }.filter { it.amount > 0 }.sortedByDescending { it.amount }
        }
    }
}