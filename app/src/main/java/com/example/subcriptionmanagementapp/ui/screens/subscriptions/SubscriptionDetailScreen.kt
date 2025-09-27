package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.CategoryTag
import com.example.subcriptionmanagementapp.ui.components.CategoryTagSize
import com.example.subcriptionmanagementapp.ui.components.CompactTopBar
import com.example.subcriptionmanagementapp.ui.components.DeleteSubscriptionConfirmationDialog
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.model.DeleteDialogState
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.ErrorColor
import com.example.subcriptionmanagementapp.ui.theme.SuccessColor
import com.example.subcriptionmanagementapp.ui.theme.WarningColor
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import com.example.subcriptionmanagementapp.util.getDaysUntil


@Composable
fun SubscriptionDetailScreen(
        navController: NavController,
        subscriptionId: Long,
        viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptionState by viewModel.selectedSubscription.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val category by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isUncategorized by
            viewModel.isSelectedSubscriptionUncategorized.collectAsStateWithLifecycle()

    var deleteDialogState by remember { mutableStateOf<DeleteDialogState>(DeleteDialogState.Hidden) }

    LaunchedEffect(subscriptionId) { viewModel.loadSubscription(subscriptionId) }

    LaunchedEffect(error) {
        if (error != null) {
            viewModel.clearError()
        }
    }

    val currentSubscription = subscriptionState

    Scaffold(
            topBar = {
                CompactTopBar(
                        title = stringResource(R.string.subscription_details),
                        navController = navController,
                        showBackButton = true
                )
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(message = error!!)
                currentSubscription == null ->
                        ErrorMessage(message = stringResource(R.string.subscription_not_found))
                else ->
                        SubscriptionDetailContent(
                                subscription = currentSubscription,
                                category = category,
                                isUncategorized = isUncategorized,
                                selectedCurrency = selectedCurrency,
                                onEditClick = {
                                    navController.navigate(
                                            Screen.AddEditSubscription.createRoute(
                                                    currentSubscription.id
                                            )
                                    )
                                },
                                onDeleteClick = {
                                    deleteDialogState = DeleteDialogState.Visible(currentSubscription)
                                },
                                onToggleReminder = {
                                    // Toggle reminder
                                }
                        )
            }
        }
    }

    // Auto-close delete dialog when deletion completes
    LaunchedEffect(isLoading, deleteDialogState) {
        if (!isLoading && deleteDialogState is DeleteDialogState.Deleting) {
            kotlinx.coroutines.delay(150)
            deleteDialogState = DeleteDialogState.Hidden
            navController.popBackStack()
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
fun SubscriptionDetailContent(
        subscription: Subscription,
        category: Category?,
        isUncategorized: Boolean,
        selectedCurrency: String,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit,
        onToggleReminder: () -> Unit
) {
    val context = LocalContext.current
    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isOverdue = daysUntil < 0L
    val isUrgent = daysUntil <= 3L

    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SubscriptionInfoCard(
                    subscription = subscription,
                    category = category,
                    isUncategorized = isUncategorized,
                    daysUntil = daysUntil,
                    isOverdue = isOverdue,
                    isUrgent = isUrgent,
                    selectedCurrency = selectedCurrency,
                    context = context
            )
        }

        item {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = onEditClick, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.edit))
                }

                OutlinedButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.weight(1f),
                        colors =
                                ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        }

        item { ReminderCard(subscription = subscription, onToggleReminder = onToggleReminder) }

        item {
            PaymentHistoryCard(
                    paymentHistory = emptyList(), // Will be populated from repository
                    selectedCurrency = selectedCurrency
            )
        }

        item { NotesCard(notes = subscription.notes ?: "") }
    }
}

