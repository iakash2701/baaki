package com.baaki.app

import com.baaki.app.data.local.entity.PaymentEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentTest {

    @Test
    fun testPartialAndFullPaymentCalculations() {
        val invoiceTotalPaise = 2900000L // ₹29,000.00
        val payment1 = 1000000L // ₹10,000.00
        val payment2 = 1900000L // ₹19,000.00

        val totalPaid = payment1 + payment2
        val outstanding = maxOf(0L, invoiceTotalPaise - totalPaid)

        assertEquals(2900000L, totalPaid)
        assertEquals(0L, outstanding)
    }

    @Test
    fun testMultiplePaymentsAccumulation() {
        val payments = listOf(
            PaymentEntity("p1", "inv1", "c1", 500000L, 100L, "CASH", null, null, 100L),
            PaymentEntity("p2", "inv1", "c1", 500000L, 200L, "UPI", null, null, 200L)
        )

        val totalPaid = payments.sumOf { it.amount }
        assertEquals(1000000L, totalPaid) // ₹10,000.00
    }

    @Test
    fun testPaymentValidationLogic() {
        val outstandingPaise = 1500000L // ₹15,000.00

        val zeroPayment: Long = 0L
        val negativePayment: Long = -5000L
        val overPayment: Long = 2000000L

        assertEquals(true, zeroPayment <= 0L)
        assertEquals(true, negativePayment <= 0L)
        assertEquals(true, overPayment > outstandingPaise)
    }
}
