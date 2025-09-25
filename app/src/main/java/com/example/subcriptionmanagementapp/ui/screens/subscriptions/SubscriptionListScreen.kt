package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.CategoryFilterRow
import com.example.subcriptionmanagementapp.ui.components.CompactSubscriptionSummary
import com.example.subcriptionmanagementapp.ui.components.CompactSubscriptionTopBar
import com.example.subcriptionmanagementapp.ui.components.CompactTabsAndFilter
import com.example.subcriptionmanagementapp.ui.components.DeleteSubscriptionConfirmationDialog
import com.example.subcriptionmanagementapp.ui.components.ModernErrorState
import com.example.subcriptionmanagementapp.ui.components.ModernFilterEmptyState
import com.example.subcriptionmanagementapp.ui.components.ModernLoadingState
import com.example.subcriptionmanagementapp.ui.components.ModernNoSubscriptionsEmptyState
import com.example.subcriptionmanagementapp.ui.components.OptimizedSubscriptionCard
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.model.DeleteDialogState
import com.example.subcriptionmanagementapp.ui.model.FilterState
import com.example.subcriptionmanagementapp.ui.model.SubscriptionListTab
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.ErrorColor
import com.example.subcriptionmanagementapp.ui.theme.WarningColor
import com.example.subcriptionmanagementapp.ui.theme.AccentBlue
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import com.example.subcriptionmanagementapp.util.getDaysUntil


@Composable
fun SubscriptionListScreen(
        navController: NavController,
        viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val filteredSubscriptions by viewModel.filteredSubscriptions.collectAsStateWithLifecycle()
    val allSubscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val upcomingSubscriptions by viewModel.upcomingSubscriptions.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val categoryFilters by viewModel.categoryFilters.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var isCategoryFilterExpanded by rememberSaveable { mutableStateOf(false) }
    var deleteDialogState by remember { mutableStateOf<DeleteDialogState>(DeleteDialogState.Hidden) }

    val totalSubscriptions = allSubscriptions.size
    val activeSubscriptions = allSubscriptions.count { it.isActive }
    val totalMonthlyCost =
            remember(allSubscriptions) {
                allSubscriptions.filter { it.isActive }.sumOf { subscription ->
                    when (subscription.billingCycle) {
                        BillingCycle.MONTHLY -> subscription.price
                        BillingCycle.YEARLY -> subscription.price / 12
                        BillingCycle.WEEKLY -> subscription.price * 4.33
                        BillingCycle.DAILY -> subscription.price * 30
                    }
                }
            }

    LaunchedEffect(Unit) {
        viewModel.loadAllSubscriptions()
        viewModel.loadCategories()
    }

    val navigateToAdd: () -> Unit = {
        navController.navigate(Screen.AddEditSubscription.createRoute(-1))
    }

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            floatingActionButtonPosition = FabPosition.Center,
            topBar = {
                CompactSubscriptionTopBar(
                        onSearchClick = {
                            // Navigate to search screen when implemented
                        }
                )
            },
            floatingActionButton = {
                AddSubscriptionFab(onClick = navigateToAdd)
            }
    ) { paddingValues ->
        Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
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
                allSubscriptions.isEmpty() ->
                        Column(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                        ) {
                            CategoryFilterRow(
                                    filters = categoryFilters,
                                    showActiveOnly = filterState.showActiveOnly,
                                    onFilterClick = { filter ->
                                        val categoryId =
                                                if (filter.id == CategoryFilter.ALL_CATEGORIES.id)
                                                        null
                                                else filter.id
                                        viewModel.filterByCategory(categoryId)
                                    },
                                    onActiveFilterToggle = { viewModel.toggleActiveFilter() },
                                    isExpanded = isCategoryFilterExpanded,
                                    onExpandToggle = {
                                        isCategoryFilterExpanded = !isCategoryFilterExpanded
                                    }
                            )

                            Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                            ) {
                                ModernNoSubscriptionsEmptyState {
                                    navigateToAdd()
                                }
                            }
                        }
                else ->
                        CompactSubscriptionListContent(
                                filteredSubscriptions = filteredSubscriptions,
                                selectedCurrency = selectedCurrency,
                                totalSubscriptions = totalSubscriptions,
                                activeSubscriptions = activeSubscriptions,
                                totalMonthlyCost = totalMonthlyCost,
                                upcomingSubscriptions = upcomingSubscriptions,
                                categoryFilters = categoryFilters,
                                filterState = filterState,
                                selectedTab = selectedTab,
                                isFilterExpanded = isCategoryFilterExpanded,
                                viewModel = viewModel,
                                onAddSubscription = navigateToAdd,
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
                                    val subscription = allSubscriptions.find { it.id == subscriptionId }
                                    if (subscription != null) {
                                        deleteDialogState = DeleteDialogState.Visible(subscription)
                                    }
                                },
                                onTabSelected = { viewModel.selectTab(it) },
                                onFilterClick = { filter ->
                                    val categoryId =
                                            if (filter.id == CategoryFilter.ALL_CATEGORIES.id) null
                                            else filter.id
                                    viewModel.filterByCategory(categoryId)
                                },
                                onActiveFilterToggle = { viewModel.toggleActiveFilter() },
                                onFilterExpandToggle = {
                                    isCategoryFilterExpanded = !isCategoryFilterExpanded
                                }
                        )
            }
        }
    }

    // Delete confirmation dialog
    when (val state = deleteDialogState) {
        is DeleteDialogState.Visible -> {
            DeleteSubscriptionConfirmationDialog(
                subscription = state.subscription,
                onConfirm = { subscription ->
                    deleteDialogState = DeleteDialogState.Deleting(subscription)
                    viewModel.deleteSubscription(subscription.id)
                },
                onDismiss = {
                    if (state !is DeleteDialogState.Deleting) {
                        deleteDialogState = DeleteDialogState.Hidden
                    }
                },
                isDeleting = state is DeleteDialogState.Deleting
            )
        }
        is DeleteDialogState.Deleting -> {
            DeleteSubscriptionConfirmationDialog(
                subscription = state.subscription,
                onConfirm = { subscription ->
                    viewModel.deleteSubscription(subscription.id)
                },
                onDismiss = {
                    // Can't dismiss while deleting
                },
                isDeleting = true
            )
        }
        DeleteDialogState.Hidden -> {
            // No dialog shown
        }
    }
}

