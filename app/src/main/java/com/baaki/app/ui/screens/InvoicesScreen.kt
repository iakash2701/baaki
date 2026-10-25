package com.baaki.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.dashboard.InvoiceWithCustomer
import com.baaki.app.ui.invoices.InvoiceFilter
import com.baaki.app.ui.invoices.InvoiceViewModel
import com.baaki.app.ui.invoices.LineItemInput
import com.baaki.app.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicesScreen(
    modifier: Modifier = Modifier,
    viewModel: InvoiceViewModel = viewModel(),
    onInvoiceClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    val filterScrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF4FBF8),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (state.customers.isNotEmpty()) {
                        showCreateDialog = true
                    }
                },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Invoice")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BaakiScreenHeader(
                title = "Invoices",
                subtitle = "Create and manage professional invoices"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    label = { Text("Search by invoice no. or customer") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = PrimaryGreen
                    )
                )

                // Filter Chips (Horizontally Scrollable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(filterScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.filterStatus == InvoiceFilter.ALL,
                        onClick = { viewModel.setFilterStatus(InvoiceFilter.ALL) },
                        label = { Text("All Invoices") }
                    )
                    FilterChip(
                        selected = state.filterStatus == InvoiceFilter.UNPAID,
                        onClick = { viewModel.setFilterStatus(InvoiceFilter.UNPAID) },
                        label = { Text("Unpaid") }
                    )
                    FilterChip(
                        selected = state.filterStatus == InvoiceFilter.PARTIALLY_PAID,
                        onClick = { viewModel.setFilterStatus(InvoiceFilter.PARTIALLY_PAID) },
                        label = { Text("Partial") }
                    )
                    FilterChip(
                        selected = state.filterStatus == InvoiceFilter.PAID,
                        onClick = { viewModel.setFilterStatus(InvoiceFilter.PAID) },
                        label = { Text("Paid") }
                    )
                    FilterChip(
                        selected = state.filterStatus == InvoiceFilter.OVERDUE,
                        onClick = { viewModel.setFilterStatus(InvoiceFilter.OVERDUE) },
                        label = { Text("Overdue") }
                    )
                }

                when {
                    state.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    state.errorMessage != null -> {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    state.invoices.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Invoices Found",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (state.customers.isEmpty()) "Please add an active customer in the Customers tab before creating an invoice." else "Create and manage professional invoices for your customers.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            if (state.customers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = { showCreateDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Create First Invoice")
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.invoices) { item ->
                                InvoiceItemCard(item = item, onClick = { onInvoiceClick(item.invoice.id) })
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            var defaultInvNo by remember { mutableStateOf("SP/2026-27/001") }
            LaunchedEffect(Unit) {
                defaultInvNo = viewModel.generateNextInvoiceNumber()
            }

            CreateInvoiceDialog(
                customers = state.customers,
                initialInvoiceNumber = defaultInvNo,
                onDismiss = { showCreateDialog = false },
                onSave = { customerId, invNo, issueDate, dueDate, notes, items ->
                    viewModel.createInvoice(
                        customerId = customerId,
                        invoiceNumber = invNo,
                        issueDate = issueDate,
                        dueDate = dueDate,
                        notes = notes,
                        items = items,
                        onSuccess = { showCreateDialog = false },
                        onError = { /* Handle error */ }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceDialog(
    customers: List<CustomerEntity>,
    initialInvoiceNumber: String,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Long, String?, List<LineItemInput>) -> Unit
) {
    var selectedCustomerId by remember { mutableStateOf(customers.firstOrNull()?.id ?: "") }
    var invoiceNumber by remember { mutableStateOf(initialInvoiceNumber) }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var items by remember {
        mutableStateOf(
            listOf(
                LineItemInput(
                    description = "Corrugated Box 5-Ply",
                    hsnSac = "4819",
                    quantity = 100.0,
                    unitPricePaise = 5000L,
                    taxPercent = 18.0
                )
            )
        )
    }

    var expandedCustomerDropdown by remember { mutableStateOf(false) }
    val selectedCustomerName = customers.find { it.id == selectedCustomerId }?.name ?: "Select Customer"

    val subTotal = items.sumOf { Math.round(it.quantity * it.unitPricePaise) }
    val taxTotal = items.sumOf {
        val lineAmt = Math.round(it.quantity * it.unitPricePaise)
        Math.round(lineAmt * (it.taxPercent / 100.0))
    }
    val grandTotal = subTotal + taxTotal

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Invoice") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                ExposedDropdownMenuBox(
                    expanded = expandedCustomerDropdown,
                    onExpandedChange = { expandedCustomerDropdown = !expandedCustomerDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedCustomerName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Customer *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCustomerDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCustomerDropdown,
                        onDismissRequest = { expandedCustomerDropdown = false }
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.name) },
                                onClick = {
                                    selectedCustomerId = customer.id
                                    expandedCustomerDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    label = { Text("Invoice Number *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Terms (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Line Items", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                items.forEachIndexed { index, item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Item #${index + 1}", style = MaterialTheme.typography.labelMedium)
                                if (items.size > 1) {
                                    IconButton(onClick = {
                                        items = items.toMutableList().apply { removeAt(index) }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = item.description,
                                onValueChange = { desc ->
                                    items = items.toMutableList().apply {
                                        this[index] = item.copy(description = desc)
                                    }
                                },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = item.quantity.toString(),
                                    onValueChange = { q ->
                                        val qty = q.toDoubleOrNull() ?: 0.0
                                        items = items.toMutableList().apply {
                                            this[index] = item.copy(quantity = qty)
                                        }
                                    },
                                    label = { Text("Qty") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = (item.unitPricePaise / 100.0).toString(),
                                    onValueChange = { p ->
                                        val rupees = p.toDoubleOrNull() ?: 0.0
                                        val paise = Math.round(rupees * 100)
                                        items = items.toMutableList().apply {
                                            this[index] = item.copy(unitPricePaise = paise)
                                        }
                                    },
                                    label = { Text("Price (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.taxPercent.toString(),
                                    onValueChange = { t ->
                                        val tax = t.toDoubleOrNull() ?: 0.0
                                        items = items.toMutableList().apply {
                                            this[index] = item.copy(taxPercent = tax)
                                        }
                                    },
                                    label = { Text("GST %") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        items = items + LineItemInput("", null, 1.0, 0L, 18.0)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Line Item")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                    Text(subTotal.paise().formatINR(), style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GST Total:", style = MaterialTheme.typography.bodyMedium)
                    Text(taxTotal.paise().formatINR(), style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Grand Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(grandTotal.paise().formatINR(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCustomerId.isBlank()) {
                        errorMessage = "Please select a customer."
                        return@Button
                    }
                    if (invoiceNumber.isBlank()) {
                        errorMessage = "Invoice number cannot be blank."
                        return@Button
                    }
                    if (items.any { it.description.isBlank() || it.quantity <= 0 }) {
                        errorMessage = "All items must have a description and valid quantity."
                        return@Button
                    }
                    val now = System.currentTimeMillis()
                    val dueDate = now + (15 * 24 * 60 * 60 * 1000L)
                    onSave(selectedCustomerId, invoiceNumber, now, dueDate, notes.ifBlank { null }, items)
                }
            ) {
                Text("Save & Issue Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InvoiceItemCard(item: InvoiceWithCustomer, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.invoice.issueDate))
    val dueDateStr = dateFormat.format(Date(item.invoice.dueDate))
    val now = System.currentTimeMillis()

    val isOverdue = item.invoice.dueDate < now && item.outstandingPaise > 0L && item.invoice.status != "PAID"
    val statusColor = when {
        item.outstandingPaise == 0L -> PrimaryGreen
        isOverdue -> Color(0xFFE53935)
        item.outstandingPaise < item.invoice.totalAmount -> Color(0xFFD97706)
        else -> Color(0xFF64748B)
    }
    val statusText = when {
        item.outstandingPaise == 0L -> "PAID"
        isOverdue -> "OVERDUE"
        item.outstandingPaise < item.invoice.totalAmount -> "PARTIAL"
        else -> "PENDING"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.invoice.invoiceNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = item.customerName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111C16)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Issued: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Due: $dueDateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverdue) Color(0xFFE53935) else Color(0xFF64748B),
                        fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.invoice.totalAmount.paise().formatINR(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.outstandingPaise > 0L) {
                        Text(
                            text = "Due: ${item.outstandingPaise.paise().formatINR()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Fully Settled",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
