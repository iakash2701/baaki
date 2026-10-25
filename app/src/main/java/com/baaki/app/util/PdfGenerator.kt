package com.baaki.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.data.local.entity.PaymentEntity
import com.baaki.app.domain.money.paise
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateInvoicePdf(
        context: Context,
        invoice: InvoiceEntity,
        customer: CustomerEntity?,
        lineItems: List<InvoiceLineItemEntity>,
        payments: List<PaymentEntity>,
        totalPaid: Long,
        outstanding: Long
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { color = Color.BLACK; textSize = 12f }
        val titlePaint = Paint().apply { color = Color.rgb(27, 77, 62); textSize = 20f; isFakeBoldText = true }
        val boldPaint = Paint().apply { color = Color.BLACK; textSize = 12f; isFakeBoldText = true }
        val grayPaint = Paint().apply { color = Color.GRAY; textSize = 10f }

        var y = 40f
        val leftMargin = 40f

        canvas.drawText("SHARMA PACKAGING 🇮🇳", leftMargin, y, titlePaint)
        y += 20f
        canvas.drawText("Offline-first Business Solutions • GSTIN: 07AAAAA0000A1Z5", leftMargin, y, grayPaint)
        y += 30f

        canvas.drawText("INVOICE: ${invoice.invoiceNumber}", leftMargin, y, boldPaint)
        canvas.drawText("Status: ${invoice.status}", 400f, y, boldPaint)
        y += 20f
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        canvas.drawText("Issue Date: ${dateFormat.format(Date(invoice.issueDate))}", leftMargin, y, paint)
        canvas.drawText("Due Date: ${dateFormat.format(Date(invoice.dueDate))}", 400f, y, paint)
        y += 30f

        canvas.drawText("Billed To:", leftMargin, y, boldPaint)
        y += 18f
        canvas.drawText("Name: ${customer?.name ?: "Unknown Customer"}", leftMargin, y, paint)
        y += 16f
        if (!customer?.phone.isNullOrBlank()) {
            canvas.drawText("Phone: ${customer.phone}", leftMargin, y, paint)
            y += 16f
        }
        if (!customer?.address.isNullOrBlank()) {
            canvas.drawText("Address: ${customer.address}", leftMargin, y, paint)
            y += 16f
        }
        y += 15f

        canvas.drawText("Description", leftMargin, y, boldPaint)
        canvas.drawText("Qty", 320f, y, boldPaint)
        canvas.drawText("Price", 380f, y, boldPaint)
        canvas.drawText("Amount", 480f, y, boldPaint)
        y += 6f
        canvas.drawLine(leftMargin, y, 555f, y, paint)
        y += 18f

        lineItems.forEach { item ->
            canvas.drawText(item.description, leftMargin, y, paint)
            canvas.drawText(item.quantity.toString(), 320f, y, paint)
            canvas.drawText(item.unitPrice.paise().formatINR(), 380f, y, paint)
            canvas.drawText(item.amount.paise().formatINR(), 480f, y, paint)
            y += 20f
        }

        y += 10f
        canvas.drawLine(leftMargin, y, 555f, y, paint)
        y += 25f

        canvas.drawText("Subtotal:", 350f, y, paint)
        canvas.drawText(invoice.subTotal.paise().formatINR(), 480f, y, paint)
        y += 20f
        canvas.drawText("GST Total:", 350f, y, paint)
        canvas.drawText(invoice.taxTotal.paise().formatINR(), 480f, y, paint)
        y += 22f
        canvas.drawText("Grand Total:", 350f, y, boldPaint)
        canvas.drawText(invoice.totalAmount.paise().formatINR(), 480f, y, boldPaint)
        y += 20f
        canvas.drawText("Paid Received:", 350f, y, paint)
        canvas.drawText(totalPaid.paise().formatINR(), 480f, y, paint)
        y += 22f
        canvas.drawText("Outstanding Due:", 350f, y, boldPaint)
        canvas.drawText(outstanding.paise().formatINR(), 480f, y, boldPaint)

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "Invoice_${invoice.invoiceNumber.replace('/', '_')}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    fun generateReceiptPdf(
        context: Context,
        payment: PaymentEntity,
        invoiceNumber: String,
        customerName: String,
        remainingBalance: Long
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { color = Color.BLACK; textSize = 12f }
        val titlePaint = Paint().apply { color = Color.rgb(27, 77, 62); textSize = 20f; isFakeBoldText = true }
        val boldPaint = Paint().apply { color = Color.BLACK; textSize = 12f; isFakeBoldText = true }

        var y = 50f
        val leftMargin = 50f

        canvas.drawText("SHARMA PACKAGING - PAYMENT RECEIPT", leftMargin, y, titlePaint)
        y += 40f

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        canvas.drawText("Receipt ID: ${payment.id.take(8).uppercase()}", leftMargin, y, boldPaint)
        y += 25f
        canvas.drawText("Payment Date: ${dateFormat.format(Date(payment.paymentDate))}", leftMargin, y, paint)
        y += 20f
        canvas.drawText("Customer Name: $customerName", leftMargin, y, paint)
        y += 20f
        canvas.drawText("Invoice Number: $invoiceNumber", leftMargin, y, paint)
        y += 20f
        canvas.drawText("Payment Method: ${payment.paymentMethod}", leftMargin, y, paint)
        y += 20f
        if (!payment.referenceNumber.isNullOrBlank()) {
            canvas.drawText("Reference / Txn ID: ${payment.referenceNumber}", leftMargin, y, paint)
            y += 20f
        }

        y += 10f
        canvas.drawLine(leftMargin, y, 545f, y, paint)
        y += 30f

        canvas.drawText("Amount Received:", leftMargin, y, boldPaint)
        canvas.drawText(payment.amount.paise().formatINR(), 350f, y, boldPaint)
        y += 25f
        canvas.drawText("Remaining Balance on Invoice:", leftMargin, y, paint)
        canvas.drawText(remainingBalance.paise().formatINR(), 350f, y, boldPaint)

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "Receipt_${payment.id.take(8)}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    fun generateFinancialSummaryPdf(
        context: Context,
        totalInvoiced: Long,
        totalCollected: Long,
        outstanding: Long,
        overdue: Long,
        paidCount: Int,
        unpaidCount: Int,
        overdueCount: Int,
        filterTitle: String
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { color = Color.BLACK; textSize = 12f }
        val titlePaint = Paint().apply { color = Color.rgb(27, 77, 62); textSize = 20f; isFakeBoldText = true }
        val boldPaint = Paint().apply { color = Color.BLACK; textSize = 12f; isFakeBoldText = true }
        val grayPaint = Paint().apply { color = Color.GRAY; textSize = 10f }

        var y = 40f
        val leftMargin = 40f

        canvas.drawText("SHARMA PACKAGING - FINANCIAL SUMMARY REPORT", leftMargin, y, titlePaint)
        y += 20f
        canvas.drawText("Period: $filterTitle • Generated via Baaki", leftMargin, y, grayPaint)
        y += 40f

        canvas.drawText("Key Metrics", leftMargin, y, boldPaint)
        y += 25f
        canvas.drawText("Total Invoiced Sales:", leftMargin, y, paint)
        canvas.drawText(totalInvoiced.paise().formatINR(), 350f, y, boldPaint)
        y += 20f
        canvas.drawText("Total Payments Collected:", leftMargin, y, paint)
        canvas.drawText(totalCollected.paise().formatINR(), 350f, y, boldPaint)
        y += 20f
        canvas.drawText("Outstanding Receivables:", leftMargin, y, paint)
        canvas.drawText(outstanding.paise().formatINR(), 350f, y, boldPaint)
        y += 20f
        canvas.drawText("Overdue Receivables:", leftMargin, y, paint)
        canvas.drawText(overdue.paise().formatINR(), 350f, y, boldPaint)
        y += 40f

        canvas.drawText("Invoice Status Breakdown", leftMargin, y, boldPaint)
        y += 25f
        canvas.drawText("Paid Invoices: $paidCount", leftMargin, y, paint)
        y += 20f
        canvas.drawText("Unpaid Invoices: $unpaidCount", leftMargin, y, paint)
        y += 20f
        canvas.drawText("Overdue Invoices: $overdueCount", leftMargin, y, paint)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "Financial_Summary_Report.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    fun sharePdf(context: Context, file: File, title: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, title)
        context.startActivity(chooser)
    }
}
