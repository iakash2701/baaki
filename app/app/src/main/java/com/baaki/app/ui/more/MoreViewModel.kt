package com.baaki.app.ui.more

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.ActivityEventEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*

data class MoreUiState(
    val isLoading: Boolean = true,
    val activities: List<ActivityEventEntity> = emptyList(),
    val errorMessage: String? = null
)

class MoreViewModel(application: Application) : AndroidViewModel(application) {
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

    val uiState: StateFlow<MoreUiState> = repository.allActivities.map { activities ->
        MoreUiState(
            isLoading = false,
            activities = activities
        )
    }.catch { e ->
        emit(MoreUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading activity"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MoreUiState(isLoading = true)
    )
}
