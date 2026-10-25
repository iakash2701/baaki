package com.baaki.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baaki.app.ui.MainScreen
import com.baaki.app.ui.theme.BaakiTheme
import com.baaki.app.ui.theme.PrimaryGreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaakiTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1200L) // 1.2 seconds professional branded splash screen presentation
                    showSplash = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainScreen()

                    AnimatedVisibility(
                        visible = showSplash,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF0FDF4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                // Prominent Full Logo Card / Artwork (~50% screen width proportional)
                                Card(
                                    shape = RoundedCornerShape(32.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                    modifier = Modifier.size(240.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        // Central Document B symbol representation
                                        Box(
                                            modifier = Modifier.size(96.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = PrimaryGreen,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text("INVOICE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                        Text("₹", color = Color(0xFF2ECC71), fontWeight = FontWeight.Bold, fontSize = 28.sp)
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Baaki",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryGreen
                                            )
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFF229954),
                                                modifier = Modifier.size(8.dp)
                                            ) {}
                                        }
                                        Text(
                                            text = "Invoices • Payments • Growth",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
