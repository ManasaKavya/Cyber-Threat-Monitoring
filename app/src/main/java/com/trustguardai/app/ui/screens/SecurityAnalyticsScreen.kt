package com.trustguardai.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityAnalyticsScreen(onBack: () -> Unit) {
    var selectedRange by remember { mutableStateOf("7D") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Analytics", fontWeight = FontWeight.Bold) },
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
                    RangeSelector(selectedRange, onRangeSelected = { selectedRange = it })
                }
                
                item {
                    AnalyticsChartCard("Risk Score Trend", listOf(20, 25, 18, 30, 45, 42, 38), TealAccent)
                }
                
                item {
                    AnalyticsChartCard("Threat Volume", listOf(2, 5, 1, 8, 3, 2, 4), ErrorRed)
                }
                
                item {
                    Text("ACTIVITY DISTRIBUTION", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatMiniCard("Scans", "1,204", BlueAccent, Modifier.weight(1f))
                        StatMiniCard("Verified", "1,180", SuccessGreen, Modifier.weight(1f))
                    }
                }
                
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("AI INSIGHT", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Threat activity has increased by 14% over the last 7 days, primarily driven by malicious URL detections.",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun RangeSelector(selected: String, onRangeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("7D", "30D", "90D", "1Y").forEach { range ->
            FilterChip(
                selected = selected == range,
                onClick = { onRangeSelected(range) },
                label = { Text(range) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealAccent,
                    selectedLabelColor = Color.Black,
                    containerColor = SurfaceNavy,
                    labelColor = TextSecondary
                ),
                border = null
            )
        }
    }
}

@Composable
fun AnalyticsChartCard(title: String, data: List<Int>, color: Color) {
    GlassCard {
        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            SimpleLineChart(data, color)
        }
    }
}

@Composable
fun SimpleLineChart(data: List<Int>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val max = data.maxOrNull()?.toFloat() ?: 100f
        val stepX = size.width / (data.size - 1)
        
        for (i in 0 until data.size - 1) {
            val startX = i * stepX
            val startY = size.height - (data[i] / max * size.height)
            val endX = (i + 1) * stepX
            val endY = size.height - (data[i + 1] / max * size.height)
            
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
            
            drawCircle(
                color = color.copy(alpha = 0.1f),
                radius = 10f,
                center = Offset(startX, startY)
            )
        }
    }
}

@Composable
fun StatMiniCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(text = label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
