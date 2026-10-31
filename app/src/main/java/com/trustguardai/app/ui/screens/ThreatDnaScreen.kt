package com.trustguardai.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatDnaScreen(
    scanResult: ScanResult,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Threat DNA Analysis", fontWeight = FontWeight.Bold) },
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
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VISUAL IDENTIFICATION",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // DNA Visualization
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DnaVisualization(scanResult)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Analysis Details
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "THREAT FINGERPRINT", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DnaDetailRow("Classification", scanResult.classification.name, if(scanResult.riskScore > 70) ErrorRed else WarningOrange)
                    DnaDetailRow("Risk Score", "${scanResult.riskScore}/100", TealAccent)
                    DnaDetailRow("Type", scanResult.type.name, BlueAccent)
                    DnaDetailRow("Confidence", "${scanResult.confidence * 100}%", TealAccent)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Recommended Response: ${scanResult.recommendation}",
                    color = if(scanResult.riskScore > 70) ErrorRed else WarningOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DnaDetailRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DnaVisualization(scanResult: ScanResult) {
    val infiniteTransition = rememberInfiniteTransition(label = "dna")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val color = if (scanResult.riskScore > 70) ErrorRed else WarningOrange

    Canvas(modifier = Modifier.size(240.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        
        // Central Node
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = 60f * pulse,
            center = center
        )
        drawCircle(
            color = color,
            radius = 30f,
            center = center
        )
        
        // Outer attribute nodes
        val attributes = listOf("Source", "Behavior", "Hash", "Network", "Entropy", "Signature")
        attributes.forEachIndexed { index, attr ->
            val angle = (rotation + (index * 60f)).toDouble() * (Math.PI / 180)
            val radius = 100f * pulse
            val x = (center.x + radius * cos(angle)).toFloat()
            val y = (center.y + radius * sin(angle)).toFloat()
            val attrPos = Offset(x, y)
            
            // Connection
            drawLine(
                color = color.copy(alpha = 0.3f),
                start = center,
                end = attrPos,
                strokeWidth = 2f
            )
            
            // Node
            drawCircle(
                color = color.copy(alpha = 0.1f),
                radius = 15f,
                center = attrPos
            )
            drawCircle(
                color = TealAccent,
                radius = 4f,
                center = attrPos
            )
        }
    }
}
