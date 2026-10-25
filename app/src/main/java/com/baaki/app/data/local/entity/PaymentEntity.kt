package com.baaki.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey
    val id: String,
    val invoiceId: String?,
    val customerId: String,
    val amount: Long, // in paise
    val paymentDate: Long,
    val paymentMethod: String, // CASH, UPI, BANK_TRANSFER, CHEQUE
    val referenceNumber: String?,
    val notes: String?,
    val createdAt: Long
)
