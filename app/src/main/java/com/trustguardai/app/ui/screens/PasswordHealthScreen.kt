package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.DemoSampleSection
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordHealthScreen(onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val strength = calculatePasswordStrength(password)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Password Health", fontWeight = FontWeight.Bold) },
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
                    text = "OFFLINE SECURITY CHECK",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Test your password strength without sending data to our servers.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = TealAccent) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DemoSampleSection(
                    onSafeClick = { password = DemoDataProvider.pwdStrong },
                    onRiskClick = { password = DemoDataProvider.pwdWeak }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Strength Indicator
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("STRENGTH", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(strength.label.uppercase(), color = strength.color, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { strength.score / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = strength.color,
                    trackColor = SurfaceLight
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Recommendations
                Text("ANALYSIS RESULTS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                PasswordRequirementRow("At least 12 characters", password.length >= 12)
                PasswordRequirementRow("Includes symbols ($%#)", password.any { !it.isLetterOrDigit() })
                PasswordRequirementRow("Includes numbers (0-9)", password.any { it.isDigit() })
                PasswordRequirementRow("Uppercase & Lowercase", password.any { it.isUpperCase() } && password.any { it.isLowerCase() })
                
                Spacer(modifier = Modifier.height(40.dp))
                
                GlassCard {
                    Text("WHY STRENGTH MATTERS", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Weak passwords are prone to brute-force and credential stuffing attacks. Aim for complexity and uniqueness.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordRequirementRow(text: String, met: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (met) SuccessGreen else SurfaceLight)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = if (met) TextPrimary else TextSecondary, fontSize = 14.sp)
    }
}

data class StrengthResult(val score: Int, val label: String, val color: Color)

fun calculatePasswordStrength(password: String): StrengthResult {
    if (password.isEmpty()) return StrengthResult(0, "Empty", TextSecondary)
    
    var score = 0
    if (password.length >= 8) score += 20
    if (password.length >= 12) score += 20
    if (password.any { it.isDigit() }) score += 20
    if (password.any { !it.isLetterOrDigit() }) score += 20
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score += 20
    
    return when {
        score >= 100 -> StrengthResult(100, "Excellent", SuccessGreen)
        score >= 80 -> StrengthResult(80, "Strong", SuccessGreen)
        score >= 60 -> StrengthResult(60, "Fair", WarningOrange)
        score >= 40 -> StrengthResult(40, "Weak", ErrorRed)
        else -> StrengthResult(20, "Critical", ErrorRed)
    }
}
