package com.example.subcriptionmanagementapp.data.local.dao

import androidx.room.*
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY reminder_date ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update suspend fun updateReminder(reminder: Reminder)

    @Delete suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE subscription_id = :subscriptionId")
    fun getRemindersBySubscriptionId(subscriptionId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE is_notified = 0 AND reminder_date <= :currentDate")
    suspend fun getPendingReminders(currentDate: Long): List<Reminder>

    @Query("UPDATE reminders SET is_notified = 1 WHERE id = :reminderId")
    suspend fun markReminderAsNotified(reminderId: Long)

    @Query("DELETE FROM reminders") suspend fun clearAllReminders()
}
