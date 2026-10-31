package com.trustguardai.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThreatClassification {
    @SerialName("SAFE") SAFE,
    @SerialName("SUSPICIOUS") SUSPICIOUS,
    @SerialName("MALICIOUS") MALICIOUS
}

@Serializable
enum class ThreatSeverity {
    @SerialName("SAFE") SAFE,
    @SerialName("LOW") LOW,
    @SerialName("MEDIUM") MEDIUM,
    @SerialName("HIGH") HIGH,
    @SerialName("CRITICAL") CRITICAL
}

@Serializable
enum class ScanType {
    @SerialName("URL") URL,
    @SerialName("IP") IP,
    @SerialName("FILE") FILE
}

@Serializable
enum class ActivityType {
    @SerialName("LOGIN") LOGIN,
    @SerialName("SCAN_URL") SCAN_URL,
    @SerialName("SCAN_IP") SCAN_IP,
    @SerialName("SCAN_FILE") SCAN_FILE,
    @SerialName("SENSITIVE_ACCESS") SENSITIVE_ACCESS,
    @SerialName("LOGOUT") LOGOUT
}

@Serializable
enum class AlertStatus {
    @SerialName("OPEN") OPEN,
    @SerialName("ACKNOWLEDGED") ACKNOWLEDGED,
    @SerialName("RESOLVED") RESOLVED
}

@Serializable
enum class UserRole {
    @SerialName("EMPLOYEE") EMPLOYEE,
    @SerialName("ADMIN") ADMIN
}

@Serializable
data class User(
    val id: String,
    val email: String,
    val role: UserRole
)

@Serializable
data class Token(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    val user: User
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class ScanResult(
    @SerialName("scan_id") val id: String,
    @SerialName("indicator") val target: String,
    @SerialName("indicator_type") val type: ScanType,
    @SerialName("risk_score") val riskScore: Int,
    val severity: ThreatSeverity = ThreatSeverity.SAFE,
    val classification: ThreatClassification,
    @SerialName("threat_indicators") val indicators: List<String>,
    val recommendation: String,
    val confidence: Float = 1.0f,
    val timestamp: String,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ThreatHistoryResponse(
    val threats: List<ScanResult>
)

@Serializable
data class EmployeeSummary(
    @SerialName("risk_score") val riskScore: Int,
    @SerialName("threats_count") val threatsCount: Int,
    @SerialName("alerts_count") val alertsCount: Int,
    @SerialName("scans_count") val scansCount: Int,
    @SerialName("recent_activities") val recentActivities: List<ActivityLog>,
    val severity: ThreatSeverity
)

@Serializable
data class ScanRequest(
    val target: String
)

@Serializable
data class ActivityLog(
    @SerialName("user_id") val userId: String,
    @SerialName("activity_type") val activityType: ActivityType,
    val timestamp: String? = null,
    @SerialName("ip_address") val ipAddress: String? = null,
    val resource: String? = null,
    val severity: ThreatSeverity = ThreatSeverity.SAFE,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class BehavioralRisk(
    @SerialName("user_id") val userId: String,
    @SerialName("anomaly_score") val anomalyScore: Int,
    val severity: ThreatSeverity,
    @SerialName("risk_factors") val riskFactors: List<String>,
    @SerialName("recommended_action") val recommendedAction: String
)

@Serializable
data class AdminRiskOverview(
    @SerialName("org_risk_score") val orgRiskScore: Int,
    @SerialName("active_threats") val activeThreats: Int,
    @SerialName("critical_incidents") val criticalIncidents: Int,
    @SerialName("employees_requiring_attention") val employeesRequiringAttention: Int,
    @SerialName("risk_distribution") val riskDistribution: Map<String, Int>,
    @SerialName("open_alerts_count") val openAlertsCount: Int = 0,
    @SerialName("critical_alerts_count") val criticalAlertsCount: Int = 0
)

@Serializable
data class Alert(
    val id: String,
    val title: String,
    val description: String,
    val severity: ThreatSeverity,
    val source: String,
    @SerialName("related_user") val relatedUser: String? = null,
    val timestamp: String,
    val status: AlertStatus,
    @SerialName("recommended_action") val recommendedAction: String
)

@Serializable
data class Incident(
    val id: String,
    val title: String,
    val severity: ThreatSeverity,
    @SerialName("risk_score") val riskScore: Int,
    @SerialName("detection_source") val detectionSource: String,
    val timeline: List<Map<String, String>>,
    @SerialName("related_activities") val relatedActivities: List<ActivityLog>,
    @SerialName("threat_indicators") val threatIndicators: List<String>,
    @SerialName("recommended_response") val recommendedResponse: String,
    val status: AlertStatus
)
