package com.baaki.app

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.domain.chase.PriorityLevel
import com.baaki.app.domain.chase.SmartRecoveryScorer
import org.junit.Assert.assertEquals
import org.junit.Test

class SmartRecoveryTest {

    @Test
    fun testSmartRecoveryScoringExcludesPaidInvoices() {
        val now = System.currentTimeMillis()
        val customer = CustomerEntity("c1", "Test Customer", "9999999999", null, null, null, 0L, 0L, now)
        val invoice = InvoiceEntity("inv1", "SP/2026-27/001", "c1", now - 100000L, now - 50000L, 1000L, 180L, 0L, 1180L, "PAID", null, now)

        val items = SmartRecoveryScorer.scoreInvoices(listOf(invoice), listOf(customer), emptyList(), now)
        assertEquals(0, items.size)
    }

    @Test
    fun testSmartRecoveryPriorityLevels() {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        val customer = CustomerEntity("c1", "Test Customer", "9999999999", null, null, null, 0L, 0L, now)
        // Overdue by 40 days -> high priority
        val invoice = InvoiceEntity("inv1", "SP/2026-27/001", "c1", now - 50 * dayMillis, now - 40 * dayMillis, 1000000L, 180000L, 0L, 1180000L, "SENT", null, now)

        val items = SmartRecoveryScorer.scoreInvoices(listOf(invoice), listOf(customer), emptyList(), now)
        assertEquals(1, items.size)
        assertEquals(PriorityLevel.HIGH, items[0].priorityLevel)
    }
}
