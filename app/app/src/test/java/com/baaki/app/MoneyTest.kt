package com.baaki.app

import com.baaki.app.domain.money.*
import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {

    @Test
    fun testMoneyArithmetic() {
        val m1 = 15000L.paise() // ₹150.00
        val m2 = 5000L.paise()  // ₹50.00

        assertEquals(20000L, (m1 + m2).paise)
        assertEquals(10000L, (m1 - m2).paise)
        assertEquals(30000L, (m1 * 2).paise)
    }

    @Test
    fun testMoneyFormattingIndian() {
        val amount = 123456789L.paise() // ₹12,34,567.89
        assertEquals("₹12,34,567.89", amount.formatINR())

        val roundAmount = 100000000L.paise() // ₹10,00,000
        assertEquals("₹10,00,000", roundAmount.formatINR())
    }

    @Test
    fun testCompactFormatting() {
        val lakhs = 250000000L.paise() // ₹25,00,000 -> ₹25.0L
        assertEquals("₹25.0L", lakhs.formatCompact())

        val crores = 1500000000L.paise() // ₹1,50,00,000 -> ₹1.5Cr
        assertEquals("₹1.5Cr", crores.formatCompact())
    }
}
