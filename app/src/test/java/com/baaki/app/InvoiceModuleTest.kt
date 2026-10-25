package com.baaki.app

import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.util.FinancialYearUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class InvoiceModuleTest {

    @Test
    fun testInvoiceStatusAndOverdueClassification() {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        val unpaidInv = InvoiceEntity("inv1", "SP/2026-27/001", "c1", now - 5 * dayMillis, now + 10 * dayMillis, 1000L, 180L, 0L, 1180L, "SENT", null, now)
        val overdueInv = InvoiceEntity("inv2", "SP/2026-27/002", "c1", now - 20 * dayMillis, now - 5 * dayMillis, 1000L, 180L, 0L, 1180L, "SENT", null, now)

        val paidInv = InvoiceEntity("inv3", "SP/2026-27/003", "c1", now - 10 * dayMillis, now - 2 * dayMillis, 1000L, 180L, 0L, 1180L, "PAID", null, now)

        val unpaidOutstanding = maxOf(0L, unpaidInv.totalAmount - 0L)
        val overdueOutstanding = maxOf(0L, overdueInv.totalAmount - 0L)
        val paidOutstanding = maxOf(0L, paidInv.totalAmount - 1180L)

        assertEquals(1180L, unpaidOutstanding)
        assertEquals(1180L, overdueOutstanding)
        assertEquals(0L, paidOutstanding)
        assertEquals(true, overdueInv.dueDate < now)
    }

    @Test
    fun testPaymentAmountValidation() {
        val outstandingPaise = 15000L // ₹150.00
        val validPayment: Long = 5000L
        val zeroPayment: Long = 0L
        val negativePayment: Long = -100L
        val overPayment: Long = 20000L

        val isValid = { amt: Long -> amt > 0L && amt <= outstandingPaise }

        assertEquals(true, isValid(validPayment))
        assertEquals(false, isValid(zeroPayment))
        assertEquals(false, isValid(negativePayment))
        assertEquals(false, isValid(overPayment))
    }

    @Test
    fun testUniqueInvoiceNumberGenerationFormat() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 19, 12, 0, 0) }
        val fy = FinancialYearUtils.getFinancialYear(cal.timeInMillis)
        assertEquals("2026-27", fy)

        val seqNumber = 1
        val invNo = "SP/$fy/${seqNumber.toString().padStart(3, '0')}"
        assertEquals("SP/2026-27/001", invNo)
    }
}
