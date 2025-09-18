package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import com.example.subcriptionmanagementapp.domain.usecase.subscription.*
import com.example.subcriptionmanagementapp.domain.usecase.category.GetAllCategoriesUseCase
import com.example.subcriptionmanagementapp.domain.usecase.settings.GetSelectedCurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val searchSubscriptionsUseCase: SearchSubscriptionsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getSelectedCurrencyUseCase: GetSelectedCurrencyUseCase,
    private val currencyRateManager: CurrencyRateManager
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

    private val _subscriptionSaved = MutableSharedFlow<Unit>()
    val subscriptionSaved: SharedFlow<Unit> = _subscriptionSaved.asSharedFlow()

    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private var allSubscriptionsJob: Job? = null
    private var activeSubscriptionsJob: Job? = null
    private var subscriptionJob: Job? = null
    private var subscriptionsByCategoryJob: Job? = null
    private var categoriesJob: Job? = null

    init {
        observeSelectedCurrency()
    }

    private fun observeSelectedCurrency() {
        getSelectedCurrencyUseCase()
            .onEach { currency ->
                _selectedCurrency.value = currency
            }
            .launchIn(viewModelScope)
    }

    fun loadAllSubscriptions() {
        allSubscriptionsJob?.cancel()
        allSubscriptionsJob =
            viewModelScope.launch {
                getAllSubscriptionsUseCase()
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load subscriptions"
                        _isLoading.value = false
                    }
                    .collectLatest { subscriptionList ->
                        _subscriptions.value = subscriptionList
                        _isLoading.value = false
                    }
            }
    }

    fun loadActiveSubscriptions() {
        activeSubscriptionsJob?.cancel()
        activeSubscriptionsJob =
            viewModelScope.launch {
                getActiveSubscriptionsUseCase()
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load active subscriptions"
                        _isLoading.value = false
                    }
                    .collectLatest { subscriptionList ->
                        _activeSubscriptions.value = subscriptionList
                        _isLoading.value = false
                    }
            }
    }

    fun loadSubscription(id: Long) {
        subscriptionJob?.cancel()
        subscriptionJob =
            viewModelScope.launch {
                getSubscriptionUseCase(id)
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load subscription"
                        _isLoading.value = false
                    }
                    .collectLatest { subscription ->
                        _selectedSubscription.value = subscription
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
                _subscriptionSaved.emit(Unit)
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
                _subscriptionSaved.emit(Unit)
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
        subscriptionsByCategoryJob?.cancel()
        subscriptionsByCategoryJob =
            viewModelScope.launch {
                getSubscriptionsByCategoryUseCase(categoryId)
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load subscriptions by category"
                        _isLoading.value = false
                    }
                    .collectLatest { subscriptionList ->
                        _subscriptions.value = subscriptionList
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

    fun loadCategories() {
        categoriesJob?.cancel()
        categoriesJob =
            viewModelScope.launch {
                getAllCategoriesUseCase()
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load categories"
                        _isLoading.value = false
                    }
                    .collectLatest { categoryList ->
                        _categories.value = categoryList
                        _isLoading.value = false
                    }
            }
    }

    fun clearError() {
        _error.value = null
    }
}