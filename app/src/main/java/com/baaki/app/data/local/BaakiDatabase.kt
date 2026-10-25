package com.baaki.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.baaki.app.data.local.dao.*
import com.baaki.app.data.local.entity.*
import com.baaki.app.data.seed.DemoDataSeeder
import com.baaki.app.util.FinancialYearUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CustomerEntity::class,
        InvoiceEntity::class,
        InvoiceLineItemEntity::class,
        PaymentEntity::class,
        ExpenseEntity::class,
        ActivityEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BaakiDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun activityEventDao(): ActivityEventDao

    companion object {
        @Volatile
        private var INSTANCE: BaakiDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE customers ADD COLUMN notes TEXT")
                db.execSQL("ALTER TABLE customers ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE customers ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE invoices ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): BaakiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaakiDatabase::class.java,
                    "baaki_database.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    DemoDataSeeder.seedIfEmpty(
                                        database.customerDao(),
                                        database.invoiceDao(),
                                        database.paymentDao(),
                                        database.expenseDao(),
                                        database.activityEventDao()
                                    )
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val invoiceDao = database.invoiceDao()
                                        val demoInv = invoiceDao.getDemoInvoice()
                                        if (demoInv != null && demoInv.invoiceNumber == "SP/2025-26/001") {
                                            val correctFy = FinancialYearUtils.getFinancialYear(demoInv.issueDate)
                                            val correctedNumber = "SP/$correctFy/001"
                                            if (correctedNumber != demoInv.invoiceNumber) {
                                                val updated = demoInv.copy(invoiceNumber = correctedNumber)
                                                invoiceDao.updateInvoice(updated)
                                            }
                                        }
                                    } catch (_: Exception) { }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
