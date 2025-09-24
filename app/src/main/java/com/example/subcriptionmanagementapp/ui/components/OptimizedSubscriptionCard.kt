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
 * Performance-optimized version of ModernSubscriptionCard with smooth expand/collapse animations:
 * - Optimized animation specs using spring animations
 * - Reduced layout complexity during transitions
 * - Memoized expensive calculations
 * - Simplified visual effects during animations
 * - Better state management for smooth transitions
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
    val transitionState = remember {
        MutableTransitionState(false).apply {
            targetState = false // Start collapsed
        }
    }

    val isExpanded by remember { derivedStateOf { transitionState.currentState } }
    val isTransitioning by remember { derivedStateOf { !transitionState.isIdle } }

    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isUrgent = daysUntil in 0L..3L
    val isOverdue = daysUntil < 0L
    val isActive = subscription.isActive

    // Resolve strings and colors outside remember blocks
    val inactiveText = stringResource(R.string.inactive)
    val overdueText = stringResource(R.string.overdue)
    val dueTodayText = stringResource(R.string.due_today)
    val dueTomorrowText = stringResource(R.string.due_tomorrow)
    val dueInDaysBaseText = stringResource(R.string.due_in_days)
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val errorContainerColor = MaterialTheme.colorScheme.errorContainer
    val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer

    // Memoize expensive calculations
    val statusInfo by
            remember(
                    isActive,
                    isOverdue,
                    isUrgent,
                    daysUntil,
                    inactiveText,
                    overdueText,
                    dueTodayText,
                    dueTomorrowText
            ) {
                derivedStateOf {
                    getStatusInfo(
                            isActive = isActive,
                            isOverdue = isOverdue,
                            isUrgent = isUrgent,
                            daysUntil = daysUntil,
                            inactiveText = inactiveText,
                            overdueText = overdueText,
                            dueTodayText = dueTodayText,
                            dueTomorrowText = dueTomorrowText,
                            dueInDaysBaseText = dueInDaysBaseText,
                            onSurfaceVariantColor = onSurfaceVariantColor
                    )
                }
            }

    val cardGradient by
            remember(
                    isActive,
                    isOverdue,
                    isUrgent,
                    surfaceVariantColor,
                    surfaceColor,
                    errorContainerColor,
                    secondaryContainerColor,
                    primaryContainerColor
            ) {
                derivedStateOf {
                    getCardGradient(
                            isActive = isActive,
                            isOverdue = isOverdue,
                            isUrgent = isUrgent,
                            surfaceVariantColor = surfaceVariantColor,
                            surfaceColor = surfaceColor,
                            errorContainerColor = errorContainerColor,
                            secondaryContainerColor = secondaryContainerColor,
                            primaryContainerColor = primaryContainerColor
                    )
                }
            }

    val billingCycleLabel by
            remember(subscription.billingCycle) {
                derivedStateOf { getBillingCycleLabel(subscription.billingCycle) }
            }

    val cardShape = RoundedCornerShape(16.dp)

    // Optimized rotation animation using spring
    val expandIconRotation by
            animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec =
                            spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                            ),
                    label = "expandIconRotation"
            )

    Card(
            onClick = { transitionState.targetState = !transitionState.currentState },
            modifier =
                    modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .shadow(
                                    elevation = if (isExpanded) 6.dp else 2.dp,
                                    shape = cardShape,
                                    clip = false
                            )
                            .clip(cardShape),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Apply gradient only when not transitioning for better performance
            if (!isTransitioning) {
                Box(
                        modifier =
                                Modifier.matchParentSize()
                                        .clip(cardShape)
                                        .background(brush = Brush.linearGradient(cardGradient))
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                // Always visible header content
                CompactCardHeader(
                        subscription = subscription,
                        statusInfo = statusInfo,
                        selectedCurrency = selectedCurrency,
                        viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Compact body that adapts to expansion state
                CompactCardBody(
                        subscription = subscription,
                        statusInfo = statusInfo,
                        billingCycleLabel = billingCycleLabel,
                        selectedCurrency = selectedCurrency,
                        isExpanded = isExpanded,
                        isTransitioning = isTransitioning,
                        expandIconRotation = expandIconRotation,
                        onExpandClick = {
                            transitionState.targetState = !transitionState.currentState
                        }
                )

                // Optimized expanded content animation
                AnimatedVisibility(
                        visibleState = transitionState,
                        enter =
                                fadeIn(
                                        animationSpec =
                                                spring(
                                                        dampingRatio =
                                                                Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                )
                                ) +
                                        expandVertically(
                                                animationSpec =
                                                        spring(
                                                                dampingRatio =
                                                                        Spring.DampingRatioMediumBouncy,
                                                                stiffness = Spring.StiffnessMedium
                                                        )
                                        ),
                        exit =
                                fadeOut(
                                        animationSpec =
                                                spring(
                                                        dampingRatio =
                                                                Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                )
                                ) +
                                        shrinkVertically(
                                                animationSpec =
                                                        spring(
                                                                dampingRatio =
                                                                        Spring.DampingRatioMediumBouncy,
                                                                stiffness = Spring.StiffnessMedium
                                                        )
                                        )
                ) {
                    ExpandedCardContent(
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
private data class StatusInfo(
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
