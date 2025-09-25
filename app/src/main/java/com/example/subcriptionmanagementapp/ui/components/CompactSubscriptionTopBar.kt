package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.theme.WarmIconBackgroundColor

@Composable
fun CompactSubscriptionTopBar(
    modifier: Modifier = Modifier,
    onSearchClick: (() -> Unit)? = null
) {
    val searchDescription = stringResource(R.string.search)
    val actions =
        onSearchClick?.let { action ->
            listOf(
                CompactTopBarAction(
                    icon = Icons.Filled.Search,
                    contentDescription = searchDescription,
                    onClick = action
                )
            )
        } ?: emptyList()

    CompactScreenTopBar(
        title = stringResource(R.string.subscriptions),
        modifier = modifier,
        actions = actions
    )
}

@Composable
fun CompactScreenTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: List<CompactTopBarAction> = emptyList()
) {
    val backgroundColor = MaterialTheme.colorScheme.background

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (actions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { action ->
                    CompactTopBarActionButton(action)
                }
            }
        }
    }
}

data class CompactTopBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val containerColor: Color = WarmIconBackgroundColor,
    val contentColor: Color? = null
)

@Composable
private fun RowScope.CompactTopBarActionButton(action: CompactTopBarAction) {
    val iconTint = action.contentColor ?: MaterialTheme.colorScheme.onBackground

    Surface(
        shape = CircleShape,
        color = action.containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        IconButton(onClick = action.onClick) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.contentDescription,
                tint = iconTint
            )
        }
    }
}
