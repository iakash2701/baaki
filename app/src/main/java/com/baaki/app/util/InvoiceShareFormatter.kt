package com.baaki.app.util

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.domain.money.paise
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoiceShareFormatter {
    fun generateSummary(
        invoice: InvoiceEntity,
        customer: CustomerEntity?,
        lineItems: List<InvoiceLineItemEntity>,
        totalPaid: Long,
        outstanding: Long,
        status: String
    ): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val issueStr = dateFormat.format(Date(invoice.issueDate))
        val dueStr = dateFormat.format(Date(invoice.dueDate))

        val sb = StringBuilder()
        sb.append("*SHARMA PACKAGING* 🇮🇳\n")
        sb.append("------------------------------------\n")
        sb.append("INVOICE: ${invoice.invoiceNumber}\n")
        sb.append("Issue Date: $issueStr\n")
        sb.append("Due Date: $dueStr\n")
        sb.append("Status: $status\n\n")

        sb.append("*Customer Details:*\n")
        sb.append("Name: ${customer?.name ?: "Unknown"}\n")
        if (!customer?.phone.isNullOrBlank()) sb.append("Phone: ${customer?.phone}\n")
        if (!customer?.gstin.isNullOrBlank()) sb.append("GSTIN: ${customer?.gstin}\n")
        sb.append("\n")

        sb.append("*Line Items:*\n")
        lineItems.forEach { item ->
            sb.append("• ${item.description} (Qty: ${item.quantity}) @ ${item.unitPrice.paise().formatINR()} = ${item.amount.paise().formatINR()}\n")
        }
        sb.append("\n")

        sb.append("Subtotal: ${invoice.subTotal.paise().formatINR()}\n")
        sb.append("GST Total: ${invoice.taxTotal.paise().formatINR()}\n")
        sb.append("*Grand Total: ${invoice.totalAmount.paise().formatINR()}*\n")
        sb.append("Paid Received: ${totalPaid.paise().formatINR()}\n")
        sb.append("*Outstanding Due: ${outstanding.paise().formatINR()}*\n")
        sb.append("------------------------------------\n")
        sb.append("Thank you for your business! Sent via Baaki.")

        return sb.toString()
    }
}
