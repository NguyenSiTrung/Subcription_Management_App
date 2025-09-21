package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.navigation.Screen

private val subscriptionDetailBaseRoute = Screen.SubscriptionDetail.route.substringBefore("/")
private val addEditSubscriptionBaseRoute = Screen.AddEditSubscription.route.substringBefore("/")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    currentRoute: String?,
    showBackButton: Boolean = false,
    showActions: Boolean = true,
    onSearchClick: (() -> Unit)? = null,
    onAddClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val normalizedRoute = currentRoute?.substringBefore("/") ?: currentRoute

    val subtitle = when (normalizedRoute) {
        Screen.SubscriptionList.route -> stringResource(R.string.subscriptions_top_bar_subtitle)
        subscriptionDetailBaseRoute -> stringResource(R.string.subscription_detail_top_bar_subtitle)
        addEditSubscriptionBaseRoute -> stringResource(R.string.add_subscription_top_bar_subtitle)
        Screen.CategoryList.route -> stringResource(R.string.categories_top_bar_subtitle)
        Screen.Statistics.route -> stringResource(R.string.statistics_top_bar_subtitle)
        Screen.Settings.route -> stringResource(R.string.settings_top_bar_subtitle)
        else -> null
    }

    val gradientColors = remember(colorScheme.primary, colorScheme.secondary) {
        listOf(colorScheme.primary, colorScheme.secondary)
    }
    val topBarShape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = topBarShape, clip = false)
            .clip(topBarShape)
            .background(brush = Brush.horizontalGradient(colors = gradientColors))
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            style = typography.bodySmall,
                            color = colorScheme.onPrimary.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                if (showBackButton) {
                    TopBarIconButton(
                        onClick = { navController.popBackStack() },
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                if (showActions) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (normalizedRoute) {
                            Screen.SubscriptionList.route -> {
                                onSearchClick?.let { action ->
                                    TopBarIconButton(
                                        onClick = action,
                                        icon = Icons.Filled.Search,
                                        contentDescription = stringResource(R.string.search)
                                    )
                                }
                                onAddClick?.let { action ->
                                    TopBarIconButton(
                                        onClick = action,
                                        icon = Icons.Filled.Add,
                                        contentDescription = stringResource(R.string.add)
                                    )
                                }
                            }
                            Screen.CategoryList.route -> {
                                onAddClick?.let { action ->
                                    TopBarIconButton(
                                        onClick = action,
                                        icon = Icons.Filled.Add,
                                        contentDescription = stringResource(R.string.add)
                                    )
                                }
                            }
                        }

                        TopBarIconButton(
                            onClick = { navController.navigate(Screen.About.route) },
                            icon = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.about)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                titleContentColor = colorScheme.onPrimary,
                navigationIconContentColor = colorScheme.onPrimary,
                actionIconContentColor = colorScheme.onPrimary
            )
        )
    }
}


@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonSize = 44.dp

    Surface(
        modifier = Modifier.size(buttonSize),
        shape = CircleShape,
        color = colorScheme.onPrimary.copy(alpha = 0.12f),
        contentColor = colorScheme.onPrimary
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}
