package com.example.subcriptionmanagementapp.ui.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.CategoryDefaults
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import com.example.subcriptionmanagementapp.domain.usecase.category.AddCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.GetAllCategoriesUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.GetCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.SeedDefaultCategoriesUseCase
import com.example.subcriptionmanagementapp.domain.usecase.settings.GetSelectedCurrencyUseCase
import com.example.subcriptionmanagementapp.domain.usecase.subscription.*
import com.example.subcriptionmanagementapp.ui.model.FilterState
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class SubscriptionViewModel
@Inject
constructor(
        private val addSubscriptionUseCase: AddSubscriptionUseCase,
        private val getSubscriptionUseCase: GetSubscriptionUseCase,
        private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
        private val getActiveSubscriptionsUseCase: GetActiveSubscriptionsUseCase,
        private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
        private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
        private val getSubscriptionsByCategoryUseCase: GetSubscriptionsByCategoryUseCase,
        private val searchSubscriptionsUseCase: SearchSubscriptionsUseCase,
        private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
        private val getCategoryUseCase: GetCategoryUseCase,
        private val addCategoryUseCase: AddCategoryUseCase,
        private val seedDefaultCategoriesUseCase: SeedDefaultCategoriesUseCase,
        private val getSelectedCurrencyUseCase: GetSelectedCurrencyUseCase,
        private val currencyRateManager: CurrencyRateManager,
        private val getMonthlySpendingUseCase: GetMonthlySpendingUseCase
) : ViewModel() {

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    private val _activeSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val activeSubscriptions: StateFlow<List<Subscription>> = _activeSubscriptions.asStateFlow()

    private val _selectedSubscription = MutableStateFlow<Subscription?>(null)
    val selectedSubscription: StateFlow<Subscription?> = _selectedSubscription.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _isSelectedSubscriptionUncategorized = MutableStateFlow(false)
    val isSelectedSubscriptionUncategorized: StateFlow<Boolean> =
            _isSelectedSubscriptionUncategorized.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _subscriptionSaved = MutableSharedFlow<Unit>()
    val subscriptionSaved: SharedFlow<Unit> = _subscriptionSaved.asSharedFlow()

    private val _categoryCreated = MutableSharedFlow<Long>()
    val categoryCreated: SharedFlow<Long> = _categoryCreated.asSharedFlow()

    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _monthlySpending = MutableStateFlow(0.0)
    val monthlySpending: StateFlow<Double> = _monthlySpending.asStateFlow()

    private val _convertedSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val convertedSubscriptions: StateFlow<List<Subscription>> =
            _convertedSubscriptions.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Filter state
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _categoryFilters = MutableStateFlow<List<CategoryFilter>>(emptyList())
    val categoryFilters: StateFlow<List<CategoryFilter>> = _categoryFilters.asStateFlow()

    private val _filteredSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val filteredSubscriptions: StateFlow<List<Subscription>> = _filteredSubscriptions.asStateFlow()

    private var allSubscriptionsJob: Job? = null
    private var activeSubscriptionsJob: Job? = null
    private var subscriptionJob: Job? = null
    private var categoryJob: Job? = null
    private var subscriptionsByCategoryJob: Job? = null
    private var categoriesJob: Job? = null
    private var filterJob: Job? = null

    init {
        observeSelectedCurrency()
        observeMonthlySpending()
        observeConvertedSubscriptions()
        observeFilteredSubscriptions()
        loadCategories()
    }

    private fun observeSelectedCurrency() {
        getSelectedCurrencyUseCase()
                .onEach { currency -> _selectedCurrency.value = currency }
                .launchIn(viewModelScope)
    }

    private fun observeConvertedSubscriptions() {
        combine(getActiveSubscriptionsUseCase(), getSelectedCurrencyUseCase()) {
                        subscriptions,
                        selectedCurrency ->
                    subscriptions.map { subscription ->
                        val convertedPrice =
                                convertSubscriptionPrice(subscription, selectedCurrency)
                        subscription.copy(price = convertedPrice)
                    }
                }
                .onEach { convertedList -> _convertedSubscriptions.value = convertedList }
                .launchIn(viewModelScope)
    }

    private suspend fun convertSubscriptionPrice(
            subscription: Subscription,
            targetCurrency: String
    ): Double {
        return if (subscription.currency == targetCurrency) {
            subscription.price
        } else {
            try {
                currencyRateManager.convertCurrency(
                        subscription.price,
                        subscription.currency,
                        targetCurrency
                )
                        ?: subscription.price
            } catch (e: Exception) {
                // If conversion fails, return original amount
                subscription.price
            }
        }
    }

    private fun observeMonthlySpending() {
        getMonthlySpendingUseCase()
                .onEach { spending -> _monthlySpending.value = spending }
                .launchIn(viewModelScope)
    }

    private fun observeFilteredSubscriptions() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            combine(
                _subscriptions,
                _filterState,
                _categories
            ) { subscriptions, filterState, _ ->
                applyFilters(subscriptions, filterState)
            }
            .collectLatest { filtered ->
                _filteredSubscriptions.value = filtered
            }
        }
    }

    private fun applyFilters(
        subscriptions: List<Subscription>,
        filterState: FilterState
    ): List<Subscription> {
        var filtered = subscriptions

        // Apply category filter
        if (filterState.selectedCategoryId != null) {
            filtered = filtered.filter { it.categoryId == filterState.selectedCategoryId }
        }

        // Apply active only filter
        if (filterState.showActiveOnly) {
            filtered = filtered.filter { it.isActive }
        }

        return filtered
    }

    private fun updateCategoryFilters(categories: List<Category>) {
        val currentFilter = _filterState.value
        val filters = mutableListOf<CategoryFilter>()
        
        // Add "All Categories" filter
        filters.add(CategoryFilter.ALL_CATEGORIES.copy(
            isSelected = currentFilter.selectedCategoryId == null
        ))
        
        // Add category filters
        categories.forEach { category ->
            filters.add(CategoryFilter(
                id = category.id,
                name = category.name,
                color = category.color,
                isSelected = currentFilter.selectedCategoryId == category.id
            ))
        }
        
        _categoryFilters.value = filters
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
                                val categoryId = subscription?.categoryId
                                _isSelectedSubscriptionUncategorized.value =
                                        subscription != null && categoryId == null
                                loadCategory(categoryId)
                                _isLoading.value = false
                            }
                }
    }

    private fun loadCategory(categoryId: Long?) {
        categoryJob?.cancel()
        if (categoryId == null) {
            _selectedCategory.value = null
            return
        }

        _isSelectedSubscriptionUncategorized.value = false

        categoryJob =
                viewModelScope.launch {
                    getCategoryUseCase(categoryId)
                            .catch { e ->
                                _error.value = e.message ?: "Failed to load category"
                                _selectedCategory.value = null
                            }
                            .collectLatest { category ->
                                _selectedCategory.value = category
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
                                _error.value =
                                        e.message ?: "Failed to load subscriptions by category"
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
                            .onStart {
                                _isLoading.value = true
                                seedDefaultCategoriesUseCase()
                            }
                            .catch { e ->
                                _error.value = e.message ?: "Failed to load categories"
                                _isLoading.value = false
                            }
                            .collectLatest { categoryList ->
                                _categories.value = categoryList
                                updateCategoryFilters(categoryList)
                                _isLoading.value = false
                            }
                }
    }

    fun createCategory(name: String, keywords: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val trimmedName = name.trim()
            if (trimmedName.isEmpty()) {
                _error.value = "Category name is required"
                _isLoading.value = false
                return@launch
            }

            val sanitizedKeywords = keywords?.trim()?.takeUnless { it.isEmpty() }

            try {
                val timestamp = System.currentTimeMillis()
                val category =
                        Category(
                                name = trimmedName,
                                color = CategoryDefaults.DEFAULT_CUSTOM_CATEGORY_COLOR,
                                icon = null,
                                isPredefined = false,
                                keywords = sanitizedKeywords,
                                createdAt = timestamp,
                                updatedAt = timestamp
                        )
                val newId = addCategoryUseCase(category)
                _categoryCreated.emit(newId)
            } catch (constraint: SQLiteConstraintException) {
                _error.value = "Category name already exists"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Filter methods
    fun filterByCategory(categoryId: Long?) {
        val currentState = _filterState.value
        val categoryName = if (categoryId == null) {
            null
        } else {
            _categories.value.find { it.id == categoryId }?.name
        }
        
        _filterState.value = currentState.copy(
            selectedCategoryId = categoryId,
            selectedCategoryName = categoryName
        )
        updateCategoryFilters(_categories.value)
    }

    fun toggleActiveFilter() {
        val currentState = _filterState.value
        _filterState.value = currentState.copy(
            showActiveOnly = !currentState.showActiveOnly
        )
    }

    fun clearFilters() {
        _filterState.value = FilterState()
        updateCategoryFilters(_categories.value)
    }

    fun getCategoryName(categoryId: Long?): String? {
        return if (categoryId == null) {
            null
        } else {
            _categories.value.find { it.id == categoryId }?.name
        }
    }
}
