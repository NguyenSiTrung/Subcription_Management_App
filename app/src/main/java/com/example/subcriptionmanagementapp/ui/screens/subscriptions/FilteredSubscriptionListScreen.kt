package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.components.EmptyState
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import kotlinx.coroutines.launch

@Composable
fun FilteredSubscriptionListScreen(
    navController: NavController,
    categoryId: Long,
    categoryName: String,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.filteredSubscriptions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        viewModel.loadAllSubscriptions()
        viewModel.loadCategories()
        viewModel.filterByCategory(categoryId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = categoryName,
                navController = navController,
                currentRoute = "filtered_subscription_list",
                showBackButton = true
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(
                    message = error!!,
                    onRetry = {
                        coroutineScope.launch {
                            viewModel.clearError()
                            viewModel.filterByCategory(categoryId)
                        }
                    }
                )
                subscriptions.isEmpty() -> EmptyState(
                    title = stringResource(R.string.no_subscriptions_in_category),
                    description = stringResource(R.string.no_subscriptions_in_category_subtitle, categoryName),
                    actionText = stringResource(R.string.add_subscription),
                    onAction = {
                        navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                    }
                )
                else -> FilteredSubscriptionListContent(
                    subscriptions = subscriptions,
                    selectedCurrency = selectedCurrency,
                    categoryName = categoryName,
                    onSubscriptionClick = { subscriptionId ->
                        navController.navigate(
                            Screen.SubscriptionDetail.createRoute(subscriptionId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FilteredSubscriptionListContent(
    subscriptions: List<Subscription>,
    selectedCurrency: String,
    categoryName: String,
    onSubscriptionClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Category header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.subscriptions_count, subscriptions.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Subscription List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subscriptions) { subscription ->
                SubscriptionListItem(
                    subscription = subscription,
                    selectedCurrency = selectedCurrency,
                    onClick = { onSubscriptionClick(subscription.id) }
                )
            }
        }
    }
}
