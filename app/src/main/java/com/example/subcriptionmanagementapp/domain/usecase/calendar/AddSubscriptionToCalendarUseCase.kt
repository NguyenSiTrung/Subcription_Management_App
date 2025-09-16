package com.example.subcriptionmanagementapp.domain.usecase.calendar

import com.example.subcriptionmanagementapp.data.calendar.GoogleCalendarManager
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import javax.inject.Inject

class AddSubscriptionToCalendarUseCase @Inject constructor(
    private val googleCalendarManager: GoogleCalendarManager
) {
    suspend operator fun invoke(subscription: Subscription): String? {
        return googleCalendarManager.addSubscriptionToCalendar(subscription)
    }
}