package com.baaki.app

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.util.InvoiceShareFormatter
import org.junit.Assert.assertTrue
import org.junit.Test

class InvoiceShareFormatterTest {

    @Test
    fun testPartiallyPaidInvoiceSummaryGeneration() {
        val invoice = InvoiceEntity(
            id = "inv1",
            invoiceNumber = "SP/2025-26/001",
            customerId = "cust1",
            issueDate = System.currentTimeMillis(),
            dueDate = System.currentTimeMillis() + 86400000L,
            subTotal = 2500000L, // ₹25,000
            taxTotal = 450000L,  // ₹4,500
            discountTotal = 0L,
            totalAmount = 2950000L, // ₹29,500
            status = "SENT",
            notes = null,
            createdAt = System.currentTimeMillis()
        )

        val customer = CustomerEntity(
            id = "cust1",
            name = "Gupta Traders",
            phone = "9876543210",
            email = "gupta@example.com",
            gstin = "07AABCG1234F1Z5",
            address = "Delhi",
            openingBalance = 0L,
            creditLimit = 1000000L,
            createdAt = System.currentTimeMillis()
        )

        val lineItems = listOf(
            InvoiceLineItemEntity("item1", "inv1", "Boxes", "4819", 100.0, 25000L, 18.0, 2950000L)
        )

        val totalPaid = 1000000L // ₹10,000 paid
        val outstanding = 1950000L // ₹19,500 due
        val status = "PARTIALLY_PAID"

        val summary = InvoiceShareFormatter.generateSummary(invoice, customer, lineItems, totalPaid, outstanding, status)

        assertTrue(summary.contains("SP/2025-26/001"))
        assertTrue(summary.contains("Gupta Traders"))
        assertTrue(summary.contains("PARTIALLY_PAID"))
        assertTrue(summary.contains("₹29,500"))
        assertTrue(summary.contains("₹10,000"))
        assertTrue(summary.contains("₹19,500"))
    }

    @Test
    fun testFullyPaidInvoiceSummaryGeneration() {
        val invoice = InvoiceEntity(
            id = "inv2",
            invoiceNumber = "SP/2025-26/002",
            customerId = "cust2",
            issueDate = System.currentTimeMillis(),
            dueDate = System.currentTimeMillis() + 86400000L,
            subTotal = 1000000L, // ₹10,000
            taxTotal = 180000L,  // ₹1,800
            discountTotal = 0L,
            totalAmount = 1180000L, // ₹11,800
            status = "SENT",
            notes = null,
            createdAt = System.currentTimeMillis()
        )

        val customer = CustomerEntity(
            id = "cust2",
            name = "Apex Logistics",
            phone = "9123456789",
            email = null,
            gstin = null,
            address = "Mumbai",
            openingBalance = 0L,
            creditLimit = 1000000L,
            createdAt = System.currentTimeMillis()
        )

        val lineItems = listOf(
            InvoiceLineItemEntity("item2", "inv2", "Tape", "3919", 50.0, 20000L, 18.0, 1180000L)
        )

        val totalPaid = 1180000L // Fully paid
        val outstanding = 0L
        val status = "PAID"

        val summary = InvoiceShareFormatter.generateSummary(invoice, customer, lineItems, totalPaid, outstanding, status)

        assertTrue(summary.contains("SP/2025-26/002"))
        assertTrue(summary.contains("Apex Logistics"))
        assertTrue(summary.contains("PAID"))
        assertTrue(summary.contains("₹11,800"))
        assertTrue(summary.contains("₹0"))
    }
}
