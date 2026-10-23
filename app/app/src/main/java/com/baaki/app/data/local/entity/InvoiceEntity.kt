package com.baaki.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey
    val id: String,
    val invoiceNumber: String,
    val customerId: String,
    val issueDate: Long,
    val dueDate: Long,
    val subTotal: Long, // in paise
    val taxTotal: Long, // in paise
    val discountTotal: Long, // in paise
    val totalAmount: Long, // in paise
    val status: String, // DRAFT, SENT, PAID, PARTIALLY_PAID, OVERDUE
    val notes: String?,
    val createdAt: Long
)
