package com.baaki.app

import com.baaki.app.util.FinancialYearUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class InvoiceNumberingTest {

    @Test
    fun testInvoiceNumberFormatForFinancialYear() {
        val calOct2026 = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 19, 12, 0, 0) }
        val fy = FinancialYearUtils.getFinancialYear(calOct2026.timeInMillis)
        assertEquals("2026-27", fy)

        val invoiceNumber = "SP/$fy/001"
        assertEquals("SP/2026-27/001", invoiceNumber)
    }

    @Test
    fun testInvoiceNumberAcrossBoundary() {
        val calMar31 = Calendar.getInstance().apply { set(2027, Calendar.MARCH, 31, 12, 0, 0) }
        val fyMar = FinancialYearUtils.getFinancialYear(calMar31.timeInMillis)
        assertEquals("2026-27", fyMar)

        val calApr1 = Calendar.getInstance().apply { set(2027, Calendar.APRIL, 1, 12, 0, 0) }
        val fyApr = FinancialYearUtils.getFinancialYear(calApr1.timeInMillis)
        assertEquals("2027-28", fyApr)
    }
}
