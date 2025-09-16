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
import kotlinx.coroutines.flow.*
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
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load payment history
                getPaymentHistoryByDateRangeUseCase(startDate, endDate)
                    .collect { paymentHistoryList ->
                        _paymentHistory.value = paymentHistoryList
                        _totalPayment.value = paymentHistoryList.sumOf { it.amount }
                    }
                
                // Load monthly spending
                getMonthlySpendingUseCase(year, month)
                    .collect { amount ->
                        _monthlySpending.value = amount
                    }
                
                // Load spending by category
                getSpendingByCategoryUseCase(startDate, endDate)
                    .collect { categorySpendingList ->
                        _spendingByCategory.value = categorySpendingList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load statistics"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadYearlyStatistics(year: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load yearly spending
                getYearlySpendingUseCase(year)
                    .collect { amount ->
                        _yearlySpending.value = amount
                    }
                
                // Load monthly spending trend
                getMonthlySpendingTrendUseCase(year)
                    .collect { monthlySpendingList ->
                        _monthlySpendingTrend.value = monthlySpendingList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load statistics"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}