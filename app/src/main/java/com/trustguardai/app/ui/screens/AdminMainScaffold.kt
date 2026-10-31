package com.trustguardai.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.ui.theme.*

sealed class AdminTab(val icon: ImageVector, val label: String) {
    object Overview : AdminTab(Icons.Default.Dashboard, "OVERVIEW")
    object Threats : AdminTab(Icons.Default.BugReport, "THREATS")
    object Employees : AdminTab(Icons.Default.Group, "EMPLOYEES")
    object Incidents : AdminTab(Icons.Default.Gavel, "INCIDENTS")
    object More : AdminTab(Icons.Default.MoreHoriz, "MORE")
}

@Composable
fun AdminMainScaffold(
    onLogout: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToIncident: (String) -> Unit,
    onNavigateToDemo: (String) -> Unit,
    onNavigateToPresentationDemo: () -> Unit,
    onNavigateToThreatDna: (ScanResult) -> Unit,
    onNavigateToThreatStory: (ScanResult) -> Unit
) {
    var selectedTab by remember { mutableStateOf<AdminTab>(AdminTab.Overview) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceNavy,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    AdminTab.Overview,
                    AdminTab.Threats,
                    AdminTab.Employees,
                    AdminTab.Incidents,
                    AdminTab.More
                )
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealAccent,
                            selectedTextColor = TealAccent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = SurfaceLight
                        )
                    )
                }
            }
        },
        containerColor = DarkNavy
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                AdminTab.Overview -> AdminDashboard(
                    onLogout = onLogout,
                    onNavigateToAlerts = onNavigateToAlerts,
                    onNavigateToPresentationDemo = onNavigateToPresentationDemo
                )
                AdminTab.Threats -> AdminThreatsScreen(
                    onNavigateToDna = onNavigateToThreatDna,
                    onNavigateToStory = onNavigateToThreatStory
                )
                AdminTab.Employees -> AdminEmployeesScreen()
                AdminTab.Incidents -> AlertsScreen(onBack = { selectedTab = AdminTab.Overview }, onNavigateToIncident = onNavigateToIncident)
                AdminTab.More -> AdminMoreScreen(
                    onLogout = onLogout,
                    onNavigateToDemo = onNavigateToDemo,
                    onNavigateToPresentationDemo = onNavigateToPresentationDemo
                )
            }
        }
    }
}
