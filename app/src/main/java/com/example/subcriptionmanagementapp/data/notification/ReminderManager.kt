package com.example.subcriptionmanagementapp.data.notification

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.repository.ReminderRepository
import com.example.subcriptionmanagementapp.domain.repository.SubscriptionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class ReminderManager
@Inject
constructor(
        private val notificationScheduler: NotificationScheduler,
        private val subscriptionRepository: SubscriptionRepository,
        private val reminderRepository: ReminderRepository
) {

    suspend fun scheduleAllReminders() {
        val subscriptions = subscriptionRepository.getAllSubscriptions().first()
        val reminders = reminderRepository.getAllReminders().first()

        reminders.forEach { reminder ->
            val subscription = subscriptions.find { it.id == reminder.subscriptionId }
            if (subscription != null && subscription.isActive) {
                notificationScheduler.scheduleReminder(subscription, reminder)
            }
        }
    }

    suspend fun scheduleRemindersForSubscription(subscriptionId: Long) {
        val subscription = subscriptionRepository.getSubscriptionById(subscriptionId)
        if (subscription != null && subscription.isActive) {
            val reminders = reminderRepository.getRemindersBySubscriptionId(subscriptionId).first()
            reminders.forEach { reminder ->
                notificationScheduler.scheduleReminder(subscription, reminder)
            }
        }
    }

    suspend fun cancelRemindersForSubscription(subscriptionId: Long) {
        val reminders = reminderRepository.getRemindersBySubscriptionId(subscriptionId).first()
        reminders.forEach { reminder -> notificationScheduler.cancelReminder(reminder.id) }
    }

    suspend fun cancelAllReminders() {
        val reminders = reminderRepository.getAllReminders().first()
        reminders.forEach { reminder -> notificationScheduler.cancelReminder(reminder.id) }
    }

    suspend fun addReminder(reminder: Reminder) {
        val reminderId = reminderRepository.insertReminder(reminder)

        // Schedule the notification
        val subscription = subscriptionRepository.getSubscriptionById(reminder.subscriptionId)
        if (subscription != null && subscription.isActive) {
            notificationScheduler.scheduleReminder(subscription, reminder)
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        // Cancel existing reminder
        notificationScheduler.cancelReminder(reminder.id)

        // Update in database
        reminderRepository.updateReminder(reminder)

        // Reschedule if subscription is active
        val subscription = subscriptionRepository.getSubscriptionById(reminder.subscriptionId)
        if (subscription != null && subscription.isActive) {
            notificationScheduler.scheduleReminder(subscription, reminder)
        }
    }

    suspend fun deleteReminder(reminder: Reminder) {
        // Cancel notification
        notificationScheduler.cancelReminder(reminder.id)
        // Delete from database
        reminderRepository.deleteReminder(reminder)
    }

    suspend fun refreshAllReminders(): Flow<List<Pair<Reminder, Subscription>>> {
        return reminderRepository.getAllReminders().map { reminders ->
            val subscriptions = subscriptionRepository.getAllSubscriptions().first()
            reminders.mapNotNull { reminder ->
                subscriptions
                        .find { subscription -> subscription.id == reminder.subscriptionId }
                        ?.let { subscription ->
                            if (subscription.isActive) reminder to subscription else null
                        }
            }
        }
    }
}
