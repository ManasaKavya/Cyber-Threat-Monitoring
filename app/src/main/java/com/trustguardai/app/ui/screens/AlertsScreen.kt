package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.Alert
import com.trustguardai.app.data.model.AlertStatus
import com.trustguardai.app.data.model.ThreatSeverity
import com.trustguardai.app.data.model.UserRole
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.data.repository.SessionManager
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onBack: () -> Unit,
    onNavigateToIncident: (String) -> Unit
) {
    val api = RetrofitClient.apiService
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userRole = remember { sessionManager.getUser()?.role ?: UserRole.EMPLOYEE }

    var realAlerts by remember { mutableStateOf<List<Alert>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDemoData by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedAlert by remember { mutableStateOf<Alert?>(null) }

    val displayAlerts = if (showDemoData || realAlerts.isEmpty()) {
        if (showDemoData) DemoDataProvider.demoAlerts else emptyList()
    } else realAlerts

    LaunchedEffect(Unit) {
        if (userRole == UserRole.ADMIN) {
            try {
                realAlerts = api.getAlerts()
                errorMessage = null
            } catch (e: HttpException) {
                if (e.code() == 403) {
                    errorMessage = "Access Denied: Admin privileges required."
                    showDemoData = true // Fallback to demo for UX
                } else {
                    errorMessage = "Error loading alerts (${e.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Unable to connect to security server."
                showDemoData = true
            } finally {
                isLoading = false
            }
        } else {
            // Employees see demo alerts by default in this view
            showDemoData = true
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Alert Center", fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.padding(padding)) {
            if (errorMessage != null && !showDemoData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage!!, color = ErrorRed)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showDemoData = true }) {
                            Text("View Demo Data")
                        }
                    }
                }
            } else if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealAccent)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        if (userRole == UserRole.ADMIN) {
                            AlertSummarySection(displayAlerts)
                        } else {
                            // Employee specific header
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text("PERSONAL SECURITY FEED", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                Text(text = "Security Awareness", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    if (displayAlerts.isEmpty() && !isLoading) {
                        item {
                            EmptyAlertsState(onViewDemo = { showDemoData = true })
                        }
                    } else {
                        items(displayAlerts) { alert ->
                            AlertItem(alert, onClick = {
                                selectedAlert = alert
                            })
                        }
                    }
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
        
        selectedAlert?.let { alert ->
            AlertDetailsDialog(
                alert = alert,
                onDismiss = { selectedAlert = null },
                onViewIncident = { 
                    selectedAlert = null
                    onNavigateToIncident(alert.id)
                }
            )
        }
    }
}

@Composable
fun AlertDetailsDialog(
    alert: Alert,
    onDismiss: () -> Unit,
    onViewIncident: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onViewIncident) {
                Text("VIEW INCIDENT", color = TealAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = TextSecondary)
            }
        },
        title = {
            Text(text = alert.title, color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = alert.description, color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailRow("SEVERITY", alert.severity.name, if(alert.severity == ThreatSeverity.CRITICAL) ErrorRed else WarningOrange)
                DetailRow("SOURCE", alert.source, TealAccent)
                DetailRow("STATUS", alert.status.name, TextPrimary)
                DetailRow("DETECTED", alert.timestamp.take(16).replace("T", " "), TextSecondary)
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("RECOMMENDED ACTION", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(text = alert.recommendedAction, color = TextPrimary, fontSize = 13.sp)
            }
        },
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun DetailRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun AlertSummarySection(alerts: List<Alert>) {
    val activeCount = alerts.count { it.status != AlertStatus.RESOLVED }
    val critical = alerts.count { it.severity == ThreatSeverity.CRITICAL }
    val high = alerts.count { it.severity == ThreatSeverity.HIGH }
    val medium = alerts.count { it.severity == ThreatSeverity.MEDIUM }
    val low = alerts.count { it.severity == ThreatSeverity.LOW }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text("SECURITY ALERTS", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Text(text = "$activeCount Active Alerts", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CounterItem("CRITICAL", critical, ErrorRed)
            CounterItem("HIGH", high, ErrorRed)
            CounterItem("MEDIUM", medium, WarningOrange)
            CounterItem("LOW", low, BlueAccent)
        }
    }
}

@Composable
fun CounterItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$count", color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(text = label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyAlertsState(onViewDemo: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Shield, null, tint = SuccessGreen.copy(alpha = 0.3f), modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("NO ACTIVE ALERTS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            "Your environment currently has no detected security alerts.",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onViewDemo,
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceLight)
        ) {
            Text("VIEW DEMO DATA", color = TealAccent)
        }
    }
}

@Composable
private fun AlertItem(alert: Alert, onClick: () -> Unit) {
    val color = when(alert.severity) {
        ThreatSeverity.CRITICAL -> ErrorRed
        ThreatSeverity.HIGH -> ErrorRed
        ThreatSeverity.MEDIUM -> WarningOrange
        else -> BlueAccent
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alert.severity.name,
                        color = color,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Badge(containerColor = if (alert.status == AlertStatus.OPEN) WarningOrange.copy(alpha = 0.1f) else SurfaceLight) {
                    Text(
                        text = alert.status.name,
                        color = if (alert.status == AlertStatus.OPEN) WarningOrange else TextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(text = alert.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = alert.description, color = TextSecondary, fontSize = 13.sp, maxLines = 2)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "SOURCE", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = alert.source, color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
