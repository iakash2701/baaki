package com.baaki.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baaki.app.ui.auth.*
import com.baaki.app.ui.navigation.Screen
import com.baaki.app.ui.screens.*
import com.baaki.app.ui.theme.BaakiTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val userSession by authViewModel.userSession.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentAuthSubScreen by remember { mutableStateOf<String?>(null) }
    var showEditProfile by remember { mutableStateOf(false) }

    if (!userSession.isLoggedIn) {
        when (currentAuthSubScreen) {
            "register" -> {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { currentAuthSubScreen = null },
                    onBack = { currentAuthSubScreen = null }
                )
            }
            "forgot" -> {
                ForgotPasswordScreen(
                    onBack = { currentAuthSubScreen = null }
                )
            }
            else -> {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { currentAuthSubScreen = null },
                    onNavigateRegister = { currentAuthSubScreen = "register" },
                    onNavigateForgot = { currentAuthSubScreen = "forgot" }
                )
            }
        }
        return
    }

    if (showEditProfile) {
        EditProfileScreen(
            userSession = userSession,
            viewModel = authViewModel,
            onSaveSuccess = { showEditProfile = false },
            onBack = { showEditProfile = false }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileDrawerContent(
                userSession = userSession,
                onEditProfile = { showEditProfile = true },
                onSecurity = { /* security action */ },
                onLogout = {
                    authViewModel.logout {
                        currentAuthSubScreen = null
                    }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val currentTitle = when {
                    currentRoute?.startsWith("invoice_detail") == true -> "Invoice Detail"
                    currentRoute?.startsWith("customer_detail") == true -> "Customer Detail"
                    currentRoute?.startsWith("add_edit_customer") == true -> "Customer Form"
                    currentRoute == Screen.SmartRecovery.route -> "Smart Recovery"
                    currentRoute == Screen.CustomerIntelligence.route -> "Customer Intelligence"
                    currentRoute == Screen.FinancialReports.route -> "Financial Reports"
                    currentRoute == Screen.MoreIntelligence.route -> "Intelligence"
                    currentRoute == Screen.MoreReports.route -> "Reports"
                    currentRoute == Screen.MoreRecovery.route -> "Recovery"
                    currentRoute == Screen.MoreSettings.route -> "Settings"
                    currentRoute == Screen.MoreHelp.route -> "Help & Support"
                    currentRoute == Screen.JudgesShowcase.route -> "Judges Showcase"
                    else -> Screen.bottomNavItems.find { it.route == currentRoute }?.title ?: "Baaki"
                }

                val showTopBar = currentRoute != Screen.Home.route &&
                        currentRoute != Screen.More.route &&
                        currentRoute != Screen.CashFlow.route &&
                        currentRoute != Screen.Invoices.route &&
                        currentRoute != Screen.Customers.route &&
                        currentRoute != Screen.MoreIntelligence.route &&
                        currentRoute != Screen.MoreReports.route &&
                        currentRoute != Screen.MoreRecovery.route &&
                        currentRoute != Screen.MoreSettings.route &&
                        currentRoute != Screen.MoreHelp.route

                if (showTopBar) {
                    TopAppBar(
                        title = { Text(text = currentTitle) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute == Screen.Home.route ||
                        currentRoute == Screen.CashFlow.route ||
                        currentRoute == Screen.Invoices.route ||
                        currentRoute == Screen.Customers.route ||
                        currentRoute == Screen.More.route

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
                        },
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
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
                composable(Screen.FinancialReports.route) {
                    FinancialReportsScreen()
                }
                composable(Screen.MoreIntelligence.route) {
                    MoreIntelligenceScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.MoreReports.route) {
                    MoreReportsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.MoreRecovery.route) {
                    MoreRecoveryScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.MoreSettings.route) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onLogout = { navController.popBackStack() }
                    )
                }
                composable(Screen.MoreHelp.route) {
                    HelpSupportScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.Invoices.route) {
                    InvoicesScreen(
                        onInvoiceClick = { invoiceId ->
                            navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                        }
                    )
                }
                composable(Screen.Customers.route) {
                    CustomersScreen(
                        onAddCustomer = {
                            navController.navigate(Screen.AddEditCustomer.createRoute())
                        },
                        onCustomerClick = { customerId ->
                            navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                        }
                    )
                }
                composable("add_edit_customer?customerId={customerId}") { backStackEntry ->
                    val customerId = backStackEntry.arguments?.getString("customerId")?.takeIf { it != "{customerId}" }
                    AddEditCustomerScreen(
                        customerId = customerId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("customer_detail/{customerId}") { backStackEntry ->
                    val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
                    CustomerDetailScreen(
                        customerId = customerId,
                        onBack = { navController.popBackStack() },
                        onEditCustomer = { id ->
                            navController.navigate(Screen.AddEditCustomer.createRoute(id))
                        },
                        onInvoiceClick = { invoiceId ->
                            navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                        }
                    )
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
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NavHostPreview()
}

@Composable
fun NavHostPreview() {
    BaakiTheme {
        MainScreen()
    }
}
