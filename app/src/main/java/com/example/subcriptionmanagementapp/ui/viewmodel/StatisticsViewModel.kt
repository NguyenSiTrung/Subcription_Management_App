package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.domain.usecase.payment.GetPaymentHistoryByDateRangeUseCase
import com.example.subcriptionmanagementapp.domain.usecase.statistics.CategorySpending
import com.example.subcriptionmanagementapp.domain.usecase.statistics.GetMonthlySpendingTrendUseCase
import com.example.subcriptionmanagementapp.domain.usecase.statistics.GetMonthlySpendingUseCase
import com.example.subcriptionmanagementapp.domain.usecase.statistics.GetSpendingByCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.statistics.GetYearlySpendingUseCase
import com.example.subcriptionmanagementapp.domain.usecase.statistics.MonthlySpending
import com.example.subcriptionmanagementapp.util.getCurrentMonth
import com.example.subcriptionmanagementapp.util.getCurrentYear
import com.example.subcriptionmanagementapp.util.getFirstDayOfMonth
import com.example.subcriptionmanagementapp.util.getLastDayOfMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StatisticsPeriod { MONTH, YEAR }

data class StatisticsUiState(
    val period: StatisticsPeriod = StatisticsPeriod.MONTH,
    val selectedMonth: Int,
    val selectedYear: Int,
    val monthlyTotal: Double? = null,
    val monthlyPaymentCount: Int = 0,
    val yearlyTotal: Double? = null,
    val categorySpending: List<CategorySpending> = emptyList(),
    val monthlyTrend: List<MonthlySpending> = emptyList(),
    val recentPayments: List<PaymentHistory> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getPaymentHistoryByDateRangeUseCase: GetPaymentHistoryByDateRangeUseCase,
    private val getMonthlySpendingUseCase: GetMonthlySpendingUseCase,
    private val getYearlySpendingUseCase: GetYearlySpendingUseCase,
    private val getSpendingByCategoryUseCase: GetSpendingByCategoryUseCase,
    private val getMonthlySpendingTrendUseCase: GetMonthlySpendingTrendUseCase
) : ViewModel() {

    private val currentMonth = getCurrentMonth()
    private val currentYear = getCurrentYear()

    private val _uiState = MutableStateFlow(
        StatisticsUiState(
            selectedMonth = currentMonth,
            selectedYear = currentYear
        )
    )
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private var monthlySpendingJob: Job? = null
    private var spendingByCategoryJob: Job? = null
    private var yearlySpendingJob: Job? = null
    private var monthlyTrendJob: Job? = null

    init {
        loadMonthlyStatistics(currentYear, currentMonth)
    }

    fun onPeriodSelected(period: StatisticsPeriod) {
        if (uiState.value.period == period) return
        _uiState.update { it.copy(period = period) }

        when (period) {
            StatisticsPeriod.MONTH -> loadMonthlyStatistics(uiState.value.selectedYear, uiState.value.selectedMonth)
            StatisticsPeriod.YEAR -> loadYearlyStatistics(uiState.value.selectedYear)
        }
    }

    fun onMonthChanged(month: Int) {
        if (month == uiState.value.selectedMonth) return
        _uiState.update { it.copy(selectedMonth = month) }
        loadMonthlyStatistics(uiState.value.selectedYear, month)
    }

    fun onYearChanged(year: Int) {
        if (year == uiState.value.selectedYear) return
        _uiState.update { it.copy(selectedYear = year) }
        when (uiState.value.period) {
            StatisticsPeriod.MONTH -> loadMonthlyStatistics(year, uiState.value.selectedMonth)
            StatisticsPeriod.YEAR -> loadYearlyStatistics(year)
        }
    }

    fun refresh() {
        val state = uiState.value
        when (state.period) {
            StatisticsPeriod.MONTH -> loadMonthlyStatistics(state.selectedYear, state.selectedMonth, refresh = true)
            StatisticsPeriod.YEAR -> loadYearlyStatistics(state.selectedYear, refresh = true)
        }
    }

    fun loadMonthlyStatistics(year: Int, month: Int, refresh: Boolean = false) {
        val startDate = getFirstDayOfMonth(month, year)
        val endDate = getLastDayOfMonth(month, year)
        val isActivePeriod = uiState.value.period == StatisticsPeriod.MONTH

        monthlySpendingJob?.cancel()
        spendingByCategoryJob?.cancel()

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedYear = year,
                    selectedMonth = month,
                    error = null,
                    isLoading = if (isActivePeriod && !refresh) true else it.isLoading,
                    isRefreshing = if (isActivePeriod && refresh) true else false
                )
            }

            try {
                val history = getPaymentHistoryByDateRangeUseCase(startDate, endDate)
                    .sortedByDescending { payment -> payment.paymentDate }
                val totalPayment = history.sumOf { payment -> payment.amount }

                _uiState.update {
                    it.copy(
                        monthlyTotal = totalPayment,
                        monthlyPaymentCount = history.size,
                        recentPayments = history.take(8),
                        isLoading = if (isActivePeriod) false else it.isLoading,
                        isRefreshing = if (isActivePeriod) false else it.isRefreshing
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load statistics",
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }

        monthlySpendingJob = viewModelScope.launch {
            getMonthlySpendingUseCase(year, month)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Failed to load statistics")
                    }
                }
                .collectLatest { amount ->
                    _uiState.update { it.copy(monthlyTotal = amount) }
                }
        }

        spendingByCategoryJob = viewModelScope.launch {
            getSpendingByCategoryUseCase(startDate, endDate)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Failed to load statistics")
                    }
                }
                .collectLatest { categorySpendingList ->
                    _uiState.update { it.copy(categorySpending = categorySpendingList) }
                }
        }
    }

    fun loadYearlyStatistics(year: Int, refresh: Boolean = false) {
        val isActivePeriod = uiState.value.period == StatisticsPeriod.YEAR

        yearlySpendingJob?.cancel()
        monthlyTrendJob?.cancel()

        yearlySpendingJob = viewModelScope.launch {
            getYearlySpendingUseCase(year)
                .onStart {
                    _uiState.update {
                        it.copy(
                            selectedYear = year,
                            error = null,
                            isLoading = if (isActivePeriod && !refresh) true else it.isLoading,
                            isRefreshing = if (isActivePeriod && refresh) true else it.isRefreshing
                        )
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "Failed to load statistics",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                .collectLatest { amount ->
                    _uiState.update {
                        it.copy(
                            yearlyTotal = amount,
                            isLoading = if (isActivePeriod) false else it.isLoading,
                            isRefreshing = if (isActivePeriod) false else it.isRefreshing
                        )
                    }
                }
        }

        monthlyTrendJob = viewModelScope.launch {
            getMonthlySpendingTrendUseCase(year)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Failed to load statistics")
                    }
                }
                .collectLatest { monthlySpendingList ->
                    _uiState.update { it.copy(monthlyTrend = monthlySpendingList) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
