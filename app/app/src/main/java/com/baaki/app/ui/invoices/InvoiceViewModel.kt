package com.baaki.app.ui.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.data.repository.BaakiRepository
import com.baaki.app.ui.dashboard.InvoiceWithCustomer
import com.baaki.app.util.FinancialYearUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class InvoiceListUiState(
    val isLoading: Boolean = true,
    val invoices: List<InvoiceWithCustomer> = emptyList(),
    val customers: List<CustomerEntity> = emptyList(),
    val errorMessage: String? = null
)

data class LineItemInput(
    val description: String,
    val hsnSac: String?,
    val quantity: Double,
    val unitPricePaise: Long,
    val taxPercent: Double
)

class InvoiceViewModel(application: Application) : AndroidViewModel(application) {
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

    val uiState: StateFlow<InvoiceListUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments
    ) { customers, invoices, payments ->
        try {
            val customerMap = customers.associateBy { it.id }
            val paymentsByInvoice = payments.groupBy { it.invoiceId }

            val list = invoices.map { inv ->
                val invPayments = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
                val outstanding = maxOf(0L, inv.totalAmount - invPayments)
                InvoiceWithCustomer(
                    invoice = inv,
                    customerName = customerMap[inv.customerId]?.name ?: "Unknown Customer",
                    outstandingPaise = outstanding
                )
            }

            InvoiceListUiState(
                isLoading = false,
                invoices = list,
                customers = customers
            )
        } catch (e: Exception) {
            InvoiceListUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading invoices")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InvoiceListUiState(isLoading = true)
    )

    suspend fun generateNextInvoiceNumber(issueDate: Long = System.currentTimeMillis()): String {
        val fy = FinancialYearUtils.getFinancialYear(issueDate)
        val invoices = repository.allInvoices.first()
        val countForFy = invoices.count { it.invoiceNumber.contains(fy) }
        return "SP/$fy/${(countForFy + 1).toString().padStart(3, '0')}"
    }

    fun createInvoice(
        customerId: String,
        invoiceNumber: String,
        issueDate: Long,
        dueDate: Long,
        notes: String?,
        items: List<LineItemInput>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (customerId.isBlank()) {
            onError("Please select a customer.")
            return
        }
        if (items.isEmpty()) {
            onError("Please add at least one line item.")
            return
        }
        if (items.any { it.description.isBlank() || it.quantity <= 0 || it.unitPricePaise < 0 }) {
            onError("Please ensure all line items have valid descriptions, quantities (>0), and prices.")
            return
        }

        viewModelScope.launch {
            try {
                val invoiceId = UUID.randomUUID().toString()
                var subTotal = 0L
                var taxTotal = 0L

                val lineItemEntities = items.map { item ->
                    val lineAmount = Math.round(item.quantity * item.unitPricePaise)
                    val lineTax = Math.round(lineAmount * (item.taxPercent / 100.0))
                    val totalLineAmount = lineAmount + lineTax

                    subTotal += lineAmount
                    taxTotal += lineTax

                    InvoiceLineItemEntity(
                        id = UUID.randomUUID().toString(),
                        invoiceId = invoiceId,
                        description = item.description.trim(),
                        hsnSac = item.hsnSac?.takeIf { it.isNotBlank() }?.trim(),
                        quantity = item.quantity,
                        unitPrice = item.unitPricePaise,
                        taxPercent = item.taxPercent,
                        amount = totalLineAmount
                    )
                }

                val totalAmount = subTotal + taxTotal

                val invoice = InvoiceEntity(
                    id = invoiceId,
                    invoiceNumber = invoiceNumber.trim(),
                    customerId = customerId,
                    issueDate = issueDate,
                    dueDate = dueDate,
                    subTotal = subTotal,
                    taxTotal = taxTotal,
                    discountTotal = 0L,
                    totalAmount = totalAmount,
                    status = "SENT",
                    notes = notes?.takeIf { it.isNotBlank() }?.trim(),
                    createdAt = System.currentTimeMillis()
                )

                repository.createInvoiceWithItems(invoice, lineItemEntities)
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to create invoice")
            }
        }
    }
}
