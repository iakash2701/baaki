package com.baaki.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baaki.app.ui.navigation.Screen
import com.baaki.app.ui.screens.*
import com.baaki.app.ui.theme.BaakiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentTitle = when {
                currentRoute?.startsWith("invoice_detail") == true -> "Invoice Detail"
                currentRoute == Screen.SmartRecovery.route -> "Smart Recovery"
                currentRoute == Screen.CashFlow.route -> "Cash Flow Intelligence"
                currentRoute == Screen.CustomerIntelligence.route -> "Customer Intelligence"
                currentRoute == Screen.JudgesShowcase.route -> "Judges Showcase"
                else -> Screen.bottomNavItems.find { it.route == currentRoute }?.title ?: "Baaki"
            }

            TopAppBar(
                title = { Text(text = currentTitle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute?.startsWith("invoice_detail") != false && Screen.bottomNavItems.any { it.route == currentRoute }

            if (showBottomBar) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                    },
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
            composable(Screen.SmartRecovery.route) {
                SmartRecoveryScreen(
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                    }
                )
            }
            composable(Screen.CashFlow.route) {
                CashFlowScreen()
            }
            composable(Screen.CustomerIntelligence.route) {
                CustomerIntelligenceScreen()
            }
            composable(Screen.Invoices.route) {
                InvoicesScreen(
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                    }
                )
            }
            composable(Screen.Customers.route) {
                CustomersScreen()
            }
            composable(Screen.JudgesShowcase.route) {
                JudgesShowcaseScreen(
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
            composable(Screen.InvoiceDetail.route) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: return@composable
                InvoiceDetailScreen(
                    invoiceId = invoiceId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BaakiTheme {
        MainScreen()
    }
}
