package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

    Scaffold(
            topBar = {
                CompactSubscriptionTopBar(
                        navController = navController,
                        onSearchClick = {
                            // Navigate to search screen when implemented
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                        }
                )
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
                allSubscriptions.isEmpty() ->
                        Column(modifier = Modifier.fillMaxSize()) {
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
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(-1)
                                    )
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
                                onAddSubscription = {
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(-1)
                                    )
                                },
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
                    // Add a minimal delay to show loading state before closing
                    kotlinx.coroutines.MainScope().launch {
                        kotlinx.coroutines.delay(200)
                        deleteDialogState = DeleteDialogState.Hidden
                    }
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
                    // Add a minimal delay to show loading state before closing
                    kotlinx.coroutines.MainScope().launch {
                        kotlinx.coroutines.delay(200)
                        deleteDialogState = DeleteDialogState.Hidden
                    }
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
    Column(modifier = Modifier.fillMaxSize()) {
        CompactSubscriptionSummary(
                totalMonthlyCost = totalMonthlyCost,
                activeSubscriptions = activeSubscriptions,
                subscriptionCount = totalSubscriptions,
                selectedCurrency = selectedCurrency,
                onAddSubscription = onAddSubscription
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            SubscriptionListTab.UPCOMING -> {
                UpcomingRenewalsSection(
                        subscriptions = upcomingSubscriptions,
                        selectedCurrency = selectedCurrency,
                        onSubscriptionClick = onSubscriptionClick,
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
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
                text = stringResource(R.string.upcoming_renewals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
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
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subscriptions) { subscription ->
                        UpcomingSubscriptionCard(
                                subscription = subscription,
                                selectedCurrency = selectedCurrency,
                                onClick = { onSubscriptionClick(subscription.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingSubscriptionCard(
        subscription: Subscription,
        selectedCurrency: String,
        onClick: () -> Unit
) {
    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isUrgent = daysUntil <= 3

    Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isUrgent) {
                                        WarningColor.copy(alpha = 0.2f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                    ),
            border = if (isUrgent) BorderStroke(1.dp, WarningColor) else null
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text =
                                buildString {
                                    append(subscription.price.formatCurrency(selectedCurrency))
                                    append("/")
                                    append(
                                            when (subscription.billingCycle) {
                                                BillingCycle.DAILY -> stringResource(R.string.daily)
                                                BillingCycle.WEEKLY ->
                                                        stringResource(R.string.weekly)
                                                BillingCycle.MONTHLY ->
                                                        stringResource(R.string.monthly)
                                                BillingCycle.YEARLY ->
                                                        stringResource(R.string.yearly)
                                            }
                                    )
                                },
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                                when {
                                    !isUrgent -> MaterialTheme.colorScheme.onSurfaceVariant
                                    daysUntil <= 0L -> ErrorColor
                                    daysUntil <= 3L -> ErrorColor
                                    daysUntil <= 7L -> WarningColor
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                )
            }

            Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = subscription.nextBillingDate.formatDate(),
                        style = MaterialTheme.typography.bodyMedium
                )
                Text(
                        text =
                                when {
                                    daysUntil < 0L -> stringResource(R.string.overdue)
                                    daysUntil == 0L -> stringResource(R.string.due_today)
                                    daysUntil == 1L -> stringResource(R.string.due_tomorrow)
                                    else -> stringResource(R.string.due_in_days, daysUntil)
                                },
                        style = MaterialTheme.typography.bodySmall,
                        color =
                                when {
                                    daysUntil < 0L -> ErrorColor
                                    daysUntil <= 3L -> WarningColor
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        fontWeight = if (isUrgent) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
