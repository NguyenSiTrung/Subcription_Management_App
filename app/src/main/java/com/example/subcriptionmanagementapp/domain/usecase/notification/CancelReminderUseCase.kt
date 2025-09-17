package com.example.subcriptionmanagementapp.domain.usecase.notification

import com.example.subcriptionmanagementapp.data.notification.ReminderManager
import javax.inject.Inject

class CancelReminderUseCase @Inject constructor(private val reminderManager: ReminderManager) {
    suspend operator fun invoke(subscriptionId: Long) {
        reminderManager.cancelRemindersForSubscription(subscriptionId)
    }
}
