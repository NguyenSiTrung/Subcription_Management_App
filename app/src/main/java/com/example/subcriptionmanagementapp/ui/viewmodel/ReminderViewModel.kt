package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.domain.usecase.reminder.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val addReminderUseCase: AddReminderUseCase,
    private val getReminderUseCase: GetReminderUseCase,
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val getRemindersBySubscriptionIdUseCase: GetRemindersBySubscriptionIdUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _selectedReminder = MutableStateFlow<Reminder?>(null)
    val selectedReminder: StateFlow<Reminder?> = _selectedReminder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadReminders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAllRemindersUseCase()
                    .collect { reminderList ->
                        _reminders.value = reminderList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load reminders"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRemindersBySubscriptionId(subscriptionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getRemindersBySubscriptionIdUseCase(subscriptionId)
                    .collect { reminderList ->
                        _reminders.value = reminderList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load reminders"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadReminder(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getReminderUseCase(id)
                    .collect { reminder ->
                        _selectedReminder.value = reminder
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load reminder"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addReminderUseCase(reminder)
                loadReminders()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add reminder"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updateReminderUseCase(reminder)
                loadReminders()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update reminder"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteReminderUseCase(reminder)
                loadReminders()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete reminder"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}