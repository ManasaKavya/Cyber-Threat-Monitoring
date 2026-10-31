package com.trustguardai.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScannerScreen(onBack: () -> Unit) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository: ThreatRepository = remember { ApiThreatRepository() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            isScanning = true
            errorMessage = null
            scope.launch {
                try {
                    scanResult = repository.scanFile(context, it)
                } catch (e: HttpException) {
                    errorMessage = "Server Error (${e.code()})"
                } catch (e: Exception) {
                    errorMessage = "Upload failed. Check connection."
                } finally {
                    isScanning = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sandbox Analyzer", fontWeight = FontWeight.Bold) },
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
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(WarningOrange.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FileUpload, null, tint = WarningOrange, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Safe Sandbox Analysis",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Deep static inspection without execution",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        DemoSampleSection(
                            onSafeClick = {
                                isScanning = true
                                scope.launch {
                                    delay(2000)
                                    scanResult = DemoDataProvider.fileSafe.result
                                    isScanning = false
                                }
                            },
                            onRiskClick = {
                                isScanning = true
                                scope.launch {
                                    delay(2000)
                                    scanResult = DemoDataProvider.fileRisk.result
                                    isScanning = false
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { launcher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = !isScanning,
                            colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("INITIATE SANDBOX SCAN", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = errorMessage!!, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            if (isScanning) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Performing static analysis...", color = WarningOrange, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(color = WarningOrange, trackColor = SurfaceLight, modifier = Modifier.fillMaxWidth().height(4.dp))
                    }
                }
            }

            scanResult?.let { result ->
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, null, tint = TealAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INTELLIGENT RISK VERDICT",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    RiskGauge(score = result.riskScore)
                    Spacer(modifier = Modifier.height(24.dp))
                    ResultCard(result = result)
                }
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
