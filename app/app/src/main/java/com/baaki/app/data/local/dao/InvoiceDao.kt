package com.baaki.app.data.local.dao

import androidx.room.*
import com.baaki.app.data.local.entity.InvoiceEntity
import com.baaki.app.data.local.entity.InvoiceLineItemEntity
import com.baaki.app.data.local.entity.InvoiceWithLineItems
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId ORDER BY issueDate DESC")
    fun getInvoicesForCustomer(customerId: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE invoiceNumber = 'SP/2025-26/001'")
    suspend fun getDemoInvoice(): InvoiceEntity?

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceWithLineItems(invoiceId: String): Flow<InvoiceWithLineItems?>

    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun getInvoiceCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(items: List<InvoiceLineItemEntity>)

    @Transaction
    suspend fun insertInvoiceWithItems(invoice: InvoiceEntity, items: List<InvoiceLineItemEntity>) {
        insertInvoice(invoice)
        insertLineItems(items)
    }

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)
}
