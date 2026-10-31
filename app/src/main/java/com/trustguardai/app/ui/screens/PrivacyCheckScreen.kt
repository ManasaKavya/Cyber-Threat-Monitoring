package com.trustguardai.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyCheckScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    
    val permissions = listOf(
        PermissionStatus("Camera", Manifest.permission.CAMERA, Icons.Default.CameraAlt),
        PermissionStatus("Location", Manifest.permission.ACCESS_FINE_LOCATION, Icons.Default.LocationOn),
        PermissionStatus("Microphone", Manifest.permission.RECORD_AUDIO, Icons.Default.Mic),
        PermissionStatus("Storage", Manifest.permission.READ_EXTERNAL_STORAGE, Icons.Default.Folder),
        PermissionStatus("Contacts", Manifest.permission.READ_CONTACTS, Icons.Default.Contacts)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Check", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavy,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TealAccent
                )
            )
        },
        containerColor = DarkNavy
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.05f))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "DEVICE PRIVACY AUDIT",
                        color = TealAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "App Permission Status",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(permissions.size) { index ->
                    val perm = permissions[index]
                    val isGranted = ContextCompat.checkSelfPermission(context, perm.permission) == PackageManager.PERMISSION_GRANTED
                    
                    PermissionCard(perm, isGranted)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("PRIVACY RECOMMENDATION", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Review permissions regularly. Revoke access for any features you don't actively use to minimize your data footprint.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun PermissionCard(status: PermissionStatus, isGranted: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) TealAccent.copy(alpha = 0.1f) else SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(status.icon, null, tint = if (isGranted) TealAccent else TextSecondary, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = status.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = if (isGranted) "Authorized" else "Not Requested", color = TextSecondary, fontSize = 12.sp)
            }
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) SuccessGreen else TextSecondary.copy(alpha = 0.3f))
            )
        }
    }
}

data class PermissionStatus(
    val name: String,
    val permission: String,
    val icon: ImageVector
)
