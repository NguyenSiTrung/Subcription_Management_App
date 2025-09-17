package com.example.subcriptionmanagementapp.domain.usecase.reminder

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.domain.repository.ReminderRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllRemindersUseCase
@Inject
constructor(private val reminderRepository: ReminderRepository) {
    operator fun invoke(): Flow<List<Reminder>> {
        return reminderRepository.getAllReminders()
    }
}
