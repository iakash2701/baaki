package com.baaki.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baaki.app.R
import com.baaki.app.ui.theme.PrimaryGreen

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateForgot: () -> Unit
) {
    var email by remember { mutableStateOf("rajesh@sharmapackaging.in") }
    var password by remember { mutableStateOf("password123") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F8F5),
                        Color(0xFFF4FBF8),
                        PrimaryGreen.copy(alpha = 0.12f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Hero Section with Brand, Text & Fintech Illustration matching reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Baaki",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontSize = 34.sp
                        )
                        Text(text = "🇮🇳", fontSize = 26.sp)
                    }
                    Text(
                        text = "Offline-first invoice collection & cash-flow",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Get Paid.\nStay Ahead.",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        lineHeight = 40.sp,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Track invoices, collect payments and grow your business — even without internet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Fintech Illustration matching reference image
                FintechHeroIllustration(
                    modifier = Modifier
                        .weight(0.9f)
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // White Rounded Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Welcome Back",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                                fontSize = 24.sp
                            )
                            Text(
                                text = "Sign in to continue to your account",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }

                        if (errorMessage != null) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Email Field with mint circular leading badge
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD1F2EB),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Mail, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = PrimaryGreen
                            )
                        )

                        // Password Field with mint circular leading badge & eye icon
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD1F2EB),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF64748B)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = PrimaryGreen
                            )
                        )

                        // Remember Me & Forgot Password Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text("Remember me", style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
                            }
                            TextButton(onClick = onNavigateForgot) {
                                Text("Forgot Password?", style = MaterialTheme.typography.bodySmall, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Pill-shaped emerald Sign In button with arrow
                        Button(
                            onClick = { viewModel.login(email, password, onLoginSuccess) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = Color.White
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Divider "or"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                            Text(text = "or", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                        }

                        // Social Buttons Row (Google, Microsoft, Apple with official brand logos)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.socialLogin("Google", onLoginSuccess) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111C16))
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_google),
                                        contentDescription = "Google",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Google", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.socialLogin("Microsoft", onLoginSuccess) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111C16))
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_microsoft),
                                        contentDescription = "Microsoft",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Microsoft", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.socialLogin("Apple", onLoginSuccess) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111C16))
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_apple),
                                        contentDescription = "Apple",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Apple", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                                }
                            }
                        }
                    }

                    // Bottom Soft Mint Registration Panel
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F8F5)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Don't have an account?", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF0A2920))
                            TextButton(onClick = onNavigateRegister) {
                                Text("Create Account →", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer Benefits (4 evenly spaced items with vertical dividers)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BenefitItemModern(icon = Icons.Default.CloudOff, label = "Works Offline")
                VerticalDivider(modifier = Modifier.height(24.dp), color = Color(0xFFCBD5E1))
                BenefitItemModern(icon = Icons.Default.Security, label = "Secure & Reliable")
                VerticalDivider(modifier = Modifier.height(24.dp), color = Color(0xFFCBD5E1))
                BenefitItemModern(icon = Icons.Default.FlashOn, label = "Fast & Simple")
                VerticalDivider(modifier = Modifier.height(24.dp), color = Color(0xFFCBD5E1))
                BenefitItemModern(icon = Icons.AutoMirrored.Filled.TrendingUp, label = "Better Cash Flow")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FintechHeroIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryGreen.copy(alpha = 0.15f),
            modifier = Modifier.size(140.dp)
        ) {}

        Card(
            modifier = Modifier
                .width(130.dp)
                .height(160.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("INVOICE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Surface(shape = CircleShape, color = Color(0xFF2ECC71), modifier = Modifier.size(8.dp)) {}
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha =.4f), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(4.dp)
                        .background(Color.White.copy(alpha =.4f), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = Color(0xFFF1C40F), modifier = Modifier.size(16.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("₹", color = Color(0xFF7D6608), fontWeight = FontWeight.Bold, fontSize = 10.sp) }
                    }
                    Text("₹15,400", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E8449),
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 12.dp, y = (-10).dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2ECC71), modifier = Modifier.size(14.dp))
                Column {
                    Text("Payment", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("Received", color = Color(0xFF2ECC71), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Surface(
            shape = CircleShape,
            color = Color(0xFFF1C40F),
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-8).dp, y = 12.dp)
                .size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("₹", color = Color(0xFF7D6608), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun BenefitItemModern(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(76.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryGreen.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            fontSize = 11.sp
        )
    }
}
