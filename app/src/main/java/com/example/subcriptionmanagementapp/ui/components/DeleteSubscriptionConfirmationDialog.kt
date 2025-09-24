package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.theme.AnimationDurations

/**
 * A performance-optimized confirmation dialog for subscription deletion.
 *
 * This dialog provides a clear warning to users before they delete a subscription,
 * explaining the permanent nature of the action and what data will be lost.
 * Features minimal animations for instant appearance, haptic feedback, and loading states.
 *
 * @param subscription The subscription to be deleted
 * @param onConfirm Callback invoked when user confirms deletion
 * @param onDismiss Callback invoked when user dismisses the dialog
 * @param isDeleting Whether the deletion is in progress (shows loading state)
 */
@Composable
fun DeleteSubscriptionConfirmationDialog(
    subscription: Subscription,
    onConfirm: (Subscription) -> Unit,
    onDismiss: () -> Unit,
    isDeleting: Boolean = false
) {
    val hapticFeedback = LocalHapticFeedback.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isDeleting,
            dismissOnClickOutside = !isDeleting,
            usePlatformDefaultWidth = false
        )
    ) {
        // Dialog content with optimized styling for faster appearance
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Icon - no animation for faster display
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )

                // Title - no animation for faster display
                Text(
                    text = stringResource(R.string.delete_subscription_confirmation_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Content - no animation for faster display
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            R.string.delete_subscription_confirmation_message,
                            subscription.name
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.delete_subscription_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                // Buttons - no animation for faster display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isDeleting,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Delete button with enhanced styling
                    FilledTonalButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onConfirm(subscription)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isDeleting,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onError,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.delete),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
