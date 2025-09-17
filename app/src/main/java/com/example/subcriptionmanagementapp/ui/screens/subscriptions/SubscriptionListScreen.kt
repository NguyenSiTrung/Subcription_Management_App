package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.components.NoSubscriptionsEmptyState
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
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadAllSubscriptions() }

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
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> LoadingIndicator()
                error != null ->
                        ErrorMessage(message = error!!) { viewModel.loadAllSubscriptions() }
                subscriptions.isEmpty() ->
                        NoSubscriptionsEmptyState {
                            navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                        }
                else ->
                        SubscriptionListContent(
                                subscriptions = subscriptions,
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
fun SubscriptionListContent(
        subscriptions: List<Subscription>,
        onSubscriptionClick: (Long) -> Unit
) {
    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subscriptions) { subscription ->
            SubscriptionListItem(
                    subscription = subscription,
                    onClick = { onSubscriptionClick(subscription.id) }
            )
        }
    }
}

@Composable
fun SubscriptionListItem(subscription: Subscription, onClick: () -> Unit) {
    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isUrgent = daysUntil <= 3L
    val isOverdue = daysUntil < 0L
    val isActive = subscription.isActive

    Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (!isActive) {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    } else if (isUrgent) {
                                        WarningColor.copy(alpha = 0.2f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                    ),
            border =
                    if (isUrgent || !isActive) {
                        BorderStroke(
                                width = 1.dp,
                                color =
                                        if (!isActive) {
                                            MaterialTheme.colorScheme.outline
                                        } else {
                                            WarningColor
                                        }
                        )
                    } else {
                        null
                    }
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color =
                                if (!isActive) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text = "${subscription.price.formatCurrency()}/" +
                                when (subscription.billingCycle) {
                                    BillingCycle.DAILY -> stringResource(R.string.daily)
                                    BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                                    BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                                    BillingCycle.YEARLY -> stringResource(R.string.yearly)
                                },
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                                if (!isActive) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                        text = subscription.nextBillingDate.formatDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                                if (!isActive) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text =
                                when {
                                    !isActive -> stringResource(R.string.inactive)
                                    isOverdue -> stringResource(R.string.overdue)
                                    daysUntil == 0L -> stringResource(R.string.due_today)
                                    daysUntil == 1L -> stringResource(R.string.due_tomorrow)
                                    else -> stringResource(R.string.due_in_days, daysUntil.toInt())
                                },
                        style = MaterialTheme.typography.bodySmall,
                        color =
                                when {
                                    !isActive -> MaterialTheme.colorScheme.onSurfaceVariant
                                    isOverdue -> ErrorColor
                                    daysUntil <= 3L -> WarningColor
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        fontWeight =
                                if (isUrgent || isOverdue || !isActive) FontWeight.Bold
                                else FontWeight.Normal
                )
            }
        }
    }
}
