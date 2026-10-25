package com.baaki.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.customers.CustomerFilter
import com.baaki.app.ui.customers.CustomerViewModel
import com.baaki.app.ui.customers.CustomerWithSummary
import com.baaki.app.ui.theme.PrimaryGreen

@Composable
fun CustomersScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomerViewModel = viewModel(),
    onAddCustomer: () -> Unit = {},
    onCustomerClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filterScrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF4FBF8),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCustomer,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BaakiScreenHeader(
                title = "Customers",
                subtitle = "Manage business customers and credit ledgers"
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
                    label = { Text("Search by name or phone") },
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
                        selected = state.filterStatus == CustomerFilter.ALL,
                        onClick = { viewModel.setFilterStatus(CustomerFilter.ALL) },
                        label = { Text("All Customers") }
                    )
                    FilterChip(
                        selected = state.filterStatus == CustomerFilter.ACTIVE,
                        onClick = { viewModel.setFilterStatus(CustomerFilter.ACTIVE) },
                        label = { Text("Active") }
                    )
                    FilterChip(
                        selected = state.filterStatus == CustomerFilter.INACTIVE,
                        onClick = { viewModel.setFilterStatus(CustomerFilter.INACTIVE) },
                        label = { Text("Inactive") }
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
                    state.customers.isEmpty() -> {
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
                                        imageVector = Icons.Default.People,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Customers Found",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add business customers to manage ledgers, credit limits, and outstanding balances.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = onAddCustomer) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add First Customer")
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.customers) { item ->
                                CustomerItemCard(item = item, onClick = { onCustomerClick(item.customer.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerItemCard(item: CustomerWithSummary, onClick: () -> Unit) {
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
                    text = item.customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (item.customer.isActive) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (item.customer.isActive) "ACTIVE" else "INACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (item.customer.isActive) PrimaryGreen else Color(0xFF991B1B)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${item.customer.phone} ${if (!item.customer.email.isNullOrBlank()) "• ${item.customer.email}" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.invoiceCount} Invoices",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Outstanding",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = item.outstandingPaise.paise().formatINR(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.outstandingPaise > 0L) Color(0xFFE53935) else PrimaryGreen
                    )
                }
            }
        }
    }
}
