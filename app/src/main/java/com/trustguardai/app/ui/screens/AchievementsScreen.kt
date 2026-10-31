package com.trustguardai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustguardai.app.ui.components.CyberGrid
import com.trustguardai.app.ui.theme.*

data class Achievement(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    val achievements = listOf(
        Achievement("First Scan", "Complete your first security analysis", Icons.Default.Star, true),
        Achievement("Threat Hunter", "Identify a critical malicious URL", Icons.Default.EmojiEvents, true),
        Achievement("Security Aware", "Read 7 consecutive daily tips", Icons.Default.EmojiEvents, false),
        Achievement("Risk Analyst", "Complete a deep sandbox analysis", Icons.Default.Star, true),
        Achievement("Alert Responder", "Acknowledge 5 security alerts", Icons.Default.EmojiEvents, false),
        Achievement("Security Champion", "Maintain 100/100 score for a week", Icons.Default.EmojiEvents, false)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Achievements", fontWeight = FontWeight.Bold) },
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
            
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                // Streak Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = WarningOrange, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("SECURITY STREAK", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text("5 Days Active", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("Your longest streak is 12 days", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("BADGE COLLECTION", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(achievements) { achievement ->
                        AchievementCard(achievement)
                    }
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) SurfaceNavy else DarkNavy
        ),
        border = if (!achievement.isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, SurfaceLight) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize().alpha(if (achievement.isUnlocked) 1f else 0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(if(achievement.isUnlocked) TealAccent.copy(alpha = 0.1f) else SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(achievement.icon, null, tint = if(achievement.isUnlocked) TealAccent else TextSecondary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = achievement.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text(text = achievement.description, color = TextSecondary, fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}
