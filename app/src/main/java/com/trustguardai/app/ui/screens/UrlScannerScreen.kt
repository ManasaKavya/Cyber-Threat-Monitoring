package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.repository.ApiThreatRepository
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.data.repository.ThreatRepository
import com.trustguardai.app.ui.components.DemoSampleSection
import com.trustguardai.app.ui.components.ResultCard
import com.trustguardai.app.ui.components.RiskGauge
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlScannerScreen(onBack: () -> Unit) {
    var url by remember { mutableStateOf("") }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repository: ThreatRepository = remember { ApiThreatRepository() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Website Scanner", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Analyze URL",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://example.com", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = TealAccent) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealAccent,
                                unfocusedBorderColor = SurfaceLight,
                                cursorColor = TealAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DemoSampleSection(
                            onSafeClick = { url = DemoDataProvider.urlSafe.input },
                            onRiskClick = { url = DemoDataProvider.urlRisk.input }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (url.isNotBlank()) {
                                    isScanning = true
                                    errorMessage = null
                                    scope.launch {
                                        try {
                                            scanResult = repository.scanUrl(url)
                                        } catch (e: HttpException) {
                                            errorMessage = if (e.code() == 403) "Access Denied" else "Server Error (${e.code()})"
                                        } catch (e: Exception) {
                                            errorMessage = "Connection Error"
                                        } finally {
                                            isScanning = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = !isScanning && url.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Text("START SECURITY SCAN", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = errorMessage!!, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        if (scanResult != null || isScanning) {
                            TextButton(
                                onClick = {
                                    url = ""
                                    scanResult = null
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Clear Analysis", color = TextSecondary)
                            }
                        }
                    }
                }
            }

            if (isScanning) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text("Analyzing threat indicators...", color = TealAccent, fontWeight = FontWeight.Medium)
                }
            }

            scanResult?.let { result ->
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    RiskGauge(score = result.riskScore)
                    Spacer(modifier = Modifier.height(24.dp))
                    ResultCard(result = result)
                }
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
