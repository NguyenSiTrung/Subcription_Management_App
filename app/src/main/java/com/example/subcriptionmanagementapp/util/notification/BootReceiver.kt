package com.example.subcriptionmanagementapp.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that handles device boot events to reschedule notifications
 * after device restart.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            Log.d("BootReceiver", "Boot completed event received, rescheduling reminders")
            
            // Start a coroutine to handle the rescheduling work
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get the application context and reschedule reminders
                    val app = context.applicationContext as com.example.subcriptionmanagementapp.Application
                    
                    // Use WorkManager or a foreground service for proper background execution
                    // For now, we'll use a simple approach with a coroutine
                    rescheduleReminders(context)
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling reminders", e)
                }
            }
        }
    }

    private suspend fun rescheduleReminders(context: Context) {
        // This is a simplified approach. In a production app, you might want to use
        // WorkManager for more reliable background execution
        try {
            // For now, we'll just log that we need to reschedule
            // The actual rescheduling should be handled by the app when it starts
            Log.d("BootReceiver", "Reminder rescheduling triggered")
            
            // You could start a foreground service here or use WorkManager
            // to ensure the rescheduling happens reliably
        } catch (e: Exception) {
            Log.e("BootReceiver", "Failed to reschedule reminders", e)
        }
    }
}