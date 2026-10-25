package com.baaki.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val gstin: String?,
    val address: String?,
    val openingBalance: Long, // in paise
    val creditLimit: Long, // in paise
    val createdAt: Long,
    val notes: String? = null,
    val isActive: Boolean = true,
    val updatedAt: Long = createdAt
)
