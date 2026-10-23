package com.baaki.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.domain.chase.PriorityLevel
import com.baaki.app.domain.chase.RecoveryItem
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.smartrecovery.SmartRecoveryViewModel
import com.baaki.app.util.InvoiceShareFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SmartRecoveryScreen(
    modifier: Modifier = Modifier,
    viewModel: SmartRecoveryViewModel = viewModel(),
    onInvoiceClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
        state.recoveryItems.isEmpty() -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Invoices in Recovery Queue",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All customer invoices are fully paid or settled. Excellent work on cash collections!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            val highPriorityCount = state.recoveryItems.count { it.priorityLevel == PriorityLevel.HIGH }
            val totalRecoverable = state.recoveryItems.sumOf { it.outstandingPaise }

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
                            Text(text = "Smart Recovery Intelligence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "$highPriorityCount high-priority invoices require attention. Potentially recoverable: ${totalRecoverable.paise().formatINR()}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                items(state.recoveryItems) { item ->
                    RecoveryCard(
                        item = item,
                        onClick = { onInvoiceClick(item.invoice.id) },
                        onShare = {
                            val summary = InvoiceShareFormatter.generateSummary(
                                invoice = item.invoice,
                                customer = item.customer,
                                lineItems = emptyList(),
                                totalPaid = item.totalPaidPaise,
                                outstanding = item.outstandingPaise,
                                status = item.invoice.status
                            )
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Payment Reminder: Invoice ${item.invoice.invoiceNumber}")
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            val chooser = Intent.createChooser(intent, "Share Payment Reminder")
                            context.startActivity(chooser)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecoveryCard(
    item: RecoveryItem,
    onClick: () -> Unit,
    onShare: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(item.invoice.dueDate))

    val badgeColor = when (item.priorityLevel) {
        PriorityLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
        PriorityLevel.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
        PriorityLevel.LOW -> MaterialTheme.colorScheme.surfaceVariant
    }
    val badgeTextColor = when (item.priorityLevel) {
        PriorityLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
        PriorityLevel.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
        PriorityLevel.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = badgeColor
                ) {
                    Text(
                        text = "${item.priorityLevel} (${item.priorityScore})",
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Invoice: ${item.invoice.invoiceNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Due: $dueDateStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total: ${item.invoice.totalAmount.paise().formatINR()}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Outstanding: ${item.outstandingPaise.paise().formatINR()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider()

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Reasons:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                item.reasons.forEach { reason ->
                    Text(text = "• $reason", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.recommendedAction, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                OutlinedButton(onClick = onShare) {
                    Text("Share Reminder")
                }
            }
        }
    }
}
