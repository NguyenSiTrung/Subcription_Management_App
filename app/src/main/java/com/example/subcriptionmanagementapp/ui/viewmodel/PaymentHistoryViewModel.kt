package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.domain.usecase.payment.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val addPaymentHistoryUseCase: AddPaymentHistoryUseCase,
    private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase,
    private val getPaymentHistoryBySubscriptionIdUseCase: GetPaymentHistoryBySubscriptionIdUseCase,
    private val getPaymentHistoryByDateRangeUseCase: GetPaymentHistoryByDateRangeUseCase,
    private val updatePaymentHistoryUseCase: UpdatePaymentHistoryUseCase,
    private val deletePaymentHistoryUseCase: DeletePaymentHistoryUseCase
) : ViewModel() {

    private val _paymentHistory = MutableStateFlow<List<PaymentHistory>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentHistory>> = _paymentHistory.asStateFlow()

    private val _selectedPaymentHistory = MutableStateFlow<PaymentHistory?>(null)
    val selectedPaymentHistory: StateFlow<PaymentHistory?> = _selectedPaymentHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPaymentHistoryBySubscriptionId(subscriptionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getPaymentHistoryBySubscriptionIdUseCase(subscriptionId)
                    .collect { paymentHistoryList ->
                        _paymentHistory.value = paymentHistoryList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load payment history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPaymentHistoryByDateRange(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getPaymentHistoryByDateRangeUseCase(startDate, endDate)
                    .collect { paymentHistoryList ->
                        _paymentHistory.value = paymentHistoryList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load payment history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPaymentHistory(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getPaymentHistoryUseCase(id)
                    .collect { paymentHistory ->
                        _selectedPaymentHistory.value = paymentHistory
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load payment history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPaymentHistory(paymentHistory: PaymentHistory) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addPaymentHistoryUseCase(paymentHistory)
                // Reload payment history after adding
                if (paymentHistory.subscriptionId != null) {
                    loadPaymentHistoryBySubscriptionId(paymentHistory.subscriptionId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add payment history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePaymentHistory(paymentHistory: PaymentHistory) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updatePaymentHistoryUseCase(paymentHistory)
                // Reload payment history after updating
                if (paymentHistory.subscriptionId != null) {
                    loadPaymentHistoryBySubscriptionId(paymentHistory.subscriptionId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update payment history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePaymentHistory(paymentHistory: PaymentHistory) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deletePaymentHistoryUseCase(paymentHistory)
                // Reload payment history after deleting
                if (paymentHistory.subscriptionId != null) {
                    loadPaymentHistoryBySubscriptionId(paymentHistory.subscriptionId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete payment history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}