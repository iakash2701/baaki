package com.baaki.app

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.util.PdfGenerator
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfDocumentTest {

    @Test
    fun testGenerateInvoicePdfFileCreation() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val now = System.currentTimeMillis()
        val invoice = InvoiceEntity("inv1", "SP/2026-27/001", "c1", now, now + 86400000L, 1000L, 180L, 0L, 1180L, "SENT", null, now)
        val customer = CustomerEntity("c1", "Test Customer", "9876543210", null, null, null, 0L, 1000L, now)
        val lineItems = listOf(InvoiceLineItemEntity("i1", "inv1", "Boxes", "4819", 10.0, 100L, 18.0, 1180L))
        val payments = emptyList<PaymentEntity>()

        val file = PdfGenerator.generateInvoicePdf(context, invoice, customer, lineItems, payments, 0L, 1180L)
        assertNotNull(file)
        assert(file.exists())
    }

    @Test
    fun testGenerateReceiptPdfFileCreation() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val now = System.currentTimeMillis()
        val payment = PaymentEntity("p1", "inv1", "c1", 500L, now, "UPI", "REF123", null, now)

        val file = PdfGenerator.generateReceiptPdf(context, payment, "SP/2026-27/001", "Test Customer", 680L)
        assertNotNull(file)
        assert(file.exists())
    }
}
