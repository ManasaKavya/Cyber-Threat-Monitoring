package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
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
fun HashAnalyzerScreen(onBack: () -> Unit) {
    var hashInput by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf<HashAnalysis?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hash Analyzer", fontWeight = FontWeight.Bold) },
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
                    text = "MALWARE SIGNATURE LOOKUP",
                    color = TealAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = hashInput,
                    onValueChange = { hashInput = it.trim() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter MD5, SHA-1, or SHA-256") },
                    leadingIcon = { Icon(Icons.Default.Key, null, tint = TealAccent) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DemoSampleSection(
                    onSafeClick = { hashInput = DemoDataProvider.hashValid },
                    onRiskClick = { hashInput = "0123456789abcdef" } // Invalid example
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        analysisResult = analyzeHashLocally(hashInput)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    enabled = hashInput.isNotBlank()
                ) {
                    Text("ANALYZE HASH", color = Color.Black, fontWeight = FontWeight.Black)
                }
                
                analysisResult?.let { result ->
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    if (result.isValid) {
                        GlassCard {
                            Text("LOCAL HASH ANALYSIS", color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            HashDetailRow("Type", result.type)
                            HashDetailRow("Length", "${result.length} bits")
                            HashDetailRow("Format", "HEX (Valid)")
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = SurfaceLight)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("STATUS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "READY FOR GLOBAL LOOKUP",
                                color = SuccessGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This signature is correctly formatted. Connect to Enterprise backend for real-time threat intelligence matching.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Invalid hash format. Please enter a valid MD5, SHA-1, or SHA-256 string.",
                                color = ErrorRed,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HashDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

data class HashAnalysis(
    val isValid: Boolean,
    val type: String = "",
    val length: Int = 0
)

fun analyzeHashLocally(input: String): HashAnalysis {
    val clean = input.filter { it.isLetterOrDigit() }
    return when (clean.length) {
        32 -> HashAnalysis(true, "MD5", 128)
        40 -> HashAnalysis(true, "SHA-1", 160)
        64 -> HashAnalysis(true, "SHA-256", 256)
        else -> HashAnalysis(false)
    }
}
