package com.example.subcriptionmanagementapp.domain.usecase.calendar

import com.example.subcriptionmanagementapp.data.calendar.GoogleCalendarManager
import javax.inject.Inject

class RemoveSubscriptionFromCalendarUseCase @Inject constructor(
    private val googleCalendarManager: GoogleCalendarManager
) {
    suspend operator fun invoke(eventId: String): Boolean {
        return googleCalendarManager.removeSubscriptionFromCalendar(eventId)
    }
}