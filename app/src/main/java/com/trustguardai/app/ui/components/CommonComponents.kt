package com.trustguardai.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.theme.*

@Composable
fun CyberGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val spacing = 80f
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = TealAccent.copy(alpha = 0.08f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += spacing
        }
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = TealAccent.copy(alpha = 0.08f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += spacing
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, GlassBorder, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceNavy.copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun CyberShield(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    icon: ImageVector = Icons.Default.Security
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .size(size * 0.8f)
                .blur(30.dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.15f))
        )
        
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = TealAccent.copy(alpha = 0.3f),
                startAngle = rotateAngle,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
            drawArc(
                color = TealAccent.copy(alpha = 0.3f),
                startAngle = rotateAngle + 180f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TealAccent,
            modifier = Modifier
                .size(size * 0.5f)
                .scale(pulseScale)
        )
    }
}

@Composable
fun SecurityScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val animatedScore = animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "score"
    )

    val color = when {
        score >= 80 -> SeveritySafe
        score >= 40 -> SeverityMedium
        else -> SeverityCritical
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size * 0.85f)) {
            drawArc(
                color = SurfaceLight,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (animatedScore.value / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = animatedScore.value.toInt().toString(),
                color = TextPrimary,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "SECURITY SCORE",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun StatusPulse(
    modifier: Modifier = Modifier,
    color: Color = SeveritySafe
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.3f))
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha))
        )
    }
}

@Composable
fun DemoSampleSection(
    onSafeClick: () -> Unit,
    onRiskClick: () -> Unit,
    label: String = "TRY SAMPLE"
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SampleButton("🟢 SAFE EXAMPLE", onSafeClick, Modifier.weight(1f))
            SampleButton("🔴 RISK EXAMPLE", onRiskClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SampleButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceLight)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
