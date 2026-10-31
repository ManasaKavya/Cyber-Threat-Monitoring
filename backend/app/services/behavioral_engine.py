from typing import List, Dict
from app.models.schemas import ActivityLog, BehavioralRisk, ThreatSeverity, ActivityType

class BehavioralEngine:
    def analyze_user_activity(self, user_id: str, activities: List[ActivityLog]) -> BehavioralRisk:
        """
        Analyzes user activity patterns to detect potential insider threats.
        """
        if not activities:
            return BehavioralRisk(
                user_id=user_id,
                anomaly_score=0,
                severity=ThreatSeverity.SAFE,
                risk_factors=[],
                recommended_action="Normal activity monitoring"
            )

        score = 0
        factors = []

        # 1. Check for suspicious result patterns (e.g. multiple malicious scans)
        malicious_scans = [a for a in activities if a.severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]]
        if len(malicious_scans) > 2:
            score += 40
            factors.append(f"High number of critical scans detected ({len(malicious_scans)})")

        # 2. Check for unusual frequency of activities
        if len(activities) > 10: # Simple threshold for demo
            score += 20
            factors.append("Abnormal frequency of security scans")

        # 3. Check for logins from multiple IPs (if applicable)
        ips = set(a.ip_address for a in activities if a.ip_address)
        if len(ips) > 2:
            score += 30
            factors.append(f"Access from multiple geographic locations ({len(ips)} IPs)")

        # 4. Check for sensitive resource access
        sensitive_access = [a for a in activities if a.activity_type == ActivityType.SENSITIVE_ACCESS]
        if sensitive_access:
            score += 25
            factors.append("Attempted access to restricted security resources")

        # Normalize score
        score = min(100, score)

        severity = ThreatSeverity.SAFE
        if score >= 80:
            severity = ThreatSeverity.CRITICAL
        elif score >= 60:
            severity = ThreatSeverity.HIGH
        elif score >= 30:
            severity = ThreatSeverity.MEDIUM
        elif score >= 10:
            severity = ThreatSeverity.LOW

        recommendation = "No action required"
        if severity == ThreatSeverity.CRITICAL:
            recommendation = "IMMEDIATE ACCOUNT LOCKDOWN AND INVESTIGATION"
        elif severity == ThreatSeverity.HIGH:
            recommendation = "Escalate to security supervisor"
        elif severity == ThreatSeverity.MEDIUM:
            recommendation = "Increased monitoring of account activity"
        elif severity == ThreatSeverity.LOW:
            recommendation = "Review recent activity logs"

        return BehavioralRisk(
            user_id=user_id,
            anomaly_score=score,
            severity=severity,
            risk_factors=factors,
            recommended_action=recommendation
        )

behavioral_engine = BehavioralEngine()
