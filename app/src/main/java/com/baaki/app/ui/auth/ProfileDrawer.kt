package com.baaki.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.baaki.app.data.repository.UserSession

@Composable
fun ProfileDrawerContent(
    userSession: UserSession,
    onEditProfile: () -> Unit,
    onSecurity: () -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(300.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initials = userSession.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
                        Text(
                            text = initials.ifEmpty { "BP" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Column {
                    Text(
                        text = userSession.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userSession.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = userSession.businessName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Edit Profile") },
                selected = false,
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                onClick = { onEditProfile(); onClose() }
            )
            NavigationDrawerItem(
                label = { Text("Security") },
                selected = false,
                icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                onClick = { onSecurity(); onClose() }
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()

            NavigationDrawerItem(
                label = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                selected = false,
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = { onLogout(); onClose() }
            )
        }
    }
}
