package com.example.subcriptionmanagementapp.domain.usecase.reminder

import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.repository.ReminderRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetReminderUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    operator fun invoke(id: Long): Flow<Reminder?> {
        return flow { emit(reminderRepository.getReminderById(id)) }
    }
}
