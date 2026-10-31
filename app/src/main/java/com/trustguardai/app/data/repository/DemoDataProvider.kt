package com.trustguardai.app.data.repository

import com.trustguardai.app.data.model.*

data class DemoScenario(
    val title: String,
    val input: String,
    val result: ScanResult
)

object DemoDataProvider {

    // URL Scenarios
    val urlSafe = DemoScenario(
        "Safe URL",
        "https://www.google.com",
        ScanResult(
            id = "demo-url-safe",
            target = "https://www.google.com",
            type = ScanType.URL,
            riskScore = 5,
            classification = ThreatClassification.SAFE,
            severity = ThreatSeverity.SAFE,
            indicators = listOf("Verified domain", "HTTPS active"),
            recommendation = "Safe to proceed",
            timestamp = "2026-08-12T10:00:00",
            confidence = 0.99f,
            metadata = mapOf("Provider" to "Heuristic Engine")
        )
    )

    val urlRisk = DemoScenario(
        "Risky URL",
        "http://secure-login-bank-verify.xyz/update",
        ScanResult(
            id = "demo-url-risk",
            target = "http://secure-login-bank-verify.xyz/update",
            type = ScanType.URL,
            riskScore = 92,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.CRITICAL,
            indicators = listOf("Suspicious TLD (.xyz)", "Keyword match: bank, login", "Domain age: 2 days"),
            recommendation = "BLOCK IMMEDIATELY: Phishing attempt detected",
            timestamp = "2026-08-12T10:05:00",
            confidence = 0.96f,
            metadata = mapOf("Source" to "VirusTotal (Multi-Engine Match)")
        )
    )

    // IP Scenarios
    val ipSafe = DemoScenario(
        "Safe IP",
        "8.8.8.8",
        ScanResult(
            id = "demo-ip-safe",
            target = "8.8.8.8",
            type = ScanType.IP,
            riskScore = 2,
            classification = ThreatClassification.SAFE,
            severity = ThreatSeverity.SAFE,
            indicators = listOf("Known public DNS", "Low report volume"),
            recommendation = "Reliable network node",
            timestamp = "2026-08-12T10:10:00",
            confidence = 0.98f,
            metadata = mapOf("Owner" to "Google LLC")
        )
    )

    val ipRisk = DemoScenario(
        "Risky IP",
        "103.45.1.1",
        ScanResult(
            id = "demo-ip-risk",
            target = "103.45.1.1",
            type = ScanType.IP,
            riskScore = 88,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.HIGH,
            indicators = listOf("Recent brute force reports", "Active botnet node", "Abuse confidence: 100%"),
            recommendation = "Restrict all inbound traffic from this source",
            timestamp = "2026-08-12T10:12:00",
            confidence = 0.94f,
            metadata = mapOf("AbuseIPDB" to "High confidence match")
        )
    )

    // File Scenarios
    val fileSafe = DemoScenario(
        "Safe File",
        "quarterly_report.pdf",
        ScanResult(
            id = "demo-file-safe",
            target = "quarterly_report.pdf",
            type = ScanType.FILE,
            riskScore = 0,
            classification = ThreatClassification.SAFE,
            severity = ThreatSeverity.SAFE,
            indicators = listOf("Standard PDF structure", "No embedded scripts"),
            recommendation = "Verified document",
            timestamp = "2026-08-12T10:15:00",
            confidence = 0.95f,
            metadata = mapOf("Size" to "1.2 MB", "MIME" to "application/pdf")
        )
    )

    val fileRisk = DemoScenario(
        "Suspicious File",
        "invoice_update.pdf.exe",
        ScanResult(
            id = "demo-file-risk",
            target = "invoice_update.pdf.exe",
            type = ScanType.FILE,
            riskScore = 95,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.CRITICAL,
            indicators = listOf("Double extension detection", "Executable disguised as PDF", "High entropy in code segment"),
            recommendation = "QUARANTINE: Dropper/Downloader detected",
            timestamp = "2026-08-12T10:20:00",
            confidence = 0.97f,
            metadata = mapOf("Sandbox" to "Suspicious API calls detected")
        )
    )

    // Message Scenarios
    val msgSafe = "Hey, are we still meeting for lunch at 1 PM?"
    val msgRisk = "URGENT: Your account access has been restricted due to suspicious activity. Log in immediately to verify your identity: http://verify-user-82.xyz"

    val msgSafeScenario = DemoScenario(
        "Safe Message",
        "Hey, are we still meeting for lunch at 1 PM?",
        ScanResult(
            id = "demo-msg-safe",
            target = "SMS Message",
            type = ScanType.FILE,
            riskScore = 5,
            classification = ThreatClassification.SAFE,
            severity = ThreatSeverity.SAFE,
            indicators = listOf("Friendly tone", "No suspicious links"),
            recommendation = "Safe communication",
            timestamp = "2026-08-12T10:25:00",
            confidence = 0.90f
        )
    )

