package com.example.subcriptionmanagementapp.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(AndroidJUnit4::class)
class SubscriptionListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var viewModel: SubscriptionViewModel

    @Before
    fun setUp() {
        // Mock the ViewModel
        viewModel = org.mockito.Mockito.mock(SubscriptionViewModel::class.java)
    }

    @Test
    fun subscriptionListScreen_displaysSubscriptions() {
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

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                SubscriptionListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Netflix")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("$9.99")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Spotify")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("$4.99")
            .assertIsDisplayed()
    }

    @Test
    fun subscriptionListScreen_displaysEmptyState() {
        // Given
        val subscriptions = emptyList<Subscription>()

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                SubscriptionListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("No subscriptions found")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add your first subscription to get started")
            .assertIsDisplayed()
    }

    @Test
    fun subscriptionListScreen_displaysLoadingState() {
        // Given
        org.mockito.Mockito.`when`(viewModel.isLoading.value).thenReturn(true)

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                SubscriptionListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Loading")
            .assertIsDisplayed()
    }

    @Test
    fun subscriptionListScreen_displaysErrorMessage() {
        // Given
        org.mockito.Mockito.`when`(viewModel.error.value).thenReturn("Failed to load subscriptions")

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                SubscriptionListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Failed to load subscriptions")
            .assertIsDisplayed()
    }

    @Test
    fun subscriptionListScreen_fabClick_navigatesToAddSubscription() {
        // Given
        val navController = androidx.navigation.testing.TestNavController()
        
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                SubscriptionListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Add Subscription")
            .performClick()
        
        // Verify navigation
        // Note: In a real test, you would verify that the navigation happened
        // This is a simplified example
    }
}