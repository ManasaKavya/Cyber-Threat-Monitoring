package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.repository.ApiThreatRepository
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.data.repository.ThreatRepository
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.DemoSampleSection
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.components.RiskGauge
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkSafetyScreen(onBack: () -> Unit) {
    var url by remember { mutableStateOf("") }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository: ThreatRepository = remember { ApiThreatRepository() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Link Safety Preview", fontWeight = FontWeight.Bold) },
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
            
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text(
                    text = "SAFE VISUAL INSPECTION",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL to Preview") },
                    leadingIcon = { Icon(Icons.Default.Language, null, tint = TealAccent) },
                    placeholder = { Text("https://...", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DemoSampleSection(
                    onSafeClick = { url = DemoDataProvider.urlSafe.input },
                    onRiskClick = { url = DemoDataProvider.urlRisk.input }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        isScanning = true
                        scope.launch {
                            try {
                                scanResult = repository.scanUrl(url)
                            } catch (e: Exception) {}
                            finally { isScanning = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    enabled = !isScanning && url.isNotBlank()
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Visibility, null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("ANALYZE SAFELY", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
                
                scanResult?.let { result ->
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    RiskGauge(score = result.riskScore, modifier = Modifier.fillMaxWidth())
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    GlassCard {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("THREAT ANALYSIS", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text(result.classification.name, color = if(result.riskScore > 70) ErrorRed else WarningOrange, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("SIGNALS OBSERVED", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        result.indicators.forEach { signal ->
                            Text("• $signal", color = TextSecondary, fontSize = 12.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("RECOMMENDATION", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(result.recommendation, color = TealAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
