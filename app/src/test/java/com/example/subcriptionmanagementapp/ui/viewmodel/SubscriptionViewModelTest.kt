package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import com.example.subcriptionmanagementapp.domain.usecase.category.AddCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.GetAllCategoriesUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.GetCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.SeedDefaultCategoriesUseCase
import com.example.subcriptionmanagementapp.domain.usecase.settings.GetSelectedCurrencyUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.AddSubscriptionUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.DeleteSubscriptionUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.GetActiveSubscriptionsUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.GetAllSubscriptionsUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.GetMonthlySpendingUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.GetSubscriptionUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.GetSubscriptionsByCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.SearchSubscriptionsUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.UpdateSubscriptionUseCase
import com.example.subcriptionmanagementapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SubscriptionViewModelTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var addSubscriptionUseCase: AddSubscriptionUseCase

    @Mock private lateinit var getSubscriptionUseCase: GetSubscriptionUseCase

    @Mock private lateinit var getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase

    @Mock private lateinit var getActiveSubscriptionsUseCase: GetActiveSubscriptionsUseCase

    @Mock private lateinit var updateSubscriptionUseCase: UpdateSubscriptionUseCase

    @Mock private lateinit var deleteSubscriptionUseCase: DeleteSubscriptionUseCase

    @Mock private lateinit var getSubscriptionsByCategoryUseCase: GetSubscriptionsByCategoryUseCase

    @Mock private lateinit var searchSubscriptionsUseCase: SearchSubscriptionsUseCase

    @Mock private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase

    @Mock private lateinit var getCategoryUseCase: GetCategoryUseCase

    @Mock private lateinit var addCategoryUseCase: AddCategoryUseCase

    @Mock private lateinit var seedDefaultCategoriesUseCase: SeedDefaultCategoriesUseCase

    @Mock private lateinit var getSelectedCurrencyUseCase: GetSelectedCurrencyUseCase

    @Mock private lateinit var currencyRateManager: CurrencyRateManager

    @Mock private lateinit var getMonthlySpendingUseCase: GetMonthlySpendingUseCase

    private lateinit var viewModel: SubscriptionViewModel

    @Before
    fun setUp() {
        whenever(getAllSubscriptionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getActiveSubscriptionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getAllCategoriesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getCategoryUseCase.invoke(any())).thenReturn(flowOf(null))
        whenever(getSelectedCurrencyUseCase()).thenReturn(flowOf("USD"))
        whenever(getMonthlySpendingUseCase()).thenReturn(flowOf(0.0))
        runBlocking {
            whenever(seedDefaultCategoriesUseCase.invoke()).thenReturn(Unit)
            whenever(currencyRateManager.convertCurrency(any(), any(), any())).thenReturn(null)
        }

        viewModel =
                SubscriptionViewModel(
                        addSubscriptionUseCase,
                        getSubscriptionUseCase,
                        getAllSubscriptionsUseCase,
                        getActiveSubscriptionsUseCase,
                        updateSubscriptionUseCase,
                        deleteSubscriptionUseCase,
                        getSubscriptionsByCategoryUseCase,
                        searchSubscriptionsUseCase,
                        getAllCategoriesUseCase,
                        getCategoryUseCase,
                        addCategoryUseCase,
                        seedDefaultCategoriesUseCase,
                        getSelectedCurrencyUseCase,
                        currencyRateManager,
                        getMonthlySpendingUseCase
                )

        clearInvocations(
                addSubscriptionUseCase,
                getSubscriptionUseCase,
                getAllSubscriptionsUseCase,
                getActiveSubscriptionsUseCase,
                updateSubscriptionUseCase,
                deleteSubscriptionUseCase,
                getSubscriptionsByCategoryUseCase,
                searchSubscriptionsUseCase,
                getAllCategoriesUseCase,
                getCategoryUseCase,
                addCategoryUseCase,
                seedDefaultCategoriesUseCase,
                getSelectedCurrencyUseCase,
                currencyRateManager,
                getMonthlySpendingUseCase
        )
    }

    @Test
    fun `updateSearchQuery should emit debounced results`() = runTest {
        val searchResult = listOf(sampleSubscription(id = 42, name = "Netflix"))
        whenever(searchSubscriptionsUseCase.invoke(any())).thenReturn(searchResult)

        viewModel.setSearchActive(true)
        viewModel.updateSearchQuery("netflix")

        advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.searchResults.test {
            skipItems(1)
            assertEquals(searchResult, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setSearchActive false should clear query and results`() = runTest {
        val searchResult = listOf(sampleSubscription(id = 99, name = "Spotify"))
        whenever(searchSubscriptionsUseCase.invoke(any())).thenReturn(searchResult)

        viewModel.setSearchActive(true)
        viewModel.updateSearchQuery("spotify")

        advanceTimeBy(350)
        advanceUntilIdle()

        assertEquals(searchResult, viewModel.searchResults.value)

        viewModel.setSearchActive(false)

        assertFalse(viewModel.isSearchActive.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `loadAllSubscriptions should update state`() = runTest {
        val subscriptions =
                listOf(
                        sampleSubscription(id = 1, name = "Netflix"),
                        sampleSubscription(id = 2, name = "Spotify", nextBillingOffsetDays = 15)
                )

        whenever(getAllSubscriptionsUseCase()).thenReturn(flowOf(subscriptions))

        viewModel.loadAllSubscriptions()

        viewModel.subscriptions.test {
            skipItems(1)
            assertEquals(subscriptions, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(getAllSubscriptionsUseCase).invoke()
    }

    @Test
    fun `loadActiveSubscriptions should update state`() = runTest {
        val activeSubscriptions = listOf(sampleSubscription(id = 3, name = "YouTube Premium"))
        whenever(getActiveSubscriptionsUseCase()).thenReturn(flowOf(activeSubscriptions))

        viewModel.loadActiveSubscriptions()

        viewModel.activeSubscriptions.test {
            skipItems(1)
            assertEquals(activeSubscriptions, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(getActiveSubscriptionsUseCase).invoke()
    }

    @Test
    fun `loadSubscription should update selectedSubscription`() = runTest {
        val subscription = sampleSubscription(id = 10, name = "Disney+")
        whenever(getSubscriptionUseCase(10)).thenReturn(flowOf(subscription))

        viewModel.loadSubscription(10)

        viewModel.selectedSubscription.test {
            skipItems(1)
            assertEquals(subscription, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(getSubscriptionUseCase).invoke(10)
    }

    @Test
    fun `loadSubscription should update selectedCategory when available`() = runTest {
        val subscription = sampleSubscription(id = 11, name = "Paramount+")
        val category =
                Category(
                        id = subscription.categoryId!!,
                        name = "Entertainment",
                        color = "#FFAA00",
                        icon = null,
                        isPredefined = true,
                        keywords = "streaming",
                        createdAt = DEFAULT_TIMESTAMP,
                        updatedAt = DEFAULT_TIMESTAMP
                )
        whenever(getSubscriptionUseCase(11)).thenReturn(flowOf(subscription))
        whenever(getCategoryUseCase.invoke(subscription.categoryId!!)).thenReturn(flowOf(category))

        viewModel.loadSubscription(11)

        viewModel.selectedCategory.test {
            skipItems(1)
            assertEquals(category, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.isSelectedSubscriptionUncategorized.test {
            skipItems(1)
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSubscription without category marks state as uncategorized`() = runTest {
        val subscription = sampleSubscription(id = 12, name = "No Category", categoryId = null)
        whenever(getSubscriptionUseCase(12)).thenReturn(flowOf(subscription))

        viewModel.loadSubscription(12)

        viewModel.isSelectedSubscriptionUncategorized.test {
            skipItems(1)
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addSubscription should refresh lists and emit saved event`() = runTest {
        val subscription = sampleSubscription(id = 0, name = "Notion")
        val updatedList = listOf(subscription.copy(id = 5))

        whenever(getAllSubscriptionsUseCase()).thenReturn(flowOf(updatedList))
        whenever(getActiveSubscriptionsUseCase()).thenReturn(flowOf(updatedList))

        viewModel.subscriptionSaved.test {
            viewModel.addSubscription(subscription)

            verify(addSubscriptionUseCase).invoke(subscription)
            verify(getAllSubscriptionsUseCase).invoke()
            verify(getActiveSubscriptionsUseCase).invoke()

            assertEquals(Unit, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.subscriptions.test {
            skipItems(1)
            assertEquals(updatedList, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateSubscription should refresh lists`() = runTest {
        val subscription = sampleSubscription(id = 7, name = "Dropbox")
        whenever(getAllSubscriptionsUseCase()).thenReturn(flowOf(listOf(subscription)))
        whenever(getActiveSubscriptionsUseCase()).thenReturn(flowOf(listOf(subscription)))

        viewModel.updateSubscription(subscription)

        verify(updateSubscriptionUseCase).invoke(subscription)
        verify(getAllSubscriptionsUseCase).invoke()
        verify(getActiveSubscriptionsUseCase).invoke()
    }

    @Test
    fun `deleteSubscription should refresh lists`() = runTest {
        val subscription = sampleSubscription(id = 9, name = "Headspace")
        whenever(getAllSubscriptionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getActiveSubscriptionsUseCase()).thenReturn(flowOf(emptyList()))

        viewModel.deleteSubscription(subscription)

        verify(deleteSubscriptionUseCase).invoke(subscription)
        verify(getAllSubscriptionsUseCase).invoke()
        verify(getActiveSubscriptionsUseCase).invoke()
    }

    @Test
    fun `searchSubscriptions should update list`() = runTest {
        val query = "net"
        val results = listOf(sampleSubscription(id = 1, name = "Netflix"))
        whenever(searchSubscriptionsUseCase(query)).thenReturn(results)

        viewModel.searchSubscriptions(query)

        viewModel.subscriptions.test {
            skipItems(1)
            assertEquals(results, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(searchSubscriptionsUseCase).invoke(query)
    }

    @Test
    fun `createCategory should emit new id and forward sanitized data`() = runTest {
        whenever(addCategoryUseCase.invoke(any())).thenReturn(42L)

        viewModel.categoryCreated.test {
            viewModel.createCategory(" AI Tools ", " ai, tech ")

            assertEquals(42L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(addCategoryUseCase).invoke(
                check { category ->
                    assertEquals("AI Tools", category.name)
                    assertEquals("ai, tech", category.keywords)
                    assertEquals(false, category.isPredefined)
                }
        )
    }

    @Test
    fun `clearError should clear message`() = runTest {
        val subscription = sampleSubscription(id = 0, name = "Figma")
        val exception = IllegalStateException("Unable to save")
        doAnswer { throw exception }.`when`(addSubscriptionUseCase).invoke(subscription)

        viewModel.addSubscription(subscription)
        assertEquals(exception.message, viewModel.error.value)

        viewModel.clearError()

        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadSubscription should preserve currency for detail display`() = runTest {
        val vndSubscription =
                sampleSubscription(id = 1, name = "Vietnamese Service", currency = "VND")

        whenever(getSubscriptionUseCase(1)).thenReturn(flowOf(vndSubscription))

        viewModel.loadSubscription(1)

        viewModel.selectedSubscription.test {
            val loadedSubscription = awaitItem()
            assertEquals("VND", loadedSubscription?.currency)
            assertEquals(9.99, loadedSubscription?.price)
            // Verify that the subscription's own currency is preserved, not the global selected
            // currency
            assertNotEquals("USD", loadedSubscription?.currency)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSubscription should preserve currency when editing`() = runTest {
        val vndSubscription =
                sampleSubscription(id = 1, name = "Vietnamese Service", currency = "VND")

        whenever(getSubscriptionUseCase(1)).thenReturn(flowOf(vndSubscription))

        viewModel.loadSubscription(1)

        viewModel.selectedSubscription.test {
            val loadedSubscription = awaitItem()
            assertEquals("VND", loadedSubscription?.currency)
            assertEquals(9.99, loadedSubscription?.price)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun sampleSubscription(
            id: Long,
            name: String,
            currency: String = "USD",
            nextBillingOffsetDays: Long = 30,
            categoryId: Long? = 1L
    ): Subscription {
        val baseTime = DEFAULT_TIMESTAMP
        val nextBillingDate = baseTime + nextBillingOffsetDays * ONE_DAY_MILLIS
        return Subscription(
                id = id,
                name = name,
                description = "description",
                price = 9.99,
                currency = currency,
                billingCycle = BillingCycle.MONTHLY,
                startDate = baseTime,
                nextBillingDate = nextBillingDate,
                endDate = null,
                reminderDays = 3,
                reminderHour = Subscription.DEFAULT_REMINDER_HOUR,
                reminderMinute = Subscription.DEFAULT_REMINDER_MINUTE,
                isActive = true,
                categoryId = categoryId,
                websiteUrl = "https://example.com",
                appPackageName = "com.example",
                notes = null,
                createdAt = baseTime,
                updatedAt = baseTime
        )
    }

    private companion object {
        const val DEFAULT_TIMESTAMP = 1_700_000_000_000L
        const val ONE_DAY_MILLIS = 86_400_000L
    }
}
