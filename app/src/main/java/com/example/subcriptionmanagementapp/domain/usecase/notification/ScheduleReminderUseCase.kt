package com.example.subcriptionmanagementapp.domain.usecase.notification

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.notification.ReminderManager
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val reminderManager: ReminderManager
) {
    suspend operator fun invoke(subscription: Subscription, reminder: Reminder) {
        reminderManager.scheduleReminderForSubscription(subscription.id)
    }
}