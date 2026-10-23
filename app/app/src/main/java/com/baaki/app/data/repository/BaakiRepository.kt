package com.baaki.app.data.repository

import com.baaki.app.data.local.dao.*
import com.baaki.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class BaakiRepository(
    private val customerDao: CustomerDao,
    private val invoiceDao: InvoiceDao,
    private val paymentDao: PaymentDao,
    private val expenseDao: ExpenseDao,
    private val activityDao: ActivityEventDao
) {
    // Customers
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

    fun getCustomerById(id: String): Flow<CustomerEntity?> = customerDao.getCustomerById(id)

    fun getCustomerWithDetails(id: String): Flow<CustomerWithDetails?> = customerDao.getCustomerWithDetails(id)

    suspend fun insertCustomer(customer: CustomerEntity) {
        customerDao.insertCustomer(customer)
        activityDao.insertActivity(
            ActivityEventEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = "Customer Added",
                description = "Added customer ${customer.name}",
                eventType = "CUSTOMER_ADDED",
                timestamp = System.currentTimeMillis(),
                relatedId = customer.id
            )
        )
    }

    suspend fun updateCustomer(customer: CustomerEntity) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: CustomerEntity) {
        customerDao.deleteCustomer(customer)
    }

    // Invoices
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    fun getInvoicesForCustomer(customerId: String): Flow<List<InvoiceEntity>> = invoiceDao.getInvoicesForCustomer(customerId)

    fun getInvoiceWithLineItems(invoiceId: String): Flow<InvoiceWithLineItems?> = invoiceDao.getInvoiceWithLineItems(invoiceId)

    suspend fun createInvoiceWithItems(invoice: InvoiceEntity, items: List<InvoiceLineItemEntity>) {
        invoiceDao.insertInvoiceWithItems(invoice, items)
        activityDao.insertActivity(
            ActivityEventEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = "Invoice Created",
                description = "Created invoice ${invoice.invoiceNumber} for total ₹${invoice.totalAmount / 100.0}",
                eventType = "INVOICE_CREATED",
                timestamp = System.currentTimeMillis(),
                relatedId = invoice.id
            )
        )
    }

    // Payments
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()

    fun getPaymentsForCustomer(customerId: String): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForCustomer(customerId)

    suspend fun recordPayment(payment: PaymentEntity) {
        paymentDao.insertPayment(payment)
        activityDao.insertActivity(
            ActivityEventEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = "Payment Received",
                description = "Recorded payment of ₹${payment.amount / 100.0} via ${payment.paymentMethod}",
                eventType = "PAYMENT_RECEIVED",
                timestamp = System.currentTimeMillis(),
                relatedId = payment.id
            )
        )
    }

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    suspend fun recordExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
        activityDao.insertActivity(
            ActivityEventEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = "Expense Recorded",
                description = "Recorded expense ${expense.title} of ₹${expense.amount / 100.0}",
                eventType = "EXPENSE_RECORDED",
                timestamp = System.currentTimeMillis(),
                relatedId = expense.id
            )
        )
    }

    // Activities
    val allActivities: Flow<List<ActivityEventEntity>> = activityDao.getAllActivities()
}
