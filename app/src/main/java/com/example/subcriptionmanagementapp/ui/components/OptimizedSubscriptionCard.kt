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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import com.example.subcriptionmanagementapp.util.getDaysUntil

/**
 * Ultra-performance-optimized subscription card with smooth animations:
 * - Pre-calculated gradients and colors cached in memory
 * - Optimized category lookup with local state
 * - Simplified animation specs for 60fps performance
 * - Lazy loading of expanded content
 * - Minimal recomposition triggers
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedSubscriptionCard(
        subscription: Subscription,
        selectedCurrency: String,
        viewModel: SubscriptionViewModel,
        onClick: () -> Unit,
        onEdit: (() -> Unit)? = null,
        onDelete: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
    // Use remember for transition state (cannot be saved with rememberSaveable)
    val transitionState = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }

    val isExpanded by remember { derivedStateOf { transitionState.currentState } }
    val isTransitioning by remember { derivedStateOf { !transitionState.isIdle } }

    // Pre-calculate all status information once
    val statusData = remember(subscription.id, subscription.nextBillingDate, subscription.isActive) {
        val daysUntil = subscription.nextBillingDate.getDaysUntil()
        val isUrgent = daysUntil in 0L..3L
        val isOverdue = daysUntil < 0L
        val isActive = subscription.isActive
        StatusDataOptimized(daysUntil, isUrgent, isOverdue, isActive)
    }

    // Cache category lookup result
    val category by remember(subscription.categoryId) {
        derivedStateOf {
            subscription.categoryId?.let { id -> viewModel.getCategoryById(id) }
        }
    }

    // Pre-calculate all strings and colors - only once per subscription state change
    val statusInfo by remember(statusData) {
        derivedStateOf {
            when {
                !statusData.isActive ->         StatusInfo(
                        icon = Icons.Default.PauseCircle,
                        color = Color.Gray, // Use direct color instead of MaterialTheme
                        text = "Inactive",
                        isOverdue = false,
                        daysUntil = 0
                )
                statusData.isOverdue ->         StatusInfo(
                        icon = Icons.Default.Error,
                        color = ErrorColor,
                        text = "Overdue",
                        isOverdue = true,
                        daysUntil = -1
                )
                statusData.daysUntil == 0L ->         StatusInfo(
                        icon = Icons.Default.Warning,
                        color = WarningColor,
                        text = "Due today",
                        isOverdue = false,
                        daysUntil = 0
                )
                statusData.daysUntil == 1L ->         StatusInfo(
                        icon = Icons.Default.Warning,
                        color = WarningColor,
                        text = "Due tomorrow",
                        isOverdue = false,
                        daysUntil = 1
                )
                statusData.isUrgent ->         StatusInfo(
                        icon = Icons.Default.Warning,
                        color = WarningColor,
                        text = "Due in ${statusData.daysUntil} days",
                        isOverdue = false,
                        daysUntil = statusData.daysUntil
                )
                else ->         StatusInfo(
                        icon = Icons.Default.CheckCircle,
                        color = SuccessColor,
                        text = "Due in ${statusData.daysUntil} days",
                        isOverdue = false,
                        daysUntil = statusData.daysUntil
                )
            }
        }
    }

    // Cache gradient colors - calculated once and reused
    val cardGradient by remember(statusData) {
        derivedStateOf {
            when {
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
    }

    val billingCycleLabel by remember(subscription.billingCycle) {
        derivedStateOf { getBillingCycleLabel(subscription.billingCycle) }
    }

    val cardShape = RoundedCornerShape(16.dp)

    // Optimized rotation animation - faster and more responsive
    val expandIconRotation by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = OptimizedAnimationSpecs.iconRotation,
            label = "expandIconRotation"
    )

    // Optimized card elevation animation
    val cardElevation by animateDpAsState(
            targetValue = if (isExpanded) 6.dp else 2.dp,
            animationSpec = OptimizedAnimationSpecs.cardElevation,
            label = "cardElevation"
    )

    Card(
            onClick = { transitionState.targetState = !transitionState.currentState },
            modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .shadow(
                            elevation = cardElevation,
                            shape = cardShape,
                            clip = false
                    )
                    .clip(cardShape),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Simplified gradient - only show when not transitioning for better performance
            if (isTransitioning.not() && statusData.isActive) {
                Box(
                        modifier = Modifier
                                .matchParentSize()
                                .clip(cardShape)
                                .background(brush = Brush.linearGradient(cardGradient))
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                // Header - optimized with cached data
                CompactCardHeaderOptimized(
                        subscription = subscription,
                        statusInfo = statusInfo,
                        category = category,
                        selectedCurrency = selectedCurrency
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Body - simplified layout
                CompactCardBodyOptimized(
                        subscription = subscription,
                        statusInfo = statusInfo,
                        billingCycleLabel = billingCycleLabel,
                        selectedCurrency = selectedCurrency,
                        isExpanded = isExpanded,
                        expandIconRotation = expandIconRotation,
                        onExpandClick = {
                            transitionState.targetState = !transitionState.currentState
                        }
                )

                // Optimized expanded content with lazy loading
                AnimatedVisibility(
                        visibleState = transitionState,
                        enter = fadeIn(animationSpec = tween(200)) +
                                expandVertically(animationSpec = tween(250)),
                        exit = fadeOut(animationSpec = tween(150)) +
                                shrinkVertically(animationSpec = tween(200))
                ) {
                    // Only render expanded content when actually expanded
                    if (isExpanded) {
                        ExpandedCardContentOptimized(
                                subscription = subscription,
                                statusInfo = statusInfo,
                                billingCycleLabel = billingCycleLabel,
                                onClick = onClick,
                                onEdit = onEdit,
                                onDelete = onDelete
                        )
                    }
                }
            }
        }
    }
}

// Data class to cache status calculations
private data class StatusDataOptimized(
        val daysUntil: Long,
        val isUrgent: Boolean,
        val isOverdue: Boolean,
        val isActive: Boolean
)

// Optimized header with reduced recompositions
@Composable
private fun CompactCardHeaderOptimized(
        subscription: Subscription,
        statusInfo: StatusInfo,
        category: Category?,
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
        CompactStatusIndicator(
                icon = statusInfo.icon,
                color = statusInfo.color,
                isActive = subscription.isActive
        )
    }
}

// Optimized body with simplified layout
@Composable
private fun CompactCardBodyOptimized(
        subscription: Subscription,
        statusInfo: StatusInfo,
        billingCycleLabel: String,
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
                CompactBillingInfoOptimized(
                        statusText = statusInfo.text,
                        statusColor = statusInfo.color,
                        billingCycle = billingCycleLabel,
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
private fun CompactBillingInfoOptimized(
        statusText: String,
        statusColor: Color,
        billingCycle: String,
        nextBillingDate: String
) {
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
private fun CompactCardHeader(
        subscription: Subscription,
        statusInfo: StatusInfo,
        selectedCurrency: String,
        viewModel: SubscriptionViewModel
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
                    style = MaterialTheme.typography.titleMedium, // Reduced from titleLarge
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            val category = subscription.categoryId?.let { id -> viewModel.getCategoryById(id) }
            if (category != null) {
                CategoryTag(category = category, size = CategoryTagSize.Small, showIcon = false)
            }
        }

        // Right side: Compact status indicator
        CompactStatusIndicator(
                icon = statusInfo.icon,
                color = statusInfo.color,
                isActive = subscription.isActive
        )
    }
}

@Composable
private fun CompactCardBody(
        subscription: Subscription,
        statusInfo: StatusInfo,
        billingCycleLabel: String,
        selectedCurrency: String,
        isExpanded: Boolean,
        isTransitioning: Boolean,
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
                style = MaterialTheme.typography.headlineSmall, // Reduced from headlineMedium
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
        )

        // Compact billing info + expand button
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isExpanded && !isTransitioning) {
                CompactBillingInfo(
                        statusText = statusInfo.text,
                        statusColor = statusInfo.color,
                        billingCycle = billingCycleLabel,
                        nextBillingDate = subscription.nextBillingDate.formatDate()
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(
                    onClick = onExpandClick,
                    modifier = Modifier.size(28.dp) // Reduced size
            ) {
                Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier =
                                Modifier.size(18.dp).graphicsLayer {
                                    rotationZ = expandIconRotation
                                }
                )
            }
        }
    }
}

@Composable
private fun CompactBillingInfo(
        statusText: String,
        statusColor: Color,
        billingCycle: String,
        nextBillingDate: String
) {
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
private fun CompactStatusIndicator(icon: ImageVector, color: Color, isActive: Boolean) {
    Box(
            modifier =
                    Modifier.size(24.dp)
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

@Composable
private fun ExpandedCardContent(
        subscription: Subscription,
        statusInfo: StatusInfo,
        billingCycleLabel: String,
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
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = statusInfo.icon,
                    contentDescription = null,
                    tint = statusInfo.color,
                    modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = statusInfo.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusInfo.color
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Billing information grid
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoItem(
                    icon = Icons.Default.Autorenew,
                    label = stringResource(R.string.billing_cycle),
                    value = billingCycleLabel,
                    modifier = Modifier.weight(1f)
            )

            InfoItem(
                    icon = Icons.Default.Event,
                    label = stringResource(R.string.next_billing_date),
                    value = subscription.nextBillingDate.formatDate(),
                    modifier = Modifier.weight(1f)
            )
        }

        // Progress indicator for active subscriptions
        if (subscription.isActive && !statusInfo.isOverdue) {
            Spacer(modifier = Modifier.height(12.dp))

            val progress = calculateBillingProgress(subscription.billingCycle, statusInfo.daysUntil)

            LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)),
                    color = statusInfo.color,
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
                        text = stringResource(R.string.view_details),
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
                            text = stringResource(R.string.edit),
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
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
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

// Helper data class and functions
private data class         StatusInfo(
        val icon: ImageVector,
        val color: Color,
        val text: String,
        val isOverdue: Boolean,
        val daysUntil: Long
)

private fun getStatusInfo(
        isActive: Boolean,
        isOverdue: Boolean,
        isUrgent: Boolean,
        daysUntil: Long,
        inactiveText: String,
        overdueText: String,
        dueTodayText: String,
        dueTomorrowText: String,
        dueInDaysBaseText: String,
        onSurfaceVariantColor: Color
): StatusInfo {
    return when {
        !isActive ->
                StatusInfo(
                        icon = Icons.Default.PauseCircle,
                        color = onSurfaceVariantColor,
                        text = inactiveText,
                        isOverdue = false,
                        daysUntil = daysUntil
                )
        isOverdue ->
                StatusInfo(
                        icon = Icons.Default.Error,
                        color = ErrorColor,
                        text = overdueText,
                        isOverdue = true,
                        daysUntil = daysUntil
                )
        daysUntil == 0L ->
                StatusInfo(
                        icon = Icons.Default.Warning,
                        color = WarningColor,
                        text = dueTodayText,
                        isOverdue = false,
                        daysUntil = daysUntil
                )
        daysUntil == 1L ->
                StatusInfo(
                        icon = Icons.Default.Warning,
                        color = WarningColor,
                        text = dueTomorrowText,
                        isOverdue = false,
                        daysUntil = daysUntil
                )
        isUrgent ->
                StatusInfo(
                        icon = Icons.Default.Warning,
                        color = WarningColor,
                        text = dueInDaysBaseText.format(daysUntil.toInt()),
                        isOverdue = false,
                        daysUntil = daysUntil
                )
        else ->
                StatusInfo(
                        icon = Icons.Default.CheckCircle,
                        color = SuccessColor,
                        text = dueInDaysBaseText.format(daysUntil.toInt()),
                        isOverdue = false,
                        daysUntil = daysUntil
                )
    }
}

private fun getCardGradient(
        isActive: Boolean,
        isOverdue: Boolean,
        isUrgent: Boolean,
        surfaceVariantColor: Color,
        surfaceColor: Color,
        errorContainerColor: Color,
        secondaryContainerColor: Color,
        primaryContainerColor: Color
): List<Color> {
    return when {
        !isActive -> listOf(surfaceVariantColor.copy(alpha = 0.3f), surfaceColor)
        isOverdue ->
                listOf(
                        ErrorColor.copy(alpha = 0.08f),
                        errorContainerColor.copy(alpha = 0.02f),
                        surfaceColor
                )
        isUrgent ->
                listOf(
                        WarningColor.copy(alpha = 0.06f),
                        secondaryContainerColor.copy(alpha = 0.02f),
                        surfaceColor
                )
        else ->
                listOf(
                        primaryContainerColor.copy(alpha = 0.08f),
                        surfaceColor.copy(alpha = 0.95f),
                        surfaceColor
                )
    }
}

private fun getBillingCycleLabel(billingCycle: BillingCycle): String {
    return when (billingCycle) {
        BillingCycle.DAILY -> "Daily"
        BillingCycle.WEEKLY -> "Weekly"
        BillingCycle.MONTHLY -> "Monthly"
        BillingCycle.YEARLY -> "Yearly"
    }
}

private fun calculateBillingProgress(billingCycle: BillingCycle, daysUntil: Long): Float {
    val daysInCycle =
            when (billingCycle) {
                BillingCycle.DAILY -> 1
                BillingCycle.WEEKLY -> 7
                BillingCycle.MONTHLY -> 30
                BillingCycle.YEARLY -> 365
            }

    val daysPassed = daysInCycle - (daysUntil % daysInCycle).coerceAtLeast(0)
    return (daysPassed.toFloat() / daysInCycle).coerceIn(0f, 1f)
}

// Optimized status info functions with pre-calculated results
@Composable
private fun getOptimizedStatusInfo(statusData: StatusDataOptimized): StatusInfo {
    return when {
        !statusData.isActive -> getInactiveStatus()
        statusData.isOverdue -> getOverdueStatus()
        statusData.daysUntil == 0L -> getDueTodayStatus()
        statusData.daysUntil == 1L -> getDueTomorrowStatus()
        statusData.isUrgent ->         StatusInfo(
                icon = Icons.Default.Warning,
                color = WarningColor,
                text = "Due in ${statusData.daysUntil} days",
                isOverdue = false,
                daysUntil = statusData.daysUntil
        )
        else ->         StatusInfo(
                icon = Icons.Default.CheckCircle,
                color = SuccessColor,
                text = "Due in ${statusData.daysUntil} days",
                isOverdue = false,
                daysUntil = statusData.daysUntil
        )
    }
}

@Composable
private fun getOptimizedCardGradient(statusData: StatusDataOptimized): List<Color> {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val errorContainerColor = MaterialTheme.colorScheme.errorContainer
    val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer

    return when {
        !statusData.isActive -> getInactiveGradient()
        statusData.isOverdue -> getOverdueGradient().map { color ->
            when (color) {
                ErrorColor -> ErrorColor.copy(alpha = 0.08f)
                errorContainerColor -> errorContainerColor.copy(alpha = 0.02f)
                else -> color
            }
        }
        statusData.isUrgent -> getUrgentGradient().map { color ->
            when (color) {
                WarningColor -> WarningColor.copy(alpha = 0.06f)
                secondaryContainerColor -> secondaryContainerColor.copy(alpha = 0.02f)
                else -> color
            }
        }
        else -> getActiveGradient().map { color ->
            when (color) {
                primaryContainerColor -> primaryContainerColor.copy(alpha = 0.08f)
                surfaceColor -> surfaceColor.copy(alpha = 0.95f)
                else -> color
            }
        }
    }
}

// Pre-calculated status info objects to avoid repeated allocations
@Composable
private fun getInactiveStatus(): StatusInfo {
    return StatusInfo(
            icon = Icons.Default.PauseCircle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = "Inactive",
            isOverdue = false,
            daysUntil = 0
    )
}

@Composable
private fun getOverdueStatus(): StatusInfo {
    return StatusInfo(
            icon = Icons.Default.Error,
            color = ErrorColor,
            text = "Overdue",
            isOverdue = true,
            daysUntil = -1
    )
}

@Composable
private fun getDueTodayStatus(): StatusInfo {
    return StatusInfo(
            icon = Icons.Default.Warning,
            color = WarningColor,
            text = "Due today",
            isOverdue = false,
            daysUntil = 0
    )
}

@Composable
private fun getDueTomorrowStatus(): StatusInfo {
    return StatusInfo(
            icon = Icons.Default.Warning,
            color = WarningColor,
            text = "Due tomorrow",
            isOverdue = false,
            daysUntil = 1
    )
}

// Pre-calculated gradient color lists
@Composable
private fun getInactiveGradient(): List<Color> {
    return listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun getOverdueGradient(): List<Color> {
    return listOf(
            ErrorColor.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.02f),
            MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun getUrgentGradient(): List<Color> {
    return listOf(
            WarningColor.copy(alpha = 0.06f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.02f),
            MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun getActiveGradient(): List<Color> {
    return listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surface
    )
}

// Optimized expanded content with lazy loading
@Composable
private fun ExpandedCardContentOptimized(
        subscription: Subscription,
        statusInfo: StatusInfo,
        billingCycleLabel: String,
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
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = statusInfo.icon,
                    contentDescription = null,
                    tint = statusInfo.color,
                    modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = statusInfo.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusInfo.color
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Billing information grid
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoItem(
                    icon = Icons.Default.Autorenew,
                    label = "Billing cycle",
                    value = billingCycleLabel,
                    modifier = Modifier.weight(1f)
            )

            InfoItem(
                    icon = Icons.Default.Event,
                    label = "Next billing",
                    value = subscription.nextBillingDate.formatDate(),
                    modifier = Modifier.weight(1f)
            )
        }

        // Progress indicator for active subscriptions
        if (subscription.isActive && !statusInfo.isOverdue) {
            Spacer(modifier = Modifier.height(12.dp))

            val progress = calculateBillingProgress(subscription.billingCycle, statusInfo.daysUntil)

            LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)),
                    color = statusInfo.color,
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
