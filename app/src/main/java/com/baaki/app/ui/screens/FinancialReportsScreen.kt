package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.reports.FinancialReportsViewModel
import com.baaki.app.ui.reports.ReportDateFilter
import com.baaki.app.util.PdfGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialReportsScreen(
    modifier: Modifier = Modifier,
    viewModel: FinancialReportsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports & Analytics") },
                actions = {
                    IconButton(onClick = {
                        val filterTitle = when (state.dateFilter) {
                            ReportDateFilter.THIS_MONTH -> "This Month"
                            ReportDateFilter.LAST_30_DAYS -> "Last 30 Days"
                            ReportDateFilter.CURRENT_FY -> "Current Financial Year"
                            ReportDateFilter.ALL_TIME -> "All Time"
                        }
                        val pdf = PdfGenerator.generateFinancialSummaryPdf(
                            context = context,
                            totalInvoiced = state.totalInvoicedPaise,
                            totalCollected = state.totalCollectedPaise,
                            outstanding = state.outstandingPaise,
                            overdue = state.overduePaise,
                            paidCount = state.paidCount,
                            unpaidCount = state.unpaidCount,
                            overdueCount = state.overdueCount,
                            filterTitle = filterTitle
                        )
                        PdfGenerator.sharePdf(context, pdf, "Financial Summary Report")
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export Report PDF")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = state.dateFilter == ReportDateFilter.ALL_TIME,
                    onClick = { viewModel.setDateFilter(ReportDateFilter.ALL_TIME) },
                    label = { Text("All Time") }
                )
                FilterChip(
                    selected = state.dateFilter == ReportDateFilter.THIS_MONTH,
                    onClick = { viewModel.setDateFilter(ReportDateFilter.THIS_MONTH) },
                    label = { Text("This Month") }
                )
                FilterChip(
                    selected = state.dateFilter == ReportDateFilter.LAST_30_DAYS,
                    onClick = { viewModel.setDateFilter(ReportDateFilter.LAST_30_DAYS) },
                    label = { Text("30 Days") }
                )
                FilterChip(
                    selected = state.dateFilter == ReportDateFilter.CURRENT_FY,
                    onClick = { viewModel.setDateFilter(ReportDateFilter.CURRENT_FY) },
                    label = { Text("Current FY") }
                )
            }

            // Summary Metrics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Invoiced Sales",
                    amount = state.totalInvoicedPaise.paise().formatINR(),
                    subtitle = "Sales in period",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
                MetricCard(
                    title = "Collections Cash",
                    amount = state.totalCollectedPaise.paise().formatINR(),
                    subtitle = "Confirmed cash",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Outstanding Balance",
                    amount = state.outstandingPaise.paise().formatINR(),
                    subtitle = "Receivables owed",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
                MetricCard(
                    title = "Overdue Receivables",
                    amount = state.overduePaise.paise().formatINR(),
                    subtitle = "${state.overdueCount} Overdue",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }

            // Invoice Status Breakdown Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Invoice Status Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Paid Invoices")
                        Text("${state.paidCount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Partially Paid Invoices")
                        Text("${state.partialCount}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Unpaid Invoices")
                        Text("${state.unpaidCount}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Overdue Invoices")
                        Text("${state.overdueCount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Customer-wise Outstanding Breakdown
            Text(text = "Customer-wise Outstanding Balances", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (state.customerOutstandings.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No customer records found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                state.customerOutstandings.forEach { (cust, out) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = cust.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = "Phone: ${cust.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = out.paise().formatINR(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (out > 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
