package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.model.SubscriptionListTab
import com.example.subcriptionmanagementapp.ui.theme.AccentBlue
import com.example.subcriptionmanagementapp.ui.theme.WarmCardColor
import com.example.subcriptionmanagementapp.ui.theme.WarmIconBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTabsAndFilter(
    selectedTab: SubscriptionListTab,
    upcomingCount: Int,
    totalCount: Int,
    onTabSelected: (SubscriptionListTab) -> Unit,
    categoryFilters: List<CategoryFilter>,
    showActiveOnly: Boolean,
    onFilterClick: (CategoryFilter) -> Unit,
    onActiveFilterToggle: () -> Unit,
    isFilterExpanded: Boolean,
    onFilterExpandToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Main row with tabs and compact filter
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tabs section
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactTabChip(
                    text = stringResource(R.string.upcoming_renewals),
                    count = upcomingCount,
                    isSelected = selectedTab == SubscriptionListTab.UPCOMING,
                    onClick = { onTabSelected(SubscriptionListTab.UPCOMING) }
                )
                CompactTabChip(
                    text = stringResource(R.string.subscription_tab_all),
                    count = totalCount,
                    isSelected = selectedTab == SubscriptionListTab.ALL,
                    onClick = { onTabSelected(SubscriptionListTab.ALL) }
                )
            }

            // Compact filter button
            CompactFilterButton(
                isExpanded = isFilterExpanded,
                hasActiveFilters = categoryFilters.any { it.isSelected && it.id != CategoryFilter.ALL_CATEGORIES.id } || showActiveOnly,
                onExpandToggle = onFilterExpandToggle
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expanded filter section
        AnimatedVisibility(
            visible = isFilterExpanded && selectedTab == SubscriptionListTab.ALL,
            enter = fadeIn() + expandIn(),
            exit = fadeOut() + shrinkOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Active filter chip
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
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(categoryFilters) { filter ->
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
            }
        }
    }
}

@Composable
private fun CompactTabChip(
    text: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
            WarmCardColor
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                shape = CircleShape,
                color = if (isSelected) {
                    AccentBlue.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
                }
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        AccentBlue
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactFilterButton(
    isExpanded: Boolean,
    hasActiveFilters: Boolean,
    onExpandToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onExpandToggle),
        shape = CircleShape,
        color = if (hasActiveFilters) {
            AccentBlue.copy(alpha = 0.15f)
        } else {
            WarmIconBackgroundColor
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.FilterList,
                contentDescription = null,
                tint = if (hasActiveFilters) {
                    AccentBlue
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
