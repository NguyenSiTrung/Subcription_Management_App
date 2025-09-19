package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.*
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
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

    // Calculate statistics for this category
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
                                    coroutineScope.launch {
                                        viewModel.clearError()
                                        viewModel.filterByCategory(categoryId)
                                    }
                                }
                        )
                subscriptions.isEmpty() ->
                        ModernEmptyState(
                                title = stringResource(R.string.no_subscriptions_in_category),
                                description =
                                        stringResource(
                                                R.string.no_subscriptions_in_category_subtitle,
                                                categoryName
                                        ),
                                icon = Icons.Default.Category,
                                actionText = stringResource(R.string.add_subscription),
                                onAction = {
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(-1)
                                    )
                                }
                        )
                else ->
                        ModernFilteredSubscriptionListContent(
                                subscriptions = subscriptions,
                                selectedCurrency = selectedCurrency,
                                categoryName = categoryName,
                                totalSubscriptions = totalSubscriptions,
                                activeSubscriptions = activeSubscriptions,
                                totalMonthlyCost = totalMonthlyCost,
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
                                }
                        )
            }
        }
    }
}

@Composable
fun ModernFilteredSubscriptionListContent(
        subscriptions: List<Subscription>,
        selectedCurrency: String,
        categoryName: String,
        totalSubscriptions: Int,
        activeSubscriptions: Int,
        totalMonthlyCost: Double,
        viewModel: SubscriptionViewModel,
        onSubscriptionClick: (Long) -> Unit,
        onEditClick: (Long) -> Unit,
        onDeleteClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Enhanced Category header with statistics
        Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                                text = categoryName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                                text =
                                        stringResource(
                                                R.string.subscriptions_count,
                                                subscriptions.size
                                        ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                                text = "Monthly Cost",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                                text = totalMonthlyCost.formatCurrency(selectedCurrency),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active subscriptions indicator
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessColor,
                            modifier = Modifier.size(16.dp)
                    )
                    Text(
                            text = "$activeSubscriptions of $totalSubscriptions active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

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
