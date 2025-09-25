package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val selectedCategories =
            filters.filter { it.isSelected && it.id != CategoryFilter.ALL_CATEGORIES.id }
    val hasActiveFilters = selectedCategories.isNotEmpty()

    val selectionSummary =
            when {
                selectedCategories.isEmpty() -> stringResource(R.string.filter_all_subscriptions)
                selectedCategories.size == 1 ->
                        stringResource(
                                R.string.filtered_by_category,
                                selectedCategories.first().name
                        )
                else -> {
                    val joinedNames = selectedCategories.joinToString(", ") { it.name }
                    stringResource(R.string.filtered_by_category, joinedNames)
                }
            }

    Card(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(enabled = onExpandToggle != null) {
                        onExpandToggle?.invoke()
                    },
            shape = RoundedCornerShape(if (isExpanded) 24.dp else 16.dp),
            colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isExpanded) 2.dp else 1.dp,
                    pressedElevation = if (isExpanded) 8.dp else 4.dp
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(if (isExpanded) 20.dp else 16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = if (isExpanded) 0.1f else 0.15f
                                    )
                            )
                    ) {
                        Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier =
                                        Modifier.size(if (isExpanded) 32.dp else 28.dp)
                                                .padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(if (isExpanded) 12.dp else 10.dp))
                    Column {
                        Text(
                                text = stringResource(R.string.filter_by_category),
                                style =
                                        if (isExpanded) MaterialTheme.typography.titleSmall
                                        else MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isExpanded || hasActiveFilters || showActiveOnly) {
                            Text(
                                    text =
                                            if (isExpanded) selectionSummary
                                            else {
                                                when {
                                                    selectedCategories.size > 1 ->
                                                            "${selectedCategories.size} categories"
                                                    selectedCategories.size == 1 ->
                                                            selectedCategories.first().name
                                                    showActiveOnly -> "Active only"
                                                    else -> "All subscriptions"
                                                }
                                            },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(if (isExpanded) 12.dp else 8.dp))

                FlowRow(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        horizontalArrangement =
                                Arrangement.spacedBy(if (isExpanded) 8.dp else 6.dp),
                        verticalArrangement = Arrangement.Center
                ) {
                    // Show Active filter chip only when expanded or when it's selected
                    AnimatedVisibility(
                            visible = isExpanded || showActiveOnly,
                            enter = fadeIn() + expandIn(),
                            exit = fadeOut() + shrinkOut()
                    ) {
                        FilterChip(
                                selected = showActiveOnly,
                                onClick = onActiveFilterToggle,
                                label = {
                                    Text(
                                            text =
                                                    if (isExpanded)
                                                            stringResource(
                                                                    R.string.filter_active_only
                                                            )
                                                    else "Active",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon =
                                        if (isExpanded) {
                                            {
                                                Icon(
                                                        imageVector = Icons.Default.PlayCircle,
                                                        contentDescription = null
                                                )
                                            }
                                        } else null,
                                colors =
                                        FilterChipDefaults.filterChipColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                                .copy(alpha = 0.4f),
                                                labelColor =
                                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                                iconColor =
                                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                                selectedContainerColor =
                                                        MaterialTheme.colorScheme.primaryContainer,
                                                selectedLabelColor =
                                                        MaterialTheme.colorScheme
                                                                .onPrimaryContainer,
                                                selectedLeadingIconColor =
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                        )
                    }

                    // Show clear button only when expanded or when filters are active
                    AnimatedVisibility(
                            visible =
                                    hasActiveFilters &&
                                            onClearFilters != null &&
                                            (isExpanded || (hasActiveFilters || showActiveOnly)),
                            enter = fadeIn() + expandIn(),
                            exit = fadeOut() + shrinkOut()
                    ) {
                        AssistChip(
                                onClick = { onClearFilters?.invoke() },
                                label = {
                                    Text(
                                            text =
                                                    if (isExpanded)
                                                            stringResource(R.string.clear_filter)
                                                    else "Clear"
                                    )
                                },
                                leadingIcon =
                                        if (isExpanded) {
                                            {
                                                Icon(
                                                        imageVector = Icons.Default.Clear,
                                                        contentDescription = null
                                                )
                                            }
                                        } else null,
                                colors =
                                        AssistChipDefaults.assistChipColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.errorContainer
                                                                .copy(alpha = 0.4f),
                                                labelColor = MaterialTheme.colorScheme.error,
                                                leadingIconContentColor =
                                                        MaterialTheme.colorScheme.error
                                        )
                        )
                    }

                    // Always show expand/collapse button but make it more prominent when collapsed
                    if (onExpandToggle != null) {
                        val buttonModifier =
                                if (isExpanded) {
                                    Modifier
                                } else {
                                    Modifier.size(40.dp)
                                }

                        Card(
                                modifier = buttonModifier,
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                        containerColor =
                                                if (isExpanded) MaterialTheme.colorScheme.surface
                                                else
                                                        MaterialTheme.colorScheme.secondaryContainer.copy(
                                                                alpha = 0.7f
                                                        )
                                )
                        ) {
                            val (icon, description) =
                                    if (isExpanded) {
                                        Icons.Default.ExpandLess to
                                                stringResource(R.string.collapse_filters)
                                    } else {
                                        Icons.Default.ExpandMore to
                                                stringResource(R.string.expand_filters)
                                    }
                            Icon(
                                    imageVector = icon,
                                    contentDescription = description,
                                    tint =
                                            if (isExpanded)
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier =
                                            if (isExpanded) Modifier.padding(8.dp)
                                            else Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        filters.forEach { filter ->
                            val displayFilter =
                                    if (filter.id == CategoryFilter.ALL_CATEGORIES.id) {
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

                    HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterRowPreview() {
    SubscriptionManagementAppTheme {
        val sampleFilters =
                listOf(
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
                onFilterClick = {},
                onActiveFilterToggle = {},
                onClearFilters = {},
                onExpandToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterRowActivePreview() {
    SubscriptionManagementAppTheme {
        val sampleFilters =
                listOf(
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
                onFilterClick = {},
                onActiveFilterToggle = {},
                onClearFilters = {},
                onExpandToggle = {}
        )
    }
}
