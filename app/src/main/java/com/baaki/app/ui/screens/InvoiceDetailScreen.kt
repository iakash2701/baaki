package com.baaki.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.invoices.InvoiceDetailViewModel
import com.baaki.app.ui.invoices.InvoiceDetailViewModelFactory
import com.baaki.app.util.InvoiceShareFormatter
import com.baaki.app.util.PdfGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: InvoiceDetailViewModel = viewModel(
        factory = InvoiceDetailViewModelFactory(context.applicationContext as android.app.Application, invoiceId)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showPaymentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.invoice?.invoiceNumber ?: "Invoice Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.invoice != null) {
                        IconButton(onClick = {
                            val pdfFile = PdfGenerator.generateInvoicePdf(
                                context = context,
                                invoice = state.invoice!!,
                                customer = state.customer,
                                lineItems = state.lineItems,
                                payments = state.payments,
                                totalPaid = state.totalPaidPaise,
                                outstanding = state.outstandingPaise
                            )
                            PdfGenerator.sharePdf(context, pdfFile, "Export Invoice PDF")
                        }) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
                        }

                        IconButton(onClick = {
                            val summary = InvoiceShareFormatter.generateSummary(
                                invoice = state.invoice!!,
                                customer = state.customer,
                                lineItems = state.lineItems,
                                totalPaid = state.totalPaidPaise,
                                outstanding = state.outstandingPaise,
                                status = state.status
                            )
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Invoice ${state.invoice!!.invoiceNumber} from Sharma Packaging")
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            val chooser = Intent.createChooser(intent, "Share Invoice")
                            context.startActivity(chooser)
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share Invoice")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (state.invoice != null && state.outstandingPaise > 0L) {
                FloatingActionButton(onClick = { showPaymentDialog = true }) {
                    Icon(Icons.Default.Payment, contentDescription = "Record Payment")
                }
            }
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
            state.invoice == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Invoice not found.")
                }
            }
            else -> {
                val invoice = state.invoice!!
                val customer = state.customer
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status & Summary Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (state.status) {
                                "PAID" -> MaterialTheme.colorScheme.primaryContainer
                                "OVERDUE" -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
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
                                    text = invoice.invoiceNumber,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = state.status,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(text = "Customer: ${customer?.name ?: "Unknown"} (${customer?.phone ?: ""})", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Issue Date: ${dateFormat.format(Date(invoice.issueDate))}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Due Date: ${dateFormat.format(Date(invoice.dueDate))}", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Financial Breakdown Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Financial Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal")
                                Text(invoice.subTotal.paise().formatINR())
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GST (Tax)")
                                Text(invoice.taxTotal.paise().formatINR())
                            }
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total", fontWeight = FontWeight.Bold)
                                Text(invoice.totalAmount.paise().formatINR(), fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Confirmed Paid")
                                Text(state.totalPaidPaise.paise().formatINR(), color = MaterialTheme.colorScheme.primary)
                            }
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Outstanding Balance", fontWeight = FontWeight.Bold, color = if (state.outstandingPaise > 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                                Text(state.outstandingPaise.paise().formatINR(), fontWeight = FontWeight.Bold, color = if (state.outstandingPaise > 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Line Items Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Line Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            state.lineItems.forEach { item ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.description, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text(text = "Qty: ${item.quantity} × ${item.unitPrice.paise().formatINR()} (${item.taxPercent}% GST)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(text = item.amount.paise().formatINR(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    // Confirmed Payments Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Confirmed Payments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            if (state.payments.isEmpty()) {
                                Text(text = "No payments recorded yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                val timeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                state.payments.forEach { pay ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Paid via ${pay.paymentMethod}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                            if (!pay.referenceNumber.isNullOrBlank()) {
                                                Text(text = "Ref: ${pay.referenceNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(text = timeFormat.format(Date(pay.paymentDate)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = pay.amount.paise().formatINR(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            TextButton(onClick = {
                                                val receiptFile = PdfGenerator.generateReceiptPdf(
                                                    context = context,
                                                    payment = pay,
                                                    invoiceNumber = state.invoice!!.invoiceNumber,
                                                    customerName = state.customer?.name ?: "Customer",
                                                    remainingBalance = state.outstandingPaise
                                                )
                                                PdfGenerator.sharePdf(context, receiptFile, "Export Payment Receipt")
                                            }) {
                                                Text("Receipt PDF", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showPaymentDialog && state.invoice != null) {
            RecordPaymentDialog(
                outstandingPaise = state.outstandingPaise,
                onDismiss = { showPaymentDialog = false },
                onSave = { amountPaise, method, ref, notes ->
                    viewModel.recordPayment(
                        amountPaise = amountPaise,
                        paymentMethod = method,
                        referenceNumber = ref,
                        notes = notes,
                        onSuccess = { showPaymentDialog = false },
                        onError = { /* Handle error */ }
                    )
                }
            )
        }
    }
}

@Composable
fun RecordPaymentDialog(
    outstandingPaise: Long,
    onDismiss: () -> Unit,
    onSave: (Long, String, String?, String?) -> Unit
) {
    var amountRupeesStr by remember { mutableStateOf((outstandingPaise / 100.0).toString()) }
    var selectedMethod by remember { mutableStateOf("UPI") }
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedMethodDropdown by remember { mutableStateOf(false) }

    val methods = listOf("CASH", "UPI", "BANK_TRANSFER", "CHEQUE")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Payment") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Text(text = "Outstanding Due: ${outstandingPaise.paise().formatINR()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountRupeesStr,
                    onValueChange = { amountRupeesStr = it },
                    label = { Text("Payment Amount (₹) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedMethodDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Method: $selectedMethod")
                    }
                    DropdownMenu(
                        expanded = expandedMethodDropdown,
                        onDismissRequest = { expandedMethodDropdown = false }
                    ) {
                        methods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    selectedMethod = method
                                    expandedMethodDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("Reference / Txn ID (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rupees = amountRupeesStr.toDoubleOrNull() ?: 0.0
                    val paise = Math.round(rupees * 100)
                    if (paise <= 0L) {
                        errorMessage = "Payment amount must be greater than zero."
                        return@Button
                    }
                    if (paise > outstandingPaise) {
                        errorMessage = "Amount cannot exceed outstanding balance (${outstandingPaise.paise().formatINR()})."
                        return@Button
                    }
                    onSave(paise, selectedMethod, referenceNumber.ifBlank { null }, notes.ifBlank { null })
                }
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
