package com.example.subcriptionmanagementapp.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.components.AppTopBar

@Composable
fun AboutScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AppTopBar(
                title = stringResource(R.string.about),
                navController = navController,
                currentRoute = "about",
                showBackButton = true,
                showActions = false
        )

        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Icon
            Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
            )

            // App Name and Version
            Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )

            Text(
                    text = "${stringResource(R.string.about_app_version)} 1.0",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Description
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
            ) {
                Text(
                        text = stringResource(R.string.about_description),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Developer Info
            AboutItem(
                    icon = Icons.Default.Code,
                    title = stringResource(R.string.about_developer),
                    subtitle = "Subscription Management App Team"
            )

            // Contact
            AboutItem(
                    icon = Icons.Default.Email,
                    title = stringResource(R.string.about_contact),
                    subtitle = "support@subscriptionapp.com"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Action Buttons
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionButton(
                        icon = Icons.Default.Star,
                        text = stringResource(R.string.about_rate_app),
                        onClick = { /* Handle rate app */}
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Thank you message
            Text(
                    text = stringResource(R.string.about_thank_you),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AboutItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(horizontal = 16.dp))
        Column {
            Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
            )
            Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
            )
            Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
            )
        }
    }
}
