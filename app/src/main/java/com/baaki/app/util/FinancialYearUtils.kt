package com.baaki.app.util

import java.util.Calendar

object FinancialYearUtils {
    fun getFinancialYear(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return if (month < Calendar.APRIL) {
            val startYear = year - 1
            val endYearSuffix = (year % 100).toString().padStart(2, '0')
            "$startYear-$endYearSuffix"
        } else {
            val startYear = year
            val endYear = year + 1
            val endYearSuffix = (endYear % 100).toString().padStart(2, '0')
            "$startYear-$endYearSuffix"
        }
    }
}
