package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.CategoryTag
import com.example.subcriptionmanagementapp.ui.components.CategoryTagSize
import com.example.subcriptionmanagementapp.ui.components.CompactSubscriptionSummary
import com.example.subcriptionmanagementapp.ui.components.CompactSubscriptionTopBar
import com.example.subcriptionmanagementapp.ui.components.CompactTabsAndFilter
import com.example.subcriptionmanagementapp.ui.components.DeleteSubscriptionConfirmationDialog
import com.example.subcriptionmanagementapp.ui.components.ModernErrorState
import com.example.subcriptionmanagementapp.ui.components.ModernFilterEmptyState
import com.example.subcriptionmanagementapp.ui.components.ModernLoadingState
import com.example.subcriptionmanagementapp.ui.components.ModernNoSubscriptionsEmptyState
import com.example.subcriptionmanagementapp.ui.components.ModernSearchEmptyState
import com.example.subcriptionmanagementapp.ui.components.OptimizedSubscriptionCard
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.model.DeleteDialogState
import com.example.subcriptionmanagementapp.ui.model.FilterState
import com.example.subcriptionmanagementapp.ui.model.SubscriptionListTab
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.SearchFieldShape
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
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearchLoading by viewModel.isSearchLoading.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

    var isCategoryFilterExpanded by rememberSaveable { mutableStateOf(false) }
    var deleteDialogState by remember { mutableStateOf<DeleteDialogState>(DeleteDialogState.Hidden) }
    val listState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val isFabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 || listState.firstVisibleItemScrollOffset < 100
        }
    }

    val totalSubscriptions = allSubscriptions.size
    val activeSubscriptions = allSubscriptions.count { it.isActive }
    val totalMonthlyCost =
            remember(allSubscriptions) {
                allSubscriptions
                        .filter { it.isActive }
                        .sumOf { subscription ->
                            when (subscription.billingCycle) {
                                BillingCycle.MONTHLY -> subscription.price
                                BillingCycle.YEARLY -> subscription.price / 12
                                BillingCycle.WEEKLY -> subscription.price * 4.33
                                BillingCycle.DAILY -> subscription.price * 30
                            }
                        }
            }

    val recentSubscriptions =
            remember(allSubscriptions) {
                allSubscriptions
                        .sortedByDescending { it.updatedAt }
                        .take(6)
            }

    BackHandler(enabled = isSearchActive) {
        viewModel.setSearchActive(false)
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
            floatingActionButtonPosition = FabPosition.End,
            topBar = {
                if (isSearchActive) {
                    SubscriptionSearchTopBar(
                            query = searchQuery,
                            onQueryChange = viewModel::updateSearchQuery,
                            onClearQuery = viewModel::clearSearch,
                            onDismiss = { viewModel.setSearchActive(false) }
                    )
                } else {
                    CompactSubscriptionTopBar(
                            onSearchClick = { viewModel.setSearchActive(true) }
                    )
                }
            },
            floatingActionButton = {
                if (!isSearchActive && allSubscriptions.isNotEmpty() && isFabVisible) {
                    AddSubscriptionFab(onClick = navigateToAdd)
                }
            }
    ) { paddingValues ->
        Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (isSearchActive) {
                SubscriptionSearchContent(
                        query = searchQuery,
                        results = searchResults,
                        isLoading = isSearchLoading,
                        selectedCurrency = selectedCurrency,
                        recentSubscriptions = recentSubscriptions,
                        onSuggestionSelected = { suggestion ->
                            viewModel.updateSearchQuery(suggestion)
                        },
                        onSubscriptionClick = { subscriptionId ->
                            viewModel.setSearchActive(false)
                            navController.navigate(
                                    Screen.SubscriptionDetail.createRoute(subscriptionId)
                            )
                        },
                        onAddSubscription = {
                            viewModel.setSearchActive(false)
                            navigateToAdd()
                        },
                        viewModel = viewModel,
                        listState = searchListState
                )
            } else {
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
                            Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                            ) {
                                ModernNoSubscriptionsEmptyState {
                                    navigateToAdd()
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
                                        val subscription =
                                                allSubscriptions.find { it.id == subscriptionId }
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
                                    },
                                    listState = listState,
                                    isFabVisible = isFabVisible
                            )
                }
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
private fun SubscriptionSearchTopBar(
        query: String,
        onQueryChange: (String) -> Unit,
        onClearQuery: () -> Unit,
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val topBarShape = androidx.compose.foundation.shape.RoundedCornerShape(
            bottomStart = 28.dp,
            bottomEnd = 28.dp
    )

    Card(
            modifier = modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = topBarShape, clip = false),
            shape = topBarShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = stringResource(R.string.search_subscriptions_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close_search)
                    )
                }
            }

            OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                                text = stringResource(R.string.search_subscriptions_placeholder),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = onClearQuery) {
                                Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                    }),
                    shape = SearchFieldShape,
                    colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                            disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            cursorColor = MaterialTheme.colorScheme.primary
                    )
            )

            Text(
                    text = stringResource(R.string.search_start_typing_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubscriptionSearchContent(
        query: String,
        results: List<Subscription>,
        isLoading: Boolean,
        selectedCurrency: String,
        recentSubscriptions: List<Subscription>,
        onSuggestionSelected: (String) -> Unit,
        onSubscriptionClick: (Long) -> Unit,
        onAddSubscription: () -> Unit,
        viewModel: SubscriptionViewModel,
        listState: LazyListState,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (query.isBlank()) {
            Box(
                    modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.TopStart
            ) {
                SubscriptionSearchSuggestions(
                        recentSubscriptions = recentSubscriptions,
                        onSuggestionSelected = onSuggestionSelected,
                        onAddSubscription = onAddSubscription
                )
            }
        } else {
            Text(
                    text = pluralStringResource(
                            R.plurals.search_result_count,
                            results.size,
                            results.size
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                    modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
            ) {
                when {
                    results.isEmpty() && isLoading -> {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    results.isEmpty() -> {
                        ModernSearchEmptyState(searchQuery = query)
                    }
                    else -> {
                        LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = results, key = { it.id }) { subscription ->
                                SearchResultCard(
                                        subscription = subscription,
                                        query = query,
                                        selectedCurrency = selectedCurrency,
                                        onClick = { onSubscriptionClick(subscription.id) },
                                        viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionSearchSuggestions(
        recentSubscriptions: List<Subscription>,
        onSuggestionSelected: (String) -> Unit,
        onAddSubscription: () -> Unit
) {
    Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
                text = stringResource(R.string.search_quick_suggestions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
        )

        if (recentSubscriptions.isEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                        text = stringResource(R.string.search_no_recent_subscriptions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(onClick = onAddSubscription) {
                    Text(text = stringResource(R.string.add_subscription))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                        text = stringResource(R.string.search_recent_subscriptions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recentSubscriptions, key = { it.id }) { subscription ->
                        SuggestionChip(
                                onClick = { onSuggestionSelected(subscription.name) },
                                label = {
                                    Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                                imageVector = Icons.Filled.History,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(subscription.name)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
        subscription: Subscription,
        query: String,
        selectedCurrency: String,
        onClick: () -> Unit,
        viewModel: SubscriptionViewModel,
        modifier: Modifier = Modifier
) {
    val highlightedName = rememberHighlightedText(text = subscription.name, query = query)
    val highlightedDescription = subscription.description?.takeIf { it.isNotBlank() }?.let {
        rememberHighlightedText(text = it, query = query)
    }
    val category = viewModel.getCategoryById(subscription.categoryId)
    val billingCycleLabel = stringResource(subscription.billingCycle.toLabelRes())
    val formattedPrice = subscription.price.formatCurrency(selectedCurrency)
    val nextBilling = subscription.nextBillingDate.formatDate()
    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isActive = subscription.isActive
    val statusColor = if (isActive) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    val dueLabel = when {
        !isActive -> null
        daysUntil < 0 -> stringResource(R.string.search_status_overdue)
        daysUntil == 0L -> stringResource(R.string.search_status_due_today)
        daysUntil == 1L -> stringResource(R.string.search_status_due_tomorrow)
        else -> stringResource(R.string.search_status_due_in_days, daysUntil.toInt())
    }

    val dueColor = when {
        !isActive -> MaterialTheme.colorScheme.onSurfaceVariant
        daysUntil < 0 -> MaterialTheme.colorScheme.error
        daysUntil <= 3 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 6.dp)
    ) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = highlightedName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                    )
                    if (highlightedDescription != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                                text = highlightedDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = if (isActive) stringResource(R.string.active)
                        else stringResource(R.string.inactive),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                )
            }

            if (category != null) {
                CategoryTag(
                        category = category,
                        size = CategoryTagSize.Small,
                        showIcon = false
                )
            }

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                            text = stringResource(
                                    R.string.search_billing_frequency,
                                    billingCycleLabel
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = stringResource(
                                    R.string.search_next_billing_label,
                                    nextBilling
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (dueLabel != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = dueLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = dueColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberHighlightedText(
        text: String,
        query: String,
        highlightColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
): AnnotatedString {
    val sanitizedQuery = query.trim()
    return remember(text, sanitizedQuery, highlightColor) {
        if (sanitizedQuery.isBlank()) {
            AnnotatedString(text)
        } else {
            val builder = AnnotatedString.Builder()
            val lowerText = text.lowercase()
            val lowerQuery = sanitizedQuery.lowercase()
            var searchIndex = 0

            while (true) {
                val matchIndex = lowerText.indexOf(lowerQuery, startIndex = searchIndex)
                if (matchIndex < 0) {
                    builder.append(text.substring(searchIndex))
                    break
                }
                if (matchIndex > searchIndex) {
                    builder.append(text.substring(searchIndex, matchIndex))
                }
                builder.pushStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.SemiBold))
                builder.append(text.substring(matchIndex, matchIndex + sanitizedQuery.length))
                builder.pop()
                searchIndex = matchIndex + sanitizedQuery.length
            }

            builder.toAnnotatedString()
        }
    }
}

private fun BillingCycle.toLabelRes(): Int =
        when (this) {
            BillingCycle.DAILY -> R.string.daily
            BillingCycle.WEEKLY -> R.string.weekly
            BillingCycle.MONTHLY -> R.string.monthly
            BillingCycle.YEARLY -> R.string.yearly
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
        onFilterExpandToggle: () -> Unit,
        listState: LazyListState,
        isFabVisible: Boolean
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
                        listState = listState,
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
                            state = listState,
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
        listState: LazyListState,
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
                        state = listState,
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
    FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .navigationBarsPadding()
                .size(56.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp,
                focusedElevation = 8.dp
            )
    ) {
        Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_subscription),
                modifier = Modifier.size(24.dp)
        )
    }
}
