package com.trustguardai.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.repository.ApiThreatRepository
import com.trustguardai.app.data.repository.DemoDataProvider
import com.trustguardai.app.data.repository.ThreatRepository
import com.trustguardai.app.ui.components.DemoSampleSection
import com.trustguardai.app.ui.components.ResultCard
import com.trustguardai.app.ui.components.RiskGauge
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository: ThreatRepository = remember { ApiThreatRepository() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var isScanning by remember { mutableStateOf(true) }
    var scannedContent by remember { mutableStateOf<String?>(null) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure QR Scanner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TealAccent
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hasCameraPermission) {
                if (isScanning) {
                    CameraPreview(
                        onBarcodeDetected = { content ->
                            scannedContent = content
                            isScanning = false
                        }
                    )
                    QrScanningFrame()
                    
                    // Demo Simulation Overlay
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.BottomCenter) {
                        DemoSampleSection(
                            label = "SIMULATE SCAN",
                            onSafeClick = {
                                scannedContent = DemoDataProvider.urlSafe.input
                                isScanning = false
                            },
                            onRiskClick = {
                                scannedContent = DemoDataProvider.urlRisk.input
                                isScanning = false
                            }
                        )
                    }
                } else {
                    QrResultView(
                        content = scannedContent ?: "",
                        isAnalyzing = isAnalyzing,
                        scanResult = scanResult,
                        onRescan = {
                            isScanning = true
                            scannedContent = null
                            scanResult = null
                        },
                        onAnalyze = {
                            isAnalyzing = true
                            scope.launch {
                                try {
                                    scanResult = repository.scanUrl(scannedContent ?: "")
                                } catch (e: Exception) {
                                    // Handle error
                                } finally {
                                    isAnalyzing = false
                                }
                            }
                        }
                    )
                }
            } else {
                PermissionDeniedView(onRequestPermission = { launcher.launch(Manifest.permission.CAMERA) })
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraPreview(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        val scanner = BarcodeScanning.getClient()
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val rawValue = barcode.rawValue
                                    if (rawValue != null) {
                                        onBarcodeDetected(rawValue)
                                        break
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    // Log error
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun QrScanningFrame() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "line"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .border(2.dp, TealAccent.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset(y = (260 * scanLineY).dp)
                    .background(TealAccent)
                    .border(2.dp, TealAccent.copy(alpha = 0.4f))
            )
        }
        
        Text(
            text = "SCANNING FOR THREATS...",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 320.dp)
        )
    }
}

@Composable
fun QrResultView(
    content: String,
    isAnalyzing: Boolean,
    scanResult: ScanResult?,
    onRescan: () -> Unit,
    onAnalyze: () -> Unit
) {
    val isUrl = content.startsWith("http") || content.contains(".")
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(DarkNavy)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(TealAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCodeScanner, null, tint = TealAccent, modifier = Modifier.size(32.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("QR CONTENT DETECTED", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (scanResult == null) {
                Button(
                    onClick = onAnalyze,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isUrl) TealAccent else SurfaceLight),
                    enabled = !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isUrl) "ANALYZE DESTINATION" else "SAFE TEXT DETECTED", color = if (isUrl) Color.Black else TextSecondary)
                    }
                }
            } else {
                RiskGauge(score = scanResult.riskScore)
                Spacer(modifier = Modifier.height(24.dp))
                ResultCard(result = scanResult)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onRescan) {
                Text("SCAN ANOTHER", color = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PermissionDeniedView(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.QrCodeScanner, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Camera Access Required", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Camera permission is needed to scan QR codes for malicious content.",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRequestPermission,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
        ) {
            Text("GRANT PERMISSION", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
