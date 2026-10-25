package com.baaki.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object SmartRecovery : Screen("smart_recovery", "Smart Recovery", Icons.Default.ReceiptLong)
    data object CashFlow : Screen("cash_flow", "Cash Flow", Icons.Default.AccountBalanceWallet)
    data object CustomerIntelligence : Screen("customer_intelligence", "Intelligence", Icons.Default.People)
    data object Invoices : Screen("invoices", "Invoices", Icons.Default.ReceiptLong)
    data object Customers : Screen("customers", "Customers", Icons.Default.People)
    data object JudgesShowcase : Screen("judges_showcase", "Showcase", Icons.Default.Star)
    data object More : Screen("more", "More", Icons.Default.Menu)
    data object InvoiceDetail : Screen("invoice_detail/{invoiceId}", "Invoice Detail", Icons.Default.ReceiptLong) {
        fun createRoute(invoiceId: String) = "invoice_detail/$invoiceId"
    }

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Home, SmartRecovery, Invoices, Customers, More)
    }
}
