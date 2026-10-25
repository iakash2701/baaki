package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.ui.customers.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    customerId: String? = null,
    onBack: () -> Unit,
    viewModel: CustomerViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val existingCustomer = remember(customerId, state.customers) {
        state.customers.find { it.customer.id == customerId }?.customer
    }

    var name by remember(existingCustomer) { mutableStateOf(existingCustomer?.name ?: "") }
    var phone by remember(existingCustomer) { mutableStateOf(existingCustomer?.phone ?: "") }
    var email by remember(existingCustomer) { mutableStateOf(existingCustomer?.email ?: "") }
    var address by remember(existingCustomer) { mutableStateOf(existingCustomer?.address ?: "") }
    var creditLimitStr by remember(existingCustomer) { mutableStateOf(existingCustomer?.creditLimit?.let { (it / 100.0).toString() } ?: "") }
    var notes by remember(existingCustomer) { mutableStateOf(existingCustomer?.notes ?: "") }
    var isActive by remember(existingCustomer) { mutableStateOf(existingCustomer?.isActive ?: true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (customerId == null) "Add Customer" else "Edit Customer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (customerId == null) "New Customer Details" else "Update Customer Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Business or Customer Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (Optional)") },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Billing Address (Optional)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = creditLimitStr,
                onValueChange = { creditLimitStr = it },
                label = { Text("Credit Limit (₹) (Optional)") },
                leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (customerId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Status", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank()) {
                        errorMessage = "Business name and phone number are required."
                        return@Button
                    }
                    val creditRupees = creditLimitStr.toDoubleOrNull() ?: 0.0
                    val creditPaise = Math.round(creditRupees * 100)

                    viewModel.saveCustomer(
                        id = customerId,
                        name = name,
                        phone = phone,
                        email = email.ifBlank { null },
                        address = address.ifBlank { null },
                        creditLimitPaise = creditPaise,
                        notes = notes.ifBlank { null },
                        isActive = isActive,
                        onSuccess = onBack,
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (customerId == null) "Save Customer" else "Update Customer")
            }
        }
    }
}
