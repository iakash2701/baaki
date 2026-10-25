package com.baaki.app.ui.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.data.repository.BaakiRepository
import com.baaki.app.domain.money.paise
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class InvoiceDetailUiState(
    val isLoading: Boolean = true,
    val invoice: InvoiceEntity? = null,
    val customer: CustomerEntity? = null,
    val lineItems: List<InvoiceLineItemEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val totalPaidPaise: Long = 0L,
    val outstandingPaise: Long = 0L,
    val status: String = "UNPAID",
    val errorMessage: String? = null
)

class InvoiceDetailViewModel(
    application: Application,
    val invoiceId: String
) : AndroidViewModel(application) {
    private val db = BaakiDatabase.getDatabase(application)
    private val repository = BaakiRepository(
        db.customerDao(), db.invoiceDao(), db.paymentDao(), db.expenseDao(), db.activityEventDao()
    )

    val uiState: StateFlow<InvoiceDetailUiState> = combine(
        db.invoiceDao().getInvoiceWithLineItems(invoiceId),
        db.paymentDao().getPaymentsForInvoice(invoiceId)
    ) { invWithItems, payments ->
        Pair(invWithItems, payments)
    }.flatMapLatest { (invWithItems, payments) ->
        if (invWithItems == null) {
            flowOf(InvoiceDetailUiState(isLoading = false, errorMessage = "Invoice not found"))
        } else {
            db.customerDao().getCustomerById(invWithItems.invoice.customerId).map { customer ->
                val invoice = invWithItems.invoice
                val totalPaid = payments.sumOf { it.amount }
                val outstanding = maxOf(0L, invoice.totalAmount - totalPaid)
                val now = System.currentTimeMillis()

                val status = when {
                    outstanding == 0L -> "PAID"
                    totalPaid > 0L -> "PARTIALLY_PAID"
                    invoice.dueDate < now -> "OVERDUE"
                    else -> "SENT"
                }

                InvoiceDetailUiState(
                    isLoading = false,
                    invoice = invoice,
                    customer = customer,
                    lineItems = invWithItems.lineItems,
                    payments = payments,
                    totalPaidPaise = totalPaid,
                    outstandingPaise = outstanding,
                    status = status
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InvoiceDetailUiState(isLoading = true)
    )

    fun recordPayment(
        amountPaise: Long,
        paymentMethod: String,
        referenceNumber: String?,
        notes: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentState = uiState.value
        val invoice = currentState.invoice
        val customer = currentState.customer

        if (invoice == null || customer == null) {
            onError("Invoice or customer information missing.")
            return
        }
        if (amountPaise <= 0L) {
            onError("Payment amount must be greater than zero.")
            return
        }
        if (amountPaise > currentState.outstandingPaise) {
            onError("Payment amount cannot exceed outstanding balance (${currentState.outstandingPaise.paise().formatINR()}).")
            return
        }

        viewModelScope.launch {
            try {
                val payment = PaymentEntity(
                    id = UUID.randomUUID().toString(),
                    invoiceId = invoice.id,
                    customerId = customer.id,
                    amount = amountPaise,
                    paymentDate = System.currentTimeMillis(),
                    paymentMethod = paymentMethod,
                    referenceNumber = referenceNumber?.takeIf { it.isNotBlank() }?.trim(),
                    notes = notes?.takeIf { it.isNotBlank() }?.trim(),
                    createdAt = System.currentTimeMillis()
                )
                repository.recordPayment(payment)
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to record payment")
            }
        }
    }
}

class InvoiceDetailViewModelFactory(
    private val application: Application,
    private val invoiceId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoiceDetailViewModel::class.java)) {
            return InvoiceDetailViewModel(application, invoiceId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
