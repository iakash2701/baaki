package com.baaki.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalOutstandingPaise: Long = 0L,
    val overdueBalancePaise: Long = 0L,
    val overdueCount: Int = 0,
    val totalCollectionsPaise: Long = 0L,
    val invoiceCount: Int = 0,
    val customerCount: Int = 0,
    val recentInvoices: List<InvoiceWithCustomer> = emptyList(),
    val errorMessage: String? = null
)

data class InvoiceWithCustomer(
    val invoice: InvoiceEntity,
    val customerName: String,
    val outstandingPaise: Long
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
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

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments
    ) { customers, invoices, payments ->
        try {
            val customerMap = customers.associateBy { it.id }
            val paymentsByInvoice = payments.groupBy { it.invoiceId }

            var totalOutstanding = 0L
            var overdueBalance = 0L
            var overdueCount = 0
            val now = System.currentTimeMillis()

            val invoiceItems = invoices.map { inv ->
                val invPayments = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
                val outstanding = maxOf(0L, inv.totalAmount - invPayments)
                totalOutstanding += outstanding

                val isOverdue = inv.dueDate < now && outstanding > 0L && inv.status != "PAID"
                if (isOverdue) {
                    overdueBalance += outstanding
                    overdueCount++
                }

                InvoiceWithCustomer(
                    invoice = inv,
                    customerName = customerMap[inv.customerId]?.name ?: "Unknown Customer",
                    outstandingPaise = outstanding
                )
            }

            val totalCollections = payments.sumOf { it.amount }

            DashboardUiState(
                isLoading = false,
                totalOutstandingPaise = totalOutstanding,
                overdueBalancePaise = overdueBalance,
                overdueCount = overdueCount,
                totalCollectionsPaise = totalCollections,
                invoiceCount = invoices.size,
                customerCount = customers.size,
                recentInvoices = invoiceItems.sortedByDescending { it.invoice.issueDate }.take(5)
            )
        } catch (e: Exception) {
            DashboardUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Unknown error")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )
}
