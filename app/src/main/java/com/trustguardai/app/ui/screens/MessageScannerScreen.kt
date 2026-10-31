package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.DemoSampleSection
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScannerScreen(onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf<MessageAnalysis?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Message Scanner", fontWeight = FontWeight.Bold) },
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
                    text = "SOCIAL ENGINEERING DETECTION",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Paste suspicious SMS or Email content here...", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DemoSampleSection(
                    onSafeClick = { messageText = DemoDataProvider.msgSafe },
                    onRiskClick = { messageText = DemoDataProvider.msgRisk }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        isAnalyzing = true
                        // Local rule-based analysis simulation
                        val result = performLocalMessageAnalysis(messageText)
                        analysisResult = result
                        isAnalyzing = false
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    enabled = !isAnalyzing && messageText.isNotBlank()
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Text("ANALYZE MESSAGE", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
                
                analysisResult?.let { result ->
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    GlassCard {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("THREAT ANALYSIS", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text(result.classification, color = result.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("RISK SCORE: ${result.score}%", color = result.color, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text("DETECTED SIGNALS", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        result.signals.forEach { signal ->
                            Text("• $signal", color = TextSecondary, fontSize = 12.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("RECOMMENDATION", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(result.recommendation, color = result.color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

data class MessageAnalysis(
    val classification: String,
    val score: Int,
    val signals: List<String>,
    val recommendation: String,
    val color: Color
)

fun performLocalMessageAnalysis(text: String): MessageAnalysis {
    val signals = mutableListOf<String>()
    var score = 0
    val lower = text.lowercase()
    
    if (lower.contains("urgent") || lower.contains("immediately") || lower.contains("suspended")) {
        score += 30
        signals.add("Urgent/Threatening language")
    }
    if (lower.contains("http") || lower.contains("www") || lower.contains(".com") || lower.contains(".xyz")) {
        score += 20
        signals.add("Embedded links detected")
    }
    if (lower.contains("login") || lower.contains("verify") || lower.contains("account")) {
        score += 25
        signals.add("Credential request pattern")
    }
    if (lower.contains("invoice") || lower.contains("payment") || lower.contains("prize")) {
        score += 20
        signals.add("Financial/Social engineering theme")
    }
    
    score = score.coerceIn(5, 100)
    
    return when {
        score >= 70 -> MessageAnalysis("MALICIOUS", score, signals, "Do not reply or click any links. Delete immediately.", ErrorRed)
        score >= 35 -> MessageAnalysis("SUSPICIOUS", score, signals, "Exercise extreme caution. Verify sender offline.", WarningOrange)
        else -> MessageAnalysis("SAFE", score, listOf("No major threat patterns detected"), "Likely safe, but stay vigilant.", SuccessGreen)
    }
}
