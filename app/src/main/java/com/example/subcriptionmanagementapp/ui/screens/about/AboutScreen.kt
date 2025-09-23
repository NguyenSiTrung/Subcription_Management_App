package com.example.subcriptionmanagementapp.ui.screens.about

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.BuildConfig
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.CompactTopBar

@Composable
fun AboutScreen(navController: NavController) {
    val metrics = remember {
        listOf(
                AboutMetric(
                        valueRes = R.string.about_metric_active_reminders_value,
                        labelRes = R.string.about_metric_active_reminders_label,
                        captionRes = R.string.about_metric_active_reminders_caption
                ),
                AboutMetric(
                        valueRes = R.string.about_metric_monthly_savings_value,
                        labelRes = R.string.about_metric_monthly_savings_label,
                        captionRes = R.string.about_metric_monthly_savings_caption
                ),
                AboutMetric(
                        valueRes = R.string.about_metric_categories_value,
                        labelRes = R.string.about_metric_categories_label,
                        captionRes = R.string.about_metric_categories_caption
                )
        )
    }
    val milestones = remember {
        listOf(
                AboutMilestone(
                        periodRes = R.string.about_milestone_launch_period,
                        titleRes = R.string.about_milestone_launch_title,
                        summaryRes = R.string.about_milestone_launch_summary
                ),
                AboutMilestone(
                        periodRes = R.string.about_milestone_reminders_period,
                        titleRes = R.string.about_milestone_reminders_title,
                        summaryRes = R.string.about_milestone_reminders_summary
                ),
                AboutMilestone(
                        periodRes = R.string.about_milestone_calendar_period,
                        titleRes = R.string.about_milestone_calendar_title,
                        summaryRes = R.string.about_milestone_calendar_summary
                )
        )
    }

    val principles = remember {
        listOf(
                ProductPrinciple(
                        titleRes = R.string.about_principle_trust_title,
                        summaryRes = R.string.about_principle_trust_summary,
                        icon = Icons.Filled.Lock
                ),
                ProductPrinciple(
                        titleRes = R.string.about_principle_clarity_title,
                        summaryRes = R.string.about_principle_clarity_summary,
                        icon = Icons.Filled.Info
                ),
                ProductPrinciple(
                        titleRes = R.string.about_principle_habits_title,
                        summaryRes = R.string.about_principle_habits_summary,
                        icon = Icons.Filled.Favorite
                )
        )
    }
    val trustItems = remember {
        listOf(
                TrustPillar(
                        icon = Icons.Filled.Lock,
                        descriptionRes = R.string.about_trust_encryption
                ),
                TrustPillar(
                        icon = Icons.Filled.CalendarToday,
                        descriptionRes = R.string.about_trust_privacy
                ),
                TrustPillar(
                        icon = Icons.Filled.HeadsetMic,
                        descriptionRes = R.string.about_trust_support
                )
        )
    }
    val supportActions = remember {
        listOf(
                SupportAction(
                        icon = Icons.Filled.Email,
                        titleRes = R.string.about_support_contact_title,
                        descriptionRes = R.string.about_support_contact_summary,
                        onClick = { /* TODO: Wire up mail intent */}
                ),
                SupportAction(
                        icon = Icons.Filled.Lock,
                        titleRes = R.string.about_support_privacy_title,
                        descriptionRes = R.string.about_support_privacy_summary,
                        onClick = { /* TODO: Navigate to privacy center */}
                ),
                SupportAction(
                        icon = Icons.Filled.Help,
                        titleRes = R.string.about_support_community_title,
                        descriptionRes = R.string.about_support_community_summary,
                        onClick = { /* TODO: Open community hub */}
                )
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CompactTopBar(
                title = stringResource(R.string.about),
                navController = navController
        )

        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                        PaddingValues(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            item { HeroSection(versionName = BuildConfig.VERSION_NAME) }
            item { MetricsSection(metrics = metrics) }
            item { MilestonesSection(milestones = milestones) }
            item { PrinciplesSection(principles = principles) }
            item { TrustSection(pillars = trustItems) }
            item { SupportSection(actions = supportActions) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HeroSection(versionName: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val gradient =
            remember(colorScheme.primaryContainer, colorScheme.secondaryContainer) {
                Brush.linearGradient(
                        colors =
                                listOf(
                                        colorScheme.primaryContainer.copy(alpha = 0.95f),
                                        colorScheme.secondaryContainer.copy(alpha = 0.9f)
                                )
                )
            }

    Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.Transparent
    ) {
        Column(
                modifier =
                        Modifier.background(gradient, shape = MaterialTheme.shapes.extraLarge)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .padding(horizontal = 28.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                    text = stringResource(R.string.app_name),
                    style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onPrimaryContainer
            )

            Text(
                    text = stringResource(R.string.about_tagline),
                    style = typography.titleMedium,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )

            Text(
                    text = stringResource(R.string.about_hero_description),
                    style = typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            Text(
                    text = stringResource(R.string.about_hero_version, versionName),
                    style = typography.labelLarge,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeroActionChip(
                        icon = Icons.Filled.Star,
                        label = stringResource(R.string.about_hero_cta_rate)
                )
                HeroActionChip(
                        icon = Icons.Filled.Email,
                        label = stringResource(R.string.about_hero_cta_share)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeroActionChip(icon: ImageVector, label: String) {
    val colorScheme = MaterialTheme.colorScheme
    AssistChip(
            onClick = { /* TODO: Implement hero action */},
            label = { Text(text = label) },
            leadingIcon = {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorScheme.onPrimaryContainer
                )
            },
            colors =
                    AssistChipDefaults.assistChipColors(
                            containerColor = colorScheme.surface.copy(alpha = 0.18f),
                            labelColor = colorScheme.onPrimaryContainer
                    )
    )
}

@Composable
private fun MetricsSection(metrics: List<AboutMetric>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = stringResource(R.string.about_metrics_title))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(metrics) { metric -> MetricCard(metric = metric) }
        }
    }
}

@Composable
private fun MetricCard(metric: AboutMetric) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
            modifier = Modifier.width(220.dp).height(160.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            shadowElevation = 0.dp,
            color = colorScheme.surfaceVariant.copy(alpha = 0.65f)
    ) {
        Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = stringResource(metric.valueRes),
                    style = typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.primary
            )
            Text(
                    text = stringResource(metric.labelRes),
                    style = typography.titleMedium,
                    color = colorScheme.onSurface
            )
            Text(
                    text = stringResource(metric.captionRes),
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MilestonesSection(milestones: List<AboutMilestone>) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionHeader(title = stringResource(R.string.about_milestones_title))
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            milestones.forEachIndexed { index, milestone ->
                TimelineItem(milestone = milestone, isLast = index == milestones.lastIndex)
            }
        }
    }
}

@Composable
private fun TimelineItem(milestone: AboutMilestone, isLast: Boolean) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(colorScheme.primary))
            if (!isLast) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                        modifier =
                                Modifier.width(2.dp)
                                        .height(48.dp)
                                        .background(colorScheme.outlineVariant)
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                    text = stringResource(milestone.periodRes),
                    style = typography.labelLarge,
                    color = colorScheme.primary
            )
            Text(
                    text = stringResource(milestone.titleRes),
                    style = typography.titleMedium,
                    color = colorScheme.onSurface
            )
            Text(
                    text = stringResource(milestone.summaryRes),
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PrinciplesSection(principles: List<ProductPrinciple>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = stringResource(R.string.about_principles_title))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            principles.forEach { principle -> PrincipleCard(principle = principle) }
        }
    }
}

