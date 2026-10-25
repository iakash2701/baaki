package com.baaki.app

import com.baaki.app.ui.invoices.LineItemInput
import org.junit.Assert.assertEquals
import org.junit.Test

class InvoiceCalculationTest {

    @Test
    fun testGstAndTotalCalculation() {
        // Line item: 100 quantity @ ₹50.00 each = ₹5000.00 (500000 paise), 18% GST
        val quantity = 100.0
        val unitPricePaise = 5000L // ₹50.00
        val taxPercent = 18.0

        val lineAmount = Math.round(quantity * unitPricePaise) // 500000 paise
        val lineTax = Math.round(lineAmount * (taxPercent / 100.0)) // 90000 paise (₹900.00)
        val totalLineAmount = lineAmount + lineTax // 590000 paise (₹5,900.00)

        assertEquals(500000L, lineAmount)
        assertEquals(90000L, lineTax)
        assertEquals(590000L, totalLineAmount)
    }

    @Test
    fun testMultipleLineItemsTotalCalculation() {
        val items = listOf(
            LineItemInput("Box A", null, 10.0, 1000L, 18.0), // 10 * 10 = 100.00 + 18% (18.00) = 118.00 (11800 paise)
            LineItemInput("Box B", null, 5.0, 2000L, 12.0)   // 5 * 20 = 100.00 + 12% (12.00) = 112.00 (11200 paise)
        )

        var subTotal = 0L
        var taxTotal = 0L

        items.forEach { item ->
            val lineAmt = Math.round(item.quantity * item.unitPricePaise)
            val lineTax = Math.round(lineAmt * (item.taxPercent / 100.0))
            subTotal += lineAmt
            taxTotal += lineTax
        }

        val grandTotal = subTotal + taxTotal
        assertEquals(20000L, subTotal) // ₹200.00
        assertEquals(3000L, taxTotal)  // ₹30.00 (18 + 12)
        assertEquals(23000L, grandTotal) // ₹230.00
    }
}
