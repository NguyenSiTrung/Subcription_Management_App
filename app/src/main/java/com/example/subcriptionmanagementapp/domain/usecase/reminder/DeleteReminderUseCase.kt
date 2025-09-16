package com.example.subcriptionmanagementapp.domain.usecase.reminder

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.repository.ReminderRepository
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder) {
        reminderRepository.deleteReminder(reminder)
    }
}