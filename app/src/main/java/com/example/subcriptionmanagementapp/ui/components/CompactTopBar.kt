package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTopBar(
        title: String,
        navController: NavController,
        showBackButton: Boolean = false,
        actions: @Composable RowScope.() -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val gradientColors =
            remember(colorScheme.primary, colorScheme.secondary) {
                listOf(colorScheme.primary, colorScheme.secondary)
            }
    val topBarShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)

    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .shadow(elevation = 4.dp, shape = topBarShape, clip = false)
                            .clip(topBarShape)
                            .background(brush = Brush.horizontalGradient(colors = gradientColors))
    ) {
        CenterAlignedTopAppBar(
                title = {
                    Text(
                            text = title,
                            style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        CompactTopBarButton(
                                onClick = { navController.popBackStack() },
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        actions()

                        CompactTopBarButton(
                                onClick = { navController.navigate(Screen.About.route) },
                                icon = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.about)
                        )
                    }
                },
                colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
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
private fun CompactTopBarButton(
        onClick: () -> Unit,
        icon: ImageVector,
        contentDescription: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonSize = 36.dp

    Surface(
            modifier = Modifier.size(buttonSize),
            shape = RoundedCornerShape(8.dp),
            color = colorScheme.onPrimary.copy(alpha = 0.15f),
            contentColor = colorScheme.onPrimary
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp)
            )
        }
    }
}
