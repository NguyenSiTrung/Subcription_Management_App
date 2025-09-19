package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSubscriptionCard(
        subscription: Subscription,
        selectedCurrency: String,
        viewModel: SubscriptionViewModel,
        onClick: () -> Unit,
        onEdit: (() -> Unit)? = null,
        onDelete: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
    val daysUntil = subscription.nextBillingDate.getDaysUntil()
    val isUrgent = daysUntil in 0L..3L
    val isOverdue = daysUntil < 0L
    val isActive = subscription.isActive

    var isSwiped by remember { mutableStateOf(false) }
    var swipeOffset by remember { mutableStateOf(0f) }

    val draggableState = rememberDraggableState { delta ->
        if (onDelete != null) {
            swipeOffset += delta
            swipeOffset = swipeOffset.coerceIn(-150f, 150f)
            isSwiped = swipeOffset < -50f
        }
    }

    val statusIcon =
            when {
                !isActive -> Icons.Default.PauseCircle
                isOverdue -> Icons.Default.Error
                isUrgent -> Icons.Default.Warning
                else -> Icons.Default.CheckCircle
            }

    val statusColor =
            when {
                !isActive -> MaterialTheme.colorScheme.onSurfaceVariant
                isOverdue -> ErrorColor
                isUrgent -> WarningColor
                else -> SuccessColor
            }

    val daysText =
            when {
                !isActive -> stringResource(R.string.inactive)
                isOverdue -> stringResource(R.string.overdue)
                daysUntil == 0L -> stringResource(R.string.due_today)
                daysUntil == 1L -> stringResource(R.string.due_tomorrow)
                else -> stringResource(R.string.due_in_days, daysUntil.toInt())
            }

    val daysColor =
            when {
                !isActive -> MaterialTheme.colorScheme.onSurfaceVariant
                isOverdue -> ErrorColor
                isUrgent -> WarningColor
                else -> SuccessColor
            }

    val statusChipText =
            when {
                !isActive -> stringResource(R.string.inactive)
                isOverdue -> stringResource(R.string.overdue)
                isUrgent -> daysText
                else -> stringResource(R.string.active)
            }

    val statusGradient =
            when {
                !isActive -> StatusGradientInactive
                isOverdue -> StatusGradientOverdue
                isUrgent -> StatusGradientUrgent
                else -> StatusGradientActive
            }

    val cardGradient =
            when {
                !isActive ->
                        listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                        )
                isOverdue ->
                        listOf(
                                ErrorGradientStart.copy(alpha = 0.9f),
                                ErrorGradientEnd.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.surface
                        )
                isUrgent ->
                        listOf(
                                WarningGradientStart.copy(alpha = 0.9f),
                                WarningGradientEnd.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.surface
                        )
                else ->
                        listOf(
                                CardGradientStart,
                                CardGradientMiddle,
                                MaterialTheme.colorScheme.surface
                        )
            }

    val billingCycleLabel =
            when (subscription.billingCycle) {
                BillingCycle.DAILY -> stringResource(R.string.daily)
                BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                BillingCycle.YEARLY -> stringResource(R.string.yearly)
            }

    val cardShape = RoundedCornerShape(24.dp)

    Card(
            modifier =
                    modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .graphicsLayer { translationX = swipeOffset }
                            .shadow(elevation = 18.dp, shape = cardShape, clip = false)
                            .draggable(
                                    state = draggableState,
                                    orientation = Orientation.Horizontal,
                                    enabled = onDelete != null
                            )
                            .clip(cardShape)
                            .clickable { if (!isSwiped) onClick() }
                            .animateContentSize(),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation =
                    CardDefaults.cardElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                    )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                    modifier =
                            Modifier.matchParentSize()
                                    .clip(cardShape)
                                    .background(brush = Brush.linearGradient(cardGradient))
            )

            Box(
                    modifier =
                            Modifier.matchParentSize()
                                    .clip(cardShape)
                                    .border(
                                            BorderStroke(
                                                    width = 1.dp,
                                                    brush = Brush.linearGradient(
                                                            colors =
                                                                    listOf(
                                                                            Color.White.copy(alpha = 0.45f),
                                                                            Color.White.copy(alpha = 0.1f)
                                                                    )
                                                    )
                                            ),
                                            shape = cardShape
                                    )
            )

            Box(
                    modifier =
                            Modifier.size(160.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 52.dp, y = (-80).dp)
                                    .background(
                                            brush =
                                                    Brush.radialGradient(
                                                            colors =
                                                                    listOf(
                                                                            Color.White.copy(alpha = 0.3f),
                                                                            Color.Transparent
                                                                    )
                                                    ),
                                            shape = CircleShape
                                    )
            )

            Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = subscription.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val category = subscription.categoryId?.let { id -> viewModel.getCategoryById(id) }
                        if (category != null) {
                            CategoryTag(
                                    category = category,
                                    size = CategoryTagSize.Small,
                                    showIcon = false
                            )
                        }
                    }

                    StatusChip(
                            icon = statusIcon,
                            text = statusChipText,
                            gradient = statusGradient,
                            contentColor = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                        text = subscription.price.formatCurrency(selectedCurrency),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoRowItem(
                            icon = Icons.Default.Autorenew,
                            label = stringResource(R.string.billing_cycle),
                            value = billingCycleLabel,
                            modifier = Modifier.weight(1f)
                    )

                    InfoRowItem(
                            icon = Icons.Default.Event,
                            label = stringResource(R.string.next_billing_date),
                            value = subscription.nextBillingDate.formatDate(),
                            modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = daysColor,
                            modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            text = daysText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = daysColor
                    )
                }

                if (isActive && !isOverdue) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val progress =
                            when (subscription.billingCycle) {
                                BillingCycle.DAILY -> {
                                    val daysInCycle = 1
                                    val daysPassed = 1 - (daysUntil % daysInCycle).coerceAtLeast(0)
                                    daysPassed.toFloat() / daysInCycle
                                }
                                BillingCycle.WEEKLY -> {
                                    val daysInCycle = 7
                                    val daysPassed = 7 - (daysUntil % daysInCycle).coerceAtLeast(0)
                                    daysPassed.toFloat() / daysInCycle
                                }
                                BillingCycle.MONTHLY -> {
                                    val daysInCycle = 30
                                    val daysPassed = 30 - (daysUntil % daysInCycle).coerceAtLeast(0)
                                    daysPassed.toFloat() / daysInCycle
                                }
                                BillingCycle.YEARLY -> {
                                    val daysInCycle = 365
                                    val daysPassed = 365 - (daysUntil % daysInCycle).coerceAtLeast(0)
                                    daysPassed.toFloat() / daysInCycle
                                }
                            }

                    LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                            color = statusColor,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                }
            }

            if (onDelete != null && isSwiped) {
                Row(
                        modifier =
                                Modifier.fillMaxSize()
                                        .clip(cardShape)
                                        .background(
                                                brush =
                                                        Brush.horizontalGradient(
                                                                colors =
                                                                        listOf(
                                                                                ErrorColor.copy(alpha = 0.85f),
                                                                                ErrorColor.copy(alpha = 0.65f)
                                                                        )
                                                        )
                                        ),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                            onClick = {
                                onDelete()
                                swipeOffset = 0f
                                isSwiped = false
                            }
                    ) {
                        Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                        )
                    }

                    if (onEdit != null) {
                        IconButton(
                                onClick = {
                                    onEdit()
                                    swipeOffset = 0f
                                    isSwiped = false
                                }
                        ) {
                            Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
        icon: ImageVector,
        text: String,
        gradient: List<Color>,
        contentColor: Color,
        modifier: Modifier = Modifier
) {
    if (text.isBlank()) return

    Box(
            modifier =
                    modifier.clip(RoundedCornerShape(50))
                            .background(brush = Brush.linearGradient(gradient))
                            .border(
                                    BorderStroke(1.dp, contentColor.copy(alpha = 0.35f)),
                                    RoundedCornerShape(50)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
            )
            Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoRowItem(
        icon: ImageVector,
        label: String,
        value: String,
        valueColor: Color = MaterialTheme.colorScheme.onSurface,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                    modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SubscriptionListHeader(
        totalSubscriptions: Int,
        activeSubscriptions: Int,
        totalMonthlyCost: Double,
        selectedCurrency: String,
        modifier: Modifier = Modifier
) {
    Card(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                        text = "Total Subscriptions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                        text = "$activeSubscriptions of $totalSubscriptions active",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                        text = "Monthly Cost",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                        text = totalMonthlyCost.formatCurrency(selectedCurrency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
