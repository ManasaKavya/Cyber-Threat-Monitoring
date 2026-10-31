package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Message
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

data class SecurityModule(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accent: Color,
    val status: String,
    val isReal: Boolean = true,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityToolkitScreen(
    onNavigateToUrlScan: () -> Unit,
    onNavigateToIpScan: () -> Unit,
    onNavigateToFileScan: () -> Unit,
    onNavigateToHashAnalyzer: () -> Unit,
    onNavigateToLinkPreview: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToMessageScanner: () -> Unit,
    onNavigateToPasswordHealth: () -> Unit,
    onNavigateToPrivacyCheck: () -> Unit,
    onNavigateToNetworkCheck: () -> Unit,
    onNavigateToDemo: (String) -> Unit
) {
    val modules = listOf(
        // THREAT ANALYSIS
        SecurityModule("URL Scanner", "Phishing & Malicious links", Icons.Default.Language, TealAccent, "ACTIVE", onClick = onNavigateToUrlScan),
        SecurityModule("IP Intelligence", "Reputation & Botnet check", Icons.Default.Wifi, BlueAccent, "ACTIVE", onClick = onNavigateToIpScan),
        SecurityModule("File Sandbox", "Deep static analysis", Icons.Default.Folder, WarningOrange, "ACTIVE", onClick = onNavigateToFileScan),
        SecurityModule("Hash Analyzer", "Global database lookup", Icons.Default.Key, SuccessGreen, "ACTIVE", onClick = onNavigateToHashAnalyzer),
        
        // EVERYDAY PROTECTION
        SecurityModule("Link Preview", "Safe visual inspection", Icons.Default.Visibility, BlueAccent, "READY", onClick = onNavigateToLinkPreview),
        SecurityModule("QR Scanner", "Secure destination check", Icons.Default.QrCodeScanner, TealAccent, "READY", onClick = onNavigateToQrScanner),
        SecurityModule("AI Message Scan", "SMS & Email social eng.", Icons.AutoMirrored.Filled.Message, WarningOrange, "ACTIVE", onClick = onNavigateToMessageScanner),
        SecurityModule("Password Health", "Breach & Strength check", Icons.Default.Lock, SuccessGreen, "ACTIVE", onClick = onNavigateToPasswordHealth),
        
        // DEVICE SECURITY
        SecurityModule("Privacy Check", "App data permissions", Icons.Default.Fingerprint, BlueAccent, "ACTIVE", onClick = onNavigateToPrivacyCheck),
        SecurityModule("Network Check", "Wi-Fi & VPN security", Icons.Default.Hub, TealAccent, "LIVE", onClick = onNavigateToNetworkCheck),
        SecurityModule("Device Security", "OS & Root integrity", Icons.Default.Smartphone, SuccessGreen, "SECURE", isReal = false, onClick = { onNavigateToDemo("Device Security") }),
        SecurityModule("Threat Intel", "Global real-time feed", Icons.Default.RssFeed, WarningOrange, "LIVE", isReal = false, onClick = { onNavigateToDemo("Threat Intel") })
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkNavy)) {
        CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.05f))
        
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SECURITY TOOLKIT",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SYSTEM READY", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
            Text(
                text = "Personal Protection",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tools for everyday digital safety and threat analysis.", color = TextSecondary, fontSize = 13.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(modules) { module ->
                    ModuleGridCard(module)
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}

@Composable
fun ModuleGridCard(module: SecurityModule) {
    Card(
        onClick = module.onClick,
        modifier = Modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(module.accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(module.icon, null, tint = module.accent, modifier = Modifier.size(18.dp))
            }
            
            Column {
                Text(text = module.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = module.description, color = TextSecondary, fontSize = 10.sp, lineHeight = 14.sp, maxLines = 2)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!module.isReal) {
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(TextSecondary))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = module.status,
                        color = if (module.isReal) module.accent else TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
            }
        }
    }
}
