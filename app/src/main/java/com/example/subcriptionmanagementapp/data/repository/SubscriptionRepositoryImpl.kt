package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.dao.CategoryDao
import com.example.subcriptionmanagementapp.data.local.dao.PaymentHistoryDao
import com.example.subcriptionmanagementapp.data.local.dao.ReminderDao
import com.example.subcriptionmanagementapp.data.local.dao.SubscriptionDao
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.ReminderType
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
        private val subscriptionDao: SubscriptionDao,
        private val categoryDao: CategoryDao,
        private val reminderDao: ReminderDao,
        private val paymentHistoryDao: PaymentHistoryDao
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getAllSubscriptions()
    }

    override fun getActiveSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getActiveSubscriptions()
    }

    override suspend fun getSubscriptionById(id: Long): Subscription? {
        return subscriptionDao.getSubscriptionById(id)
    }

    override suspend fun insertSubscription(subscription: Subscription): Long {
        val subscriptionId = subscriptionDao.insertSubscription(subscription)

        // Create reminder for the subscription
        if (subscription.reminderDays > 0) {
            val reminderDate =
                    subscription.nextBillingDate - (subscription.reminderDays * 24 * 60 * 60 * 1000)
            val reminder =
                    Reminder(
                            subscriptionId = subscriptionId,
                            reminderDate = reminderDate,
                            reminderType = ReminderType.RENEWAL,
                            isNotified = false,
                            notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                    )
            reminderDao.insertReminder(reminder)
        }

        return subscriptionId
    }

    override suspend fun updateSubscription(subscription: Subscription) {
        subscriptionDao.updateSubscription(subscription)

        // Update reminder if needed
        val reminders = reminderDao.getRemindersBySubscriptionId(subscription.id).first()
        for (reminder in reminders) {
            if (reminder.reminderType == ReminderType.RENEWAL) {
                val newReminderDate =
                        subscription.nextBillingDate -
                                (subscription.reminderDays * 24 * 60 * 60 * 1000)
                val updatedReminder =
                        reminder.copy(
                                reminderDate = newReminderDate,
                                updatedAt = System.currentTimeMillis()
                        )
                reminderDao.updateReminder(updatedReminder)
            }
        }
    }

    override suspend fun deleteSubscription(subscription: Subscription) {
        // Delete related reminders
        val reminders = reminderDao.getRemindersBySubscriptionId(subscription.id).first()
        for (reminder in reminders) {
            reminderDao.deleteReminder(reminder)
        }

        // Delete related payment history
        val paymentHistory =
                paymentHistoryDao.getPaymentHistoryBySubscriptionId(subscription.id).first()
        for (payment in paymentHistory) {
            paymentHistoryDao.deletePaymentHistory(payment)
        }

        // Delete subscription
        subscriptionDao.deleteSubscription(subscription)
    }

    override fun getSubscriptionsByCategory(categoryId: Long): Flow<List<Subscription>> {
        return subscriptionDao.getSubscriptionsByCategory(categoryId)
    }

    override suspend fun getSubscriptionsByBillingDateRange(
            startDate: Long,
            endDate: Long
    ): List<Subscription> {
        return subscriptionDao.getSubscriptionsByBillingDateRange(startDate, endDate)
    }

    override suspend fun searchSubscriptions(searchQuery: String): List<Subscription> {
        return subscriptionDao.searchSubscriptions(searchQuery)
    }

    override suspend fun clearAllSubscriptions() {
        subscriptionDao.clearAllSubscriptions()
    }
}
