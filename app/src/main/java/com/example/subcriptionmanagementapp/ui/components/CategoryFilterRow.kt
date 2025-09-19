package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme

@Composable
fun CategoryFilterRow(
    filters: List<CategoryFilter>,
    showActiveOnly: Boolean,
    onFilterClick: (CategoryFilter) -> Unit,
    onActiveFilterToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Filter header with active toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.filter_by_category),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Active only toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.active_only),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = showActiveOnly,
                    onCheckedChange = { onActiveFilterToggle() },
                    modifier = Modifier.size(width = 32.dp, height = 20.dp)
                )
            }
        }
        
        // Scrollable filter chips
        if (filters.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
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
        
        // Divider
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
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
                isSelected = false
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
            onActiveFilterToggle = { }
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
            )
        )
        
        CategoryFilterRow(
            filters = sampleFilters,
            showActiveOnly = true,
            onFilterClick = { },
            onActiveFilterToggle = { }
        )
    }
}
