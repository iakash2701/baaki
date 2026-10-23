package com.baaki.app.ui.cashflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.ExpenseEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*

data class CashFlowUiState(
    val isLoading: Boolean = true,
    val totalInflowPaise: Long = 0L,
    val totalOutflowPaise: Long = 0L,
    val netCashFlowPaise: Long = 0L,
    val payments: List<PaymentEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val errorMessage: String? = null
)

class CashFlowViewModel(application: Application) : AndroidViewModel(application) {
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

    val uiState: StateFlow<CashFlowUiState> = combine(
        repository.allPayments,
        repository.allExpenses
    ) { payments, expenses ->
        try {
            val totalInflow = payments.sumOf { it.amount }
            val totalOutflow = expenses.sumOf { it.amount }
            val netFlow = totalInflow - totalOutflow

            CashFlowUiState(
                isLoading = false,
                totalInflowPaise = totalInflow,
                totalOutflowPaise = totalOutflow,
                netCashFlowPaise = netFlow,
                payments = payments,
                expenses = expenses
            )
        } catch (e: Exception) {
            CashFlowUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading cash flow")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CashFlowUiState(isLoading = true)
    )
}