@Composable
private fun PrincipleCard(principle: ProductPrinciple) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                            .background(colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
                modifier = Modifier.height(44.dp).width(44.dp),
                shape = MaterialTheme.shapes.medium,
                color = colorScheme.primary.copy(alpha = 0.18f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                        imageVector = principle.icon,
                        contentDescription = null,
                        tint = colorScheme.primary
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                    text = stringResource(principle.titleRes),
                    style = typography.titleSmall,
                    color = colorScheme.onSurface
            )
            Text(
                    text = stringResource(principle.summaryRes),
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrustSection(pillars: List<TrustPillar>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = stringResource(R.string.about_trust_title))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            pillars.forEach { pillar -> TrustRow(pillar = pillar) }
        }
    }
}

@Composable
private fun TrustRow(pillar: TrustPillar) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                            .background(colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = pillar.icon, contentDescription = null, tint = colorScheme.primary)
        Text(
                text = stringResource(pillar.descriptionRes),
                style = typography.bodyMedium,
                color = colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupportSection(actions: List<SupportAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = stringResource(R.string.about_support_title))
        FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { actions.forEach { action -> SupportCard(action = action) } }
    }
}

@Composable
private fun SupportCard(action: SupportAction) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    ElevatedCard(
            onClick = action.onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surface)
    ) {
        Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = action.icon, contentDescription = null, tint = colorScheme.primary)
            Text(
                    text = stringResource(action.titleRes),
                    style = typography.titleMedium,
                    color = colorScheme.onSurface
            )
            Text(
                    text = stringResource(action.descriptionRes),
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
    )
}

private data class AboutMetric(
        @StringRes val valueRes: Int,
        @StringRes val labelRes: Int,
        @StringRes val captionRes: Int
)

private data class AboutMilestone(
        @StringRes val periodRes: Int,
        @StringRes val titleRes: Int,
        @StringRes val summaryRes: Int
)

private data class ProductPrinciple(
        @StringRes val titleRes: Int,
        @StringRes val summaryRes: Int,
        val icon: ImageVector
)

private data class TrustPillar(val icon: ImageVector, @StringRes val descriptionRes: Int)

private data class SupportAction(
        val icon: ImageVector,
        @StringRes val titleRes: Int,
        @StringRes val descriptionRes: Int,
        val onClick: () -> Unit
)
