package com.trustguardai.app.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkCheckScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var networkInfo by remember { mutableStateOf(getNetworkInfo(context)) }

    LaunchedEffect(Unit) {
        while(true) {
            networkInfo = getNetworkInfo(context)
            delay(5000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Security", fontWeight = FontWeight.Bold) },
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
                        text = "REAL-TIME MONITORING",
                        color = TealAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Current Connection Status",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    ConnectionCard(networkInfo)
                }
                
                item {
                    Text("NETWORK SIGNALS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        NetworkSignalRow("Internet Connectivity", networkInfo.hasInternet)
                        NetworkSignalRow("Connection Validated", networkInfo.isValidated)
                        NetworkSignalRow("Metered Network", networkInfo.isMetered)
                        NetworkSignalRow("VPN Connected", networkInfo.isVpn)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("SECURITY RECOMMENDATION", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (networkInfo.isVpn) "Your connection is encrypted via VPN. This is recommended for remote work." 
                                   else "Consider using a VPN when connecting to untrusted networks to protect your data.",
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
fun ConnectionCard(info: NetworkInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (info.hasInternet) TealAccent.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        info.type == "Wi-Fi" -> Icons.Default.Wifi
                        info.type == "Mobile" -> Icons.Default.SignalCellularAlt
                        else -> Icons.Default.CloudOff
                    },
                    null,
                    tint = if (info.hasInternet) TealAccent else ErrorRed,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = if (info.hasInternet) "ONLINE" else "OFFLINE", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(text = "Transport: ${info.type}", color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun NetworkSignalRow(label: String, active: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(text = if (active) "YES" else "NO", color = if (active) SuccessGreen else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

data class NetworkInfo(
    val type: String,
    val hasInternet: Boolean,
    val isValidated: Boolean,
    val isMetered: Boolean,
    val isVpn: Boolean
)

fun getNetworkInfo(context: Context): NetworkInfo {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetwork
    val caps = cm.getNetworkCapabilities(activeNetwork)
    
    return NetworkInfo(
        type = when {
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "Unknown"
        },
        hasInternet = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true,
        isValidated = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true,
        isMetered = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == false,
        isVpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    )
}
