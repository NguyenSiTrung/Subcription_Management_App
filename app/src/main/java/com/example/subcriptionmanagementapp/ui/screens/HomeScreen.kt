package com.example.subcriptionmanagementapp.ui.screens

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
fun HomeScreen(
        navController: NavController,
        viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.activeSubscriptions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadActiveSubscriptions() }

    Scaffold(
            topBar = {
                AppTopBar(
                        title = stringResource(R.string.app_name),
                        navController = navController,
                        currentRoute = Screen.Home.route
                )
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(message = error!!)
                subscriptions.isEmpty() ->
                        NoSubscriptionsEmptyState {
                            navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                        }
                else ->
                        HomeContent(
                                subscriptions = subscriptions,
                                onSubscriptionClick = { subscriptionId ->
                                    navController.navigate(
                                            Screen.SubscriptionDetail.createRoute(subscriptionId)
                                    )
                                },
                                onAddSubscription = {
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(-1)
                                    )
                                },
                                onViewAllSubscriptions = {
                                    navController.navigate(Screen.SubscriptionList.route)
                                }
                        )
            }
        }
    }
}

@Composable
fun HomeContent(
        subscriptions: List<Subscription>,
        onSubscriptionClick: (Long) -> Unit,
        onAddSubscription: () -> Unit,
        onViewAllSubscriptions: () -> Unit
) {
    val upcomingSubscriptions =
            subscriptions
                    .filter { it.nextBillingDate > System.currentTimeMillis() }
                    .sortedBy { it.nextBillingDate }
                    .take(5)

    val totalMonthlyCost =
            subscriptions.filter { it.billingCycle == BillingCycle.MONTHLY }.sumOf { it.price }

    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryCard(
                    totalMonthlyCost = totalMonthlyCost,
                    subscriptionCount = subscriptions.size,
                    onAddSubscription = onAddSubscription
            )
        }

        item {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = stringResource(R.string.upcoming_renewals),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onViewAllSubscriptions) {
                    Text(text = stringResource(R.string.view_all_subscriptions))
                }
            }
        }

        if (upcomingSubscriptions.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
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
            }
        } else {
            items(upcomingSubscriptions) { subscription ->
                UpcomingSubscriptionCard(
                        subscription = subscription,
                        onClick = { onSubscriptionClick(subscription.id) }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(totalMonthlyCost: Double, subscriptionCount: Int, onAddSubscription: () -> Unit) {
    val cardContainerColor = MaterialTheme.colorScheme.primaryContainer
    val cardContentColor = MaterialTheme.colorScheme.onPrimaryContainer

    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                    text = stringResource(R.string.monthly_summary),
                    style = MaterialTheme.typography.titleMedium,
                    color = cardContentColor,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                            text = stringResource(R.string.total_monthly_cost),
                            style = MaterialTheme.typography.bodyMedium,
                            color = cardContentColor
                    )

                    Text(
                            text = totalMonthlyCost.formatCurrency(),
                            style = MaterialTheme.typography.titleLarge,
                            color = cardContentColor,
                            fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = stringResource(R.string.active_subscriptions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = cardContentColor
                    )

                    Text(
                            text = subscriptionCount.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = cardContentColor,
                            fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                    onClick = onAddSubscription,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                            )
            ) { Text(stringResource(R.string.add_subscription)) }
        }
    }
}

@Composable
fun UpcomingSubscriptionCard(subscription: Subscription, onClick: () -> Unit) {
    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isUrgent = daysUntil <= 3

    Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isUrgent) WarningColor.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface
                    ),
            border =
                    if (isUrgent) {
                        BorderStroke(1.dp, WarningColor)
                    } else {
                        null
                    }
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text =
                                subscription.price.formatCurrency() +
                                        "/" +
                                        when (subscription.billingCycle) {
                                            BillingCycle.DAILY -> stringResource(R.string.daily)
                                            BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                                            BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                                            BillingCycle.YEARLY -> stringResource(R.string.yearly)
                                        },
                        style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                        text = subscription.nextBillingDate.formatDate(),
                        style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

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
