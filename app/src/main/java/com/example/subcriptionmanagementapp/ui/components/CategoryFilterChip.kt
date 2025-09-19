package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChip(
    filter: CategoryFilter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null
) {
    val categoryColor = runCatching {
        Color(android.graphics.Color.parseColor(filter.color))
    }.getOrElse { MaterialTheme.colorScheme.primary }

    val trailingBadge: (@Composable () -> Unit)? = count?.takeIf { it > 0 }?.let { total ->
        @Composable {
            Surface(
                shape = CircleShape,
                color = if (filter.isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                },
                tonalElevation = 0.dp,
                modifier = Modifier.sizeIn(minWidth = 22.dp, minHeight = 22.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                    Text(
                        text = total.coerceAtMost(99).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (filter.isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }

    FilterChip(
        modifier = modifier
            .semantics { role = Role.Button },
        selected = filter.isSelected,
        onClick = onClick,
        label = {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (filter.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                if (filter.isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        },
        trailingIcon = trailingBadge,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterChipPreview() {
    SubscriptionManagementAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryFilterChip(
                filter = CategoryFilter.ALL_CATEGORIES.copy(isSelected = true),
                onClick = { },
                count = 25
            )

            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 1L,
                    name = "Entertainment",
                    color = "#FF6B6B",
                    isSelected = false
                ),
                onClick = { },
                count = 8
            )

            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 2L,
                    name = "Productivity",
                    color = "#4ECDC4",
                    isSelected = true
                ),
                onClick = { },
                count = 3
            )

            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 3L,
                    name = "Health & Fitness",
                    color = "#45B7D1",
                    isSelected = false
                ),
                onClick = { },
                count = 12
            )

            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 4L,
                    name = "Education",
                    color = "#96CEB4",
                    isSelected = false
                ),
                onClick = { }
            )
        }
    }
}
