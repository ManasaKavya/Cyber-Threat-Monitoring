package com.trustguardai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
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
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.data.repository.DemoScenario
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.ResultCard
import com.trustguardai.app.ui.components.RiskGauge
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentationDemoScreen(onBack: () -> Unit) {
    var selectedScenario by remember { mutableStateOf<DemoScenario?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val safeScenarios = listOf(
        DemoDataProvider.urlSafe,
        DemoDataProvider.ipSafe,
        DemoDataProvider.fileSafe,
        DemoDataProvider.msgSafeScenario,
        DemoDataProvider.qrSafe
    )
    
    val riskScenarios = listOf(
        DemoDataProvider.urlRisk,
        DemoDataProvider.ipRisk,
        DemoDataProvider.fileRisk,
        DemoDataProvider.msgRiskScenario,
        DemoDataProvider.qrRisk
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presentation Demo Mode", fontWeight = FontWeight.Bold) },
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
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ACADEMIC DEMONSTRATION",
                        color = TealAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "End-to-End Security Flows",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Text("SAFE SCENARIOS", color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(safeScenarios) { scenario ->
                    ScenarioCard(scenario) {
                        scope.launch {
                            selectedScenario = null
                            isAnalyzing = true
                            delay(2000)
                            selectedScenario = scenario
                            isAnalyzing = false
                        }
                    }
                }

                item {
                    Text("RISK SCENARIOS", color = ErrorRed, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(riskScenarios) { scenario ->
                    ScenarioCard(scenario) {
                        scope.launch {
                            selectedScenario = null
                            isAnalyzing = true
                            delay(2000)
                            selectedScenario = scenario
                            isAnalyzing = false
                        }
                    }
                }

                if (isAnalyzing) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TealAccent)
                        }
                    }
                }

                selectedScenario?.let { scenario ->
                    item {
                        Divider(color = SurfaceLight)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "SIMULATED DEMONSTRATION",
                            color = WarningOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        RiskGauge(score = scenario.result.riskScore)
                        Spacer(modifier = Modifier.height(24.dp))
                        ResultCard(result = scenario.result)
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun ScenarioCard(scenario: DemoScenario, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    .background(TealAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = TealAccent)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = scenario.title, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(text = scenario.input, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}
