package com.baaki.app.domain.chase

import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.PaymentEntity
import java.util.Locale
import kotlin.math.min

data class RecoveryItem(
    val invoice: InvoiceEntity,
    val customer: CustomerEntity,
    val outstandingPaise: Long,
    val totalPaidPaise: Long,
    val daysOverdue: Long,
    val priorityScore: Int,
    val priorityLevel: PriorityLevel,
    val reasons: List<String>,
    val recommendedAction: String
)

enum class PriorityLevel {
    HIGH, MEDIUM, LOW
}

object SmartRecoveryScorer {
    fun scoreInvoices(
        invoices: List<InvoiceEntity>,
        customers: List<CustomerEntity>,
        payments: List<PaymentEntity>,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): List<RecoveryItem> {
        val customerMap = customers.associateBy { it.id }
        val paymentsByInvoice = payments.groupBy { it.invoiceId }
        val unpaidInvoices = invoices.filter { inv ->
            val paid = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
            val outstanding = maxOf(0L, inv.totalAmount - paid)
            outstanding > 0L && inv.status != "PAID"
        }

        if (unpaidInvoices.isEmpty()) return emptyList()

        val maxOutstanding = unpaidInvoices.maxOf { inv ->
            val paid = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
            maxOf(0L, inv.totalAmount - paid)
        }.coerceAtLeast(1L)

        val dayMillis = 24 * 60 * 60 * 1000L

        return unpaidInvoices.map { inv ->
            val customer = customerMap[inv.customerId] ?: CustomerEntity(
                id = inv.customerId,
                name = "Unknown Customer",
                phone = "",
                email = null,
                gstin = null,
                address = null,
                openingBalance = 0L,
                creditLimit = 0L,
                createdAt = currentTimeMillis
            )
            val paid = paymentsByInvoice[inv.id]?.sumOf { it.amount } ?: 0L
            val outstanding = maxOf(0L, inv.totalAmount - paid)

            val diffMillis = currentTimeMillis - inv.dueDate
            val daysOverdue = if (diffMillis > 0) diffMillis / dayMillis else 0L

            val reasons = mutableListOf<String>()
            var score = 10 // base score

            // 1. Overdue contribution (up to 50 points)
            if (daysOverdue > 0) {
                val overduePoints = min(50L, daysOverdue * 2)
                score += overduePoints.toInt()
                reasons.add("$daysOverdue days overdue")
            } else {
                val daysUntilDue = (-diffMillis) / dayMillis
                reasons.add("Due in $daysUntilDue days")
            }

            // 2. Outstanding balance relative contribution (up to 20 points)
            val balanceRatio = outstanding.toDouble() / maxOutstanding.toDouble()
            val balancePoints = (balanceRatio * 20).toInt()
            score += balancePoints
            reasons.add("₹${String.format(Locale.getDefault(), "%.2f", outstanding / 100.0)} outstanding")

            // 3. Partial payment status boost (10 points)
            if (paid > 0L) {
                score += 10
                reasons.add("Partially paid; balance remains")
            }

            // 4. Customer payment history check
            if (customer.openingBalance > 0L) {
                score += 10
                reasons.add("Has prior ledger opening balance")
            }

            val finalScore = score.coerceIn(0, 100)
            val level = when {
                finalScore >= 70 -> PriorityLevel.HIGH
                finalScore >= 40 -> PriorityLevel.MEDIUM
                else -> PriorityLevel.LOW
            }

            val action = when (level) {
                PriorityLevel.HIGH -> "Urgent follow-up & share reminder"
                PriorityLevel.MEDIUM -> "Send payment reminder"
                PriorityLevel.LOW -> "Monitor / Friendly follow-up"
            }

            RecoveryItem(
                invoice = inv,
                customer = customer,
                outstandingPaise = outstanding,
                totalPaidPaise = paid,
                daysOverdue = daysOverdue,
                priorityScore = finalScore,
                priorityLevel = level,
                reasons = reasons,
                recommendedAction = action
            )
        }.sortedByDescending { it.priorityScore }
    }
}
