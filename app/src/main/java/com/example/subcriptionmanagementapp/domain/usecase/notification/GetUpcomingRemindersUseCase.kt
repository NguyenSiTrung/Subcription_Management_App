package com.example.subcriptionmanagementapp.domain.usecase.notification

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.notification.ReminderManager
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUpcomingRemindersUseCase
@Inject
constructor(private val reminderManager: ReminderManager) {
    operator fun invoke(): Flow<List<Pair<Subscription, Reminder>>> {
        return kotlinx.coroutines.flow.flow {
            reminderManager.refreshAllReminders().collect { reminderSubscriptionPairs ->
                val result =
                        reminderSubscriptionPairs.map { (reminder, subscription) ->
                            subscription to reminder
                        }
                emit(result)
            }
        }
    }
}
