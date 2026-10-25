package com.baaki.app

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.PaymentEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialDataModelTest {

    @Test
    fun testCustomerEntityCreationWithNewFields() {
        val now = System.currentTimeMillis()
        val customer = CustomerEntity(
            id = "c1",
            name = "Test Business",
            phone = "9876543210",
            email = "test@business.com",
            gstin = "07AABCG1234F1Z5",
            address = "New Delhi",
            openingBalance = 1000L,
            creditLimit = 50000L,
            notes = "Key enterprise account",
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        assertEquals("Test Business", customer.name)
        assertEquals("Key enterprise account", customer.notes)
        assertEquals(true, customer.isActive)
    }

    @Test
    fun testInvoiceEntityWithUpdatedAt() {
        val now = System.currentTimeMillis()
        val invoice = InvoiceEntity(
            id = "inv1",
            invoiceNumber = "SP/2026-27/001",
            customerId = "c1",
            issueDate = now,
            dueDate = now + 86400000L,
            subTotal = 10000L,
            taxTotal = 1800L,
            discountTotal = 0L,
            totalAmount = 11800L,
            status = "SENT",
            notes = null,
            createdAt = now,
            updatedAt = now
        )

        assertEquals("SP/2026-27/001", invoice.invoiceNumber)
        assertEquals(11800L, invoice.totalAmount)
        assertEquals(now, invoice.updatedAt)
    }

    @Test
    fun testPartialPaymentsAndOutstandingCalculation() {
        val totalAmount = 2900000L // ₹29,000.00
        val payments = listOf(
            PaymentEntity("p1", "inv1", "c1", 1000000L, 100L, "UPI", null, null, 100L),
            PaymentEntity("p2", "inv1", "c1", 500000L, 200L, "CASH", null, null, 200L)
        )

        val totalPaid = payments.sumOf { it.amount }
        val outstanding = maxOf(0L, totalAmount - totalPaid)

        assertEquals(1500000L, totalPaid)
        assertEquals(1400000L, outstanding) // ₹14,000.00
    }
}
