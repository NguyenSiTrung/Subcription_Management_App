package com.example.subcriptionmanagementapp.domain.usecase.calendar

import com.example.subcriptionmanagementapp.data.calendar.GoogleCalendarManager
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import javax.inject.Inject

class UpdateSubscriptionInCalendarUseCase @Inject constructor(
    private val googleCalendarManager: GoogleCalendarManager
) {
    suspend operator fun invoke(subscription: Subscription, eventId: String): Boolean {
        return googleCalendarManager.updateSubscriptionInCalendar(subscription, eventId)
    }
}