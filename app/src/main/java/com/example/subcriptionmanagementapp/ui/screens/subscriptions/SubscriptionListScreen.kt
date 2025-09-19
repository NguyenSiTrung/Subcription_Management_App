package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.*
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.model.FilterState
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel

@Composable
fun SubscriptionListScreen(
        navController: NavController,
        viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.filteredSubscriptions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val categoryFilters by viewModel.categoryFilters.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    // Calculate statistics
    val totalSubscriptions = subscriptions.size
    val activeSubscriptions = subscriptions.count { it.isActive }
    val totalMonthlyCost =
            remember(subscriptions) {
                subscriptions.filter { it.isActive }.sumOf { subscription ->
                    when (subscription.billingCycle) {
                        com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
                                .MONTHLY -> subscription.price
                        com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
                                .YEARLY -> subscription.price / 12
                        com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
                                .WEEKLY -> subscription.price * 4.33
                        com.example.subcriptionmanagementapp.data.local.entity.BillingCycle.DAILY ->
                                subscription.price * 30
                    }
                }
            }

    LaunchedEffect(Unit) {
        viewModel.loadAllSubscriptions()
        viewModel.loadCategories()
    }

    Scaffold(
            topBar = {
                AppTopBar(
                        title = stringResource(R.string.subscriptions),
                        navController = navController,
                        currentRoute = Screen.SubscriptionList.route,
                        onSearchClick = {
                            // Navigate to search screen
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                        }
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                        visible = !isLoading && error == null,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                ) {
                    FloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_subscription)
                        )
                    }
                }
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> ModernLoadingState()
                error != null ->
                        ModernErrorState(
                                message = error!!,
                                onRetry = {
                                    viewModel.clearError()
                                    viewModel.loadAllSubscriptions()
                                }
                        )
                subscriptions.isEmpty() ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Filter Row - always show even when empty
                            CategoryFilterRow(
                                    filters = categoryFilters,
                                    showActiveOnly = filterState.showActiveOnly,
                                    onFilterClick = { filter ->
                                        val categoryId =
                                                if (filter.id ==
                                                                com.example.subcriptionmanagementapp
                                                                        .ui.model.CategoryFilter
                                                                        .ALL_CATEGORIES
                                                                        .id
                                                )
                                                        null
                                                else filter.id
                                        viewModel.filterByCategory(categoryId)
                                    },
                                    onActiveFilterToggle = { viewModel.toggleActiveFilter() }
                            )

                            // Empty state content
                            Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                            ) {
                                ModernNoSubscriptionsEmptyState {
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(-1)
                                    )
                                }
                            }
                        }
                else ->
                        ModernSubscriptionListContent(
                                subscriptions = subscriptions,
                                selectedCurrency = selectedCurrency,
                                totalSubscriptions = totalSubscriptions,
                                activeSubscriptions = activeSubscriptions,
                                totalMonthlyCost = totalMonthlyCost,
                                categoryFilters = categoryFilters,
                                filterState = filterState,
                                viewModel = viewModel,
                                onSubscriptionClick = { subscriptionId ->
                                    navController.navigate(
                                            Screen.SubscriptionDetail.createRoute(subscriptionId)
                                    )
                                },
                                onEditClick = { subscriptionId ->
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(subscriptionId)
                                    )
                                },
                                onDeleteClick = { subscriptionId ->
                                    // Handle delete - you might want to show a confirmation dialog
                                    viewModel.deleteSubscription(subscriptionId)
                                },
                                onFilterClick = { filter ->
                                    val categoryId =
                                            if (filter.id ==
                                                            com.example.subcriptionmanagementapp.ui
                                                                    .model.CategoryFilter
                                                                    .ALL_CATEGORIES
                                                                    .id
                                            )
                                                    null
                                            else filter.id
                                    viewModel.filterByCategory(categoryId)
                                },
                                onActiveFilterToggle = { viewModel.toggleActiveFilter() }
                        )
            }
        }
    }
}

@Composable
fun ModernSubscriptionListContent(
        subscriptions: List<Subscription>,
        selectedCurrency: String,
        totalSubscriptions: Int,
        activeSubscriptions: Int,
        totalMonthlyCost: Double,
        categoryFilters: List<com.example.subcriptionmanagementapp.ui.model.CategoryFilter>,
        filterState: com.example.subcriptionmanagementapp.ui.model.FilterState,
        viewModel: SubscriptionViewModel,
        onSubscriptionClick: (Long) -> Unit,
        onEditClick: (Long) -> Unit,
        onDeleteClick: (Long) -> Unit,
        onFilterClick: (com.example.subcriptionmanagementapp.ui.model.CategoryFilter) -> Unit,
        onActiveFilterToggle: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Statistics Header
        SubscriptionListHeader(
                totalSubscriptions = totalSubscriptions,
                activeSubscriptions = activeSubscriptions,
                totalMonthlyCost = totalMonthlyCost,
                selectedCurrency = selectedCurrency
        )

        // Filter Row
        CategoryFilterRow(
                filters = categoryFilters,
                showActiveOnly = filterState.showActiveOnly,
                onFilterClick = onFilterClick,
                onActiveFilterToggle = onActiveFilterToggle
        )

        // Subscription List
        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = subscriptions) { subscription ->
                AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                ) {
                    ModernSubscriptionCard(
                            subscription = subscription,
                            selectedCurrency = selectedCurrency,
                            viewModel = viewModel,
                            onClick = { onSubscriptionClick(subscription.id) },
                            onEdit = { onEditClick(subscription.id) },
                            onDelete = { onDeleteClick(subscription.id) }
                    )
                }
            }
        }
    }
}
