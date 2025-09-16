package com.example.subcriptionmanagementapp.domain.usecase.reminder

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(id: Long): Flow<Reminder?> {
        return reminderRepository.getReminderById(id)
    }
}