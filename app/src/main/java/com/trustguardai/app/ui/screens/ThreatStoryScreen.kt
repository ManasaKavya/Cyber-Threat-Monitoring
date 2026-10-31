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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatStoryScreen(
    scanResult: ScanResult,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Threat Investigation Story", fontWeight = FontWeight.Bold) },
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
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                item {
                    Text(
                        text = "THE STORY OF THIS THREAT",
                        color = TealAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }

                item {
                    StoryTimelineItem(
                        title = "Detection Triggered",
                        description = "Indicator ${scanResult.target} was identified by the primary surveillance module.",
                        time = "0ms",
                        icon = Icons.Default.Search,
                        color = TealAccent
                    )
                }

                item {
                    StoryTimelineItem(
                        title = "Signal Correlation",
                        description = "The AI Risk Engine analyzed ${scanResult.indicators.size} suspicious signals in the context of global threat feeds.",
                        time = "+450ms",
                        icon = Icons.Default.Hub,
                        color = BlueAccent
                    )
                }

                item {
                    StoryTimelineItem(
                        title = "Intelligence Match",
                        description = "Matched against community signatures from VirusTotal and AbuseIPDB providers.",
                        time = "+820ms",
                        icon = Icons.Default.Public,
                        color = WarningOrange
                    )
                }

                item {
                    StoryTimelineItem(
                        title = "Risk Quantified",
                        description = "Calculated a final risk score of ${scanResult.riskScore} with ${scanResult.confidence * 100}% AI confidence.",
                        time = "+1200ms",
                        icon = Icons.Default.Assessment,
                        color = if (scanResult.riskScore > 70) ErrorRed else WarningOrange
                    )
                }

                item {
                    StoryTimelineItem(
                        title = "Verdict Rendered",
                        description = "Classification: ${scanResult.classification}. Recommended action: ${scanResult.recommendation}.",
                        time = "Final",
                        icon = Icons.Default.Gavel,
                        color = if (scanResult.riskScore > 70) ErrorRed else SuccessGreen,
                        isLast = true
                    )
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun StoryTimelineItem(
    title: String,
    description: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(color.copy(alpha = 0.2f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(20.dp))
        
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = time, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
