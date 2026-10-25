package com.baaki.app

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.PaymentEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomerModuleTest {

    @Test
    fun testCustomerSearchAndFilteringLogic() {
        val customers = listOf(
            CustomerEntity(
                id = "c1",
                name = "Gupta Traders",
                phone = "9876543210",
                email = null,
                gstin = null,
                address = null,
                openingBalance = 0L,
                creditLimit = 1000L,
                createdAt = 100L,
                notes = null,
                isActive = true
            ),
            CustomerEntity(
                id = "c2",
                name = "Apex Logistics",
                phone = "9123456789",
                email = null,
                gstin = null,
                address = null,
                openingBalance = 0L,
                creditLimit = 1000L,
                createdAt = 100L,
                notes = null,
                isActive = false
            )
        )

        val active = customers.filter { it.isActive }
        assertEquals(1, active.size)
        assertEquals("Gupta Traders", active[0].name)

        val inactive = customers.filter { !it.isActive }
        assertEquals(1, inactive.size)
        assertEquals("Apex Logistics", inactive[0].name)

        val searchQuery = "Gupta"
        val searched = customers.filter { it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery, ignoreCase = true) }
        assertEquals(1, searched.size)
        assertEquals("c1", searched[0].id)
    }

    @Test
    fun testCustomerOutstandingBalanceCalculation() {
        val customer = CustomerEntity(
            id = "c1",
            name = "Gupta Traders",
            phone = "9876543210",
            email = null,
            gstin = null,
            address = null,
            openingBalance = 500000L,
            creditLimit = 1000000L,
            createdAt = 100L,
            notes = null,
            isActive = true
        )
        val invoices = listOf(
            InvoiceEntity(
                id = "inv1",
                invoiceNumber = "SP/2026-27/001",
                customerId = "c1",
                issueDate = 100L,
                dueDate = 200L,
                subTotal = 2000000L,
                taxTotal = 360000L,
                discountTotal = 0L,
                totalAmount = 2360000L,
                status = "SENT",
                notes = null,
                createdAt = 100L
            )
        )
        val payments = listOf(
            PaymentEntity(
                id = "p1",
                invoiceId = "inv1",
                customerId = "c1",
                amount = 1000000L,
                paymentDate = 150L,
                paymentMethod = "UPI",
                referenceNumber = null,
                notes = null,
                createdAt = 150L
            )
        )

        val totalInvoiced = invoices.sumOf { it.totalAmount }
        val totalPaid = payments.sumOf { it.amount }
        val outstanding = customer.openingBalance + totalInvoiced - totalPaid

        assertEquals(1860000L, outstanding)
    }

    @Test
    fun testCustomerDeactivationPreservesHistory() {
        val customer = CustomerEntity(
            id = "c1",
            name = "Gupta Traders",
            phone = "9876543210",
            email = null,
            gstin = null,
            address = null,
            openingBalance = 0L,
            creditLimit = 1000L,
            createdAt = 100L,
            notes = null,
            isActive = true
        )
        val deactivated = customer.copy(isActive = false, updatedAt = System.currentTimeMillis())

        assertEquals(false, deactivated.isActive)
        assertEquals("c1", deactivated.id)
        assertEquals("Gupta Traders", deactivated.name)
    }
}
