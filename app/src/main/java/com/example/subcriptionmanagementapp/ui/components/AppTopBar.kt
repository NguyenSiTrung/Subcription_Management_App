package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
    var showMenu by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LaunchedEffect(showActions) {
        if (!showActions && showMenu) {
            showMenu = false
        }
    }

    LaunchedEffect(currentRoute) {
        showMenu = false
    }

    val normalizedRoute = currentRoute?.substringBefore("/") ?: currentRoute

    val subtitle = when (normalizedRoute) {
        Screen.Home.route -> stringResource(R.string.home_top_bar_subtitle)
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

                        Box {
                            TopBarIconButton(
                                onClick = { showMenu = !showMenu },
                                icon = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.more_options)
                            )

                            ModalDropdownLayer(
                                expanded = showMenu,
                                onDismiss = { showMenu = false }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(24.dp),
                                    color = colorScheme.surface,
                                    tonalElevation = 0.dp,
                                    shadowElevation = 18.dp,
                                    border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.08f))
                                ) {
                                    Column(
                                        modifier =
                                            Modifier
                                                .widthIn(min = 220.dp)
                                                .padding(vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.menu_quick_actions),
                                            style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 20.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OverflowMenuItem(
                                            title = stringResource(R.string.settings),
                                            subtitle = stringResource(R.string.settings_menu_subtitle),
                                            icon = Icons.Outlined.Settings,
                                            accentColor = colorScheme.primary
                                        ) {
                                            showMenu = false
                                            navController.navigate(Screen.Settings.route)
                                        }

                                        OverflowMenuItem(
                                            title = stringResource(R.string.about),
                                            subtitle = stringResource(R.string.about_menu_subtitle),
                                            icon = Icons.Outlined.Info,
                                            accentColor = colorScheme.secondary
                                        ) {
                                            showMenu = false
                                            // Navigate to about screen
                                        }
                                    }
                                }
                            }
                        }
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
private fun OverflowMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val containerShape = RoundedCornerShape(18.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = containerShape,
        tonalElevation = 3.dp,
        shadowElevation = 0.dp,
        color = colorScheme.surface,
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(containerShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier =
                    Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = colorScheme.outline
            )
        }
    }
}

@Composable
private fun ModalDropdownLayer(
    expanded: Boolean,
    onDismiss: () -> Unit,
    dropdownContent: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    if (expanded) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                focusable = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.scrim.copy(alpha = 0.32f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onDismiss
                    )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 80.dp, end = 16.dp)
                            .widthIn(max = 300.dp)
                    ) {
                        dropdownContent()
                    }
                }
            }
        }
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
