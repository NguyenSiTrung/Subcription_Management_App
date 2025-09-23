package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
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
 * Optimized version of ModernSubscriptionCard with improved UI/UX:
 * - Better content hierarchy and spacing
 * - More efficient use of card height
 * - Improved visual organization
 * - Enhanced readability and accessibility
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
    var isExpanded by remember { mutableStateOf(false) }

    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isUrgent = daysUntil in 0L..3L
    val isOverdue = daysUntil < 0L
    val isActive = subscription.isActive

    var isSwiped by remember { mutableStateOf(false) }
    var swipeOffset by remember { mutableStateOf(0f) }
    
    // Animated swipe offset for smoother visual feedback
    val animatedSwipeOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "swipeOffset"
    )

    val draggableState = rememberDraggableState { delta ->
        if (onDelete != null && !isExpanded) {
            swipeOffset += delta
            swipeOffset = swipeOffset.coerceIn(-200f, 50f)
            isSwiped = swipeOffset < -80f // Increased threshold for better UX
        }
    }
    
    // Auto-reset swipe when not swiped
    LaunchedEffect(isSwiped) {
        if (!isSwiped && swipeOffset != 0f) {
            swipeOffset = 0f
        }
    }

    val statusInfo = getStatusInfo(isActive, isOverdue, isUrgent, daysUntil)
    val cardGradient = getCardGradient(isActive, isOverdue, isUrgent)
    val billingCycleLabel = getBillingCycleLabel(subscription.billingCycle)

    val cardShape = RoundedCornerShape(16.dp) // Reduced from 24dp for better proportion

    // Animated rotation for expand/collapse icon
    val expandIconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "expandIconRotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp) // Reduced vertical padding
            .graphicsLayer { translationX = animatedSwipeOffset }
            .shadow(
                elevation = if (isExpanded) 8.dp else 4.dp, // Reduced elevation
                shape = cardShape,
                clip = false
            )
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = onDelete != null && !isExpanded
            )
            .clip(cardShape)
            .animateContentSize(animationSpec = tween(300)),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background gradient overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(cardShape)
                    .background(brush = Brush.linearGradient(cardGradient))
            )

            // Glass-morphism effect border
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(cardShape)
                    .border(
                        BorderStroke(
                            width = 0.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            )
                        ),
                        shape = cardShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp) // Reduced from 16dp
            ) {
                // COLLAPSED STATE - Optimized compact layout
                CompactCardHeader(
                    subscription = subscription,
                    statusInfo = statusInfo,
                    selectedCurrency = selectedCurrency,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

                CompactCardBody(
                    subscription = subscription,
                    statusInfo = statusInfo,
                    billingCycleLabel = billingCycleLabel,
                    selectedCurrency = selectedCurrency,
                    isExpanded = isExpanded,
                    expandIconRotation = expandIconRotation,
                    onExpandClick = { 
                        if (!isSwiped) {
                            isExpanded = !isExpanded
                        }
                    }
                )

                // EXPANDED STATE - Detailed information
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(animationSpec = tween(300)) + 
                           fadeIn(animationSpec = tween(300)),
                    exit = shrinkVertically(animationSpec = tween(300)) + 
                          fadeOut(animationSpec = tween(300))
                ) {
                    ExpandedCardContent(
                        subscription = subscription,
                        statusInfo = statusInfo,
                        billingCycleLabel = billingCycleLabel,
                        onClick = onClick,
                        onEdit = onEdit
                    )
                }
            }

            // Swipe to delete overlay
            SwipeDeleteOverlay(
                isVisible = onDelete != null && isSwiped && !isExpanded,
                cardShape = cardShape,
                onDelete = {
                    onDelete?.invoke()
                    swipeOffset = 0f
                    isSwiped = false
                },
                onEdit = onEdit?.let { editFn ->
                    {
                        editFn()
                        swipeOffset = 0f
                        isSwiped = false
                    }
                }
            )
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

