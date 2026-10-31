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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.AlertStatus
import com.trustguardai.app.data.model.Incident
import com.trustguardai.app.data.model.ThreatSeverity
import com.trustguardai.app.ui.components.RiskGauge
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailsScreen(
    incidentId: String,
    onBack: () -> Unit
) {
    val api = RetrofitClient.apiService
    var incident by remember { mutableStateOf<Incident?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(incidentId) {
        scope.launch {
            try {
                // In this demo, if alertId passed, we try to find a matching incident
                val allIncidents = api.getIncidents()
                incident = allIncidents.find { it.title.contains(incidentId) } ?: allIncidents.firstOrNull()
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incident Analysis", fontWeight = FontWeight.Bold) },
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealAccent)
            }
        } else if (incident == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Incident not found", color = TextSecondary)
            }
        } else {
            val inc = incident!!
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = inc.title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(containerColor = if (inc.severity == ThreatSeverity.CRITICAL) ErrorRed else WarningOrange) {
                            Text(inc.severity.name, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Status: ${inc.status.name}", color = TealAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    RiskGauge(score = inc.riskScore, modifier = Modifier.fillMaxWidth().height(160.dp))
                }

                item {
                    Text("Timeline", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceNavy)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            inc.timeline.forEach { entry ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Text(text = entry["time"]?.take(16) ?: "", color = TealAccent, fontSize = 10.sp, modifier = Modifier.width(100.dp))
                                    Text(text = entry["event"] ?: "", color = TextPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Threat Indicators", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    inc.threatIndicators.forEach { ind ->
                        Text(text = "• $ind", color = TextSecondary, fontSize = 14.sp)
                    }
                }

                item {
                    Text("Recommended Response", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = inc.recommendedResponse, color = WarningOrange, fontWeight = FontWeight.Medium)
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { /* Update status */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                        ) {
                            Text("Acknowledge", color = Color.Black)
                        }
                        Button(
                            onClick = { /* Update status */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceLight)
                        ) {
                            Text("Resolve")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
