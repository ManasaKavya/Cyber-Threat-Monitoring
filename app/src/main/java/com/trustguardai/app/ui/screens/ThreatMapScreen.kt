package com.trustguardai.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.theme.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatMapScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Threat Map", fontWeight = FontWeight.Bold) },
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
            CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.1f))
            
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MapStatItem("Live Nodes", "1,240", TealAccent)
                    MapStatItem("Malware", "86", ErrorRed)
                    MapStatItem("Phishing", "142", WarningOrange)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Map Container
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        WorldMapCanvas()
                        
                        Text(
                            text = "DEMO THREAT VISUALIZATION",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Legend
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    LegendItem("Malware", ErrorRed)
                    LegendItem("Phishing", WarningOrange)
                    LegendItem("Suspicious IP", BlueAccent)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MapStatItem(label: String, value: String, color: Color) {
    Column {
        Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun WorldMapCanvas() {
    val infiniteTransition = rememberInfiniteTransition(label = "map")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val seed = 42L
        val random = Random(seed)
        
        repeat(15) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            val color = listOf(ErrorRed, WarningOrange, BlueAccent).random(random)
            
            drawCircle(
                color = color.copy(alpha = 0.2f * pulse),
                radius = 40f * pulse,
                center = Offset(x, y)
            )
            drawCircle(
                color = color,
                radius = 6f,
                center = Offset(x, y)
            )
            
            drawLine(
                color = color.copy(alpha = 0.1f),
                start = Offset(x, y),
                end = Offset(size.width / 2, size.height / 2),
                strokeWidth = 1f
            )
        }
    }
}
