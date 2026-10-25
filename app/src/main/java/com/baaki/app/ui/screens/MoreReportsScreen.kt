package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.baaki.app.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreReportsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF4FBF8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Section matching reference image
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(shape = CircleShape, color = PrimaryGreen.copy(alpha = 0.15f), modifier = Modifier.size(90.dp)) {}
                    Surface(shape = CircleShape, color = PrimaryGreen.copy(alpha = 0.25f), modifier = Modifier.size(60.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(30.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Business Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Text(
                    text = "View detailed reports and track your business performance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            FeatureDetailCard(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = "Sales Report",
                subtitle = "Invoice & collection summary",
                onClick = { onNavigate("financial_reports") }
            )
            FeatureDetailCard(
                icon = Icons.Default.People,
                title = "Customer Report",
                subtitle = "Customer-wise analysis",
                onClick = { onNavigate("customers") }
            )
            FeatureDetailCard(
                icon = Icons.Default.Payments,
                title = "Payment Report",
                subtitle = "Payment status & history",
                onClick = { onNavigate("cash_flow") }
            )
            FeatureDetailCard(
                icon = Icons.Default.Assessment,
                title = "Summary Report",
                subtitle = "Overall business performance",
                onClick = { onNavigate("financial_reports") }
            )
        }
    }
}
