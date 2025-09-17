package com.example.subcriptionmanagementapp.domain.usecase.subscription

import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSubscriptionUseCase
@Inject
constructor(private val subscriptionRepository: SubscriptionRepository) {
    operator fun invoke(id: Long): Flow<Subscription?> {
        // Since repository returns a single item, we need to convert it to Flow
        return subscriptionRepository.getAllSubscriptions().map { subscriptions ->
            subscriptions.find { it.id == id }
        }
    }
}
