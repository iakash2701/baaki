package com.baaki.app.ui.cashflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.ExpenseEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

enum class CashFlowDateRange {
    ALL, THIS_MONTH, LAST_30_DAYS
}

data class CashFlowUiState(
    val isLoading: Boolean = true,
    val totalInflowPaise: Long = 0L,
    val totalOutflowPaise: Long = 0L,
    val netCashFlowPaise: Long = 0L,
    val payments: List<PaymentEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val dateRange: CashFlowDateRange = CashFlowDateRange.ALL,
    val errorMessage: String? = null
)

class CashFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BaakiRepository

    private val _dateRange = MutableStateFlow(CashFlowDateRange.ALL)
    val dateRange: StateFlow<CashFlowDateRange> = _dateRange.asStateFlow()

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

    fun setDateRange(range: CashFlowDateRange) {
        _dateRange.value = range
    }

    val uiState: StateFlow<CashFlowUiState> = combine(
        repository.allPayments,
        repository.allExpenses,
        _dateRange
    ) { payments, expenses, range ->
        try {
            val now = System.currentTimeMillis()
            val filteredPayments = when (range) {
                CashFlowDateRange.ALL -> payments
                CashFlowDateRange.THIS_MONTH -> {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }
                    val startOfMonth = cal.timeInMillis
                    payments.filter { it.paymentDate >= startOfMonth }
                }
                CashFlowDateRange.LAST_30_DAYS -> {
                    val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
                    payments.filter { it.paymentDate >= thirtyDaysAgo }
                }
            }

            val filteredExpenses = when (range) {
                CashFlowDateRange.ALL -> expenses
                CashFlowDateRange.THIS_MONTH -> {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }
                    val startOfMonth = cal.timeInMillis
                    expenses.filter { it.expenseDate >= startOfMonth }
                }
                CashFlowDateRange.LAST_30_DAYS -> {
                    val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
                    expenses.filter { it.expenseDate >= thirtyDaysAgo }
                }
            }

            val totalInflow = filteredPayments.sumOf { it.amount }
            val totalOutflow = filteredExpenses.sumOf { it.amount }
            val netFlow = totalInflow - totalOutflow

            CashFlowUiState(
                isLoading = false,
                totalInflowPaise = totalInflow,
                totalOutflowPaise = totalOutflow,
                netCashFlowPaise = netFlow,
                payments = filteredPayments,
                expenses = filteredExpenses,
                dateRange = range
            )
        } catch (e: Exception) {
            CashFlowUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading cash flow")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CashFlowUiState(isLoading = true)
    )

    fun addExpense(
        title: String,
        category: String,
        amountPaise: Long,
        notes: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (title.isBlank() || amountPaise <= 0L) {
            onError("Expense title and positive amount are required.")
            return
        }
        viewModelScope.launch {
            try {
                val expense = ExpenseEntity(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    category = category,
                    amount = amountPaise,
                    expenseDate = System.currentTimeMillis(),
                    notes = notes?.takeIf { it.isNotBlank() }?.trim(),
                    createdAt = System.currentTimeMillis()
                )
                repository.recordExpense(expense)
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to record expense")
            }
        }
    }
}
