package com.baaki.app.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.baaki.app.ui.customers.CustomerDetailViewModel
import com.baaki.app.ui.customers.CustomerDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: String,
    onBack: () -> Unit,
    onEditCustomer: (String) -> Unit,
    onInvoiceClick: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: CustomerDetailViewModel = viewModel(
        factory = CustomerDetailViewModelFactory(context.applicationContext as Application, customerId)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeactivateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.customer?.name ?: "Customer Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.customer != null) {
                        IconButton(onClick = { onEditCustomer(customerId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Customer")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
                }
            }
            state.customer == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Customer not found.")
                }
            }
            else -> {
                val customer = state.customer!!
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header & Contact Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = customer.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = if (customer.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = if (customer.isActive) "ACTIVE" else "INACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (customer.isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                            Text(text = "Phone: ${customer.phone}", style = MaterialTheme.typography.bodyMedium)
                            if (!customer.email.isNullOrBlank()) {
                                Text(text = "Email: ${customer.email}", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (!customer.address.isNullOrBlank()) {
                                Text(text = "Address: ${customer.address}", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (!customer.notes.isNullOrBlank()) {
                                Text(text = "Notes: ${customer.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (customer.creditLimit > 0L) {
                                Text(text = "Credit Limit: ${customer.creditLimit.paise().formatINR()}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // Financial Summary Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Financial Ledger Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Invoiced")
                                Text(state.totalInvoicedPaise.paise().formatINR(), fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Confirmed Paid")
                                Text(state.totalPaidPaise.paise().formatINR(), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Overdue Balance")
                                Text(state.overduePaise.paise().formatINR(), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                            }
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Current Outstanding Balance", fontWeight = FontWeight.Bold, color = if (state.outstandingPaise > 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                                Text(state.outstandingPaise.paise().formatINR(), fontWeight = FontWeight.Bold, color = if (state.outstandingPaise > 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Invoice History Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Invoice History (${state.invoices.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            if (state.invoices.isEmpty()) {
                                Text(text = "No invoices for this customer.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                state.invoices.forEach { inv ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = inv.invoiceNumber, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(text = "Issued: ${dateFormat.format(Date(inv.issueDate))} • Due: ${dateFormat.format(Date(inv.dueDate))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = inv.totalAmount.paise().formatINR(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(text = inv.status, style = MaterialTheme.typography.labelSmall, color = if (inv.status == "PAID") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }

                    if (customer.isActive) {
                        OutlinedButton(
                            onClick = { showDeactivateDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Deactivate Customer")
                        }
                    }
                }
            }
        }

        if (showDeactivateDialog) {
            AlertDialog(
                onDismissRequest = { showDeactivateDialog = false },
                title = { Text("Deactivate Customer") },
                text = { Text("Deactivating this customer archives their account while preserving all financial invoice and payment history. Continue?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeactivateDialog = false
                            viewModel.deactivateCustomer { onBack() }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Deactivate")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeactivateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
