package com.baaki.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object SmartRecovery : Screen("smart_recovery", "Smart Recovery", Icons.Default.ReceiptLong)
    data object CashFlow : Screen("cash_flow", "Cash Flow", Icons.Default.AccountBalanceWallet)
    data object CustomerIntelligence : Screen("customer_intelligence", "Intelligence", Icons.Default.People)
    data object Invoices : Screen("invoices", "Invoices", Icons.Default.ReceiptLong)
    data object Customers : Screen("customers", "Customers", Icons.Default.People)
    data object FinancialReports : Screen("financial_reports", "Financial Reports", Icons.Default.ReceiptLong)
    data object JudgesShowcase : Screen("judges_showcase", "Showcase", Icons.Default.Star)
    data object More : Screen("more", "More", Icons.Default.MoreHoriz)

    // More Sub-screens
    data object MoreIntelligence : Screen("more_intelligence", "Intelligence", Icons.Default.Insights)
    data object MoreReports : Screen("more_reports", "Reports", Icons.Default.BarChart)
    data object MoreRecovery : Screen("more_recovery", "Recovery", Icons.AutoMirrored.Filled.TrendingUp)
    data object MoreSettings : Screen("more_settings", "Settings", Icons.Default.Settings)
    data object MoreHelp : Screen("more_help", "Help & Support", Icons.Default.Info)

    data object InvoiceDetail : Screen("invoice_detail/{invoiceId}", "Invoice Detail", Icons.Default.ReceiptLong) {
        fun createRoute(invoiceId: String) = "invoice_detail/$invoiceId"
    }
    data object AddEditCustomer : Screen("add_edit_customer?customerId={customerId}", "Add/Edit Customer", Icons.Default.People) {
        fun createRoute(customerId: String? = null) = if (customerId != null) "add_edit_customer?customerId=$customerId" else "add_edit_customer"
    }
    data object CustomerDetail : Screen("customer_detail/{customerId}", "Customer Details", Icons.Default.People) {
        fun createRoute(customerId: String) = "customer_detail/$customerId"
    }

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Home, CashFlow, Invoices, Customers, More)
    }
}
