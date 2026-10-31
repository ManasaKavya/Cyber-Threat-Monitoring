package com.trustguardai.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.model.ActivityLog
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.model.ThreatClassification
import com.trustguardai.app.data.model.ThreatSeverity
import com.trustguardai.app.ui.theme.*

@Composable
fun RiskGauge(score: Int, modifier: Modifier = Modifier) {
    val animatedScore = remember { Animatable(0f) }
    
    LaunchedEffect(score) {
        animatedScore.animateTo(score.toFloat(), animationSpec = tween(1000))
    }

    val color = when {
        score >= 80 -> ErrorRed
        score >= 40 -> WarningOrange
        else -> TealAccent
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawArc(
                color = SurfaceLight,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = (animatedScore.value / 100) * 270f,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = animatedScore.value.toInt().toString(),
                color = TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "RISK SCORE",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ResultCard(result: ScanResult) {
    val accent = when (result.classification) {
        ThreatClassification.MALICIOUS -> ErrorRed
        ThreatClassification.SUSPICIOUS -> WarningOrange
        ThreatClassification.SAFE -> TealAccent
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = result.severity.name,
                        color = accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "CONFIDENCE: ${result.confidence * 100}%",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Target Identification",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = result.target,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            if (result.indicators.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Intelligence Signals",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                result.indicators.forEach { indicator ->
                    Text(text = "• $indicator", color = TextPrimary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AI Recommendation",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = result.recommendation,
                color = accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (result.metadata.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SurfaceLight, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
                result.metadata.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = key, color = TextSecondary, fontSize = 11.sp)
                        Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(log: ActivityLog) {
    val color = when(log.severity) {
        ThreatSeverity.CRITICAL, ThreatSeverity.HIGH -> ErrorRed
        ThreatSeverity.MEDIUM -> WarningOrange
        else -> BlueAccent
    }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = when(log.activityType.name) {
                    "LOGIN" -> Icons.Default.Login
                    "SCAN_URL" -> Icons.Default.Language
                    "SCAN_IP" -> Icons.Default.Wifi
                    "SCAN_FILE" -> Icons.Default.Folder
                    else -> Icons.Default.Security
                },
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(log.activityType.name.replace("_", " "), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(log.resource ?: "System Access", color = TextSecondary, fontSize = 11.sp, maxLines = 1)
        }
        Text(log.severity.name, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
