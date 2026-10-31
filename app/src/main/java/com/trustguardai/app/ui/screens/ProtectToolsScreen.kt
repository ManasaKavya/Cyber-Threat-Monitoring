package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.theme.*

data class SecurityTool(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accent: Color,
    val status: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectToolsScreen(
    onNavigateToUrlScan: () -> Unit,
    onNavigateToIpScan: () -> Unit,
    onNavigateToFileScan: () -> Unit
) {
    val tools = listOf(
        SecurityTool("URL Scanner", "Phishing & Malicious links", Icons.Default.Language, TealAccent, "ACTIVE", onNavigateToUrlScan),
        SecurityTool("IP Intelligence", "Reputation & Botnet check", Icons.Default.Wifi, BlueAccent, "ACTIVE", onNavigateToIpScan),
        SecurityTool("File Sandbox", "Deep static analysis", Icons.Default.Folder, WarningOrange, "ACTIVE", onNavigateToFileScan),
        SecurityTool("Hash Check", "Database lookup", Icons.Default.Key, SuccessGreen, "READY", {}),
        SecurityTool("Device Security", "System integrity", Icons.Default.PhoneAndroid, SuccessGreen, "SECURE", {}),
        SecurityTool("Password Sec", "Strength & Leak check", Icons.Default.Lock, BlueAccent, "READY", {}),
        SecurityTool("Threat Intel", "Global feed", Icons.Default.RssFeed, TealAccent, "LIVE", {}),
        SecurityTool("Analytics", "Personal metrics", Icons.Default.BarChart, WarningOrange, "ACTIVE", {})
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkNavy)) {
        CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.05f))
        
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "SECURITY TOOLS",
                color = TealAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Protected Modules",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tools) { tool ->
                    ToolGridCard(tool)
                }
                item { 
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ToolGridCard(tool: SecurityTool) {
    Card(
        onClick = tool.onClick,
        modifier = Modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(tool.accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon, null, tint = tool.accent, modifier = Modifier.size(20.dp))
            }
            
            Column {
                Text(text = tool.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = tool.description, color = TextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tool.status,
                    color = tool.accent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextSecondary, modifier = Modifier.size(12.dp))
            }
        }
    }
}
