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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.baaki.app.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreIntelligenceScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intelligence") },
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
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(30.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Intelligent Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Text(
                    text = "Get smart recommendations and valuable insights to make better business decisions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            FeatureDetailCard(
                icon = Icons.Default.Analytics,
                title = "Invoice Analysis",
                subtitle = "Track patterns & trends",
                onClick = { onNavigate("customer_intelligence") }
            )
            FeatureDetailCard(
                icon = Icons.Default.People,
                title = "Customer Insights",
                subtitle = "Know your best customers",
                onClick = { onNavigate("customer_intelligence") }
            )
            FeatureDetailCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Payment Predictions",
                subtitle = "Forecast collection chances",
                onClick = { onNavigate("smart_recovery") }
            )
            FeatureDetailCard(
                icon = Icons.Default.Lightbulb,
                title = "Business Suggestions",
                subtitle = "Grow with smart tips",
                onClick = { onNavigate("cash_flow") }
            )
        }
    }
}

@Composable
fun FeatureDetailCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD1F2EB),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF111C16))
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}
