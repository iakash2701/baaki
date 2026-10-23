package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.baaki.app.ui.dashboard.DashboardViewModel
import com.baaki.app.ui.dashboard.InvoiceWithCustomer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
    onInvoiceClick: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.errorMessage != null) {
        Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // A. Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Baaki • Sharma Packaging 🇮🇳",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your business, in control.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = { onNavigate("judges_showcase") }) {
                    Icon(Icons.Default.Star, contentDescription = "Judges Showcase", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // B. Financial Overview
        Text(
            text = "Financial Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Outstanding",
                amount = state.totalOutstandingPaise.paise().formatINR(),
                subtitle = "${state.invoiceCount} Invoices",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
            MetricCard(
                title = "Collections",
                amount = state.totalCollectionsPaise.paise().formatINR(),
                subtitle = "Confirmed received",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Overdue Receivables",
                amount = state.overdueBalancePaise.paise().formatINR(),
                subtitle = "${state.overdueCount} Overdue",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
            MetricCard(
                title = "Active Customers",
                amount = state.customerCount.toString(),
                subtitle = "Ledger accounts",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // C. Smart Recovery Spotlight
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate("smart_recovery") },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text(text = "Smart Recovery Queue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${state.overdueCount} invoices need urgent follow-up. Potential recovery: ${state.overdueBalancePaise.paise().formatINR()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                TextButton(onClick = { onNavigate("smart_recovery") }) {
                    Text("View Queue →")
                }
            }
        }

        // D. Quick Actions Grid
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        val shortcuts = listOf(
            Triple("Invoices", Icons.Default.ReceiptLong) { onNavigate("invoices") },
            Triple("Customers", Icons.Default.People) { onNavigate("customers") },
            Triple("Cash Flow", Icons.Default.AccountBalanceWallet) { onNavigate("cash_flow") },
            Triple("Intelligence", Icons.Default.Insights) { onNavigate("customer_intelligence") },
            Triple("Recovery", Icons.Default.TrendingUp) { onNavigate("smart_recovery") },
            Triple("Showcase", Icons.Default.Star) { onNavigate("judges_showcase") }
        )

        val rows = shortcuts.chunked(3)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (title, icon, action) ->
                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp),
                        onClick = action
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(text = title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // F. Cash Flow Snapshot
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate("cash_flow") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "Cash Flow Snapshot", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = "Confirmed collections: ${state.totalCollectionsPaise.paise().formatINR()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { onNavigate("cash_flow") }) {
                    Text("Details →")
                }
            }
        }

        // E. Recent Invoices Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Invoices (${state.invoiceCount} Total)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { onNavigate("invoices") }) {
                Text("View All")
            }
        }

        if (state.recentInvoices.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No invoices recorded yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            state.recentInvoices.forEach { item ->
                RecentInvoiceItemCard(item = item, onClick = { onInvoiceClick(item.invoice.id) })
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    amount: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = amount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RecentInvoiceItemCard(item: InvoiceWithCustomer, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.invoice.issueDate))

    val statusColor = when {
        item.outstandingPaise == 0L -> MaterialTheme.colorScheme.primary
        item.invoice.dueDate < System.currentTimeMillis() -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }
    val statusText = when {
        item.outstandingPaise == 0L -> "PAID"
        item.invoice.dueDate < System.currentTimeMillis() -> "OVERDUE"
        else -> "UNPAID"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                Text(text = item.invoice.invoiceNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = item.customerName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = formattedDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = item.invoice.totalAmount.paise().formatINR(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
