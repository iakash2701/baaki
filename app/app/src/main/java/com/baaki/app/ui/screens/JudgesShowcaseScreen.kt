package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun JudgesShowcaseScreen(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Baaki Judges Showcase 🇮🇳",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Explore the offline-first financial intelligence engine built for Indian small businesses.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "Feature Hub",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        ShowcaseCard(
            title = "Smart Recovery Intelligence",
            description = "Explainable priority scoring (0–100) for overdue invoices with recommended actions and direct reminders.",
            icon = Icons.Default.TrendingUp,
            onClick = { onNavigate("smart_recovery") }
        )

        ShowcaseCard(
            title = "Cash Flow Intelligence & Ageing",
            description = "Track collections, total invoiced, net cash flow, and receivables ageing buckets.",
            icon = Icons.Default.AccountBalanceWallet,
            onClick = { onNavigate("cash_flow") }
        )

        ShowcaseCard(
            title = "Customer Intelligence",
            description = "Analyze customer payment reliability, historical collections, and average payment delays.",
            icon = Icons.Default.People,
            onClick = { onNavigate("customer_intelligence") }
        )

        ShowcaseCard(
            title = "Invoices & Instant Sharing",
            description = "Create GST/non-GST invoices, record partial or full payments, and share summaries via Android Sharesheet.",
            icon = Icons.Default.ReceiptLong,
            onClick = { onNavigate("invoices") }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Hackathon Demo Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "1. Open Smart Recovery to review urgent overdue invoices.\n2. Tap an invoice to open details and share a payment reminder.\n3. Record a partial or full payment and watch real-time balance updates.\n4. Explore Cash Flow & Customer Intelligence to verify aggregated financial metrics.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ShowcaseCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
