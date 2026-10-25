package com.baaki.app

import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.PaymentEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardCalculationTest {

    @Test
    fun testOutstandingBalanceCalculation() {
        val invoiceTotal = 2900000L // ₹29,000.00 in paise
        val paymentAmount = 1000000L // ₹10,000.00 in paise

        val outstanding = maxOf(0L, invoiceTotal - paymentAmount)
        assertEquals(1900000L, outstanding) // ₹19,000.00
    }

    @Test
    fun testConfirmedCollectionsSumming() {
        val payments = listOf(
            PaymentEntity("p1", "inv1", "c1", 1000000L, 100L, "UPI", null, null, 100L),
            PaymentEntity("p2", "inv2", "c2", 500000L, 100L, "CASH", null, null, 100L)
        )

        val totalCollections = payments.sumOf { it.amount }
        assertEquals(1500000L, totalCollections) // ₹15,000.00
    }
}
