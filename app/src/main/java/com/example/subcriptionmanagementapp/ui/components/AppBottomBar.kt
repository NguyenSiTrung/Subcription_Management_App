package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.*

@Composable
fun AppBottomBar(navController: NavController, currentRoute: String?) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val isDarkTheme = isSystemInDarkTheme()

    // Adaptive height based on screen size
    val bottomBarHeight =
            with(density) {
                when {
                    configuration.screenHeightDp < 600 -> 64.dp // Compact screens
                    configuration.screenHeightDp < 800 -> 68.dp // Medium screens
                    else -> 72.dp // Large screens
                }
            }

    val navigationBarsPadding =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imePadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val bottomPadding = navigationBarsPadding.coerceAtLeast(imePadding)

    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .padding(bottom = bottomPadding),
            contentAlignment = Alignment.BottomCenter
    ) {
        OptimizedBottomBar(height = bottomBarHeight, isDarkTheme = isDarkTheme) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                OptimizedBottomBarItem(
                        item = item,
                        isSelected = isSelected,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
private fun OptimizedBottomBar(
        height: androidx.compose.ui.unit.Dp,
        isDarkTheme: Boolean,
        modifier: Modifier = Modifier,
        content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    // Adaptive colors based on theme
    val surfaceColor =
            if (isDarkTheme) {
                Color(0xEE1C1C1E) // Dark surface with high opacity
            } else {
                Color(0xF5FFFFFF) // Light surface with high opacity
            }

    val borderColor =
            if (isDarkTheme) {
                Color(0x40FFFFFF) // Light border on dark
            } else {
                Color(0x20000000) // Dark border on light
            }

    val shadowColor =
            if (isDarkTheme) {
                Color(0x60000000) // Darker shadow
            } else {
                Color(0x30000000) // Lighter shadow
            }

    Box(modifier = modifier.fillMaxWidth().height(height)) {
        // Single optimized shadow
        Surface(
                modifier = Modifier.fillMaxWidth().height(height).clip(shape).blur(radius = 12.dp),
                color = shadowColor,
                shape = shape
        ) {}

        // Main surface with simplified glassmorphism
        Surface(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(height)
                                .clip(shape)
                                .border(width = 0.5.dp, color = borderColor, shape = shape),
                color = surfaceColor,
                tonalElevation = if (isDarkTheme) 3.dp else 1.dp,
                shadowElevation = 8.dp,
                shape = shape
        ) {
            // Subtle gradient overlay
            val gradientOverlay =
                    if (isDarkTheme) {
                        Brush.verticalGradient(
                                colors =
                                        listOf(
                                                Color(0x10FFFFFF),
                                                Color.Transparent,
                                                Color(0x05000000)
                                        )
                        )
                    } else {
                        Brush.verticalGradient(
                                colors =
                                        listOf(
                                                Color(0x08000000),
                                                Color.Transparent,
                                                Color(0x03000000)
                                        )
                        )
                    }

            Box(modifier = Modifier.fillMaxWidth().background(gradientOverlay)) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        content = content
                )
            }
        }
    }
}

@Composable
private fun RowScope.OptimizedBottomBarItem(
        item: BottomNavItem,
        isSelected: Boolean,
        isDarkTheme: Boolean,
        onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    // Optimized animations with better performance
    val indicatorWidth by
            animateDpAsState(
                    targetValue = if (isSelected) 48.dp else 0.dp,
                    animationSpec =
                            spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                            ),
                    label = "IndicatorWidth"
            )

    val iconScale by
            animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1.0f,
                    animationSpec =
                            spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                            ),
                    label = "IconScale"
            )

    // Improved contrast colors for accessibility
    val iconColor by
            animateColorAsState(
                    targetValue =
                            if (isSelected) {
                                if (isDarkTheme) Color(0xFFFFFFFF)
                                else MaterialTheme.colorScheme.primary
                            } else {
                                if (isDarkTheme) Color(0xCCFFFFFF) else Color(0x99000000)
                            },
                    animationSpec =
                            spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                            ),
                    label = "IconColor"
            )

    val textColor by
            animateColorAsState(
                    targetValue =
                            if (isSelected) {
                                if (isDarkTheme) Color(0xFFFFFFFF)
                                else MaterialTheme.colorScheme.primary
                            } else {
                                if (isDarkTheme) Color(0xB3FFFFFF) else Color(0x80000000)
                            },
                    animationSpec =
                            spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                            ),
                    label = "TextColor"
            )

    val indicatorColor =
            if (isDarkTheme) {
                Color(0x40FFFFFF)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            }

    // Optimized container
    Box(
        modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Selection indicator that surrounds both icon and text when selected
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = if (isSelected) indicatorColor else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Content column (icon + text)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon with badge
                BadgedBox(
                    badge = {
                        // Show notification badge for subscriptions
                        if (item.screen == Screen.SubscriptionList) {
                            Badge(
                                modifier = Modifier,
                                containerColor =
                                    if (isDarkTheme) Color(0xFFFF453A)
                                    else Color(0xFFEF4444),
                                contentColor = Color.White
                            ) { 
                                Text(text = "3", fontSize = 10.sp, fontWeight = FontWeight.Medium) 
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = stringResource(id = item.labelResId),
                        modifier = Modifier
                            .size(24.dp)
                            .scale(iconScale),
                        tint = iconColor
                    )
                }

                // Label text with better readability
                if (isSelected) {
                    Text(
                        text = stringResource(id = item.labelResId),
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 4.dp)  // Consistent padding between icon and text
                            .graphicsLayer {
                                shadowElevation = if (isDarkTheme) 2.dp.toPx() else 0f
                            }
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
        val screen: Screen,
        val icon: ImageVector,
        val selectedIcon: ImageVector,
        val labelResId: Int
)

val bottomNavItems =
        listOf(
                BottomNavItem(
                        screen = Screen.SubscriptionList,
                        icon = Icons.Outlined.Subscriptions,
                        selectedIcon = Icons.Rounded.Subscriptions,
                        labelResId = R.string.subscriptions
                ),
                BottomNavItem(
                        screen = Screen.CategoryList,
                        icon = Icons.Outlined.Category,
                        selectedIcon = Icons.Rounded.Category,
                        labelResId = R.string.categories
                ),
                BottomNavItem(
                        screen = Screen.Statistics,
                        icon = Icons.Outlined.Analytics,
                        selectedIcon = Icons.Rounded.Analytics,
                        labelResId = R.string.statistics
                ),
                BottomNavItem(
                        screen = Screen.Settings,
                        icon = Icons.Outlined.Settings,
                        selectedIcon = Icons.Rounded.Settings,
                        labelResId = R.string.settings
                )
        )
