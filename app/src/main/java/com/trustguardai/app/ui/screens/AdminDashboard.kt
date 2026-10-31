package com.trustguardai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.*
import com.trustguardai.app.data.repository.SessionManager
import com.trustguardai.app.ui.components.*
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboard(
    onLogout: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToPresentationDemo: () -> Unit
) {
    val api = RetrofitClient.apiService
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userRole = remember { sessionManager.getUser()?.role ?: UserRole.EMPLOYEE }

    var overview by remember { mutableStateOf<AdminRiskOverview?>(null) }
    var employeeRisks by remember { mutableStateOf<List<BehavioralRisk>>(emptyList()) }
    var activities by remember { mutableStateOf<List<ActivityLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isPolling by remember { mutableStateOf(false) }
    var lastUpdated by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    suspend fun refreshData() {
        if (userRole != UserRole.ADMIN) {
            errorMessage = "Access Denied: Admin role required."
            isLoading = false
            return
        }

        if (!isPolling) isLoading = true
        try {
            supervisorScope {
                val overviewDeferred = async { api.getAdminRiskOverview() }
                val risksDeferred = async { api.getEmployeeRisks() }
                val activitiesDeferred = async { api.getAdminActivities() }
                
                try {
                    overview = overviewDeferred.await()
                    employeeRisks = risksDeferred.await()
                    activities = activitiesDeferred.await()
                    lastUpdated = dateFormat.format(Date())
                    errorMessage = null
                } catch (e: HttpException) {
                    if (e.code() == 403) {
                        errorMessage = "Access Denied: Insufficient permissions."
                    } else {
                        errorMessage = "Server Error (${e.code()})"
                    }
                } catch (e: Exception) {
                    errorMessage = "Data Load Failure"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Unable to connect to security server."
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (userRole == UserRole.ADMIN) {
            refreshData()
            while(true) {
                delay(30000)
                refreshData()
            }
        } else {
            errorMessage = "Access Denied: Admin privileges required."
            isLoading = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "soc")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        CyberGrid(modifier = Modifier.fillMaxSize())

        if (isLoading && overview == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealAccent)
            }
        } else if (errorMessage != null && overview == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.Lock, null, tint = ErrorRed, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage!!, color = ErrorRed, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        scope.launch {
                            isPolling = false
                            isLoading = true
                            refreshData() 
                        }
                    }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SocHeader(onLogout, lastUpdated)
                }

                overview?.let { ov ->
                    item {
                        GlobalRiskOverviewCard(ov.orgRiskScore, glow)
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SocStatCard("Active Threats", "${ov.activeThreats}", Icons.Default.Warning, ErrorRed, Modifier.weight(1f))
                            SocStatCard("Critical Alerts", "${ov.criticalAlertsCount}", Icons.Default.NotificationsActive, WarningOrange, Modifier.weight(1f))
                        }
                    }
                    
                    item {
                        Button(
                            onClick = onNavigateToAlerts,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy)
                        ) {
                            Text("VIEW ALL ALERTS", color = TealAccent, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        PredictiveOutlookCard(ov.orgRiskScore)
                    }

                    item {
                        Card(
                            onClick = onNavigateToPresentationDemo,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(TealAccent.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DesktopWindows, null, tint = TealAccent)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("PRESENTATION DEMO MODE", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                    Text("Simulated scenarios for demo", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (activities.isNotEmpty()) {
                    item {
                        Text("LIVE SECURITY FEED", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    }
                    items(activities.take(5)) { log ->
                        ActivityLogItem(log)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun SocHeader(onLogout: () -> Unit, lastUpdated: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPulse(modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("LIVE SOC MONITORING", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            }
            Text("Admin Command", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            if (lastUpdated.isNotEmpty()) {
                Text("Sync: $lastUpdated", color = TextSecondary, fontSize = 10.sp)
            }
        }
        IconButton(onClick = onLogout) {
            Icon(Icons.Default.Logout, null, tint = TextSecondary)
        }
    }
}

@Composable
fun GlobalRiskOverviewCard(score: Int, glow: Float) {
    GlassCard {
        Text("ORGANIZATIONAL SECURITY SCORE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SecurityScoreRing(score = score, size = 160.dp, modifier = Modifier.weight(1f))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RiskMetricRow("Malware", "LOW", SuccessGreen)
                RiskMetricRow("Phishing", "MED", WarningOrange)
                RiskMetricRow("Insider", "HIGH", ErrorRed)
                RiskMetricRow("Network", "LOW", SuccessGreen)
            }
        }
    }
}

@Composable
fun RiskMetricRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun SocStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PredictiveOutlookCard(currentScore: Int) {
    GlassCard {
        Text("NEXT 24H SECURITY OUTLOOK", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Projected Risk", color = TextSecondary, fontSize = 11.sp)
                Text("${currentScore + 2}", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Trend", color = TextSecondary, fontSize = 11.sp)
                Text("↑ INCREASING", color = WarningOrange, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Increased threat activity expected from external IP ranges. Advise high vigilance for employees.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
