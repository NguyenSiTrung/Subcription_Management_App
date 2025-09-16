package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        padding = 16.dp
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAction
            ) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun NoSubscriptionsEmptyState(
    onAddSubscription: () -> Unit
) {
    EmptyState(
        title = stringResource(R.string.no_subscriptions),
        description = stringResource(R.string.no_subscriptions_description),
        actionText = stringResource(R.string.add_subscription),
        onAction = onAddSubscription
    )
}

@Composable
fun NoCategoriesEmptyState(
    onAddCategory: () -> Unit
) {
    EmptyState(
        title = stringResource(R.string.no_categories),
        description = stringResource(R.string.no_categories_description),
        actionText = stringResource(R.string.add_category),
        onAction = onAddCategory
    )
}