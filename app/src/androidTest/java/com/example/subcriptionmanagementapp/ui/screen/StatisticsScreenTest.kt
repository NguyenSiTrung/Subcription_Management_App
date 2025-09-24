package com.example.subcriptionmanagementapp.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.domain.usecase.statistics.CategorySpending
import com.example.subcriptionmanagementapp.domain.usecase.statistics.MonthlySpending
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsPeriod
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsUiState
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsViewModel
import com.example.subcriptionmanagementapp.ui.screens.statistics.StatisticsScreen
import com.example.subcriptionmanagementapp.util.formatCurrency
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class StatisticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: StatisticsViewModel
    private lateinit var uiStateFlow: MutableStateFlow<StatisticsUiState>

    private lateinit var monthlyState: StatisticsUiState
    private lateinit var yearlyState: StatisticsUiState
    private lateinit var emptyErrorState: StatisticsUiState

    @Before
    fun setUp() {
        viewModel = Mockito.mock(StatisticsViewModel::class.java)
        monthlyState = buildMonthlyState()
        yearlyState = buildYearlyState()
        emptyErrorState = buildErrorState()

        uiStateFlow = MutableStateFlow(monthlyState)
        Mockito.`when`(viewModel.uiState).thenReturn(uiStateFlow)
    }

    @Test
    fun statisticsScreen_displaysMonthlyOverview() {
        composeScreen()

        composeTestRule.onNodeWithText("Monthly overview").assertIsDisplayed()
        composeTestRule.onNodeWithText(monthlyState.monthlyTotal!!.formatCurrency()).assertIsDisplayed()
        monthlyState.recentPayments.firstOrNull()?.notes?.let { note ->
            composeTestRule.onNodeWithText(note).assertIsDisplayed()
        }
        composeTestRule.onNodeWithText("Recent payments").assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysCategoryDistribution() {
        composeScreen()

        composeTestRule.onNodeWithText("Category distribution").assertIsDisplayed()
        composeTestRule.onNodeWithText(monthlyState.categorySpending.first().category.name).assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysYearlyTrendWhenStateChanges() {
        composeScreen()
        composeTestRule.runOnUiThread { uiStateFlow.value = yearlyState }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Yearly overview").assertIsDisplayed()
        composeTestRule.onNodeWithText(yearlyState.yearlyTotal!!.formatCurrency()).assertIsDisplayed()
        composeTestRule.onNodeWithText("Spending trend").assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_showsErrorStateWhenNoData() {
        uiStateFlow.value = emptyErrorState
        composeScreen()

        composeTestRule.onNodeWithText(emptyErrorState.error!!).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    private fun composeScreen() {
        composeTestRule.setContent {
            SubscriptionManagementAppTheme {
                val navController = rememberNavController()
                StatisticsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    private fun buildMonthlyState(): StatisticsUiState {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 15)
        }

        val entertainmentCategory = Category(
            id = 1,
            name = "Entertainment",
            color = "#FF6B6B",
            icon = null,
            isPredefined = false,
            keywords = null,
            createdAt = calendar.timeInMillis,
            updatedAt = calendar.timeInMillis
        )
        val productivityCategory = entertainmentCategory.copy(
            id = 2,
            name = "Productivity",
            color = "#4D96FF"
        )

        val paymentOne = PaymentHistory(
            id = 1,
            subscriptionId = 1,
            amount = 19.99,
            currency = "USD",
            paymentDate = calendar.timeInMillis,
            paymentMethod = "Card",
            transactionId = "txn-1",
            notes = "Netflix Premium",
            createdAt = calendar.timeInMillis,
            updatedAt = calendar.timeInMillis
        )
        val paymentTwo = paymentOne.copy(
            id = 2,
            subscriptionId = 2,
            amount = 9.99,
            notes = "Notion Plus",
            transactionId = "txn-2"
        )

        return StatisticsUiState(
            period = StatisticsPeriod.MONTH,
            selectedMonth = Calendar.JANUARY,
            selectedYear = 2025,
            monthlyTotal = paymentOne.amount + paymentTwo.amount,
            monthlyPaymentCount = 2,
            categorySpending = listOf(
                CategorySpending(entertainmentCategory, paymentOne.amount),
                CategorySpending(productivityCategory, paymentTwo.amount)
            ),
            recentPayments = listOf(paymentOne, paymentTwo)
        )
    }

    private fun buildYearlyState(): StatisticsUiState {
        val trend = listOf(
            MonthlySpending(year = 2025, month = Calendar.JANUARY, amount = 120.0),
            MonthlySpending(year = 2025, month = Calendar.FEBRUARY, amount = 95.0),
            MonthlySpending(year = 2025, month = Calendar.MARCH, amount = 110.0)
        )

        return monthlyState.copy(
            period = StatisticsPeriod.YEAR,
            yearlyTotal = trend.sumOf { it.amount },
            monthlyTrend = trend
        )
    }

    private fun buildErrorState(): StatisticsUiState {
        return StatisticsUiState(
            period = StatisticsPeriod.MONTH,
            selectedMonth = Calendar.JANUARY,
            selectedYear = 2025,
            monthlyTotal = null,
            monthlyPaymentCount = 0,
            categorySpending = emptyList(),
            monthlyTrend = emptyList(),
            recentPayments = emptyList(),
            error = "Failed to load statistics",
            isLoading = false
        )
    }
}
