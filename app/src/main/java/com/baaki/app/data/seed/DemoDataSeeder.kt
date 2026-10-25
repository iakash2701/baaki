package com.baaki.app.data.seed

import com.baaki.app.data.local.dao.*
import com.baaki.app.data.local.entity.*
import com.baaki.app.util.FinancialYearUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object DemoDataSeeder {
    suspend fun seedIfEmpty(
        customerDao: CustomerDao,
        invoiceDao: InvoiceDao,
        paymentDao: PaymentDao,
        expenseDao: ExpenseDao,
        activityDao: ActivityEventDao
    ) {
        val existing = customerDao.getCustomerByName("Gupta Traders")
        if (existing != null) return

        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val fy = FinancialYearUtils.getFinancialYear(now)
            val dayMillis = 24 * 60 * 60 * 1000L

            // 6 Customers
            val c1 = UUID.randomUUID().toString()
            val c2 = UUID.randomUUID().toString()
            val c3 = UUID.randomUUID().toString()
            val c4 = UUID.randomUUID().toString()
            val c5 = UUID.randomUUID().toString()
            val c6 = UUID.randomUUID().toString()

            val customers = listOf(
                CustomerEntity(c1, "Gupta Traders", "9876543210", "gupta.traders@example.com", "07AABCG1234F1Z5", "Chandni Chowk, New Delhi", 500000L, 10000000L, now - 20 * dayMillis),
                CustomerEntity(c2, "Apex Logistics", "9123456789", "contact@apexlogistics.in", "27AADCA5678G1Z2", "Andheri East, Mumbai", 0L, 25000000L, now - 15 * dayMillis),
                CustomerEntity(c3, "Shree Ram Enterprises", "9988776655", null, null, "Station Road, Jaipur", 120000L, 5000000L, now - 12 * dayMillis),
                CustomerEntity(c4, "Balaji Packaging", "9811122233", "orders@balajipkg.co.in", "24AABCB9876Q1Z1", "Gandhi Nagar, Ahmedabad", 0L, 15000000L, now - 10 * dayMillis),
                CustomerEntity(c5, "National Logistics", "9700011122", null, null, "Park Street, Kolkata", 0L, 10000000L, now - 8 * dayMillis),
                CustomerEntity(c6, "Perfect Printers", "9655544332", "hello@perfectprinters.in", "33AABCP5555R1Z9", "Mount Road, Chennai", 0L, 5000000L, now - 2 * dayMillis)
            )

            customers.forEach { customerDao.insertCustomer(it) }

            // 10-12 Invoices across customers
            val invData = listOf(
                Triple(c1, "SP/$fy/001", 2900000L),
                Triple(c1, "SP/$fy/002", 4500000L),
                Triple(c2, "SP/$fy/003", 12000000L),
                Triple(c2, "SP/$fy/004", 8500000L),
                Triple(c3, "SP/$fy/005", 1500000L),
                Triple(c3, "SP/$fy/006", 3200000L),
                Triple(c4, "SP/$fy/007", 6500000L),
                Triple(c4, "SP/$fy/008", 4100000L),
                Triple(c5, "SP/$fy/009", 1800000L),
                Triple(c5, "SP/$fy/010", 2500000L)
            )

            invData.forEachIndexed { index, (custUuid, invNo, amount) ->
                val invId = UUID.randomUUID().toString()
                val sub = (amount * 85 / 100)
                val tax = amount - sub
                val issue = now - ((15 - index) * dayMillis)
                val due = issue + (10 * dayMillis)

                val invoice = InvoiceEntity(
                    id = invId,
                    invoiceNumber = invNo,
                    customerId = custUuid,
                    issueDate = issue,
                    dueDate = due,
                    subTotal = sub,
                    taxTotal = tax,
                    discountTotal = 0L,
                    totalAmount = amount,
                    status = if (index == 2 || index == 8 || index == 9) "PAID" else "SENT",
                    notes = "Demo order batch #${index + 1}",
                    createdAt = issue
                )

                val item = InvoiceLineItemEntity(
                    id = UUID.randomUUID().toString(),
                    invoiceId = invId,
                    description = "Industrial Corrugated Boxes Batch ${index + 1}",
                    hsnSac = "481910",
                    quantity = 200.0,
                    unitPrice = sub / 200L,
                    taxPercent = 18.0,
                    amount = amount
                )

                invoiceDao.insertInvoiceWithItems(invoice, listOf(item))

                if (index == 0) {
                    paymentDao.insertPayment(PaymentEntity(UUID.randomUUID().toString(), invId, custUuid, 1000000L, issue + 2 * dayMillis, "UPI", "UPI/REF101", "Partial payment", issue + 2 * dayMillis))
                } else if (index == 2 || index == 8 || index == 9) {
                    paymentDao.insertPayment(PaymentEntity(UUID.randomUUID().toString(), invId, custUuid, amount, issue + 3 * dayMillis, "BANK_TRANSFER", "NEFT/REF20${index}", "Full settlement", issue + 3 * dayMillis))
                } else if (index == 4) {
                    paymentDao.insertPayment(PaymentEntity(UUID.randomUUID().toString(), invId, custUuid, 500000L, issue + 2 * dayMillis, "CASH", null, "Advance cash", issue + 2 * dayMillis))
                }
            }

            expenseDao.insertExpense(ExpenseEntity(UUID.randomUUID().toString(), "Godown Rent", "RENT", 1500000L, now - 8 * dayMillis, "Monthly rent", now - 8 * dayMillis))
            expenseDao.insertExpense(ExpenseEntity(UUID.randomUUID().toString(), "Kraft Paper Raw Material", "SUPPLIES", 4500000L, now - 5 * dayMillis, "Mill purchase", now - 5 * dayMillis))

            activityDao.insertActivity(ActivityEventEntity(UUID.randomUUID().toString(), "Demo Data Initialized", "Seeded 6 Indian business customers & 10 invoices for judges showcase.", "SYSTEM", now, null))
        }
    }
}
