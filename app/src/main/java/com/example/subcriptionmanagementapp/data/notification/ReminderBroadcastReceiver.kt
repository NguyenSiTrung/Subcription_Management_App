package com.example.subcriptionmanagementapp.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    @Inject
    lateinit var subscriptionRepository: SubscriptionRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        val subscriptionId = intent.getLongExtra("subscription_id", -1)
        val reminderId = intent.getLongExtra("reminder_id", -1)

        if (subscriptionId != -1L && reminderId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val subscription = subscriptionRepository.getSubscriptionById(subscriptionId)
                    val reminder = reminderRepository.getReminderById(reminderId)

                    if (subscription != null && reminder != null) {
                        notificationScheduler.showNotification(subscription, reminder)
                        
                        // Schedule next reminder if needed
                        val nextBillingDate = calculateNextBillingDate(subscription)
                        if (nextBillingDate > System.currentTimeMillis()) {
                            val updatedSubscription = subscription.copy(
                                nextBillingDate = nextBillingDate
                            )
                            subscriptionRepository.updateSubscription(updatedSubscription)
                            
                            notificationScheduler.scheduleReminder(updatedSubscription, reminder)
                        }
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    private fun calculateNextBillingDate(subscription: Subscription): Long {
        val calendar = java.util.Calendar.getInstance().apply {
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