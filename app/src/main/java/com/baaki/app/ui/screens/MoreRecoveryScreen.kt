package com.baaki.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
fun MoreRecoveryScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recovery") },
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
                            Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(30.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Invoice Recovery",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Text(
                    text = "Track overdue invoices and follow up with your customers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            FeatureDetailCard(
                icon = Icons.Default.AccessTime,
                title = "Overdue Invoices",
                subtitle = "View pending payments",
                onClick = { onNavigate("smart_recovery") }
            )
            FeatureDetailCard(
                icon = Icons.Default.Email,
                title = "Follow Up",
                subtitle = "Send reminders",
                onClick = { onNavigate("smart_recovery") }
            )
            FeatureDetailCard(
                icon = Icons.Default.FormatListBulleted,
                title = "Recovery Queue",
                subtitle = "Priority collection list",
                onClick = { onNavigate("smart_recovery") }
            )
            FeatureDetailCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Recovery Analytics",
                subtitle = "Track recovery performance",
                onClick = { onNavigate("smart_recovery") }
            )
        }
    }
}
