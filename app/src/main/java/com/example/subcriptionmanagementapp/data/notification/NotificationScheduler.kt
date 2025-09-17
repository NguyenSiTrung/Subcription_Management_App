package com.example.subcriptionmanagementapp.data.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.subcriptionmanagementapp.MainActivity
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "subscription_reminders"
        private const val NOTIFICATION_CHANNEL_NAME = "Subscription Reminders"
        private const val NOTIFICATION_ID = 1
        private const val REMINDER_REQUEST_CODE = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                                    NOTIFICATION_CHANNEL_ID,
                                    NOTIFICATION_CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_HIGH
                            )
                            .apply {
                                description = "Notifications for subscription reminders"
                                enableLights(true)
                                enableVibration(true)
                            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(subscription: Subscription, reminder: Reminder) {
        val reminderTime = reminder.reminderDate
        val intent = createReminderIntent(subscription, reminder)
        val pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REMINDER_REQUEST_CODE + reminder.id.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
            )
        } catch (e: SecurityException) {
            // Handle the case where the app doesn't have permission to schedule exact alarms
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
        }
    }

    fun cancelReminder(reminderId: Long) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REMINDER_REQUEST_CODE + reminderId.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(subscription: Subscription, reminder: Reminder) {
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.subscription_reminder))
                        .setContentText(
                                context.getString(
                                        R.string.subscription_reminder_message,
                                        subscription.name
                                )
                        )
                        .setStyle(
                                NotificationCompat.BigTextStyle()
                                        .bigText(
                                                context.getString(
                                                        R.string.subscription_reminder_message,
                                                        subscription.name
                                                )
                                        )
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

        notificationManager.notify(NOTIFICATION_ID + reminder.id.toInt(), notification)
    }

    private fun calculateReminderTime(subscription: Subscription, reminder: Reminder): Long {
        return reminder.reminderDate
    }

    private fun createReminderIntent(subscription: Subscription, reminder: Reminder): Intent {
        return Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("subscription_id", subscription.id)
            putExtra("reminder_id", reminder.id)
            putExtra("subscription_name", subscription.name)
        }
    }
}
