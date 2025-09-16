package com.example.subcriptionmanagementapp.domain.usecase.notification

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.notification.ReminderManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUpcomingRemindersUseCase @Inject constructor(
    private val reminderManager: ReminderManager
) {
    operator fun invoke(): Flow<List<Pair<Subscription, Reminder>>> {
        return reminderManager.getUpcomingReminders()
    }
}