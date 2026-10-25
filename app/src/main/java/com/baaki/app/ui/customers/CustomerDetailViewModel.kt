package com.baaki.app.ui.customers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CustomerDetailUiState(
    val isLoading: Boolean = true,
    val customer: CustomerEntity? = null,
    val invoices: List<InvoiceEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val totalInvoicedPaise: Long = 0L,
    val totalPaidPaise: Long = 0L,
    val outstandingPaise: Long = 0L,
    val overduePaise: Long = 0L,
    val errorMessage: String? = null
)

class CustomerDetailViewModel(
    application: Application,
    private val customerId: String
) : AndroidViewModel(application) {
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

    val uiState: StateFlow<CustomerDetailUiState> = combine(
        repository.getCustomerById(customerId),
        repository.allInvoices,
        repository.allPayments
    ) { customer, invoices, payments ->
        try {
            if (customer == null) {
                return@combine CustomerDetailUiState(isLoading = false, errorMessage = "Customer not found")
            }

            val custInvoices = invoices.filter { it.customerId == customerId }
            val custPayments = payments.filter { it.customerId == customerId }
            val paymentsByInvoice = payments.groupBy { it.invoiceId }

            val totalInvoiced = custInvoices.sumOf { it.totalAmount }
            val totalPaid = custPayments.sumOf { it.amount }
            val outstanding = maxOf(0L, customer.openingBalance + totalInvoiced - totalPaid)

            val now = System.currentTimeMillis()
            var overdue = 0L
            custInvoices.forEach { inv ->
                val invPaid = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
                val invOutstanding = maxOf(0L, inv.totalAmount - invPaid)
                if (inv.dueDate < now && invOutstanding > 0L && inv.status != "PAID") {
                    overdue += invOutstanding
                }
            }

            CustomerDetailUiState(
                isLoading = false,
                customer = customer,
                invoices = custInvoices,
                payments = custPayments,
                totalInvoicedPaise = totalInvoiced,
                totalPaidPaise = totalPaid,
                outstandingPaise = outstanding,
                overduePaise = overdue
            )
        } catch (e: Exception) {
            CustomerDetailUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading customer details")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerDetailUiState(isLoading = true)
    )

    fun deactivateCustomer(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val cust = uiState.value.customer
                if (cust != null) {
                    val updated = cust.copy(isActive = false, updatedAt = System.currentTimeMillis())
                    repository.updateCustomer(updated)
                    onSuccess()
                }
            } catch (_: Exception) {}
        }
    }
}

class CustomerDetailViewModelFactory(
    private val application: Application,
    private val customerId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            return CustomerDetailViewModel(application, customerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
