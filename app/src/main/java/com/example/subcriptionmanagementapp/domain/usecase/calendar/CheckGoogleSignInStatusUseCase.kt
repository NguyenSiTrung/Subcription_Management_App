package com.example.subcriptionmanagementapp.domain.usecase.calendar

import com.example.subcriptionmanagementapp.data.calendar.GoogleCalendarManager
import javax.inject.Inject

class CheckGoogleSignInStatusUseCase @Inject constructor(
    private val googleCalendarManager: GoogleCalendarManager
) {
    operator fun invoke(): Boolean {
        return googleCalendarManager.isSignedIn()
    }
}