    val msgRiskScenario = DemoScenario(
        "Phishing Message",
        "URGENT: Your account access has been restricted. Verify here: http://verify-user-82.xyz",
        ScanResult(
            id = "demo-msg-risk",
            target = "SMS Message",
            type = ScanType.FILE,
            riskScore = 88,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.HIGH,
            indicators = listOf("Urgency detected", "Suspicious link", "Social engineering pattern"),
            recommendation = "Do not click. Report and delete.",
            timestamp = "2026-08-12T10:30:00",
            confidence = 0.92f
        )
    )

    val qrSafe = DemoScenario(
        "Safe QR Code",
        "https://www.trustguard.ai/welcome",
        ScanResult(
            id = "demo-qr-safe",
            target = "https://www.trustguard.ai/welcome",
            type = ScanType.URL,
            riskScore = 2,
            classification = ThreatClassification.SAFE,
            severity = ThreatSeverity.SAFE,
            indicators = listOf("Official company domain", "HTTPS enforced"),
            recommendation = "Safe to open",
            timestamp = "2026-08-12T10:35:00",
            confidence = 0.99f
        )
    )

    val qrRisk = DemoScenario(
        "Risky QR Code",
        "http://gift-claim-102.top/collect",
        ScanResult(
            id = "demo-qr-risk",
            target = "http://gift-claim-102.top/collect",
            type = ScanType.URL,
            riskScore = 82,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.HIGH,
            indicators = listOf("Suspicious TLD (.top)", "Redirect pattern", "Low reputation domain"),
            recommendation = "Do not open. Potential credential harvester.",
            timestamp = "2026-08-12T10:40:00",
            confidence = 0.94f
        )
    )

    // Password Scenarios
    val pwdWeak = "123456"
    val pwdStrong = "K#p9!wQ$2xZaVn"

    // Hash Scenarios
    val hashValid = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

    // --- Added for Phase 11 ---

    val demoAlerts = listOf(
        Alert(
            id = "alert-demo-1",
            title = "Suspicious Login Attempt",
            description = "Multiple unsuccessful login attempts were detected from an unusual geographic location.",
            severity = ThreatSeverity.HIGH,
            source = "Authentication Monitor",
            timestamp = "2026-08-13T20:00:00",
            status = AlertStatus.OPEN,
            recommendedAction = "Review account access logs and consider changing credentials."
        ),
        Alert(
            id = "alert-demo-2",
            title = "Suspicious Link Detected",
            description = "A potentially unsafe URL was detected during a system-wide background security scan.",
            severity = ThreatSeverity.MEDIUM,
            source = "URL Scanner",
            timestamp = "2026-08-13T18:30:00",
            status = AlertStatus.ACKNOWLEDGED,
            recommendedAction = "Verify the destination domain before allowing enterprise access."
        ),
        Alert(
            id = "alert-demo-3",
            title = "Unusual Activity Pattern",
            description = "Behavioral analysis detected an abnormal volume of outbound data transfer from this terminal.",
            severity = ThreatSeverity.LOW,
            source = "Behavioral Monitor",
            timestamp = "2026-08-12T22:15:00",
            status = AlertStatus.RESOLVED,
            recommendedAction = "Monitor subsequent activity for persistent anomalies."
        )
    )

    val demoThreats = listOf(
        ScanResult(
            id = "threat-demo-1",
            target = "http://phish-secure-portal.top",
            type = ScanType.URL,
            riskScore = 94,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.CRITICAL,
            indicators = listOf("DGA Domain", "Phishing Signature Match"),
            recommendation = "Block domain at firewall level",
            timestamp = "2026-08-13T21:10:00",
            confidence = 0.98f
        ),
        ScanResult(
            id = "threat-demo-2",
            target = "185.234.xx.xx",
            type = ScanType.IP,
            riskScore = 82,
            classification = ThreatClassification.MALICIOUS,
            severity = ThreatSeverity.HIGH,
            indicators = listOf("Known Malware C2 Node", "High Abuse Reports"),
            recommendation = "Restrict all communication with this IP",
            timestamp = "2026-08-13T20:45:00",
            confidence = 0.95f
        ),
        ScanResult(
            id = "threat-demo-3",
            target = "suspicious_payload.apk",
            type = ScanType.FILE,
            riskScore = 61,
            classification = ThreatClassification.SUSPICIOUS,
            severity = ThreatSeverity.MEDIUM,
            indicators = listOf("Hidden Permissions", "Obfuscated Code"),
            recommendation = "Perform manual sandbox inspection",
            timestamp = "2026-08-13T19:20:00",
            confidence = 0.88f
        ),
        ScanResult(
            id = "threat-demo-4",
            target = "agent_smith",
            type = ScanType.FILE,
            riskScore = 34,
            classification = ThreatClassification.SUSPICIOUS,
            severity = ThreatSeverity.LOW,
            indicators = listOf("Abnormal Working Hours", "Sensitive Folder Access"),
            recommendation = "Increase behavioral monitoring window",
            timestamp = "2026-08-13T18:05:00",
            confidence = 0.75f
        )
    )
}
