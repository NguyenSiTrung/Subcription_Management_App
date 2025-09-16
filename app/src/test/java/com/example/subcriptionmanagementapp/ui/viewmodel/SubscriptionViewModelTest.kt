package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.repository.SubscriptionRepository
import com.example.subcriptionmanagementapp.domain.usecase.subscription.*
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
class SubscriptionViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var subscriptionRepository: SubscriptionRepository

    @Mock
    private lateinit var addSubscriptionUseCase: AddSubscriptionUseCase

    @Mock
    private lateinit var getSubscriptionUseCase: GetSubscriptionUseCase

    @Mock
    private lateinit var getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase

    @Mock
    private lateinit var updateSubscriptionUseCase: UpdateSubscriptionUseCase

    @Mock
    private lateinit var deleteSubscriptionUseCase: DeleteSubscriptionUseCase

    private lateinit var viewModel: SubscriptionViewModel

    @Before
    fun setUp() {
        viewModel = SubscriptionViewModel(
            addSubscriptionUseCase,
            getSubscriptionUseCase,
            getAllSubscriptionsUseCase,
            updateSubscriptionUseCase,
            deleteSubscriptionUseCase
        )
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(
            addSubscriptionUseCase,
            getSubscriptionUseCase,
            getAllSubscriptionsUseCase,
            updateSubscriptionUseCase,
            deleteSubscriptionUseCase
        )
    }

    @Test
    fun `loadSubscriptions should update subscriptions state`() = runTest {
        // Given
        val subscriptions = listOf(
            Subscription(
                id = 1,
                name = "Netflix",
                price = 9.99,
                billingCycle = com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY,
                nextBillingDate = Date().time + 30 * 24 * 60 * 60 * 1000,
                isActive = true,
                reminderDays = 3
            ),
            Subscription(
                id = 2,
                name = "Spotify",
                price = 4.99,
                billingCycle = com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY,
                nextBillingDate = Date().time + 15 * 24 * 60 * 60 * 1000,
                isActive = true,
                reminderDays = 5
            )
        )
        
        whenever(getAllSubscriptionsUseCase()).thenReturn(flow { emit(subscriptions) })

        // When
        viewModel.loadSubscriptions()

        // Then
        viewModel.subscriptions.test {
            assertEquals(subscriptions, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getAllSubscriptionsUseCase).invoke()
    }

    @Test
    fun `loadSubscription should update selectedSubscription state`() = runTest {
        // Given
        val subscription = Subscription(
            id = 1,
            name = "Netflix",
            price = 9.99,
            billingCycle = com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY,
            nextBillingDate = Date().time + 30 * 24 * 60 * 60 * 1000,
            isActive = true,
            reminderDays = 3
        )
        
        whenever(getSubscriptionUseCase(1)).thenReturn(flow { emit(subscription) })

        // When
        viewModel.loadSubscription(1)

        // Then
        viewModel.selectedSubscription.test {
            assertEquals(subscription, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getSubscriptionUseCase).invoke(1)
    }

    @Test
    fun `addSubscription should call use case and reload subscriptions`() = runTest {
        // Given
        val subscription = Subscription(
            id = 0,
            name = "Netflix",
            price = 9.99,
            billingCycle = com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY,
            nextBillingDate = Date().time + 30 * 24 * 60 * 60 * 1000,
            isActive = true,
            reminderDays = 3
        )
        
        val subscriptions = listOf(subscription.copy(id = 1))
        whenever(getAllSubscriptionsUseCase()).thenReturn(flow { emit(subscriptions) })

        // When
        viewModel.addSubscription(subscription)

        // Then
        verify(addSubscriptionUseCase).invoke(subscription)
        verify(getAllSubscriptionsUseCase).invoke()
        
        viewModel.subscriptions.test {
            assertEquals(subscriptions, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `updateSubscription should call use case and reload subscriptions`() = runTest {
        // Given
        val subscription = Subscription(
            id = 1,
            name = "Netflix",
            price = 9.99,
            billingCycle = com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY,
            nextBillingDate = Date().time + 30 * 24 * 60 * 60 * 1000,
            isActive = true,
            reminderDays = 3
        )
        
        val subscriptions = listOf(subscription)
        whenever(getAllSubscriptionsUseCase()).thenReturn(flow { emit(subscriptions) })

        // When
        viewModel.updateSubscription(subscription)

        // Then
        verify(updateSubscriptionUseCase).invoke(subscription)
        verify(getAllSubscriptionsUseCase).invoke()
        
        viewModel.subscriptions.test {
            assertEquals(subscriptions, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `deleteSubscription should call use case and reload subscriptions`() = runTest {
        // Given
        val subscription = Subscription(
            id = 1,
            name = "Netflix",
            price = 9.99,
            billingCycle = com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.MONTHLY,
            nextBillingDate = Date().time + 30 * 24 * 60 * 60 * 1000,
            isActive = true,
            reminderDays = 3
        )
        
        val subscriptions = emptyList<Subscription>()
        whenever(getAllSubscriptionsUseCase()).thenReturn(flow { emit(subscriptions) })

        // When
        viewModel.deleteSubscription(subscription)

        // Then
        verify(deleteSubscriptionUseCase).invoke(subscription)
        verify(getAllSubscriptionsUseCase).invoke()
        
        viewModel.subscriptions.test {
            assertEquals(subscriptions, awaitItem())
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