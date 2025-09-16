package com.example.subcriptionmanagementapp.domain.usecase.reminder

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.repository.ReminderRepository
import javax.inject.Inject

class GetPendingRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(currentDate: Long): List<Reminder> {
        return reminderRepository.getPendingReminders(currentDate)
    }
}