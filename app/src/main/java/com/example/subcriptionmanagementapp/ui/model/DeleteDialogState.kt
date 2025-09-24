package com.example.subcriptionmanagementapp.ui.model

import com.example.subcriptionmanagementapp.data.local.entity.Subscription

/**
 * Sealed class representing the state of the delete confirmation dialog
 */
sealed class DeleteDialogState {
    object Hidden : DeleteDialogState()
    data class Visible(val subscription: Subscription) : DeleteDialogState()
    data class Deleting(val subscription: Subscription) : DeleteDialogState()
}