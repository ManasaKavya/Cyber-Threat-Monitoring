package com.trustguardai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.model.ScanType
import com.trustguardai.app.data.model.ThreatClassification
import com.trustguardai.app.data.model.ThreatSeverity
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminThreatsScreen(
    onNavigateToDna: (ScanResult) -> Unit,
    onNavigateToStory: (ScanResult) -> Unit
) {
    val api = RetrofitClient.apiService
    var realThreats by remember { mutableStateOf<List<ScanResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDemoData by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedThreat by remember { mutableStateOf<ScanResult?>(null) }

    val displayThreats = if (showDemoData || (realThreats.isEmpty() && !isLoading && errorMessage != null)) {
        DemoDataProvider.demoThreats
    } else realThreats

    LaunchedEffect(Unit) {
        try {
            // In a real app, this would be filtered for threats only
            realThreats = api.getThreatHistory().threats.filter { it.classification != ThreatClassification.SAFE }
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Access Denied or Connection Error"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkNavy)) {
        CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.05f))
        
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "THREAT INTELLIGENCE",
                color = TealAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Security Pulse",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealAccent)
                }
            } else if (errorMessage != null && !showDemoData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage!!, color = ErrorRed)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showDemoData = true }) {
                            Text("View Demo Intelligence")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ThreatSummaryCounters(displayThreats)
                    }
                    
                    item {
                        ThreatCategoryBreakdown(displayThreats)
                    }

                    if (displayThreats.isEmpty()) {
                        item {
                            EmptyThreatsState(onViewDemo = { showDemoData = true })
                        }
                    } else {
                        item {
                            Text("RECENT THREATS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        items(displayThreats) { threat ->
                            ThreatIntelligenceCard(threat, onClick = { 
                                selectedThreat = threat
                            })
                        }
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
        
        selectedThreat?.let { threat ->
            ThreatDetailsDialog(
                threat = threat,
                onDismiss = { selectedThreat = null },
                onViewDna = {
                    selectedThreat = null
                    onNavigateToDna(threat)
                },
                onViewStory = {
                    selectedThreat = null
                    onNavigateToStory(threat)
                }
            )
        }
    }
}

@Composable
fun ThreatDetailsDialog(
    threat: ScanResult,
    onDismiss: () -> Unit,
    onViewDna: () -> Unit,
    onViewStory: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onViewStory) {
                    Text("THREAT STORY", color = TealAccent, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onViewDna) {
                    Text("THREAT DNA", color = TealAccent, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = TextSecondary)
            }
        },
        title = {
            Text(text = "Threat Details", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = threat.target, color = TealAccent, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                
                ThreatDetailItem("CLASSIFICATION", threat.classification.name, if(threat.riskScore > 70) ErrorRed else WarningOrange)
                ThreatDetailItem("TYPE", threat.type.name, TextPrimary)
                ThreatDetailItem("RISK SCORE", "${threat.riskScore}/100", if(threat.riskScore > 70) ErrorRed else WarningOrange)
                ThreatDetailItem("CONFIDENCE", "${threat.confidence * 100}%", TealAccent)
                ThreatDetailItem("DETECTED", threat.timestamp.take(16).replace("T", " "), TextSecondary)
                
                if (threat.indicators.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("THREAT INDICATORS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    threat.indicators.forEach { ind ->
                        Text("• $ind", color = TextPrimary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("RECOMMENDED RESPONSE", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(text = threat.recommendation, color = TextPrimary, fontSize = 13.sp)
            }
        },
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun ThreatDetailItem(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun ThreatSummaryCounters(threats: List<ScanResult>) {
    val critical = threats.count { it.severity == ThreatSeverity.CRITICAL }
    val high = threats.count { it.severity == ThreatSeverity.HIGH }
    val medium = threats.count { it.severity == ThreatSeverity.MEDIUM }
    val low = threats.count { it.severity == ThreatSeverity.LOW }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        SummaryCounterItem("CRITICAL", critical, ErrorRed, Modifier.weight(1f))
        SummaryCounterItem("HIGH", high, ErrorRed, Modifier.weight(1f))
        SummaryCounterItem("MEDIUM", medium, WarningOrange, Modifier.weight(1f))
        SummaryCounterItem("LOW", low, BlueAccent, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCounterItem(label: String, count: Int, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$count", color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(text = label, color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ThreatCategoryBreakdown(threats: List<ScanResult>) {
    val total = if (threats.isEmpty()) 1 else threats.size
    val urlCount = threats.count { it.type == ScanType.URL }
    val ipCount = threats.count { it.type == ScanType.IP }
    val fileCount = threats.count { it.type == ScanType.FILE }
    
    // Add demo behavioral/auth for visuals if empty or as extra
    val behaviorCount = if (threats == DemoDataProvider.demoThreats) 4 else 0
    val authCount = if (threats == DemoDataProvider.demoThreats) 3 else 0

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text("THREAT CATEGORIES", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        CategoryProgressRow("URL Threats", urlCount, total)
        CategoryProgressRow("IP Threats", ipCount, total)
        CategoryProgressRow("File Threats", fileCount, total)
        CategoryProgressRow("Behavioral", behaviorCount, total)
        CategoryProgressRow("Authentication", authCount, total)
    }
}

@Composable
fun CategoryProgressRow(label: String, count: Int, total: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = TextSecondary, fontSize = 12.sp)
            Text(text = "$count", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { count.toFloat() / total.toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = TealAccent,
            trackColor = SurfaceLight
        )
    }
}

@Composable
fun EmptyThreatsState(onViewDemo: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.VerifiedUser, null, tint = SuccessGreen.copy(alpha = 0.2f), modifier = Modifier.size(70.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text("NO ACTIVE THREATS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            "Security systems report a stable environment.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onViewDemo,
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy)
        ) {
            Text("VIEW DEMO DATA", color = TealAccent)
        }
    }
}

@Composable
fun ThreatIntelligenceCard(threat: ScanResult, onClick: () -> Unit) {
    val color = when(threat.severity) {
        ThreatSeverity.CRITICAL, ThreatSeverity.HIGH -> ErrorRed
        ThreatSeverity.MEDIUM -> WarningOrange
        else -> BlueAccent
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(threat.type) {
                        ScanType.URL -> Icons.Default.Language
                        ScanType.IP -> Icons.Default.Wifi
                        else -> Icons.Default.Folder
                    },
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = threat.target, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = threat.classification.name, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(TextSecondary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Risk: ${threat.riskScore}", color = TextSecondary, fontSize = 11.sp)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}
