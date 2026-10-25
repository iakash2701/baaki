package com.baaki.app.data.local.dao

import androidx.room.*
import com.baaki.app.data.local.entity.CustomerEntity
import com.baaki.app.data.local.entity.CustomerWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: String): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE name = :name LIMIT 1")
    suspend fun getCustomerByName(name: String): CustomerEntity?

    @Transaction
    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerWithDetails(id: String): Flow<CustomerWithDetails?>

    @Query("SELECT COUNT(*) FROM customers")
    suspend fun getCustomerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)
}
