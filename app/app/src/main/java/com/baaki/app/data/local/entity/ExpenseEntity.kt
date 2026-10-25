package com.baaki.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String, // RENT, UTILITIES, SUPPLIES, SALARY, OTHER
    val amount: Long, // in paise
    val expenseDate: Long,
    val notes: String?,
    val createdAt: Long
)
