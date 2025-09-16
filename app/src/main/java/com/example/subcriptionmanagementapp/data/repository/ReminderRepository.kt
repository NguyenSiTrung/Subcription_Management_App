package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(id: Long): Reminder?
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    fun getRemindersBySubscriptionId(subscriptionId: Long): Flow<List<Reminder>>
    suspend fun getPendingReminders(currentDate: Long): List<Reminder>
    suspend fun markReminderAsNotified(reminderId: Long)
}