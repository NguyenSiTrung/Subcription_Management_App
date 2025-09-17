package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.domain.usecase.payment.GetPaymentHistoryByDateRangeUseCase
import com.example.subcriptionmanagementapp.domain.usecase.statistics.*
import com.example.subcriptionmanagementapp.util.getCurrentMonth
import com.example.subcriptionmanagementapp.util.getCurrentYear
import com.example.subcriptionmanagementapp.util.getFirstDayOfMonth
import com.example.subcriptionmanagementapp.util.getLastDayOfMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getPaymentHistoryByDateRangeUseCase: GetPaymentHistoryByDateRangeUseCase,
    private val getMonthlySpendingUseCase: GetMonthlySpendingUseCase,
    private val getYearlySpendingUseCase: GetYearlySpendingUseCase,
    private val getSpendingByCategoryUseCase: GetSpendingByCategoryUseCase,
    private val getMonthlySpendingTrendUseCase: GetMonthlySpendingTrendUseCase
) : ViewModel() {

    private val _paymentHistory = MutableStateFlow<List<PaymentHistory>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentHistory>> = _paymentHistory.asStateFlow()

    private val _totalPayment = MutableStateFlow<Double?>(null)
    val totalPayment: StateFlow<Double?> = _totalPayment.asStateFlow()

    private val _monthlySpending = MutableStateFlow<Double?>(null)
    val monthlySpending: StateFlow<Double?> = _monthlySpending.asStateFlow()

    private val _yearlySpending = MutableStateFlow<Double?>(null)
    val yearlySpending: StateFlow<Double?> = _yearlySpending.asStateFlow()

    private val _spendingByCategory = MutableStateFlow<List<CategorySpending>>(emptyList())
    val spendingByCategory: StateFlow<List<CategorySpending>> = _spendingByCategory.asStateFlow()

    private val _monthlySpendingTrend = MutableStateFlow<List<MonthlySpending>>(emptyList())
    val monthlySpendingTrend: StateFlow<List<MonthlySpending>> = _monthlySpendingTrend.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var monthlySpendingJob: Job? = null
    private var spendingByCategoryJob: Job? = null
    private var yearlySpendingJob: Job? = null
    private var monthlyTrendJob: Job? = null

    init {
        loadCurrentMonthStatistics()
    }

    fun loadCurrentMonthStatistics() {
        val currentMonth = getCurrentMonth()
        val currentYear = getCurrentYear()
        loadMonthlyStatistics(currentYear, currentMonth)
    }

    fun loadMonthlyStatistics(year: Int, month: Int) {
        val startDate = getFirstDayOfMonth(month, year)
        val endDate = getLastDayOfMonth(month, year)

        monthlySpendingJob?.cancel()
        spendingByCategoryJob?.cancel()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val paymentHistoryList =
                    getPaymentHistoryByDateRangeUseCase(startDate, endDate)
                _paymentHistory.value = paymentHistoryList
                _totalPayment.value = paymentHistoryList.sumOf { payment -> payment.amount }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load statistics"
            } finally {
                _isLoading.value = false
            }
        }

        monthlySpendingJob =
            viewModelScope.launch {
                getMonthlySpendingUseCase(year, month)
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load statistics"
                    }
                    .collectLatest { amount: Double ->
                        _monthlySpending.value = amount
                    }
            }

        spendingByCategoryJob =
            viewModelScope.launch {
                getSpendingByCategoryUseCase(startDate, endDate)
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load statistics"
                    }
                    .collectLatest { categorySpendingList: List<CategorySpending> ->
                        _spendingByCategory.value = categorySpendingList
                    }
            }
    }

    fun loadYearlyStatistics(year: Int) {
        yearlySpendingJob?.cancel()
        monthlyTrendJob?.cancel()

        yearlySpendingJob =
            viewModelScope.launch {
                getYearlySpendingUseCase(year)
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load statistics"
                        _isLoading.value = false
                    }
                    .collectLatest { amount: Double ->
                        _yearlySpending.value = amount
                        _isLoading.value = false
                    }
            }

        monthlyTrendJob =
            viewModelScope.launch {
                getMonthlySpendingTrendUseCase(year)
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load statistics"
                    }
                    .collectLatest { monthlySpendingList: List<MonthlySpending> ->
                        _monthlySpendingTrend.value = monthlySpendingList
                    }
            }
    }

    fun clearError() {
        _error.value = null
    }
}