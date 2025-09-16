package com.example.subcriptionmanagementapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.EmptyState
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.components.NoSubscriptionsEmptyState
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import com.example.subcriptionmanagementapp.util.getDaysUntil
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: SubscriptionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val subscriptions by viewModel.activeSubscriptions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadActiveSubscriptions()
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                navController = navController,
                currentRoute = Screen.Home.route,
                showActions = false
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
                error != null -> ErrorMessage(message = error!!)
                subscriptions.isEmpty() -> NoSubscriptionsEmptyState {
                    navController.navigate(Screen.AddEditSubscription.createRoute(-1))
                }
                else -> HomeContent(
                    subscriptions = subscriptions,
                    onSubscriptionClick = { subscriptionId ->
                        navController.navigate(Screen.SubscriptionDetail.createRoute(subscriptionId))
                    },
                    onAddSubscription = {
                        navController.navigate(Screen.AddEditSubscription.createRoute(-1))
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
    onAddSubscription: () -> Unit
) {
    val upcomingSubscriptions = subscriptions
        .filter { it.nextBillingDate > System.currentTimeMillis() }
        .sortedBy { it.nextBillingDate }
        .take(5)
    
    val totalMonthlyCost = subscriptions
        .filter { it.billingCycle == BillingCycle.MONTHLY }
        .sumOf { it.price }
    
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
            Text(
                text = stringResource(R.string.upcoming_renewals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (upcomingSubscriptions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
fun SummaryCard(
    totalMonthlyCost: Double,
    subscriptionCount: Int,
    onAddSubscription: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.monthly_summary),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
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
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    
                    Text(
                        text = formatCurrency(totalMonthlyCost),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(R.string.active_subscriptions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    
                    Text(
                        text = subscriptionCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAddSubscription,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = PrimaryColor
                )
            ) {
                Text(stringResource(R.string.add_subscription))
            }
        }
    }
}

@Composable
fun UpcomingSubscriptionCard(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val daysUntil = getDaysUntil(subscription.nextBillingDate)
    val isUrgent = daysUntil <= 3
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isUrgent) WarningColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isUrgent) {
            CardDefaults.outlinedCardBorder().copy(
                width = 1.dp,
                color = WarningColor
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    text = formatCurrency(subscription.price) + "/" + when (subscription.billingCycle) {
                        BillingCycle.DAILY -> stringResource(R.string.daily)
                        BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                        BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                        BillingCycle.YEARLY -> stringResource(R.string.yearly)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatDate(subscription.nextBillingDate),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = when {
                        daysUntil < 0 -> stringResource(R.string.overdue)
                        daysUntil == 0 -> stringResource(R.string.due_today)
                        daysUntil == 1 -> stringResource(R.string.due_tomorrow)
                        else -> stringResource(R.string.due_in_days, daysUntil)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        daysUntil < 0 -> ErrorColor
                        daysUntil <= 3 -> WarningColor
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (isUrgent) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}