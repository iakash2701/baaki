package com.baaki.app.data.local.dao

import androidx.room.*
import com.baaki.app.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY paymentDate DESC")
    fun getPaymentsForCustomer(customerId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY paymentDate DESC")
    fun getPaymentsForInvoice(invoiceId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)
}
