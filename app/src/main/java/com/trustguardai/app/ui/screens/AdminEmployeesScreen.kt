package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.BehavioralRisk
import com.trustguardai.app.data.model.ThreatSeverity
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminEmployeesScreen() {
    val api = RetrofitClient.apiService
    var risks by remember { mutableStateOf<List<BehavioralRisk>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            risks = api.getEmployeeRisks()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Access Denied or Connection Error"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkNavy)) {
        CyberGrid(modifier = Modifier.fillMaxSize().alpha(0.05f))
        
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "USER MONITORING",
                color = TealAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Employee Behavioral Risk",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search agents...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TealAccent) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealAccent,
                    unfocusedBorderColor = SurfaceLight
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealAccent)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage!!, color = ErrorRed)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(risks.filter { it.userId.contains(searchQuery, ignoreCase = true) }) { risk ->
                        EmployeeDetailedRiskCard(risk)
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }
}

@Composable
fun EmployeeDetailedRiskCard(risk: BehavioralRisk) {
    val color = when(risk.severity) {
        ThreatSeverity.CRITICAL, ThreatSeverity.HIGH -> ErrorRed
        ThreatSeverity.MEDIUM -> WarningOrange
        else -> TealAccent
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(TealAccent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = TealAccent, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = risk.userId, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Badge(containerColor = color.copy(alpha = 0.1f)) {
                    Text(text = risk.severity.name, color = color, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("ANOMALY SCORE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${risk.anomalyScore}", color = color, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RISK FACTORS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${risk.riskFactors.size} detected", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            if (risk.riskFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceLight)
                Spacer(modifier = Modifier.height(12.dp))
                risk.riskFactors.forEach { factor ->
                    Text(text = "• $factor", color = TextSecondary, fontSize = 11.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "RECOMMENDED ACTION", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = risk.recommendedAction, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
