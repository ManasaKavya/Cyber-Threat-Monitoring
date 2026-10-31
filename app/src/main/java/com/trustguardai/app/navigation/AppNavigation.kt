package com.trustguardai.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.UserRole
import com.trustguardai.app.data.repository.SessionManager
import com.trustguardai.app.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    LaunchedEffect(Unit) {
        RetrofitClient.onSessionExpired = {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (sessionManager.isLoggedIn()) {
            if (sessionManager.getUser()?.role == UserRole.ADMIN) "admin_main" else "employee_main"
        } else "splash"
    ) {

        composable("splash") {
            SplashScreen(
                onGetStarted = {
                    navController.navigate("landing") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        composable("landing") {
            LandingScreen(
                onGetStarted = {
                    navController.navigate("login")
                },
                onSignIn = {
                    navController.navigate("login")
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = { user ->
                    val dest = if (user.role == UserRole.ADMIN) "admin_main" else "employee_main"
                    navController.navigate(dest) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("employee_main") {
            val user = sessionManager.getUser() ?: return@composable
            EmployeeMainScaffold(
                user = user,
                onLogout = {
                    sessionManager.logout()
                    navController.navigate("login") {
                        popUpTo("employee_main") { inclusive = true }
                    }
                },
                onNavigateToUrlScan = { navController.navigate("url_scanner") },
                onNavigateToIpScan = { navController.navigate("ip_scanner") },
                onNavigateToFileScan = { navController.navigate("file_scanner") },
                onNavigateToHashAnalyzer = { navController.navigate("hash_analyzer") },
                onNavigateToLinkPreview = { navController.navigate("link_safety") },
                onNavigateToQrScanner = { navController.navigate("qr_scan") },
                onNavigateToMessageScanner = { navController.navigate("message_scan") },
                onNavigateToPasswordHealth = { navController.navigate("password_health") },
                onNavigateToPrivacyCheck = { navController.navigate("privacy_check") },
                onNavigateToNetworkCheck = { navController.navigate("network_check") },
                onNavigateToDemo = { title -> navController.navigate("demo/$title") }
            )
        }

        composable("admin_main") {
            AdminMainScaffold(
                onLogout = {
                    sessionManager.logout()
                    navController.navigate("login") {
                        popUpTo("admin_main") { inclusive = true }
                    }
                },
                onNavigateToAlerts = { navController.navigate("alerts") },
                onNavigateToIncident = { id -> navController.navigate("incident/$id") },
                onNavigateToDemo = { title -> navController.navigate("demo/$title") },
                onNavigateToPresentationDemo = { navController.navigate("presentation_demo") },
                onNavigateToThreatDna = { threat -> 
                    // For now, navigating to the demo DNA view
                    navController.navigate("threat_dna")
                },
                onNavigateToThreatStory = { threat ->
                    navController.navigate("threat_story")
                }
            )
        }

        composable("alerts") {
            AlertsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToIncident = { id -> navController.navigate("incident/$id") }
            )
        }

        composable("incident/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            IncidentDetailsScreen(
                incidentId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable("url_scanner") {
            UrlScannerScreen(onBack = { navController.popBackStack() })
        }

        composable("ip_scanner") {
            IpScannerScreen(onBack = { navController.popBackStack() })
        }

        composable("file_scanner") {
            FileScannerScreen(onBack = { navController.popBackStack() })
        }

        composable("hash_analyzer") {
            HashAnalyzerScreen(onBack = { navController.popBackStack() })
        }

        composable("checkup") {
            SecurityCheckupScreen(onBack = { navController.popBackStack() })
        }

        composable("toolkit") {
            // This is actually not used directly now as it's part of EmployeeMainScaffold's SCAN tab
            // but keeping it for completeness if needed.
        }

        composable("analytics") {
            SecurityAnalyticsScreen(onBack = { navController.popBackStack() })
        }

        composable("achievements") {
            AchievementsScreen(onBack = { navController.popBackStack() })
        }

        composable("password_health") {
            PasswordHealthScreen(onBack = { navController.popBackStack() })
        }

        composable("message_scan") {
            MessageScannerScreen(onBack = { navController.popBackStack() })
        }

        composable("link_safety") {
            LinkSafetyScreen(onBack = { navController.popBackStack() })
        }

        composable("qr_scan") {
            QrScannerScreen(onBack = { navController.popBackStack() })
        }

        composable("privacy_check") {
            PrivacyCheckScreen(onBack = { navController.popBackStack() })
        }

        composable("network_check") {
            NetworkCheckScreen(onBack = { navController.popBackStack() })
        }

        composable("threat_map") {
            ThreatMapScreen(onBack = { navController.popBackStack() })
        }

        composable("presentation_demo") {
            PresentationDemoScreen(onBack = { navController.popBackStack() })
        }

        composable("report") {
            SecurityReportScreen(onBack = { navController.popBackStack() })
        }

        composable("threat_dna") {
            val user = sessionManager.getUser()
            if (user != null) {
                val dummy = com.trustguardai.app.data.model.ScanResult(
                    id = "demo", target = "malicious.xyz", type = com.trustguardai.app.data.model.ScanType.URL,
                    riskScore = 85, classification = com.trustguardai.app.data.model.ThreatClassification.MALICIOUS,
                    indicators = listOf("Suspicious TLD"), recommendation = "Block access",
                    timestamp = "2026-08-09T10:00:00", confidence = 0.95f
                )
                ThreatDnaScreen(scanResult = dummy, onBack = { navController.popBackStack() })
            }
        }

        composable("threat_story") {
            val user = sessionManager.getUser()
            if (user != null) {
                val dummy = com.trustguardai.app.data.model.ScanResult(
                    id = "demo-story", target = "payload.exe", type = com.trustguardai.app.data.model.ScanType.FILE,
                    riskScore = 95, classification = com.trustguardai.app.data.model.ThreatClassification.MALICIOUS,
                    severity = com.trustguardai.app.data.model.ThreatSeverity.CRITICAL,
                    indicators = listOf("Double Extension", "C2 Beaconing"), recommendation = "Quarantine File",
                    timestamp = "2026-08-09T11:00:00", confidence = 0.97f
                )
                ThreatStoryScreen(scanResult = dummy, onBack = { navController.popBackStack() })
            }
        }
        
        composable("demo/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Module"
            DemoScreen(title = title, onBack = { navController.popBackStack() })
        }
    }
}
