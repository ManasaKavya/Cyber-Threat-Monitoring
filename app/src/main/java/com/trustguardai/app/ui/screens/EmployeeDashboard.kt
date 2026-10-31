package com.trustguardai.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.EmployeeSummary
import com.trustguardai.app.data.model.ThreatSeverity
import com.trustguardai.app.data.repository.DailyTipProvider
import com.trustguardai.app.ui.components.*
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun EmployeeDashboard(
    onLogout: () -> Unit,
    onNavigateToUrlScan: () -> Unit,
    onNavigateToIpScan: () -> Unit,
    onNavigateToFileScan: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val api = RetrofitClient.apiService
    var summary by remember { mutableStateOf<EmployeeSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isCheckingSec by remember { mutableStateOf(false) }
    var checkupReport by remember { mutableStateOf<CheckupResult?>(null) }
    val scope = rememberCoroutineScope()
    val dailyTip = remember { DailyTipProvider.getDailyTip() }

    fun loadData() {
        scope.launch {
            isLoading = true
            isError = false
            try {
                summary = api.getEmployeeSummary()
            } catch (e: HttpException) {
                isError = true
            } catch (e: Exception) {
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkNavy)) {
        CyberGrid(modifier = Modifier.fillMaxSize())

        if (isLoading && summary == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    DashboardHeader(onLogout)
                }

                summary?.let { s ->
                    item {
                        SecurityScoreRing(score = s.riskScore, modifier = Modifier.fillMaxWidth())
                    }
                    
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DashboardStatCard("Scans", "${s.scansCount}", Icons.Default.Wifi, BlueAccent, Modifier.weight(1f))
                            DashboardStatCard("Threats", "${s.threatsCount}", Icons.Default.BugReport, ErrorRed, Modifier.weight(1f))
                            DashboardStatCard("Alerts", "${s.alertsCount}", Icons.Default.Notifications, WarningOrange, Modifier.weight(1f))
                        }
                    }
                }

                // SECURE ME Action
                item {
                    SecureMeButton(
                        isScanning = isCheckingSec,
                        onClick = {
                            scope.launch {
                                isCheckingSec = true
                                delay(3000) // Animated assessment
                                checkupReport = CheckupResult(87, "GOOD", listOf("Strong passwords", "2FA Active"), listOf("Public Wi-Fi detected"))
                                isCheckingSec = false
                            }
                        }
                    )
                }

                checkupReport?.let { report ->
                    item {
                        SecurityCheckupReportCard(report, onDismiss = { checkupReport = null })
                    }
                }

                item {
                    AiAdvisorSection(summary)
                }

                item {
                    DailyTipCard(dailyTip.title, dailyTip.description)
                }

                item {
                    Text("Quick Tools", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickToolIconCard("URL", Icons.Default.Language, TealAccent, onNavigateToUrlScan, Modifier.weight(1f))
                        QuickToolIconCard("IP", Icons.Default.Wifi, BlueAccent, onNavigateToIpScan, Modifier.weight(1f))
                        QuickToolIconCard("FILE", Icons.Default.Folder, WarningOrange, onNavigateToFileScan, Modifier.weight(1f))
                    }
                }

                summary?.let { s ->
                    if (s.recentActivities.isNotEmpty()) {
                        item {
                            Text("Real-Time Signals", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        items(s.recentActivities.reversed().take(3)) { activity ->
                            ActivityLogItem(activity)
                        }
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
fun DashboardHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = TextSecondary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("AGENT TERMINAL", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Welcome Back", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPulse()
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.Logout, null, tint = TextSecondary)
            }
        }
    }
}

@Composable
fun SecureMeButton(isScanning: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
        enabled = !isScanning
    ) {
        if (isScanning) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text("ANALYZING SECURITY...", color = Color.Black, fontWeight = FontWeight.Black)
        } else {
            Icon(Icons.Default.Bolt, null, tint = Color.Black)
            Spacer(modifier = Modifier.width(12.dp))
            Text("SECURE ME", color = Color.Black, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
    }
}

data class CheckupResult(val score: Int, val status: String, val strengths: List<String>, val weaknesses: List<String>)

@Composable
fun SecurityCheckupReportCard(result: CheckupResult, onDismiss: () -> Unit) {
    GlassCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("CHECKUP COMPLETE", color = TealAccent, fontSize = 12.sp, fontWeight = FontWeight.Black)
            IconButton(onClick = onDismiss, modifier = Modifier.size(16.dp)) {
                Icon(Icons.Default.Close, null, tint = TextSecondary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${result.score}", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("POSTURE: ${result.status}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Reliability: High", color = TextSecondary, fontSize = 11.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("STRENGTHS", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        result.strengths.forEach { Text("• $it", color = TextSecondary, fontSize = 12.sp) }
        
        if (result.weaknesses.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("VULNERABILITIES", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            result.weaknesses.forEach { Text("• $it", color = TextSecondary, fontSize = 12.sp) }
        }
    }
}

@Composable
fun AiAdvisorSection(summary: EmployeeSummary?) {
    Column {
        Text("AI SECURITY ADVISOR", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = TealAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if ((summary?.riskScore ?: 0) > 40) 
                        "High priority risk factors detected. Review your recent scan history immediately." 
                        else "Your security posture is good. Keep up the high scan frequency to maintain protection.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun DailyTipCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(TealAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lightbulb, null, tint = TealAccent)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("DAILY SECURITY TIP", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun DashboardStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickToolIconCard(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
