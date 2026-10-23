package com.baaki.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class InvoiceWithLineItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val lineItems: List<InvoiceLineItemEntity>
)

data class CustomerWithDetails(
    @Embedded val customer: CustomerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val invoices: List<InvoiceEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val payments: List<PaymentEntity>
)
