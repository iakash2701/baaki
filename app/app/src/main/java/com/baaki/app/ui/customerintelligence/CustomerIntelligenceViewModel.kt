package com.baaki.app.ui.customerintelligence

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*

data class CustomerInsight(
    val customer: com.baaki.app.data.local.entity.CustomerEntity,
    val totalInvoicedPaise: Long,
    val totalPaidPaise: Long,
    val outstandingPaise: Long,
    val unpaidInvoiceCount: Int,
    val averagePaymentDelayDays: Double?
)

data class CustomerIntelligenceUiState(
    val isLoading: Boolean = true,
    val insights: List<CustomerInsight> = emptyList(),
    val errorMessage: String? = null
)

class CustomerIntelligenceViewModel(application: Application) : AndroidViewModel(application) {
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

    val uiState: StateFlow<CustomerIntelligenceUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments
    ) { customers, invoices, payments ->
        try {
            val invoicesByCustomer = invoices.groupBy { it.customerId }
            val paymentsByCustomer = payments.groupBy { it.customerId }
            val paymentsByInvoice = payments.groupBy { it.invoiceId }

            val dayMillis = 24 * 60 * 60 * 1000L

            val insights = customers.map { cust ->
                val custInvoices = invoicesByCustomer[cust.id] ?: emptyList()
                val custPayments = paymentsByCustomer[cust.id] ?: emptyList()

                val totalInvoiced = custInvoices.sumOf { it.totalAmount }
                val totalPaid = custPayments.sumOf { it.amount }
                val outstanding = maxOf(0L, cust.openingBalance + totalInvoiced - totalPaid)
                val unpaidCount = custInvoices.count { inv ->
                    val paid = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
                    maxOf(0L, inv.totalAmount - paid) > 0L && inv.status != "PAID"
                }

                val delays = mutableListOf<Long>()
                custInvoices.forEach { inv ->
                    val invPayments = paymentsByInvoice[inv.id] ?: emptyList()
                    val paidForInv = invPayments.sumOf { it.amount }
                    if (paidForInv >= inv.totalAmount && invPayments.isNotEmpty()) {
                        val lastPaymentDate = invPayments.maxOf { it.paymentDate }
                        val delay = (lastPaymentDate - inv.dueDate) / dayMillis
                        delays.add(delay)
                    }
                }

                val avgDelay = if (delays.size >= 2) delays.average() else null

                CustomerInsight(
                    customer = cust,
                    totalInvoicedPaise = totalInvoiced,
                    totalPaidPaise = totalPaid,
                    outstandingPaise = outstanding,
                    unpaidInvoiceCount = unpaidCount,
                    averagePaymentDelayDays = avgDelay
                )
            }.sortedByDescending { it.outstandingPaise }

            CustomerIntelligenceUiState(
                isLoading = false,
                insights = insights
            )
        } catch (e: Exception) {
            CustomerIntelligenceUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading customer intelligence")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerIntelligenceUiState(isLoading = true)
    )
}
