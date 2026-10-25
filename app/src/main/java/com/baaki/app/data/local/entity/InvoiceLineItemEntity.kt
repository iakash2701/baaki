package com.baaki.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoice_line_items")
data class InvoiceLineItemEntity(
    @PrimaryKey
    val id: String,
    val invoiceId: String,
    val description: String,
    val hsnSac: String?,
    val quantity: Double,
    val unitPrice: Long, // in paise
    val taxPercent: Double,
    val amount: Long // in paise
)
