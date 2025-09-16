package com.example.subcriptionmanagementapp.data.notification

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    private val notificationScheduler: NotificationScheduler,
    private val subscriptionRepository: SubscriptionRepository,
    private val reminderRepository: ReminderRepository
) {

    suspend fun scheduleAllReminders() {
        val subscriptions = subscriptionRepository.getAllSubscriptions()
        val reminders = reminderRepository.getAllReminders()
        
        reminders.forEach { reminder ->
            val subscription = subscriptions.find { it.id == reminder.subscriptionId }
            if (subscription != null && subscription.isActive) {
                notificationScheduler.scheduleReminder(subscription, reminder)
            }
        }
    }

    suspend fun scheduleReminderForSubscription(subscriptionId: Long) {
        val subscription = subscriptionRepository.getSubscriptionById(subscriptionId)
        if (subscription != null && subscription.isActive) {
            val reminders = reminderRepository.getRemindersBySubscriptionId(subscriptionId)
            reminders.forEach { reminder ->
                notificationScheduler.scheduleReminder(subscription, reminder)
            }
        }
    }

    suspend fun cancelReminderForSubscription(subscriptionId: Long) {
        val reminders = reminderRepository.getRemindersBySubscriptionId(subscriptionId)
        reminders.forEach { reminder ->
            notificationScheduler.cancelReminder(reminder.id)
        }
    }

    suspend fun cancelAllReminders() {
        val reminders = reminderRepository.getAllReminders()
        reminders.forEach { reminder ->
            notificationScheduler.cancelReminder(reminder.id)
        }
    }

    suspend fun addReminder(reminder: Reminder) {
        val reminderId = reminderRepository.insertReminder(reminder)
        val newReminder = reminder.copy(id = reminderId)
        
        val subscription = subscriptionRepository.getSubscriptionById(reminder.subscriptionId)
        if (subscription != null && subscription.isActive) {
            notificationScheduler.scheduleReminder(subscription, newReminder)
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        // Cancel existing reminder
        notificationScheduler.cancelReminder(reminder.id)
        
        // Update reminder in database
        reminderRepository.updateReminder(reminder)
        
        // Schedule new reminder
        val subscription = subscriptionRepository.getSubscriptionById(reminder.subscriptionId)
        if (subscription != null && subscription.isActive) {
            notificationScheduler.scheduleReminder(subscription, reminder)
        }
    }

    suspend fun deleteReminder(reminder: Reminder) {
        // Cancel reminder
        notificationScheduler.cancelReminder(reminder.id)
        
        // Delete from database
        reminderRepository.deleteReminder(reminder)
    }

    fun getUpcomingReminders(): Flow<List<Pair<Subscription, Reminder>>> {
        return reminderRepository.getAllReminders().map { reminders ->
            val subscriptions = subscriptionRepository.getAllSubscriptions()
            reminders.mapNotNull { reminder ->
                val subscription = subscriptions.find { it.id == reminder.subscriptionId }
                if (subscription != null && subscription.isActive) {
                    Pair(subscription, reminder)
                } else {
                    null
                }
            }
        }
    }
}