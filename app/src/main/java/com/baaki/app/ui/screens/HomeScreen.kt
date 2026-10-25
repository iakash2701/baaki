package com.baaki.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.domain.money.paise
import com.baaki.app.ui.dashboard.DashboardViewModel
import com.baaki.app.ui.dashboard.InvoiceWithCustomer
import com.baaki.app.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
    authViewModel: com.baaki.app.ui.auth.AuthViewModel = viewModel(),
    onInvoiceClick: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val userSession by authViewModel.userSession.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.errorMessage != null) {
        Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dark Forest-Green Header with Gradient / Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryGreen,
                            PrimaryGreen.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top row: Title and Notification bell
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Manage your business finances",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = { /* Notification action */ }) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                            // Notification badge dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFE53935), shape = CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }

                // Business Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF111C16)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    onClick = onOpenDrawer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Avatar with initials "DU"
                            Surface(
                                shape = CircleShape,
                                color = PrimaryGreen,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val initials = userSession.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
                                    Text(
                                        text = initials.ifEmpty { "DU" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "${userSession.businessName} 🇮🇳",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Welcome back, ${userSession.fullName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Profile",
                            tint = Color(0xFF475569)
                        )
                    }
                }
            }
        }

        // Body Container with padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Financial Overview Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Financial Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF475569)
                )
            }

            // 4 Summary Cards in 2x2 Grid matching reference image
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Outstanding",
                    amount = state.totalOutstandingPaise.paise().formatINR(),
                    subtitle = "${state.invoiceCount} Invoices total",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconBgColor = Color(0xFFD1F2EB),
                    iconTint = Color(0xFF0E6251),
                    containerColor = Color(0xFFE8F8F5),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Collections",
                    amount = state.totalCollectionsPaise.paise().formatINR(),
                    subtitle = "Confirmed received",
                    icon = Icons.Default.Payments,
                    iconBgColor = Color(0xFFD4E6F1),
                    iconTint = Color(0xFF1B4F72),
                    containerColor = Color(0xFFEBF5FB),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Overdue Receivables",
                    amount = state.overdueBalancePaise.paise().formatINR(),
                    subtitle = "${state.overdueCount} Overdue invoices",
                    icon = Icons.Default.Warning,
                    iconBgColor = Color(0xFFFADBD8),
                    iconTint = Color(0xFF922B21),
                    containerColor = Color(0xFFFDEDEC),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Customers",
                    amount = state.customerCount.toString(),
                    subtitle = "Ledger accounts",
                    icon = Icons.Default.People,
                    iconBgColor = Color(0xFFD4EFDF),
                    iconTint = Color(0xFF196F3D),
                    containerColor = Color(0xFFEAFAF1),
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Smart Recovery Queue Card (Yellow)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                onClick = { onNavigate("smart_recovery") },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFEF9E7)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF39C12).copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFFB7950B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Smart Recovery Queue",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7D6608)
                            )
                            Text(
                                text = "${state.overdueCount} invoices need urgent follow-up.\nPotential recovery: ${state.overdueBalancePaise.paise().formatINR()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7D6608).copy(alpha = 0.85f)
                            )
                        }
                    }
                    Button(
                        onClick = { onNavigate("smart_recovery") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1C40F),
                            contentColor = Color(0xFF7D6608)
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("View Queue →", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. Quick Actions Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF475569)
                )
            }

            // Quick Actions: Exactly 3 items in one row (Invoices, Customers, Cash Flow)
            val shortcuts = listOf(
                Triple("Invoices", Icons.Default.ReceiptLong) { onNavigate("invoices") },
                Triple("Customers", Icons.Default.People) { onNavigate("customers") },
                Triple("Cash Flow", Icons.Default.AccountBalanceWallet) { onNavigate("cash_flow") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                shortcuts.forEach { (title, icon, action) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        onClick = action
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD1F2EB),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111C16)
                            )
                        }
                    }
                }
            }

            // 5. Cash Flow Snapshot Card (Dark forest-green background matching reference)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                onClick = { onNavigate("cash_flow") },
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Cash Flow Snapshot",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Confirmed collections: ${state.totalCollectionsPaise.paise().formatINR()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    TextButton(
                        onClick = { onNavigate("cash_flow") },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text("Details →", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 6. Recent Invoices Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Invoices (${state.invoiceCount} Total)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate("invoices") }) {
                    Text("View All")
                }
            }

            if (state.recentInvoices.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No invoices recorded yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                state.recentInvoices.forEach { item ->
                    RecentInvoiceItemCard(item = item, onClick = { onInvoiceClick(item.invoice.id) })
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    amount: String,
    subtitle: String,
    icon: ImageVector = Icons.Default.Info,
    iconBgColor: Color = Color(0xFFD1F2EB),
    iconTint: Color = PrimaryGreen,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color = Color(0xFF111C16)
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBgColor,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun RecentInvoiceItemCard(item: InvoiceWithCustomer, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.invoice.issueDate))

    val statusColor = when {
        item.outstandingPaise == 0L -> PrimaryGreen
        item.invoice.dueDate < System.currentTimeMillis() -> Color(0xFFE53935)
        else -> Color(0xFFD97706)
    }
    val statusText = when {
        item.outstandingPaise == 0L -> "PAID"
        item.invoice.dueDate < System.currentTimeMillis() -> "OVERDUE"
        else -> "UNPAID"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                Text(text = item.invoice.invoiceNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = item.customerName, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF475569))
                Text(text = formattedDate, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = item.invoice.totalAmount.paise().formatINR(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
