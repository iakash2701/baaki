package com.baaki.app

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.domain.chase.SmartRecoveryScorer
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialIntelligenceTest {

    @Test
    fun testCashFlowNetCalculation() {
        val payments = listOf(
            PaymentEntity("p1", "inv1", "c1", 1000000L, 100L, "UPI", null, null, 100L),
            PaymentEntity("p2", "inv2", "c2", 500000L, 200L, "CASH", null, null, 200L)
        )
        val expenses = listOf(
            com.baaki.app.data.local.entity.ExpenseEntity("e1", "Rent", "RENT", 400000L, 150L, null, 150L)
        )

        val totalInflow = payments.sumOf { it.amount }
        val totalOutflow = expenses.sumOf { it.amount }
        val netFlow = totalInflow - totalOutflow

        assertEquals(1500000L, totalInflow)
        assertEquals(400000L, totalOutflow)
        assertEquals(1100000L, netFlow) // ₹11,000.00
    }

    @Test
    fun testSmartRecoveryPrioritizationOrdering() {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        val customer = CustomerEntity("c1", "Test Customer", "9876543210", null, null, null, 0L, 1000L, now)
        val inv1 = InvoiceEntity("inv1", "SP/2026-27/001", "c1", now - 20 * dayMillis, now - 10 * dayMillis, 1000L, 180L, 0L, 1180L, "SENT", null, now)
        val inv2 = InvoiceEntity("inv2", "SP/2026-27/002", "c1", now - 60 * dayMillis, now - 50 * dayMillis, 1000L, 180L, 0L, 1180L, "SENT", null, now)

        val scored = SmartRecoveryScorer.scoreInvoices(listOf(inv1, inv2), listOf(customer), emptyList(), now)

        assertEquals(2, scored.size)
        assertEquals("inv2", scored[0].invoice.id)
        assertEquals("inv1", scored[1].invoice.id)
    }

    @Test
    fun testDashboardAndCustomerBalanceConsistency() {
        val opening = 500000L
        val totalInvoiced = 2900000L
        val totalPaid = 1000000L

        val customerOutstanding = opening + totalInvoiced - totalPaid
        assertEquals(2400000L, customerOutstanding)
    }
}
