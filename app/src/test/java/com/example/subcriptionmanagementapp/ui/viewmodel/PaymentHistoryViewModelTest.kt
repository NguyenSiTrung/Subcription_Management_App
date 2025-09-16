package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.usecase.payment.*
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
class PaymentHistoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var addPaymentHistoryUseCase: AddPaymentHistoryUseCase

    @Mock
    private lateinit var getPaymentHistoryUseCase: GetPaymentHistoryUseCase

    @Mock
    private lateinit var getAllPaymentHistoryUseCase: GetAllPaymentHistoryUseCase

    @Mock
    private lateinit var getPaymentHistoryBySubscriptionIdUseCase: GetPaymentHistoryBySubscriptionIdUseCase

    @Mock
    private lateinit var getPaymentHistoryByDateRangeUseCase: GetPaymentHistoryByDateRangeUseCase

    @Mock
    private lateinit var updatePaymentHistoryUseCase: UpdatePaymentHistoryUseCase

    @Mock
    private lateinit var deletePaymentHistoryUseCase: DeletePaymentHistoryUseCase

    private lateinit var viewModel: PaymentHistoryViewModel

    @Before
    fun setUp() {
        viewModel = PaymentHistoryViewModel(
            addPaymentHistoryUseCase,
            getPaymentHistoryUseCase,
            getAllPaymentHistoryUseCase,
            getPaymentHistoryBySubscriptionIdUseCase,
            getPaymentHistoryByDateRangeUseCase,
            updatePaymentHistoryUseCase,
            deletePaymentHistoryUseCase
        )
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(
            addPaymentHistoryUseCase,
            getPaymentHistoryUseCase,
            getAllPaymentHistoryUseCase,
            getPaymentHistoryBySubscriptionIdUseCase,
            getPaymentHistoryByDateRangeUseCase,
            updatePaymentHistoryUseCase,
            deletePaymentHistoryUseCase
        )
    }

    @Test
    fun `loadPaymentHistory should update paymentHistory state`() = runTest {
        // Given
        val paymentHistory = listOf(
            PaymentHistory(
                id = 1,
                subscriptionId = 1,
                amount = 9.99,
                paymentDate = Date().time,
                paymentMethod = "Credit Card",
                status = "Completed",
                notes = "Monthly payment"
            ),
            PaymentHistory(
                id = 2,
                subscriptionId = 2,
                amount = 4.99,
                paymentDate = Date().time - 30 * 24 * 60 * 60 * 1000,
                paymentMethod = "PayPal",
                status = "Completed",
                notes = "Monthly payment"
            )
        )
        
        whenever(getAllPaymentHistoryUseCase()).thenReturn(flow { emit(paymentHistory) })

        // When
        viewModel.loadPaymentHistory()

        // Then
        viewModel.paymentHistory.test {
            assertEquals(paymentHistory, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getAllPaymentHistoryUseCase).invoke()
    }

    @Test
    fun `loadPaymentHistoryBySubscriptionId should update paymentHistory state`() = runTest {
        // Given
        val subscriptionId = 1L
        val paymentHistory = listOf(
            PaymentHistory(
                id = 1,
                subscriptionId = subscriptionId,
                amount = 9.99,
                paymentDate = Date().time,
                paymentMethod = "Credit Card",
                status = "Completed",
                notes = "Monthly payment"
            )
        )
        
        whenever(getPaymentHistoryBySubscriptionIdUseCase(subscriptionId)).thenReturn(flow { emit(paymentHistory) })

        // When
        viewModel.loadPaymentHistoryBySubscriptionId(subscriptionId)

        // Then
        viewModel.paymentHistory.test {
            assertEquals(paymentHistory, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getPaymentHistoryBySubscriptionIdUseCase).invoke(subscriptionId)
    }

    @Test
    fun `loadPaymentHistoryByDateRange should update paymentHistory state`() = runTest {
        // Given
        val startDate = Date().time - 30 * 24 * 60 * 60 * 1000
        val endDate = Date().time
        val paymentHistory = listOf(
            PaymentHistory(
                id = 1,
                subscriptionId = 1,
                amount = 9.99,
                paymentDate = Date().time,
                paymentMethod = "Credit Card",
                status = "Completed",
                notes = "Monthly payment"
            )
        )
        
        whenever(getPaymentHistoryByDateRangeUseCase(startDate, endDate)).thenReturn(flow { emit(paymentHistory) })

        // When
        viewModel.loadPaymentHistoryByDateRange(startDate, endDate)

        // Then
        viewModel.paymentHistory.test {
            assertEquals(paymentHistory, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getPaymentHistoryByDateRangeUseCase).invoke(startDate, endDate)
    }

    @Test
    fun `loadPaymentHistory should update totalPayment state`() = runTest {
        // Given
        val paymentHistory = listOf(
            PaymentHistory(
                id = 1,
                subscriptionId = 1,
                amount = 9.99,
                paymentDate = Date().time,
                paymentMethod = "Credit Card",
                status = "Completed",
                notes = "Monthly payment"
            ),
            PaymentHistory(
                id = 2,
                subscriptionId = 2,
                amount = 4.99,
                paymentDate = Date().time,
                paymentMethod = "PayPal",
                status = "Completed",
                notes = "Monthly payment"
            )
        )
        
        whenever(getAllPaymentHistoryUseCase()).thenReturn(flow { emit(paymentHistory) })

        // When
        viewModel.loadPaymentHistory()

        // Then
        viewModel.totalPayment.test {
            assertEquals(14.98, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getAllPaymentHistoryUseCase).invoke()
    }

    @Test
    fun `addPaymentHistory should call use case and reload paymentHistory`() = runTest {
        // Given
        val paymentHistory = PaymentHistory(
            id = 0,
            subscriptionId = 1,
            amount = 9.99,
            paymentDate = Date().time,
            paymentMethod = "Credit Card",
            status = "Completed",
            notes = "Monthly payment"
        )
        
        val paymentHistoryList = listOf(paymentHistory.copy(id = 1))
        whenever(getAllPaymentHistoryUseCase()).thenReturn(flow { emit(paymentHistoryList) })

        // When
        viewModel.addPaymentHistory(paymentHistory)

        // Then
        verify(addPaymentHistoryUseCase).invoke(paymentHistory)
        verify(getAllPaymentHistoryUseCase).invoke()
        
        viewModel.paymentHistory.test {
            assertEquals(paymentHistoryList, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `updatePaymentHistory should call use case and reload paymentHistory`() = runTest {
        // Given
        val paymentHistory = PaymentHistory(
            id = 1,
            subscriptionId = 1,
            amount = 9.99,
            paymentDate = Date().time,
            paymentMethod = "Credit Card",
            status = "Completed",
            notes = "Monthly payment"
        )
        
        val paymentHistoryList = listOf(paymentHistory)
        whenever(getAllPaymentHistoryUseCase()).thenReturn(flow { emit(paymentHistoryList) })

        // When
        viewModel.updatePaymentHistory(paymentHistory)

        // Then
        verify(updatePaymentHistoryUseCase).invoke(paymentHistory)
        verify(getAllPaymentHistoryUseCase).invoke()
        
        viewModel.paymentHistory.test {
            assertEquals(paymentHistoryList, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `deletePaymentHistory should call use case and reload paymentHistory`() = runTest {
        // Given
        val paymentHistory = PaymentHistory(
            id = 1,
            subscriptionId = 1,
            amount = 9.99,
            paymentDate = Date().time,
            paymentMethod = "Credit Card",
            status = "Completed",
            notes = "Monthly payment"
        )
        
        val paymentHistoryList = emptyList<PaymentHistory>()
        whenever(getAllPaymentHistoryUseCase()).thenReturn(flow { emit(paymentHistoryList) })

        // When
        viewModel.deletePaymentHistory(paymentHistory)

        // Then
        verify(deletePaymentHistoryUseCase).invoke(paymentHistory)
        verify(getAllPaymentHistoryUseCase).invoke()
        
        viewModel.paymentHistory.test {
            assertEquals(paymentHistoryList, awaitItem())
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