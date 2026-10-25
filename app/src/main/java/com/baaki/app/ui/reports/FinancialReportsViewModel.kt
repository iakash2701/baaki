package com.baaki.app.ui.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*
import java.util.Calendar

enum class ReportDateFilter {
    THIS_MONTH, LAST_30_DAYS, CURRENT_FY, ALL_TIME
}

data class CustomerStatementRow(
    val date: Long,
    val reference: String,
    val description: String,
    val debitPaise: Long,
    val creditPaise: Long,
    val runningBalancePaise: Long
)

data class FinancialReportsUiState(
    val isLoading: Boolean = true,
    val dateFilter: ReportDateFilter = ReportDateFilter.ALL_TIME,
    val totalInvoicedPaise: Long = 0L,
    val totalCollectedPaise: Long = 0L,
    val outstandingPaise: Long = 0L,
    val overduePaise: Long = 0L,
    val paidCount: Int = 0,
    val partialCount: Int = 0,
    val unpaidCount: Int = 0,
    val overdueCount: Int = 0,
    val customerOutstandings: List<Pair<CustomerEntity, Long>> = emptyList(),
    val errorMessage: String? = null
)

class FinancialReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BaakiRepository

    private val _dateFilter = MutableStateFlow(ReportDateFilter.ALL_TIME)
    val dateFilter: StateFlow<ReportDateFilter> = _dateFilter.asStateFlow()

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

    fun setDateFilter(filter: ReportDateFilter) {
        _dateFilter.value = filter
    }

    val uiState: StateFlow<FinancialReportsUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments,
        _dateFilter
    ) { customers, invoices, payments, filter ->
        try {
            val now = System.currentTimeMillis()
            val startTime = when (filter) {
                ReportDateFilter.THIS_MONTH -> {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.timeInMillis
                }
                ReportDateFilter.LAST_30_DAYS -> now - (30L * 24 * 60 * 60 * 1000)
                ReportDateFilter.CURRENT_FY -> {
                    val cal = Calendar.getInstance().apply { timeInMillis = now }
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH)
                    val startYear = if (month < Calendar.APRIL) year - 1 else year
                    Calendar.getInstance().apply {
                        set(startYear, Calendar.APRIL, 1, 0, 0, 0)
                    }.timeInMillis
                }
                ReportDateFilter.ALL_TIME -> 0L
            }

            val filteredInvoices = invoices.filter { it.issueDate >= startTime }
            val filteredPayments = payments.filter { it.paymentDate >= startTime }

            val totalInvoiced = filteredInvoices.sumOf { it.totalAmount }
            val totalCollected = filteredPayments.sumOf { it.amount }

            val paymentsByInvoice = payments.groupBy { it.invoiceId }
            var totalOutstanding = 0L
            var overdueBalance = 0L
            var paid = 0
            var partial = 0
            var unpaid = 0
            var overdue = 0

            invoices.forEach { inv ->
                val invPayments = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
                val outstanding = maxOf(0L, inv.totalAmount - invPayments)
                totalOutstanding += outstanding

                val isOverdue = inv.dueDate < now && outstanding > 0L && inv.status != "PAID"
                if (isOverdue) {
                    overdueBalance += outstanding
                    overdue++
                }

                when {
                    outstanding == 0L -> paid++
                    invPayments > 0L -> partial++
                    else -> unpaid++
                }
            }

            val invoicesByCust = invoices.groupBy { it.customerId }
            val paymentsByCust = payments.groupBy { it.customerId }

            val customerOutstandings = customers.map { cust ->
                val custInvoices = invoicesByCust[cust.id] ?: emptyList()
                val custPayments = paymentsByCust[cust.id] ?: emptyList()
                val invSum = custInvoices.sumOf { it.totalAmount }
                val paySum = custPayments.sumOf { it.amount }
                val out = maxOf(0L, cust.openingBalance + invSum - paySum)
                cust to out
            }.sortedByDescending { it.second }

            FinancialReportsUiState(
                isLoading = false,
                dateFilter = filter,
                totalInvoicedPaise = totalInvoiced,
                totalCollectedPaise = totalCollected,
                outstandingPaise = totalOutstanding,
                overduePaise = overdueBalance,
                paidCount = paid,
                partialCount = partial,
                unpaidCount = unpaid,
                overdueCount = overdue,
                customerOutstandings = customerOutstandings
            )
        } catch (e: Exception) {
            FinancialReportsUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error generating financial reports")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialReportsUiState(isLoading = true)
    )

    suspend fun getCustomerStatementRows(customerId: String): List<CustomerStatementRow> {
        val invoices = repository.allInvoices.first().filter { it.customerId == customerId }.sortedBy { it.issueDate }
        val payments = repository.allPayments.first().filter { it.customerId == customerId }.sortedBy { it.paymentDate }
        val customer = repository.getCustomerById(customerId).firstOrNull()

        val events = mutableListOf<Triple<Long, String, Pair<Long, Long>>>()
        var running = customer?.openingBalance ?: 0L

        invoices.forEach { inv ->
            events.add(Triple(inv.issueDate, "Invoice #${inv.invoiceNumber}", inv.totalAmount to 0L))
        }
        payments.forEach { pay ->
            events.add(Triple(pay.paymentDate, "Payment via ${pay.paymentMethod}", 0L to pay.amount))
        }

        val sortedEvents = events.sortedBy { it.first }
        val rows = mutableListOf<CustomerStatementRow>()

        sortedEvents.forEach { (time, desc, amts) ->
            val (debit, credit) = amts
            running = running + debit - credit
            rows.add(CustomerStatementRow(time, desc.split(" ").last(), desc, debit, credit, running))
        }

        return rows
    }
}
