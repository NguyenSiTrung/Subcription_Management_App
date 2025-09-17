package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.dao.ReminderDao
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(private val reminderDao: ReminderDao) :
        ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders()
    }

    override suspend fun getReminderById(id: Long): Reminder? {
        return reminderDao.getReminderById(id)
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    override fun getRemindersBySubscriptionId(subscriptionId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersBySubscriptionId(subscriptionId)
    }

    override suspend fun getPendingReminders(currentDate: Long): List<Reminder> {
        return reminderDao.getPendingReminders(currentDate)
    }

    override suspend fun markReminderAsNotified(reminderId: Long) {
        reminderDao.markReminderAsNotified(reminderId)
    }

    override suspend fun clearAllReminders() {
        reminderDao.clearAllReminders()
    }
}