@Composable
fun SubscriptionInfoCard(
        subscription: Subscription,
        category: Category?,
        isUncategorized: Boolean,
        daysUntil: Long,
        isOverdue: Boolean,
        isUrgent: Boolean,
        selectedCurrency: String,
        context: android.content.Context
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (!subscription.isActive) {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    } else if (isUrgent) {
                                        WarningColor.copy(alpha = 0.2f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                    ),
            border =
                    if (isUrgent || !subscription.isActive) {
                        BorderStroke(
                                width = 1.dp,
                                color =
                                        if (!subscription.isActive) {
                                            MaterialTheme.colorScheme.outline
                                        } else {
                                            WarningColor
                                        }
                        )
                    } else {
                        null
                    }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color =
                                if (!subscription.isActive) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                )

                if (!subscription.isActive) {
                    Text(
                            text = stringResource(R.string.inactive),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryTag(
                    category = category,
                    isUncategorized = isUncategorized,
                    size = CategoryTagSize.Medium,
                    modifier = Modifier.wrapContentWidth(),
                    showIcon = true,
                    maxWidth = 200.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                            text = stringResource(R.string.price),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                            text = subscription.price.formatCurrency(subscription.currency),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color =
                                    if (!subscription.isActive) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = stringResource(R.string.billing_cycle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                            text =
                                    when (subscription.billingCycle) {
                                        BillingCycle.DAILY -> stringResource(R.string.daily)
                                        BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                                        BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                                        BillingCycle.YEARLY -> stringResource(R.string.yearly)
                                    },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color =
                                    if (!subscription.isActive) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                            text = stringResource(R.string.next_billing_date),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                            text = subscription.nextBillingDate.formatDate(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color =
                                    if (!subscription.isActive) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = stringResource(R.string.status),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                            text =
                                    when {
                                        !subscription.isActive -> stringResource(R.string.inactive)
                                        isOverdue -> stringResource(R.string.overdue)
                                        daysUntil == 0L -> stringResource(R.string.due_today)
                                        daysUntil == 1L -> stringResource(R.string.due_tomorrow)
                                        else ->
                                                stringResource(
                                                        R.string.due_in_days,
                                                        daysUntil.toInt()
                                                )
                                    },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color =
                                    when {
                                        !subscription.isActive ->
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        isOverdue -> ErrorColor
                                        daysUntil <= 3L -> WarningColor
                                        else -> SuccessColor
                                    }
                    )
                }
            }

            if (subscription.websiteUrl != null || subscription.appPackageName != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = stringResource(R.string.access),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (subscription.websiteUrl != null) {
                        OutlinedButton(
                                onClick = {
                                    val websiteUrl =
                                            if (subscription.websiteUrl.startsWith("http://") ||
                                                            subscription.websiteUrl.startsWith(
                                                                    "https://"
                                                            )
                                            ) {
                                                subscription.websiteUrl
                                            } else {
                                                "https://${subscription.websiteUrl}"
                                            }
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                        ) { Text(stringResource(R.string.open_website)) }
                    }

                    if (subscription.appPackageName != null) {
                        OutlinedButton(
                                onClick = {
                                    val intent =
                                            context.packageManager.getLaunchIntentForPackage(
                                                    subscription.appPackageName
                                            )
                                    if (intent != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val playStoreIntent =
                                                Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "market://details?id=${subscription.appPackageName}"
                                                        )
                                                )
                                        if (playStoreIntent.resolveActivity(
                                                        context.packageManager
                                                ) != null
                                        ) {
                                            context.startActivity(playStoreIntent)
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                        ) { Text(stringResource(R.string.open_app)) }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(subscription: Subscription, onToggleReminder: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                            text = stringResource(R.string.reminder),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                }

                Switch(
                        checked = subscription.reminderDays > 0,
                        onCheckedChange = { onToggleReminder() }
                )
            }

            if (subscription.reminderDays > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                        text =
                                stringResource(
                                        R.string.reminder_days_before,
                                        subscription.reminderDays
                                ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PaymentHistoryCard(paymentHistory: List<PaymentHistory>, selectedCurrency: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                    text = stringResource(R.string.payment_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            if (paymentHistory.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                        text = stringResource(R.string.no_payment_history),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(paymentHistory) { payment ->
                        PaymentHistoryItem(payment = payment, selectedCurrency = selectedCurrency)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryItem(payment: PaymentHistory, selectedCurrency: String) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                    text = payment.paymentDate.formatDate(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
            )

            if (payment.notes != null) {
                Text(
                        text = payment.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
                text = payment.amount.formatCurrency(selectedCurrency),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NotesCard(notes: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                    text = stringResource(R.string.notes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (notes.isEmpty()) {
                Text(
                        text = stringResource(R.string.no_notes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
