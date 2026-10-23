package com.baaki.app.ui.smartrecovery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.repository.BaakiRepository
import com.baaki.app.domain.chase.RecoveryItem
import com.baaki.app.domain.chase.SmartRecoveryScorer
import kotlinx.coroutines.flow.*

data class SmartRecoveryUiState(
    val isLoading: Boolean = true,
    val recoveryItems: List<RecoveryItem> = emptyList(),
    val errorMessage: String? = null
)

class SmartRecoveryViewModel(application: Application) : AndroidViewModel(application) {
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

    val uiState: StateFlow<SmartRecoveryUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments
    ) { customers, invoices, payments ->
        try {
            val items = SmartRecoveryScorer.scoreInvoices(invoices, customers, payments)
            SmartRecoveryUiState(
                isLoading = false,
                recoveryItems = items
            )
        } catch (e: Exception) {
            SmartRecoveryUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading recovery queue")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SmartRecoveryUiState(isLoading = true)
    )
}
