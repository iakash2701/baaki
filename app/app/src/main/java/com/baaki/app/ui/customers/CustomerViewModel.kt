package com.baaki.app.ui.customers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CustomerWithSummary(
    val customer: CustomerEntity,
    val invoiceCount: Int,
    val outstandingPaise: Long
)

data class CustomerListUiState(
    val isLoading: Boolean = true,
    val customers: List<CustomerWithSummary> = emptyList(),
    val errorMessage: String? = null
)

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BaakiRepository

    init {
        val db = BaakiDatabase.getDatabase(application)
        repository = BaakiRepository(
            db.customerDao(),
            db.invoiceDao(),
            db.paymentDao(),
            db.expenseDao(),
            db.activityEventDao()
        )
    }

    val uiState: StateFlow<CustomerListUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments
    ) { customers, invoices, payments ->
        try {
            val invoicesByCustomer = invoices.groupBy { it.customerId }
            val paymentsByCustomer = payments.groupBy { it.customerId }

            val list = customers.map { cust ->
                val custInvoices = invoicesByCustomer[cust.id] ?: emptyList()
                val custPayments = paymentsByCustomer[cust.id] ?: emptyList()

                val totalInvoiced = custInvoices.sumOf { it.totalAmount }
                val totalPaid = custPayments.sumOf { it.amount }
                // Outstanding = openingBalance + totalInvoiced - totalPaid
                val outstanding = cust.openingBalance + totalInvoiced - totalPaid

                CustomerWithSummary(
                    customer = cust,
                    invoiceCount = custInvoices.size,
                    outstandingPaise = maxOf(0L, outstanding)
                )
            }

            CustomerListUiState(
                isLoading = false,
                customers = list
            )
        } catch (e: Exception) {
            CustomerListUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading customers")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerListUiState(isLoading = true)
    )

    fun addCustomer(
        name: String,
        phone: String,
        email: String?,
        gstin: String?,
        address: String?,
        openingBalancePaise: Long,
        creditLimitPaise: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank() || phone.isBlank()) {
            onError("Business name and phone number are required.")
            return
        }
        viewModelScope.launch {
            try {
                val customer = CustomerEntity(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email?.takeIf { it.isNotBlank() }?.trim(),
                    gstin = gstin?.takeIf { it.isNotBlank() }?.trim(),
                    address = address?.takeIf { it.isNotBlank() }?.trim(),
                    openingBalance = openingBalancePaise,
                    creditLimit = creditLimitPaise,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertCustomer(customer)
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to add customer")
            }
        }
    }
}
