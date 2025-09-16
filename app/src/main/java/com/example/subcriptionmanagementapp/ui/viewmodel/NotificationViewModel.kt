package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.usecase.notification.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val getUpcomingRemindersUseCase: GetUpcomingRemindersUseCase
) : ViewModel() {

    private val _upcomingReminders = MutableStateFlow<List<Pair<Subscription, Reminder>>>(emptyList())
    val upcomingReminders: StateFlow<List<Pair<Subscription, Reminder>>> = _upcomingReminders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUpcomingReminders()
    }

    fun loadUpcomingReminders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getUpcomingRemindersUseCase()
                    .collect { reminderList ->
                        _upcomingReminders.value = reminderList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load upcoming reminders"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun scheduleReminder(subscription: Subscription, reminder: Reminder) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                scheduleReminderUseCase(subscription, reminder)
                loadUpcomingReminders()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to schedule reminder"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelReminder(subscriptionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cancelReminderUseCase(subscriptionId)
                loadUpcomingReminders()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to cancel reminder"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}