package com.example.subcriptionmanagementapp.data.calendar

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCalendarManager @Inject constructor(
    private val context: Context
) {
    private val HTTP_TRANSPORT = NetHttpTransport()
    private val JSON_FACTORY = GsonFactory.getDefaultInstance()
    
    private var calendarService: Calendar? = null
    
    companion object {
        private const val CALENDAR_ID = "primary"
        private const val APPLICATION_NAME = "Subscription Management App"
        private const val CALENDAR_EVENT_PREFIX = "[Subscription] "
    }
    
    suspend fun initialize(credential: GoogleAccountCredential) {
        calendarService = Calendar.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            credential
        )
            .setApplicationName(APPLICATION_NAME)
            .build()
    }
    
    suspend fun addSubscriptionToCalendar(subscription: Subscription): String? {
        if (calendarService == null) {
            return null
        }
        
        try {
            val event = com.google.api.services.calendar.model.Event().apply {
                summary = CALENDAR_EVENT_PREFIX + subscription.name
                description = buildEventDescription(subscription)
                
                // Set start time
                val startTime = com.google.api.services.calendar.model.EventDateTime().apply {
                    dateTime = com.google.api.client.util.DateTime(subscription.nextBillingDate)
                    timeZone = TimeZone.getDefault().id
                }
                setStart(startTime)
                
                // Set end time (1 hour after start time)
                val endTime = com.google.api.services.calendar.model.EventDateTime().apply {
                    dateTime = com.google.api.client.util.DateTime(subscription.nextBillingDate + 3600000) // 1 hour in milliseconds
                    timeZone = TimeZone.getDefault().id
                }
                setEnd(endTime)
                
                // Set recurrence based on billing cycle
                recurrence = listOf(buildRecurrenceRule(subscription.billingCycle))
                
                // Add reminders
                reminders = com.google.api.services.calendar.model.Event.Reminders().apply {
                    useDefault = false
                    overrides = listOf(
                        com.google.api.services.calendar.model.EventReminder().apply {
                            method = "email"
                            minutes = subscription.reminderDays * 24 * 60 // Convert days to minutes
                        },
                        com.google.api.services.calendar.model.EventReminder().apply {
                            method = "popup"
                            minutes = subscription.reminderDays * 24 * 60 // Convert days to minutes
                        }
                    )
                }
            }
            
            val result = calendarService!!.events().insert(CALENDAR_ID, event).execute()
            return result.id
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    suspend fun updateSubscriptionInCalendar(subscription: Subscription, eventId: String): Boolean {
        if (calendarService == null) {
            return false
        }
        
        try {
            val event = calendarService!!.events().get(CALENDAR_ID, eventId).execute()
            
            event.summary = CALENDAR_EVENT_PREFIX + subscription.name
            event.description = buildEventDescription(subscription)
            
            // Update start time
            val startTime = com.google.api.services.calendar.model.EventDateTime().apply {
                dateTime = com.google.api.client.util.DateTime(subscription.nextBillingDate)
                timeZone = TimeZone.getDefault().id
            }
            event.setStart(startTime)
            
            // Update end time (1 hour after start time)
            val endTime = com.google.api.services.calendar.model.EventDateTime().apply {
                dateTime = com.google.api.client.util.DateTime(subscription.nextBillingDate + 3600000) // 1 hour in milliseconds
                timeZone = TimeZone.getDefault().id
            }
            event.setEnd(endTime)
            
            // Update recurrence
            event.recurrence = listOf(buildRecurrenceRule(subscription.billingCycle))
            
            // Update reminders
            event.reminders = com.google.api.services.calendar.model.Event.Reminders().apply {
                useDefault = false
                overrides = listOf(
                    com.google.api.services.calendar.model.EventReminder().apply {
                        method = "email"
                        minutes = subscription.reminderDays * 24 * 60 // Convert days to minutes
                    },
                    com.google.api.services.calendar.model.EventReminder().apply {
                        method = "popup"
                        minutes = subscription.reminderDays * 24 * 60 // Convert days to minutes
                    }
                )
            }
            
            calendarService!!.events().update(CALENDAR_ID, eventId, event).execute()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    suspend fun removeSubscriptionFromCalendar(eventId: String): Boolean {
        if (calendarService == null) {
            return false
        }
        
        try {
            calendarService!!.events().delete(CALENDAR_ID, eventId).execute()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    private fun buildEventDescription(subscription: Subscription): String {
        val description = StringBuilder()
        description.append("Subscription: ${subscription.name}\n")
        description.append("Price: ${subscription.price}\n")
        description.append("Billing Cycle: ${subscription.billingCycle.name}\n")
        
        if (subscription.description != null) {
            description.append("Description: ${subscription.description}\n")
        }
        
        if (subscription.websiteUrl != null) {
            description.append("Website: ${subscription.websiteUrl}\n")
        }
        
        if (subscription.notes != null) {
            description.append("Notes: ${subscription.notes}\n")
        }
        
        return description.toString()
    }
    
    private fun buildRecurrenceRule(billingCycle: com.example.subcriptionmanagementapp.data.local.entity.BillingCycle): String {
        return when (billingCycle) {
            com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.DAILY -> "RRULE:FREQ=DAILY"
            com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.WEEKLY -> "RRULE:FREQ=WEEKLY"
            com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY -> "RRULE:FREQ=MONTHLY"
            com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.YEARLY -> "RRULE:FREQ=YEARLY"
        }
    }
    
    fun requestGoogleSignIn(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = getGoogleSignInIntent()
        launcher.launch(signInIntent)
    }
    
    private fun getGoogleSignInIntent(): Intent {
        // This would typically use Google Sign-In API
        // For now, we'll return a placeholder intent
        return Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com"))
    }
    
    fun isSignedIn(): Boolean {
        // This would typically check if the user is signed in with Google
        // For now, we'll return false
        return false
    }
    
    fun signOut() {
        // This would typically sign the user out from Google
        // For now, we'll do nothing
    }
}