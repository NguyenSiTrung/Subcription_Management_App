package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.usecase.reminder.*
import com.example.subcriptionmanagementapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ReminderViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var addReminderUseCase: AddReminderUseCase

    @Mock
    private lateinit var getReminderUseCase: GetReminderUseCase

    @Mock
    private lateinit var getAllRemindersUseCase: GetAllRemindersUseCase

    @Mock
    private lateinit var getRemindersBySubscriptionIdUseCase: GetRemindersBySubscriptionIdUseCase

    @Mock
    private lateinit var updateReminderUseCase: UpdateReminderUseCase

    @Mock
    private lateinit var deleteReminderUseCase: DeleteReminderUseCase

    private lateinit var viewModel: ReminderViewModel

    @Before
    fun setUp() {
        viewModel = ReminderViewModel(
            addReminderUseCase,
            getReminderUseCase,
            getAllRemindersUseCase,
            getRemindersBySubscriptionIdUseCase,
            updateReminderUseCase,
            deleteReminderUseCase
        )
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(
            addReminderUseCase,
            getReminderUseCase,
            getAllRemindersUseCase,
            getRemindersBySubscriptionIdUseCase,
            updateReminderUseCase,
            deleteReminderUseCase
        )
    }

    @Test
    fun `loadReminders should update reminders state`() = runTest {
        // Given
        val reminders = listOf(
            Reminder(
                id = 1,
                subscriptionId = 1,
                daysBefore = 3,
                time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(10, 0),
                type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
                isActive = true
            ),
            Reminder(
                id = 2,
                subscriptionId = 2,
                daysBefore = 5,
                time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(15, 30),
                type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
                isActive = true
            )
        )
        
        whenever(getAllRemindersUseCase()).thenReturn(flow { emit(reminders) })

        // When
        viewModel.loadReminders()

        // Then
        viewModel.reminders.test {
            assertEquals(reminders, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getAllRemindersUseCase).invoke()
    }

    @Test
    fun `loadRemindersBySubscriptionId should update reminders state`() = runTest {
        // Given
        val subscriptionId = 1L
        val reminders = listOf(
            Reminder(
                id = 1,
                subscriptionId = subscriptionId,
                daysBefore = 3,
                time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(10, 0),
                type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
                isActive = true
            )
        )
        
        whenever(getRemindersBySubscriptionIdUseCase(subscriptionId)).thenReturn(flow { emit(reminders) })

        // When
        viewModel.loadRemindersBySubscriptionId(subscriptionId)

        // Then
        viewModel.reminders.test {
            assertEquals(reminders, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getRemindersBySubscriptionIdUseCase).invoke(subscriptionId)
    }

    @Test
    fun `loadReminder should update selectedReminder state`() = runTest {
        // Given
        val reminder = Reminder(
            id = 1,
            subscriptionId = 1,
            daysBefore = 3,
            time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(10, 0),
            type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
            isActive = true
        )
        
        whenever(getReminderUseCase(1)).thenReturn(flow { emit(reminder) })

        // When
        viewModel.loadReminder(1)

        // Then
        viewModel.selectedReminder.test {
            assertEquals(reminder, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getReminderUseCase).invoke(1)
    }

    @Test
    fun `addReminder should call use case and reload reminders`() = runTest {
        // Given
        val reminder = Reminder(
            id = 0,
            subscriptionId = 1,
            daysBefore = 3,
            time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(10, 0),
            type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
            isActive = true
        )
        
        val reminders = listOf(reminder.copy(id = 1))
        whenever(getAllRemindersUseCase()).thenReturn(flow { emit(reminders) })

        // When
        viewModel.addReminder(reminder)

        // Then
        verify(addReminderUseCase).invoke(reminder)
        verify(getAllRemindersUseCase).invoke()
        
        viewModel.reminders.test {
            assertEquals(reminders, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `updateReminder should call use case and reload reminders`() = runTest {
        // Given
        val reminder = Reminder(
            id = 1,
            subscriptionId = 1,
            daysBefore = 3,
            time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(10, 0),
            type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
            isActive = true
        )
        
        val reminders = listOf(reminder)
        whenever(getAllRemindersUseCase()).thenReturn(flow { emit(reminders) })

        // When
        viewModel.updateReminder(reminder)

        // Then
        verify(updateReminderUseCase).invoke(reminder)
        verify(getAllRemindersUseCase).invoke()
        
        viewModel.reminders.test {
            assertEquals(reminders, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `deleteReminder should call use case and reload reminders`() = runTest {
        // Given
        val reminder = Reminder(
            id = 1,
            subscriptionId = 1,
            daysBefore = 3,
            time = com.example.subcriptionmanagementapp.data.local.entity.ReminderTime(10, 0),
            type = com.example.subcriptionmanagementapp.data.local.entity.ReminderType.NOTIFICATION,
            isActive = true
        )
        
        val reminders = emptyList<Reminder>()
        whenever(getAllRemindersUseCase()).thenReturn(flow { emit(reminders) })

        // When
        viewModel.deleteReminder(reminder)

        // Then
        verify(deleteReminderUseCase).invoke(reminder)
        verify(getAllRemindersUseCase).invoke()
        
        viewModel.reminders.test {
            assertEquals(reminders, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `clearError should set error state to null`() = runTest {
        // Given
        viewModel._error.value = "Test error"

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }
}