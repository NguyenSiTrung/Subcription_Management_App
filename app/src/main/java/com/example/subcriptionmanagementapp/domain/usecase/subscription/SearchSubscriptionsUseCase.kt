package com.example.subcriptionmanagementapp.domain.usecase.subscription

import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepository
import javax.inject.Inject

class SearchSubscriptionsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke(searchQuery: String): List<Subscription> {
        return subscriptionRepository.searchSubscriptions(searchQuery)
    }
}