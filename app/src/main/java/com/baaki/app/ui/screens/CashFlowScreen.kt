package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.cashflow.CashFlowDateRange
import com.baaki.app.ui.cashflow.CashFlowViewModel
import com.baaki.app.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFlowScreen(
    modifier: Modifier = Modifier,
    viewModel: CashFlowViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF4FBF8),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseDialog = true },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Record Expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BaakiScreenHeader(
                title = "Cash Flow",
                subtitle = "Confirmed collections and business expenses"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Range Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.dateRange == CashFlowDateRange.ALL,
                        onClick = { viewModel.setDateRange(CashFlowDateRange.ALL) },
                        label = { Text("All Time") }
                    )
                    FilterChip(
                        selected = state.dateRange == CashFlowDateRange.THIS_MONTH,
                        onClick = { viewModel.setDateRange(CashFlowDateRange.THIS_MONTH) },
                        label = { Text("This Month") }
                    )
                    FilterChip(
                        selected = state.dateRange == CashFlowDateRange.LAST_30_DAYS,
                        onClick = { viewModel.setDateRange(CashFlowDateRange.LAST_30_DAYS) },
                        label = { Text("Last 30 Days") }
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
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Summary Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF111C16)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Cash Flow Intelligence",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = "Inflow (Collections)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                            Text(text = state.totalInflowPaise.paise().formatINR(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                        }
                                        Column {
                                            Text(text = "Outflow (Expenses)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                            Text(text = state.totalOutflowPaise.paise().formatINR(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                        }
                                        Column {
                                            Text(text = "Net Cash Flow", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                            Text(text = state.netCashFlowPaise.paise().formatINR(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF111C16))
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Recorded Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (state.payments.isEmpty() && state.expenses.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text("No transactions found for the selected period.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.payments) { payment ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = MaterialTheme.shapes.medium,
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(text = "Payment Received (${payment.paymentMethod})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                                    Text(text = dateFormat.format(Date(payment.paymentDate)), style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                                }
                                                Text(text = "+${payment.amount.paise().formatINR()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                            }
                                        }
                                    }
                                    items(state.expenses) { expense ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = MaterialTheme.shapes.medium,
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(text = "Expense: ${expense.title} (${expense.category})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                                    Text(text = dateFormat.format(Date(expense.expenseDate)), style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                                }
                                                Text(text = "-${expense.amount.paise().formatINR()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddExpenseDialog) {
            AddExpenseDialog(
                onDismiss = { showAddExpenseDialog = false },
                onSave = { title, category, amountPaise, notes ->
                    viewModel.addExpense(
                        title = title,
                        category = category,
                        amountPaise = amountPaise,
                        notes = notes,
                        onSuccess = { showAddExpenseDialog = false },
                        onError = { /* Handle error */ }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Long, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("SUPPLIES") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    val categories = listOf("RENT", "SUPPLIES", "UTILITIES", "SALARY", "TRANSPORT", "OTHER")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Expense") },
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategoryDropdown,
                    onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoryDropdown,
                        onDismissRequest = { expandedCategoryDropdown = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

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
                    if (title.isBlank()) {
                        errorMessage = "Expense title cannot be blank."
                        return@Button
                    }
                    val rupees = amountStr.toDoubleOrNull() ?: 0.0
                    val paise = Math.round(rupees * 100)
                    if (paise <= 0L) {
                        errorMessage = "Amount must be greater than zero."
                        return@Button
                    }
                    onSave(title, selectedCategory, paise, notes.ifBlank { null })
                }
            ) {
                Text("Save Expense")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
