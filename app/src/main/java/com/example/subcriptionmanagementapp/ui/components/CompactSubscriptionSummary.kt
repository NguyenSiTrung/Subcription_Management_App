package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.theme.WarmCardColor
import com.example.subcriptionmanagementapp.util.formatCurrency

@Composable
fun CompactSubscriptionSummary(
    totalMonthlyCost: Double,
    activeSubscriptions: Int,
    subscriptionCount: Int,
    selectedCurrency: String,
    modifier: Modifier = Modifier
) {
    val containerColor = WarmCardColor
    val labelColor = Color(0xFF8C8374)
    val headlineColor = MaterialTheme.colorScheme.onBackground

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SummaryMetric(
                    title = stringResource(R.string.monthly_spending),
                    value = totalMonthlyCost.formatCurrency(selectedCurrency),
                    labelColor = labelColor,
                    valueColor = headlineColor
                )

                SummaryMetric(
                    title = stringResource(R.string.active_subscriptions),
                    value = "$activeSubscriptions / $subscriptionCount",
                    labelColor = labelColor,
                    valueColor = headlineColor,
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    title: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}
