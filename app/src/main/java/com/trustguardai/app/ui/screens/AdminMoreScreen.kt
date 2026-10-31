package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMoreScreen(
    onLogout: () -> Unit,
    onNavigateToDemo: (String) -> Unit,
    onNavigateToPresentationDemo: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(DarkNavy)) {
        CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.05f))
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ADMINISTRATION",
                color = TealAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "System Control",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            MoreCategory("SECURITY OPS") {
                MoreRow("Presentation Demo Mode", Icons.Default.DesktopWindows, onClick = onNavigateToPresentationDemo)
                MoreRow("Security Analytics", Icons.Default.QueryStats, onClick = { onNavigateToDemo("Security Analytics") })
                MoreRow("Audit Logs", Icons.Default.ReceiptLong, onClick = { onNavigateToDemo("Audit Logs") })
                MoreRow("Threat Intelligence", Icons.Default.Public, onClick = { onNavigateToDemo("Global Threat Intel") })
                MoreRow("System Health", Icons.Default.HealthAndSafety, onClick = { onNavigateToDemo("System Health") })
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MoreCategory("ORGANIZATION") {
                MoreRow("Security Policies", Icons.Default.Policy, onClick = { onNavigateToDemo("Policies") })
                MoreRow("Access Control", Icons.Default.LockPerson, onClick = { onNavigateToDemo("Access Control") })
                MoreRow("Integrations", Icons.Default.Extension, onClick = { onNavigateToDemo("Integrations") })
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MoreCategory("PREFERENCES") {
                MoreRow("Notifications", Icons.Default.Notifications, onClick = { onNavigateToDemo("Notifications") })
                MoreRow("App Settings", Icons.Default.Settings, onClick = { onNavigateToDemo("Settings") })
                MoreRow("Logout", Icons.AutoMirrored.Filled.Logout, color = ErrorRed, onClick = onLogout)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun MoreCategory(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun MoreRow(
    label: String,
    icon: ImageVector,
    color: Color = TextPrimary,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (color == TextPrimary) TealAccent else color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, color = color, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}