@Composable
private fun CompactCardBody(
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
            style = MaterialTheme.typography.headlineSmall, // Reduced from headlineMedium
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Compact billing info + expand button
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isExpanded) {
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
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer { rotationZ = expandIconRotation }
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
private fun CompactStatusIndicator(
    icon: ImageVector,
    color: Color,
    isActive: Boolean
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(
                BorderStroke(1.dp, color.copy(alpha = 0.3f)),
                CircleShape
            ),
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
    onEdit: (() -> Unit)?
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            thickness = 0.5.dp
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Detailed status information
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50)),
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
            FilledTonalButton(
                onClick = onClick,
                modifier = Modifier.weight(1f)
            ) {
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
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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

@Composable
private fun SwipeDeleteOverlay(
    isVisible: Boolean,
    cardShape: RoundedCornerShape,
    onDelete: () -> Unit,
    onEdit: (() -> Unit)?
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(200)),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(150)
        ) + fadeOut(animationSpec = tween(150))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ErrorColor.copy(alpha = 0.9f),
                            ErrorColor.copy(alpha = 0.95f)
                        ),
                        startX = 0f,
                        endX = Float.POSITIVE_INFINITY
                    )
                ),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            
            if (onEdit != null) {
                FilledIconButton(
                    onClick = onEdit,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            FilledIconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
        }
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

@Composable
private fun getStatusInfo(
    isActive: Boolean,
    isOverdue: Boolean,
    isUrgent: Boolean,
    daysUntil: Long
): StatusInfo {
    return when {
        !isActive -> StatusInfo(
            icon = Icons.Default.PauseCircle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = stringResource(R.string.inactive),
            isOverdue = false,
            daysUntil = daysUntil
        )
        isOverdue -> StatusInfo(
            icon = Icons.Default.Error,
            color = ErrorColor,
            text = stringResource(R.string.overdue),
            isOverdue = true,
            daysUntil = daysUntil
        )
        daysUntil == 0L -> StatusInfo(
            icon = Icons.Default.Warning,
            color = WarningColor,
            text = stringResource(R.string.due_today),
            isOverdue = false,
            daysUntil = daysUntil
        )
        daysUntil == 1L -> StatusInfo(
            icon = Icons.Default.Warning,
            color = WarningColor,
            text = stringResource(R.string.due_tomorrow),
            isOverdue = false,
            daysUntil = daysUntil
        )
        isUrgent -> StatusInfo(
            icon = Icons.Default.Warning,
            color = WarningColor,
            text = stringResource(R.string.due_in_days, daysUntil.toInt()),
            isOverdue = false,
            daysUntil = daysUntil
        )
        else -> StatusInfo(
            icon = Icons.Default.CheckCircle,
            color = SuccessColor,
            text = stringResource(R.string.due_in_days, daysUntil.toInt()),
            isOverdue = false,
            daysUntil = daysUntil
        )
    }
}

@Composable
private fun getCardGradient(
    isActive: Boolean,
    isOverdue: Boolean,
    isUrgent: Boolean
): List<Color> {
    return when {
        !isActive -> listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
        isOverdue -> listOf(
            ErrorColor.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.02f),
            MaterialTheme.colorScheme.surface
        )
        isUrgent -> listOf(
            WarningColor.copy(alpha = 0.06f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.02f),
            MaterialTheme.colorScheme.surface
        )
        else -> listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun getBillingCycleLabel(billingCycle: BillingCycle): String {
    return when (billingCycle) {
        BillingCycle.DAILY -> stringResource(R.string.daily)
        BillingCycle.WEEKLY -> stringResource(R.string.weekly)
        BillingCycle.MONTHLY -> stringResource(R.string.monthly)
        BillingCycle.YEARLY -> stringResource(R.string.yearly)
    }
}

private fun calculateBillingProgress(billingCycle: BillingCycle, daysUntil: Long): Float {
    val daysInCycle = when (billingCycle) {
        BillingCycle.DAILY -> 1
        BillingCycle.WEEKLY -> 7
        BillingCycle.MONTHLY -> 30
        BillingCycle.YEARLY -> 365
    }
    
    val daysPassed = daysInCycle - (daysUntil % daysInCycle).coerceAtLeast(0)
    return (daysPassed.toFloat() / daysInCycle).coerceIn(0f, 1f)
}