@Composable
fun CompactSubscriptionListContent(
        filteredSubscriptions: List<Subscription>,
        selectedCurrency: String,
        totalSubscriptions: Int,
        activeSubscriptions: Int,
        totalMonthlyCost: Double,
        upcomingSubscriptions: List<Subscription>,
        categoryFilters: List<CategoryFilter>,
        filterState: FilterState,
        selectedTab: SubscriptionListTab,
        isFilterExpanded: Boolean,
        viewModel: SubscriptionViewModel,
        onAddSubscription: () -> Unit,
        onSubscriptionClick: (Long) -> Unit,
        onEditClick: (Long) -> Unit,
        onDeleteClick: (Long) -> Unit,
        onTabSelected: (SubscriptionListTab) -> Unit,
        onFilterClick: (CategoryFilter) -> Unit,
        onActiveFilterToggle: () -> Unit,
        onFilterExpandToggle: () -> Unit
) {
    Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CompactSubscriptionSummary(
                totalMonthlyCost = totalMonthlyCost,
                activeSubscriptions = activeSubscriptions,
                subscriptionCount = totalSubscriptions,
                selectedCurrency = selectedCurrency
        )

        CompactTabsAndFilter(
                selectedTab = selectedTab,
                upcomingCount = upcomingSubscriptions.size,
                totalCount = totalSubscriptions,
                categoryFilters = categoryFilters,
                showActiveOnly = filterState.showActiveOnly,
                isFilterExpanded = isFilterExpanded,
                onTabSelected = onTabSelected,
                onFilterClick = onFilterClick,
                onActiveFilterToggle = onActiveFilterToggle,
                onFilterExpandToggle = onFilterExpandToggle
        )

        when (selectedTab) {
            SubscriptionListTab.UPCOMING -> {
                UpcomingRenewalsSection(
                        subscriptions = upcomingSubscriptions,
                        selectedCurrency = selectedCurrency,
                        onSubscriptionClick = onSubscriptionClick,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            SubscriptionListTab.ALL -> {

                if (filteredSubscriptions.isEmpty()) {
                    Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                    ) {
                        val filterName =
                                when {
                                    filterState.selectedCategoryName != null ->
                                            filterState.selectedCategoryName
                                    filterState.showActiveOnly ->
                                            stringResource(R.string.active_subscriptions)
                                    else -> stringResource(R.string.filter_all_subscriptions)
                                }

                        if (filterState.selectedCategoryId != null || filterState.showActiveOnly) {
                            ModernFilterEmptyState(filterName = filterName)
                        } else {
                            ModernNoSubscriptionsEmptyState(onAddSubscription = onAddSubscription)
                        }
                    }
                } else {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = filteredSubscriptions) { subscription ->
                            OptimizedSubscriptionCard(
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
    }
}

@Composable
private fun UpcomingRenewalsSection(
        subscriptions: List<Subscription>,
        selectedCurrency: String,
        onSubscriptionClick: (Long) -> Unit,
        viewModel: SubscriptionViewModel,
        modifier: Modifier = Modifier
) {
    Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopStart
    ) {
            if (subscriptions.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().align(Alignment.Center)) {
                    Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = stringResource(R.string.no_upcoming_renewals),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subscriptions) { subscription ->
                        OptimizedSubscriptionCard(
                                subscription = subscription,
                                selectedCurrency = selectedCurrency,
                                viewModel = viewModel,
                                onClick = { onSubscriptionClick(subscription.id) },
                                onEdit = { 
                                    // Navigate to edit screen through the callback
                                    // The actual navigation will be handled by the parent
                                },
                                onDelete = { 
                                    // Handle delete through the callback
                                    // The actual delete will be handled by the parent
                                }
                        )
                    }
                }
        }
    }
}

@Composable
private fun AddSubscriptionFab(onClick: () -> Unit) {
    LargeFloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = AccentBlue,
            contentColor = Color.White,
            modifier = Modifier.navigationBarsPadding()
    ) {
        Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_subscription)
        )
    }
}
