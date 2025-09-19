package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.ui.model.CategoryFilter
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChip(
    filter: CategoryFilter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (filter.isSelected) {
        try {
            Color(android.graphics.Color.parseColor(filter.color))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val contentColor = if (filter.isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val borderColor = if (filter.isSelected) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.outline
    }

    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (filter.isSelected) FontWeight.Medium else FontWeight.Normal,
                color = contentColor
            )
        },
        selected = filter.isSelected,
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = backgroundColor,
            selectedLabelColor = contentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = filter.isSelected,
            borderColor = borderColor,
            selectedBorderColor = Color.Transparent,
            borderWidth = 1.dp
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterChipPreview() {
    SubscriptionManagementAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All Categories chip (selected)
            CategoryFilterChip(
                filter = CategoryFilter.ALL_CATEGORIES.copy(isSelected = true),
                onClick = { }
            )
            
            // Regular category chip (unselected)
            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 1L,
                    name = "Entertainment",
                    color = "#FF6B6B",
                    isSelected = false
                ),
                onClick = { }
            )
            
            // Regular category chip (selected)
            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 2L,
                    name = "Productivity",
                    color = "#4ECDC4",
                    isSelected = true
                ),
                onClick = { }
            )
            
            // Long name category
            CategoryFilterChip(
                filter = CategoryFilter(
                    id = 3L,
                    name = "Health & Fitness",
                    color = "#45B7D1",
                    isSelected = false
                ),
                onClick = { }
            )
        }
    }
}
