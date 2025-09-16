package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.domain.usecase.subscription.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val getSubscriptionUseCase: GetSubscriptionUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getActiveSubscriptionsUseCase: GetActiveSubscriptionsUseCase,
    private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
    private val getSubscriptionsByCategoryUseCase: GetSubscriptionsByCategoryUseCase,
    private val searchSubscriptionsUseCase: SearchSubscriptionsUseCase
) : ViewModel() {

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    private val _activeSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val activeSubscriptions: StateFlow<List<Subscription>> = _activeSubscriptions.asStateFlow()

    private val _selectedSubscription = MutableStateFlow<Subscription?>(null)
    val selectedSubscription: StateFlow<Subscription?> = _selectedSubscription.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllSubscriptions()
        loadActiveSubscriptions()
    }

    fun loadAllSubscriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAllSubscriptionsUseCase()
                    .collect { subscriptionList ->
                        _subscriptions.value = subscriptionList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load subscriptions"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadActiveSubscriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getActiveSubscriptionsUseCase()
                    .collect { subscriptionList ->
                        _activeSubscriptions.value = subscriptionList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load active subscriptions"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSubscription(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getSubscriptionUseCase(id)
                    .collect { subscription ->
                        _selectedSubscription.value = subscription
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load subscription"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addSubscription(subscription: Subscription) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addSubscriptionUseCase(subscription)
                loadAllSubscriptions()
                loadActiveSubscriptions()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add subscription"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updateSubscriptionUseCase(subscription)
                loadAllSubscriptions()
                loadActiveSubscriptions()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update subscription"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteSubscriptionUseCase(subscription)
                loadAllSubscriptions()
                loadActiveSubscriptions()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete subscription"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSubscriptionsByCategory(categoryId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getSubscriptionsByCategoryUseCase(categoryId)
                    .collect { subscriptionList ->
                        _subscriptions.value = subscriptionList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load subscriptions by category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchSubscriptions(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = searchSubscriptionsUseCase(query)
                _subscriptions.value = results
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to search subscriptions"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}