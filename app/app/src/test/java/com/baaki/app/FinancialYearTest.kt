package com.baaki.app

import com.baaki.app.util.FinancialYearUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class FinancialYearTest {

    @Test
    fun testFinancialYearBoundaries() {
        // March 31, 2026 -> 2025-26
        val calMar = Calendar.getInstance().apply { set(2026, Calendar.MARCH, 31, 12, 0, 0) }
        assertEquals("2025-26", FinancialYearUtils.getFinancialYear(calMar.timeInMillis))

        // April 1, 2026 -> 2026-27
        val calApr = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 1, 12, 0, 0) }
        assertEquals("2026-27", FinancialYearUtils.getFinancialYear(calApr.timeInMillis))

        // March 31, 2027 -> 2026-27
        val calMar27 = Calendar.getInstance().apply { set(2027, Calendar.MARCH, 31, 12, 0, 0) }
        assertEquals("2026-27", FinancialYearUtils.getFinancialYear(calMar27.timeInMillis))

        // April 1, 2027 -> 2027-28
        val calApr27 = Calendar.getInstance().apply { set(2027, Calendar.APRIL, 1, 12, 0, 0) }
        assertEquals("2027-28", FinancialYearUtils.getFinancialYear(calApr27.timeInMillis))
    }
}
