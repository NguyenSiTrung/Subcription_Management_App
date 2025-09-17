package com.example.subcriptionmanagementapp.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.repository.ReminderRepository
import com.example.subcriptionmanagementapp.domain.repository.SubscriptionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val subscriptionId = intent.getLongExtra("subscription_id", -1)
        val reminderId = intent.getLongExtra("reminder_id", -1)

        if (subscriptionId != -1L && reminderId != -1L) {
            val entryPoint =
                    EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            ReminderBroadcastReceiverEntryPoint::class.java
                    )
            val notificationScheduler = entryPoint.notificationScheduler()
            val subscriptionRepository = entryPoint.subscriptionRepository()
            val reminderRepository = entryPoint.reminderRepository()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get subscription and reminder from database
                    val subscription = subscriptionRepository.getSubscriptionById(subscriptionId)
                    val reminder = reminderRepository.getReminderById(reminderId)

                    if (subscription != null && reminder != null) {
                        // Show notification
                        notificationScheduler.showNotification(subscription, reminder)

                        // Update subscription's next billing date
                        val updatedSubscription =
                                subscription.copy(
                                        nextBillingDate = calculateNextBillingDate(subscription),
                                        updatedAt = System.currentTimeMillis()
                                )
                        subscriptionRepository.updateSubscription(updatedSubscription)

                        // Mark reminder as notified
                        reminderRepository.markReminderAsNotified(reminder.id)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun calculateNextBillingDate(subscription: Subscription): Long {
        val calendar =
                java.util.Calendar.getInstance().apply {
                    timeInMillis = subscription.nextBillingDate
                    when (subscription.billingCycle) {
                        BillingCycle.DAILY -> add(java.util.Calendar.DAY_OF_MONTH, 1)
                        BillingCycle.WEEKLY -> add(java.util.Calendar.WEEK_OF_YEAR, 1)
                        BillingCycle.MONTHLY -> add(java.util.Calendar.MONTH, 1)
                        BillingCycle.YEARLY -> add(java.util.Calendar.YEAR, 1)
                    }
                }
        return calendar.timeInMillis
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderBroadcastReceiverEntryPoint {
    fun notificationScheduler(): NotificationScheduler
    fun subscriptionRepository(): SubscriptionRepository
    fun reminderRepository(): ReminderRepository
}
