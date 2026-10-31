package com.trustguardai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.model.Incident
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentCopilotScreen(
    incident: Incident,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Incident Copilot", fontWeight = FontWeight.Bold) },
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
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = TealAccent, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "INTELLIGENT ANALYSIS",
                        color = TealAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                CopilotCard(
                    title = "WHAT HAPPENED?",
                    content = "A high-severity incident was triggered by ${incident.detectionSource}. The activity involved ${incident.title} with a calculated risk impact of ${incident.riskScore}/100."
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                CopilotCard(
                    title = "WHY IS IT SUSPICIOUS?",
                    content = "Correlation engine identified ${incident.threatIndicators.size} critical indicators. Behavior deviates significantly from baseline patterns recorded in organizational metrics."
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                CopilotCard(
                    title = "RECOMMENDED RESPONSE",
                    content = incident.recommendedResponse,
                    color = ErrorRed
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = { /* Auto-mitigate */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("EXECUTE MITIGATION", color = Color.White, fontWeight = FontWeight.Black)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CopilotCard(title: String, content: String, color: Color = TealAccent) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = content, color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp)
    }
}
