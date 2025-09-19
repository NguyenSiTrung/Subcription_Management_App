package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.util.CategoryUtils

/**
 * Enum class for different CategoryTag sizes
 */
enum class CategoryTagSize {
    Small,
    Medium,
    Large
}

/**
 * A reusable category tag component that displays category information in an attractive tag style
 */
@Composable
fun CategoryTag(
    category: Category?,
    isUncategorized: Boolean = false,
    size: CategoryTagSize = CategoryTagSize.Medium,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    maxWidth: Dp? = null,
    onClick: (() -> Unit)? = null
) {
    val isUncategorizedState = CategoryUtils.shouldShowAsUncategorized(category, isUncategorized)
    val displayName = CategoryUtils.getCategoryDisplayName(category, isUncategorizedState)
    val backgroundColor = if (isUncategorizedState) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        CategoryUtils.getCategoryBackgroundColor(category, alpha = 0.15f)
    }
    
    val borderColor = if (isUncategorizedState) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    } else {
        CategoryUtils.getCategoryColor(category).copy(alpha = 0.3f)
    }
    
    val textColor = if (isUncategorizedState) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        CategoryUtils.getCategoryColor(category)
    }

    val (horizontalPadding, verticalPadding, iconSize, textStyle, cornerRadius) = when (size) {
        CategoryTagSize.Small -> {
            Tuple5(8.dp, 4.dp, 14.dp, MaterialTheme.typography.labelSmall, 12.dp)
        }
        CategoryTagSize.Medium -> {
            Tuple5(12.dp, 6.dp, 16.dp, MaterialTheme.typography.labelMedium, 16.dp)
        }
        CategoryTagSize.Large -> {
            Tuple5(16.dp, 8.dp, 20.dp, MaterialTheme.typography.labelLarge, 20.dp)
        }
    }

    val tagModifier = modifier
        .let { if (maxWidth != null) it.widthIn(max = maxWidth) else it }
        .clip(RoundedCornerShape(cornerRadius))
        .background(backgroundColor)
        .then(
            if (onClick != null) {
                Modifier.clickable { onClick() }
            } else {
                Modifier
            }
        )
        .padding(horizontal = horizontalPadding, vertical = verticalPadding)

    Row(
        modifier = tagModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showIcon) {
            val iconVector = if (isUncategorizedState) {
                Icons.Default.Category
            } else {
                // You can extend this to support custom icons from category.icon
                Icons.Default.Category
            }
            
            Icon(
                imageVector = iconVector,
                contentDescription = "Category: $displayName",
                modifier = Modifier.size(iconSize),
                tint = textColor.copy(alpha = 0.8f)
            )
        }

        Text(
            text = displayName,
            style = textStyle,
            color = textColor,
            fontWeight = if (isUncategorizedState) FontWeight.Normal else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

/**
 * A clickable category tag variant
 */
@Composable
fun ClickableCategoryTag(
    category: Category?,
    isUncategorized: Boolean = false,
    size: CategoryTagSize = CategoryTagSize.Medium,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    maxWidth: Dp? = null,
    onClick: () -> Unit
) {
    CategoryTag(
        category = category,
        isUncategorized = isUncategorized,
        size = size,
        modifier = modifier,
        showIcon = showIcon,
        maxWidth = maxWidth,
        onClick = onClick
    )
}

/**
 * A category tag with border variant for more emphasis
 */
@Composable
fun BorderedCategoryTag(
    category: Category?,
    isUncategorized: Boolean = false,
    size: CategoryTagSize = CategoryTagSize.Medium,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    maxWidth: Dp? = null,
    onClick: (() -> Unit)? = null
) {
    val isUncategorizedState = CategoryUtils.shouldShowAsUncategorized(category, isUncategorized)
    val displayName = CategoryUtils.getCategoryDisplayName(category, isUncategorizedState)
    
    val backgroundColor = if (isUncategorizedState) {
        MaterialTheme.colorScheme.surface
    } else {
        CategoryUtils.getCategoryBackgroundColor(category, alpha = 0.08f)
    }
    
    val borderColor = if (isUncategorizedState) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    } else {
        CategoryUtils.getCategoryColor(category).copy(alpha = 0.4f)
    }
    
    val textColor = if (isUncategorizedState) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        CategoryUtils.getCategoryColor(category)
    }

    val (horizontalPadding, verticalPadding, iconSize, textStyle, cornerRadius) = when (size) {
        CategoryTagSize.Small -> {
            Tuple5(8.dp, 4.dp, 14.dp, MaterialTheme.typography.labelSmall, 12.dp)
        }
        CategoryTagSize.Medium -> {
            Tuple5(12.dp, 6.dp, 16.dp, MaterialTheme.typography.labelMedium, 16.dp)
        }
        CategoryTagSize.Large -> {
            Tuple5(16.dp, 8.dp, 20.dp, MaterialTheme.typography.labelLarge, 20.dp)
        }
    }

    Surface(
        modifier = modifier
            .let { if (maxWidth != null) it.widthIn(max = maxWidth) else it }
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showIcon) {
                val iconVector = if (isUncategorizedState) {
                    Icons.Default.Category
                } else {
                    Icons.Default.Category
                }
                
                Icon(
                    imageVector = iconVector,
                    contentDescription = "Category: $displayName",
                    modifier = Modifier.size(iconSize),
                    tint = textColor.copy(alpha = 0.8f)
                )
            }

            Text(
                text = displayName,
                style = textStyle,
                color = textColor,
                fontWeight = if (isUncategorizedState) FontWeight.Normal else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

/**
 * Helper data class for tuple of 5 elements
 */
private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
