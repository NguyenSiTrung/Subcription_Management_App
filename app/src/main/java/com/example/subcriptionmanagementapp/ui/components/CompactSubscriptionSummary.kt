package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.util.formatCurrency

@Composable
fun CompactSubscriptionSummary(
    totalMonthlyCost: Double,
    activeSubscriptions: Int,
    subscriptionCount: Int,
    onAddSubscription: () -> Unit,
    selectedCurrency: String,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Monthly spending section
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.monthly_spending),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = totalMonthlyCost.formatCurrency(selectedCurrency),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Active subscriptions section
            Column(
                modifier = Modifier.weight(0.8f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.active_subscriptions),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$activeSubscriptions / $subscriptionCount",
                    style = MaterialTheme.typography.titleSmall,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Add button
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = contentColor,
                contentColor = containerColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onAddSubscription),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_subscription),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}