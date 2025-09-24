package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import com.example.subcriptionmanagementapp.util.getDaysUntil

/**
 * Simplified performance-optimized subscription card with smooth animations
 * This version focuses on the core performance optimizations while being compilable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedSubscriptionCardSimple(
        subscription: Subscription,
        selectedCurrency: String,
        viewModel: SubscriptionViewModel,
        onClick: () -> Unit,
        onEdit: (() -> Unit)? = null,
        onDelete: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
    // Use remember to survive configuration changes
    var isExpanded by remember { mutableStateOf(false) }
    
    // Pre-calculate status information once
    val statusData = remember(subscription.id, subscription.nextBillingDate, subscription.isActive) {
        val daysUntil = subscription.nextBillingDate.getDaysUntil()
        val isUrgent = daysUntil in 0L..3L
        val isOverdue = daysUntil < 0L
        val isActive = subscription.isActive
        StatusDataSimple(daysUntil, isUrgent, isOverdue, isActive)
    }

    // Cache category lookup result
    val category by remember(subscription.categoryId) {
        derivedStateOf {
            subscription.categoryId?.let { id -> viewModel.getCategoryById(id) }
        }
    }

    // Optimized rotation animation - faster and more responsive
    val expandIconRotation by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
            ),
            label = "expandIconRotation"
    )

    // Optimized card elevation animation
    val cardElevation by animateDpAsState(
            targetValue = if (isExpanded) 6.dp else 2.dp,
            animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
            ),
            label = "cardElevation"
    )

    Card(
            onClick = { isExpanded = !isExpanded },
            modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .shadow(
                            elevation = cardElevation,
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                    )
                    .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Simplified gradient - only show when not transitioning for better performance
            if (isExpanded) {
                Box(
                        modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                        brush = Brush.linearGradient(
                                                colors = getOptimizedCardGradientSimple(statusData)
                                        )
                                )
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                // Header - optimized with cached data
                CompactCardHeaderSimple(
                        subscription = subscription,
                        statusData = statusData,
                        category = category,
                        selectedCurrency = selectedCurrency
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Body - simplified layout
                CompactCardBodySimple(
                        subscription = subscription,
                        statusData = statusData,
                        selectedCurrency = selectedCurrency,
                        isExpanded = isExpanded,
                        expandIconRotation = expandIconRotation,
                        onExpandClick = { isExpanded = !isExpanded }
                )

                // Optimized expanded content with lazy loading
                AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn(animationSpec = tween(200)) +
                                expandVertically(animationSpec = tween(250)),
                        exit = fadeOut(animationSpec = tween(150)) +
                                shrinkVertically(animationSpec = tween(200))
                ) {
                    ExpandedCardContentSimple(
                            subscription = subscription,
                            statusData = statusData,
                            onClick = onClick,
                            onEdit = onEdit,
                            onDelete = onDelete
                    )
                }
            }
        }
    }
}

// Data class to cache status calculations
private data class StatusDataSimple(
        val daysUntil: Long,
        val isUrgent: Boolean,
        val isOverdue: Boolean,
        val isActive: Boolean
)

// Optimized header with reduced recompositions
@Composable
private fun CompactCardHeaderSimple(
        subscription: Subscription,
        statusData: StatusDataSimple,
        category: com.example.subcriptionmanagementapp.data.local.entity.Category?,
        selectedCurrency: String
) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
    ) {
        // Left side: Name + Category
        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (category != null) {
                CategoryTag(
                        category = category,
                        size = CategoryTagSize.Small,
                        showIcon = false
                )
            }
        }

        // Right side: Compact status indicator
        CompactStatusIndicatorSimple(
                statusData = statusData
        )
    }
}

// Optimized body with simplified layout
@Composable
private fun CompactCardBodySimple(
        subscription: Subscription,
        statusData: StatusDataSimple,
        selectedCurrency: String,
        isExpanded: Boolean,
        expandIconRotation: Float,
        onExpandClick: () -> Unit
) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
    ) {
        // Price - prominent display
        Text(
                text = subscription.price.formatCurrency(selectedCurrency),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
        )

        // Compact billing info + expand button
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isExpanded) {
                CompactBillingInfoSimple(
                        statusData = statusData,
                        billingCycle = getBillingCycleLabelSimple(subscription.billingCycle),
                        nextBillingDate = subscription.nextBillingDate.formatDate()
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(
                    onClick = onExpandClick,
                    modifier = Modifier.size(28.dp)
            ) {
                Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer { rotationZ = expandIconRotation }
                )
            }
        }
    }
}

// Optimized billing info
@Composable
private fun CompactBillingInfoSimple(
        statusData: StatusDataSimple,
        billingCycle: String,
        nextBillingDate: String
) {
    val statusText = when {
        !statusData.isActive -> "Inactive"
        statusData.isOverdue -> "Overdue"
        statusData.daysUntil == 0L -> "Due today"
        statusData.daysUntil == 1L -> "Due tomorrow"
        statusData.isUrgent -> "Due in ${statusData.daysUntil} days"
        else -> "Due in ${statusData.daysUntil} days"
    }
    
    val statusColor = when {
        !statusData.isActive -> MaterialTheme.colorScheme.onSurfaceVariant
        statusData.isOverdue -> ErrorColor
        statusData.isUrgent -> WarningColor
        else -> SuccessColor
    }

    Column(horizontalAlignment = Alignment.End) {
        // Status text with icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                    maxLines = 1
            )
        }

        // Billing cycle and date
        Text(
                text = "$billingCycle • $nextBillingDate",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CompactStatusIndicatorSimple(statusData: StatusDataSimple) {
    val color = when {
        !statusData.isActive -> MaterialTheme.colorScheme.onSurfaceVariant
        statusData.isOverdue -> ErrorColor
        statusData.isUrgent -> WarningColor
        else -> SuccessColor
    }
    
    val icon = when {
        !statusData.isActive -> Icons.Default.PauseCircle
        statusData.isOverdue -> Icons.Default.Error
        statusData.isUrgent -> Icons.Default.Warning
        else -> Icons.Default.CheckCircle
    }

    Box(
            modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
                    .border(BorderStroke(1.dp, color.copy(alpha = 0.3f)), CircleShape),
            contentAlignment = Alignment.Center
    ) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
        )
    }
}

// Optimized expanded content with lazy loading
@Composable
private fun ExpandedCardContentSimple(
        subscription: Subscription,
        statusData: StatusDataSimple,
        onClick: () -> Unit,
        onEdit: (() -> Unit)?,
        onDelete: (() -> Unit)?
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                thickness = 0.5.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Detailed status information
        val statusText = when {
            !statusData.isActive -> "Inactive"
            statusData.isOverdue -> "Overdue"
            statusData.daysUntil == 0L -> "Due today"
            statusData.daysUntil == 1L -> "Due tomorrow"
            statusData.isUrgent -> "Due in ${statusData.daysUntil} days"
            else -> "Due in ${statusData.daysUntil} days"
        }
        
        val statusColor = when {
            !statusData.isActive -> MaterialTheme.colorScheme.onSurfaceVariant
            statusData.isOverdue -> ErrorColor
            statusData.isUrgent -> WarningColor
            else -> SuccessColor
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Billing information grid
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoItemSimple(
                    icon = Icons.Default.Autorenew,
                    label = "Billing cycle",
                    value = getBillingCycleLabelSimple(subscription.billingCycle),
                    modifier = Modifier.weight(1f)
            )

            InfoItemSimple(
                    icon = Icons.Default.Event,
                    label = "Next billing",
                    value = subscription.nextBillingDate.formatDate(),
                    modifier = Modifier.weight(1f)
            )
        }

        // Progress indicator for active subscriptions
        if (subscription.isActive && !statusData.isOverdue) {
            Spacer(modifier = Modifier.height(12.dp))

            val progress = calculateBillingProgressSimple(subscription.billingCycle, statusData.daysUntil)

            LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)),
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons - more compact
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(onClick = onClick, modifier = Modifier.weight(1f)) {
                Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text = "View details",
                        style = MaterialTheme.typography.labelMedium
                )
            }

            if (onEdit != null) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "Edit",
                            style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (onDelete != null) {
                OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor)
                ) {
                    Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "Delete",
                            style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItemSimple(
        icon: ImageVector,
        label: String,
        value: String,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
        )
    }
}

// Helper functions
private fun getBillingCycleLabelSimple(billingCycle: BillingCycle): String {
    return when (billingCycle) {
        BillingCycle.DAILY -> "Daily"
        BillingCycle.WEEKLY -> "Weekly"
        BillingCycle.MONTHLY -> "Monthly"
        BillingCycle.YEARLY -> "Yearly"
    }
}

private fun calculateBillingProgressSimple(billingCycle: BillingCycle, daysUntil: Long): Float {
    val daysInCycle = when (billingCycle) {
        BillingCycle.DAILY -> 1
        BillingCycle.WEEKLY -> 7
        BillingCycle.MONTHLY -> 30
        BillingCycle.YEARLY -> 365
    }

    val daysPassed = daysInCycle - (daysUntil % daysInCycle).coerceAtLeast(0)
    return (daysPassed.toFloat() / daysInCycle).coerceIn(0f, 1f)
}

// Helper data class
private data class StatusInfoSimple(
        val icon: ImageVector,
        val color: Color,
        val text: String,
        val isOverdue: Boolean,
        val daysUntil: Long
)

// Gradient function
private fun getOptimizedCardGradientSimple(statusData: StatusDataSimple): List<Color> {
    return when {
        !statusData.isActive -> listOf(
                Color.LightGray.copy(alpha = 0.3f),
                Color.White
        )
        statusData.isOverdue -> listOf(
                ErrorColor.copy(alpha = 0.08f),
                Color.Red.copy(alpha = 0.02f),
                Color.White
        )
        statusData.isUrgent -> listOf(
                WarningColor.copy(alpha = 0.06f),
                Color.Yellow.copy(alpha = 0.02f),
                Color.White
        )
        else -> listOf(
                SuccessColor.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.95f),
                Color.White
        )
    }
}