package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.usecase.calendar.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GoogleCalendarViewModel
@Inject
constructor(
        private val addSubscriptionToCalendarUseCase: AddSubscriptionToCalendarUseCase,
        private val updateSubscriptionInCalendarUseCase: UpdateSubscriptionInCalendarUseCase,
        private val removeSubscriptionFromCalendarUseCase: RemoveSubscriptionFromCalendarUseCase,
        private val checkGoogleSignInStatusUseCase: CheckGoogleSignInStatusUseCase
) : ViewModel() {

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        checkSignInStatus()
    }

    fun checkSignInStatus() {
        _isSignedIn.value = checkGoogleSignInStatusUseCase()
    }

    fun requestGoogleSignIn(launcher: ActivityResultLauncher<android.content.Intent>) {
        // This would typically use Google Sign-In API
        // For now, we'll just show a message
        _successMessage.value = "Google Sign-In requested"
    }

    fun addSubscriptionToCalendar(subscription: Subscription) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val eventId = addSubscriptionToCalendarUseCase(subscription)
                if (eventId != null) {
                    _successMessage.value = "Subscription added to Google Calendar"
                } else {
                    _error.value = "Failed to add subscription to Google Calendar"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add subscription to Google Calendar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSubscriptionInCalendar(subscription: Subscription, eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = updateSubscriptionInCalendarUseCase(subscription, eventId)
                if (success) {
                    _successMessage.value = "Subscription updated in Google Calendar"
                } else {
                    _error.value = "Failed to update subscription in Google Calendar"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update subscription in Google Calendar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeSubscriptionFromCalendar(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = removeSubscriptionFromCalendarUseCase(eventId)
                if (success) {
                    _successMessage.value = "Subscription removed from Google Calendar"
                } else {
                    _error.value = "Failed to remove subscription from Google Calendar"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to remove subscription from Google Calendar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
