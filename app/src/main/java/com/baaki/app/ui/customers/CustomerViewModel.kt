package com.baaki.app.ui.customers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baaki.app.data.local.BaakiDatabase
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.repository.BaakiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class CustomerFilter {
    ALL, ACTIVE, INACTIVE
}

data class CustomerWithSummary(
    val customer: CustomerEntity,
    val invoiceCount: Int,
    val outstandingPaise: Long
)

data class CustomerListUiState(
    val isLoading: Boolean = true,
    val customers: List<CustomerWithSummary> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: CustomerFilter = CustomerFilter.ALL,
    val errorMessage: String? = null
)

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BaakiRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow(CustomerFilter.ALL)
    val filterStatus: StateFlow<CustomerFilter> = _filterStatus.asStateFlow()

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

    fun setFilterStatus(filter: CustomerFilter) {
        _filterStatus.value = filter
    }

    val uiState: StateFlow<CustomerListUiState> = combine(
        repository.allCustomers,
        repository.allInvoices,
        repository.allPayments,
        _searchQuery,
        _filterStatus
    ) { customers, invoices, payments, query, filter ->
        try {
            val invoicesByCustomer = invoices.groupBy { it.customerId }
            val paymentsByCustomer = payments.groupBy { it.customerId }

            val filtered = customers.filter { cust ->
                val matchesQuery = query.isBlank() ||
                        cust.name.contains(query, ignoreCase = true) ||
                        cust.phone.contains(query, ignoreCase = true)

                val matchesStatus = when (filter) {
                    CustomerFilter.ALL -> true
                    CustomerFilter.ACTIVE -> cust.isActive
                    CustomerFilter.INACTIVE -> !cust.isActive
                }

                matchesQuery && matchesStatus
            }

            val list = filtered.map { cust ->
                val custInvoices = invoicesByCustomer[cust.id] ?: emptyList()
                val custPayments = paymentsByCustomer[cust.id] ?: emptyList()

                val totalInvoiced = custInvoices.sumOf { it.totalAmount }
                val totalPaid = custPayments.sumOf { it.amount }
                val outstanding = cust.openingBalance + totalInvoiced - totalPaid

                CustomerWithSummary(
                    customer = cust,
                    invoiceCount = custInvoices.size,
                    outstandingPaise = maxOf(0L, outstanding)
                )
            }

            CustomerListUiState(
                isLoading = false,
                customers = list,
                searchQuery = query,
                filterStatus = filter
            )
        } catch (e: Exception) {
            CustomerListUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading customers")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerListUiState(isLoading = true)
    )

    fun saveCustomer(
        id: String?,
        name: String,
        phone: String,
        email: String?,
        address: String?,
        creditLimitPaise: Long,
        notes: String?,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank() || phone.isBlank()) {
            onError("Business name and phone number are required.")
            return
        }
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                if (id.isNullOrBlank()) {
                    val customer = CustomerEntity(
                        id = UUID.randomUUID().toString(),
                        name = name.trim(),
                        phone = phone.trim(),
                        email = email?.takeIf { it.isNotBlank() }?.trim(),
                        gstin = null,
                        address = address?.takeIf { it.isNotBlank() }?.trim(),
                        openingBalance = 0L,
                        creditLimit = creditLimitPaise,
                        notes = notes?.takeIf { it.isNotBlank() }?.trim(),
                        isActive = isActive,
                        createdAt = now,
                        updatedAt = now
                    )
                    repository.insertCustomer(customer)
                } else {
                    val existing = repository.getCustomerById(id).firstOrNull()
                    val customer = CustomerEntity(
                        id = id,
                        name = name.trim(),
                        phone = phone.trim(),
                        email = email?.takeIf { it.isNotBlank() }?.trim(),
                        gstin = existing?.gstin,
                        address = address?.takeIf { it.isNotBlank() }?.trim(),
                        openingBalance = existing?.openingBalance ?: 0L,
                        creditLimit = creditLimitPaise,
                        notes = notes?.takeIf { it.isNotBlank() }?.trim(),
                        isActive = isActive,
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now
                    )
                    repository.updateCustomer(customer)
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to save customer")
            }
        }
    }

    fun deactivateCustomer(customerId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val existing = repository.getCustomerById(customerId).firstOrNull()
                if (existing != null) {
                    val updated = existing.copy(isActive = false, updatedAt = System.currentTimeMillis())
                    repository.updateCustomer(updated)
                    onSuccess()
                }
            } catch (_: Exception) {}
        }
    }
}
