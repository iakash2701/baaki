package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.customerintelligence.CustomerInsight
import com.baaki.app.ui.customerintelligence.CustomerIntelligenceViewModel

@Composable
fun CustomerIntelligenceScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomerIntelligenceViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.errorMessage != null -> {
            Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            }
        }
        state.insights.isEmpty() -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Customer Intelligence Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add customers and record invoices to view payment patterns and insights.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "Customer Intelligence Hub", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Track customer payment history, outstanding ledgers, and reliability metrics.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                items(state.insights) { insight ->
                    CustomerInsightCard(insight = insight)
                }
            }
        }
    }
}

@Composable
fun CustomerInsightCard(insight: CustomerInsight) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = insight.customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Due: ${insight.outstandingPaise.paise().formatINR()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (insight.outstandingPaise > 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Text(text = "Phone: ${insight.customer.phone} ${if (!insight.customer.gstin.isNullOrBlank()) "• GSTIN: ${insight.customer.gstin}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Total Invoiced", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = insight.totalInvoicedPaise.paise().formatINR(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Total Paid", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = insight.totalPaidPaise.paise().formatINR(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }

            val delayText = if (insight.averagePaymentDelayDays != null) {
                val days = insight.averagePaymentDelayDays
                if (days <= 0) "Pays on time (Avg ${Math.abs(Math.round(days))} days early)"
                else "Average payment delay: ~${Math.round(days)} days"
            } else {
                "Insufficient payment history for timing analysis"
            }

            Text(
                text = delayText,
                style = MaterialTheme.typography.bodySmall,
                color = if (insight.averagePaymentDelayDays != null && insight.averagePaymentDelayDays > 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
