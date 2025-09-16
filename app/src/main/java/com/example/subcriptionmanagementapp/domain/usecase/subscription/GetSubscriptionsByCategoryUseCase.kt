package com.example.subcriptionmanagementapp.domain.usecase.subscription

import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionsByCategoryUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(categoryId: Long): Flow<List<Subscription>> {
        return subscriptionRepository.getSubscriptionsByCategory(categoryId)
    }
}