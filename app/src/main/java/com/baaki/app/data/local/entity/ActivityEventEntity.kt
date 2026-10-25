package com.baaki.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_events")
data class ActivityEventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val eventType: String, // INVOICE_CREATED, PAYMENT_RECEIVED, CUSTOMER_ADDED, EXPENSE_RECORDED
    val timestamp: Long,
    val relatedId: String?
)
