package com.trustguardai.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.trustguardai.app.data.model.User
import com.trustguardai.app.ui.theme.*

sealed class EmployeeTab(val route: String, val icon: ImageVector, val label: String) {
    object Home : EmployeeTab("home", Icons.Default.Home, "HOME")
    object Scan : EmployeeTab("scan", Icons.Default.Shield, "SCAN")
    object Alerts : EmployeeTab("alerts", Icons.Default.Notifications, "ALERTS")
    object Security : EmployeeTab("security", Icons.Default.History, "SECURITY")
    object Profile : EmployeeTab("profile", Icons.Default.Person, "PROFILE")
}

@Composable
fun EmployeeMainScaffold(
    user: User,
    onLogout: () -> Unit,
    onNavigateToUrlScan: () -> Unit,
    onNavigateToIpScan: () -> Unit,
    onNavigateToFileScan: () -> Unit,
    onNavigateToHashAnalyzer: () -> Unit,
    onNavigateToLinkPreview: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToMessageScanner: () -> Unit,
    onNavigateToPasswordHealth: () -> Unit,
    onNavigateToPrivacyCheck: () -> Unit,
    onNavigateToNetworkCheck: () -> Unit,
    onNavigateToDemo: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf<EmployeeTab>(EmployeeTab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceNavy,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    EmployeeTab.Home,
                    EmployeeTab.Scan,
                    EmployeeTab.Alerts,
                    EmployeeTab.Security,
                    EmployeeTab.Profile
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
                EmployeeTab.Home -> EmployeeDashboard(
                    onLogout = onLogout,
                    onNavigateToUrlScan = onNavigateToUrlScan,
                    onNavigateToIpScan = onNavigateToIpScan,
                    onNavigateToFileScan = onNavigateToFileScan,
                    onNavigateToHistory = { selectedTab = EmployeeTab.Security }
                )
                EmployeeTab.Scan -> SecurityToolkitScreen(
                    onNavigateToUrlScan = onNavigateToUrlScan,
                    onNavigateToIpScan = onNavigateToIpScan,
                    onNavigateToFileScan = onNavigateToFileScan,
                    onNavigateToHashAnalyzer = onNavigateToHashAnalyzer,
                    onNavigateToLinkPreview = onNavigateToLinkPreview,
                    onNavigateToQrScanner = onNavigateToQrScanner,
                    onNavigateToMessageScanner = onNavigateToMessageScanner,
                    onNavigateToPasswordHealth = onNavigateToPasswordHealth,
                    onNavigateToPrivacyCheck = onNavigateToPrivacyCheck,
                    onNavigateToNetworkCheck = onNavigateToNetworkCheck,
                    onNavigateToDemo = onNavigateToDemo
                )
                EmployeeTab.Alerts -> AlertsScreen(
                    onBack = { selectedTab = EmployeeTab.Home },
                    onNavigateToIncident = {}
                )
                EmployeeTab.Security -> EmployeeHistoryScreen(onBack = { selectedTab = EmployeeTab.Home })
                EmployeeTab.Profile -> ProfileScreen(user = user, onLogout = onLogout)
            }
        }
    }
}
