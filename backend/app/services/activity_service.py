from typing import List, Dict
from datetime import datetime
from app.models.schemas import ActivityLog, AdminRiskOverview, BehavioralRisk, ThreatSeverity
from app.services.behavioral_engine import behavioral_engine
from app.services.alert_service import alert_service

class ActivityService:
    def __init__(self):
        self.activities: List[ActivityLog] = []
        # Pre-populate with some demo data for Admin Dashboard
        self._seed_demo_data()

    def log_activity(self, activity: ActivityLog):
        self.activities.append(activity)
        # Check for immediate alert triggering if needed
        if activity.severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]:
            # This is already handled by ThreatService for scans,
            # but we could add general logging alerts here if needed.
            pass

    def get_all_activities(self) -> List[ActivityLog]:
        return sorted(self.activities, key=lambda x: x.timestamp, reverse=True)

    def get_employee_risks(self) -> List[BehavioralRisk]:
        user_ids = set(a.user_id for a in self.activities)
        risks = []
        for uid in user_ids:
            user_activities = [a for a in self.activities if a.user_id == uid]
            risk = behavioral_engine.analyze_user_activity(uid, user_activities)
            risks.append(risk)
            # Phase 7: Trigger behavioral alerts
            alert_service.create_alert_from_behavior(uid, risk.anomaly_score, risk.severity, risk.risk_factors, risk.recommended_action)

        return sorted(risks, key=lambda x: x.anomaly_score, reverse=True)

    def get_risk_overview(self) -> AdminRiskOverview:
        risks = self.get_employee_risks()

        active_threats = len([a for a in self.activities if a.severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]])
        critical_incidents = len([r for r in alert_service.get_all_incidents() if r.status == "OPEN"])
        attention_required = len([r for r in risks if r.severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]])

        avg_score = int(sum(r.anomaly_score for r in risks) / len(risks)) if risks else 0

        dist = {"SAFE": 0, "LOW": 0, "MEDIUM": 0, "HIGH": 0, "CRITICAL": 0}
        for r in risks:
            dist[r.severity.value] += 1

        all_alerts = alert_service.get_all_alerts()
        open_alerts = [a for a in all_alerts if a.status == "OPEN"]
        critical_alerts = [a for a in open_alerts if a.severity == ThreatSeverity.CRITICAL]

        return AdminRiskOverview(
            org_risk_score=avg_score,
            active_threats=active_threats,
            critical_incidents=critical_incidents,
            employees_requiring_attention=attention_required,
            risk_distribution=dist,
            open_alerts_count=len(open_alerts),
            critical_alerts_count=len(critical_alerts)
        )

    def get_employee_summary(self, user_id: str):
        from app.models.schemas import EmployeeSummary, ActivityType
        user_activities = [a for a in self.activities if a.user_id == user_id]
        risk = behavioral_engine.analyze_user_activity(user_id, user_activities)

        scans = [a for a in user_activities if a.activity_type in [ActivityType.SCAN_URL, ActivityType.SCAN_IP, ActivityType.SCAN_FILE]]
        threats = [a for a in scans if a.severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]]
        alerts = [a for a in alert_service.get_all_alerts() if a.related_user == user_id and a.status == "OPEN"]

        return EmployeeSummary(
            risk_score=risk.anomaly_score,
            threats_count=len(threats),
            alerts_count=len(alerts),
            scans_count=len(scans),
            recent_activities=user_activities[-5:],
            severity=risk.severity
        )

    def _seed_demo_data(self):
        from app.models.schemas import ActivityType
        # Demo User 1: High Risk
        uid1 = "agent_007"
        self.activities.append(ActivityLog(user_id=uid1, activity_type=ActivityType.LOGIN, ip_address="1.2.3.4", severity=ThreatSeverity.SAFE))
        self.activities.append(ActivityLog(user_id=uid1, activity_type=ActivityType.SCAN_URL, resource="malicious-site.com", severity=ThreatSeverity.CRITICAL))
        self.activities.append(ActivityLog(user_id=uid1, activity_type=ActivityType.SCAN_FILE, resource="payload.exe", severity=ThreatSeverity.HIGH))
        self.activities.append(ActivityLog(user_id=uid1, activity_type=ActivityType.SENSITIVE_ACCESS, resource="AdminDB_Backup", severity=ThreatSeverity.HIGH))

        # Demo User 2: Low Risk
        uid2 = "agent_smith"
        self.activities.append(ActivityLog(user_id=uid2, activity_type=ActivityType.LOGIN, ip_address="192.168.1.10", severity=ThreatSeverity.SAFE))
        self.activities.append(ActivityLog(user_id=uid2, activity_type=ActivityType.SCAN_URL, resource="google.com", severity=ThreatSeverity.SAFE))

activity_service = ActivityService()
