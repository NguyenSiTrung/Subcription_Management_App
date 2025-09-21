package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterRow(
    filters: List<CategoryFilter>,
    showActiveOnly: Boolean,
    onFilterClick: (CategoryFilter) -> Unit,
    onActiveFilterToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onClearFilters: (() -> Unit)? = null,
    isExpanded: Boolean = true,
    onExpandToggle: (() -> Unit)? = null
) {
    val selectedCategories = filters.filter { it.isSelected && it.id != CategoryFilter.ALL_CATEGORIES.id }
    val hasActiveFilters = selectedCategories.isNotEmpty()

    val selectionSummary = when {
        selectedCategories.isEmpty() -> stringResource(R.string.filter_all_subscriptions)
        selectedCategories.size == 1 -> stringResource(
            R.string.filtered_by_category,
            selectedCategories.first().name
        )
        else -> {
            val joinedNames = selectedCategories.joinToString(", ") { it.name }
            stringResource(R.string.filtered_by_category, joinedNames)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp).padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.filter_by_category),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = selectionSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = showActiveOnly,
                        onClick = onActiveFilterToggle,
                        label = {
                            Text(
                                text = stringResource(R.string.filter_active_only),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    AnimatedVisibility(
                        visible = hasActiveFilters && onClearFilters != null,
                        enter = fadeIn() + expandIn(),
                        exit = fadeOut() + shrinkOut()
                    ) {
                        AssistChip(
                            onClick = { onClearFilters?.invoke() },
                            label = { Text(text = stringResource(R.string.clear_filter)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.error,
                                leadingIconContentColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }

                    if (onExpandToggle != null) {
                        IconButton(onClick = { onExpandToggle() }) {
                            val (icon, description) =
                                if (isExpanded) {
                                    Icons.Default.ExpandLess to stringResource(R.string.collapse_filters)
                                } else {
                                    Icons.Default.ExpandMore to stringResource(R.string.expand_filters)
                                }
                            Icon(
                                imageVector = icon,
                                contentDescription = description,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        filters.forEach { filter ->
                            val displayFilter = if (filter.id == CategoryFilter.ALL_CATEGORIES.id) {
                                filter.copy(name = stringResource(R.string.all_categories))
                            } else {
                                filter
                            }

                            CategoryFilterChip(
                                filter = displayFilter,
                                onClick = { onFilterClick(filter) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterRowPreview() {
    SubscriptionManagementAppTheme {
        val sampleFilters = listOf(
            CategoryFilter.ALL_CATEGORIES.copy(isSelected = true),
            CategoryFilter(
                id = 1L,
                name = "Entertainment",
                color = "#FF6B6B",
                isSelected = false
            ),
            CategoryFilter(
                id = 2L,
                name = "Productivity",
                color = "#4ECDC4",
                isSelected = true
            ),
            CategoryFilter(
                id = 3L,
                name = "Health & Fitness",
                color = "#45B7D1",
                isSelected = false
            ),
            CategoryFilter(
                id = 4L,
                name = "Education",
                color = "#96CEB4",
                isSelected = false
            ),
            CategoryFilter(
                id = 5L,
                name = "Business",
                color = "#FFEAA7",
                isSelected = false
            )
        )

        CategoryFilterRow(
            filters = sampleFilters,
            showActiveOnly = false,
            onFilterClick = { },
            onActiveFilterToggle = { },
            onClearFilters = { },
            onExpandToggle = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterRowActivePreview() {
    SubscriptionManagementAppTheme {
        val sampleFilters = listOf(
            CategoryFilter.ALL_CATEGORIES.copy(isSelected = false),
            CategoryFilter(
                id = 1L,
                name = "Entertainment",
                color = "#FF6B6B",
                isSelected = true
            ),
            CategoryFilter(
                id = 2L,
                name = "Productivity",
                color = "#4ECDC4",
                isSelected = false
            ),
            CategoryFilter(
                id = 3L,
                name = "Health & Fitness",
                color = "#45B7D1",
                isSelected = true
            )
        )

        CategoryFilterRow(
            filters = sampleFilters,
            showActiveOnly = true,
            onFilterClick = { },
            onActiveFilterToggle = { },
            onClearFilters = { },
            onExpandToggle = { }
        )
    }
}
