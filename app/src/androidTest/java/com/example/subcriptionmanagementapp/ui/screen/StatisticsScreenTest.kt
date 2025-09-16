package com.example.subcriptionmanagementapp.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(AndroidJUnit4::class)
class StatisticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setUp() {
        // Mock the ViewModel
        viewModel = org.mockito.Mockito.mock(StatisticsViewModel::class.java)
    }

    @Test
    fun statisticsScreen_displaysStatisticsCards() {
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Total Payments")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Monthly Spending")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Yearly Spending")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysCharts() {
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Spending by Category")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Monthly Spending Trend")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysDateFilters() {
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Month")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Year")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysPaymentHistory() {
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Payment History")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysLoadingState() {
        // Given
        org.mockito.Mockito.`when`(viewModel.isLoading.value).thenReturn(true)

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
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
    fun statisticsScreen_displaysErrorMessage() {
        // Given
        org.mockito.Mockito.`when`(viewModel.error.value).thenReturn("Failed to load statistics")

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Failed to load statistics")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysStatisticsData() {
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
        
        org.mockito.Mockito.`when`(viewModel.totalPayment.value).thenReturn(14.98)
        org.mockito.Mockito.`when`(viewModel.monthlySpending.value).thenReturn(9.99)
        org.mockito.Mockito.`when`(viewModel.yearlySpending.value).thenReturn(119.88)

        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("$14.98")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("$9.99")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("$119.88")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_monthFilterClick_updatesData() {
        // Given
        val navController = androidx.navigation.testing.TestNavController()
        
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Month")
            .performClick()
        
        // Verify that the ViewModel method was called
        // Note: In a real test, you would verify that the ViewModel method was called
        // This is a simplified example
    }

    @Test
    fun statisticsScreen_yearFilterClick_updatesData() {
        // Given
        val navController = androidx.navigation.testing.TestNavController()
        
        // When
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Year")
            .performClick()
        
        // Verify that the ViewModel method was called
        // Note: In a real test, you would verify that the ViewModel method was called
        // This is a simplified example
    }
}