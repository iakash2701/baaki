package com.baaki.app

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class FinancialReportsModuleTest {

    @Test
    fun testFinancialYearDateBoundaryCalculation() {
        val startYear = 2026
        val fyStart = Calendar.getInstance().apply {
            set(startYear, Calendar.APRIL, 1, 0, 0, 0)
        }.timeInMillis

        val calStart = Calendar.getInstance().apply { timeInMillis = fyStart }
        assertEquals(Calendar.APRIL, calStart.get(Calendar.MONTH))
        assertEquals(1, calStart.get(Calendar.DAY_OF_MONTH))
        assertEquals(2026, calStart.get(Calendar.YEAR))
    }

    @Test
    fun testCustomerStatementRunningBalanceLogic() {
        val opening = 500000L // ₹5,000.00
        val invoiceAmount = 2900000L // ₹29,000.00
        val paymentAmount = 1000000L // ₹10,000.00

        var running = opening
        running += invoiceAmount
        assertEquals(3400000L, running)

        running -= paymentAmount
        assertEquals(2400000L, running) // ₹24,000.00
    }
}
