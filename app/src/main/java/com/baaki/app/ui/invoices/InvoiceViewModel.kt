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

enum class InvoiceFilter {
    ALL, UNPAID, PARTIALLY_PAID, PAID, OVERDUE
}

data class InvoiceListUiState(
    val isLoading: Boolean = true,
    val invoices: List<InvoiceWithCustomer> = emptyList(),
    val customers: List<CustomerEntity> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: InvoiceFilter = InvoiceFilter.ALL,
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow(InvoiceFilter.ALL)
    val filterStatus: StateFlow<InvoiceFilter> = _filterStatus.asStateFlow()

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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterStatus(filter: InvoiceFilter) {
        _filterStatus.value = filter
    }

    val uiState: StateFlow<InvoiceListUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments,
        _searchQuery,
        _filterStatus
    ) { customers, invoices, payments, query, filter ->
        try {
            val customerMap = customers.associateBy { it.id }
            val paymentsByInvoice = payments.groupBy { it.invoiceId }
            val now = System.currentTimeMillis()

            val mapped = invoices.map { inv ->
                val invPayments = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
                val outstanding = maxOf(0L, inv.totalAmount - invPayments)
                val customerName = customerMap[inv.customerId]?.name ?: "Unknown Customer"
                InvoiceWithCustomer(
                    invoice = inv,
                    customerName = customerName,
                    outstandingPaise = outstanding
                )
            }

            val filtered = mapped.filter { item ->
                val inv = item.invoice
                val matchesQuery = query.isBlank() ||
                        inv.invoiceNumber.contains(query, ignoreCase = true) ||
                        item.customerName.contains(query, ignoreCase = true)

                val isOverdue = inv.dueDate < now && item.outstandingPaise > 0L && inv.status != "PAID"
                val matchesStatus = when (filter) {
                    InvoiceFilter.ALL -> true
                    InvoiceFilter.UNPAID -> item.outstandingPaise == inv.totalAmount && !isOverdue
                    InvoiceFilter.PARTIALLY_PAID -> item.outstandingPaise in 1 until inv.totalAmount && !isOverdue
                    InvoiceFilter.PAID -> item.outstandingPaise == 0L
                    InvoiceFilter.OVERDUE -> isOverdue
                }

                matchesQuery && matchesStatus
            }

            InvoiceListUiState(
                isLoading = false,
                invoices = filtered,
                customers = customers.filter { it.isActive },
                searchQuery = query,
                filterStatus = filter
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
