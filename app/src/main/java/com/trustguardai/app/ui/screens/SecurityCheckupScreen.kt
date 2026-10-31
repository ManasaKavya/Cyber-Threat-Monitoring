package com.trustguardai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.components.GlassCard
import com.trustguardai.app.ui.components.SecurityScoreRing
import com.trustguardai.app.ui.theme.*

data class CheckupQuestion(
    val id: Int,
    val question: String,
    val impact: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCheckupScreen(onBack: () -> Unit) {
    val questions = listOf(
        CheckupQuestion(1, "Do you use unique passwords for all accounts?", 20),
        CheckupQuestion(2, "Is Two-Factor Authentication (2FA) enabled?", 25),
        CheckupQuestion(3, "Do you avoid using public Wi-Fi for work?", 15),
        CheckupQuestion(4, "Do you verify links before clicking them?", 20),
        CheckupQuestion(5, "Is your device OS currently up to date?", 20)
    )

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Checkup", fontWeight = FontWeight.Bold) },
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
            
            if (!isComplete) {
                val currentQuestion = questions[currentQuestionIndex]
                
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "QUESTION ${currentQuestionIndex + 1} of ${questions.size}",
                        color = TealAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = currentQuestion.question,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = {
                            score += currentQuestion.impact
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                isComplete = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Text("YES, ALWAYS", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                isComplete = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceLight)
                    ) {
                        Text("NO / NOT SURE", color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                CheckupResultView(score = score, onFinish = onBack)
            }
        }
    }
}

@Composable
fun CheckupResultView(score: Int, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "ASSESSMENT COMPLETE",
            color = SuccessGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SecurityScoreRing(score = score)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("AI RECOMMENDATION", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (score >= 80) "Your security habits are excellent. Maintain your current posture." 
                       else "Critical vulnerabilities found in personal security habits. Prioritize enabling 2FA.",
                color = TextPrimary,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceLight)
        ) {
            Text("FINISH", color = TextPrimary, fontWeight = FontWeight.Black)
        }
    }
}
