import uuid
from datetime import datetime
from typing import List, Optional
from app.models.schemas import Alert, AlertStatus, ThreatSeverity, Incident, ActivityLog, ScanResponse

class AlertService:
    def __init__(self):
        self.alerts: List[Alert] = []
        self.incidents: List[Incident] = []
        self._seed_demo_data()

    def create_alert_from_scan(self, scan: ScanResponse):
        if scan.severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]:
            alert = Alert(
                id=str(uuid.uuid4()),
                title=f"Critical Threat Detected: {scan.indicator}",
                description=f"Automated scanner identified a {scan.severity.value} threat.",
                severity=scan.severity,
                source=f"{scan.indicator_type.value} Scanner",
                timestamp=datetime.now(),
                status=AlertStatus.OPEN,
                recommended_action=scan.recommendation
            )
            self.alerts.append(alert)
            self._auto_create_incident(alert, scan)

    def create_alert_from_behavior(self, user_id: str, anomaly_score: int, severity: ThreatSeverity, factors: List[str], rec: str):
        if severity in [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL]:
            alert = Alert(
                id=str(uuid.uuid4()),
                title=f"Suspicious Behavior: {user_id}",
                description=f"Behavioral analysis detected an anomaly score of {anomaly_score}. Factors: {', '.join(factors)}",
                severity=severity,
                source="Behavioral Engine",
                related_user=user_id,
                timestamp=datetime.now(),
                status=AlertStatus.OPEN,
                recommended_action=rec
            )
            self.alerts.append(alert)

    def get_all_alerts(self) -> List[Alert]:
        return sorted(self.alerts, key=lambda x: x.timestamp, reverse=True)

    def get_alert_by_id(self, alert_id: str) -> Optional[Alert]:
        return next((a for a in self.alerts if a.id == alert_id), None)

    def update_alert_status(self, alert_id: str, status: AlertStatus) -> Optional[Alert]:
        alert = self.get_alert_by_id(alert_id)
        if alert:
            alert.status = status
            return alert
        return None

    def get_all_incidents(self) -> List[Incident]:
        return self.incidents

    def get_incident_by_id(self, incident_id: str) -> Optional[Incident]:
        return next((i for i in self.incidents if i.id == incident_id), None)

    def update_incident_status(self, incident_id: str, status: AlertStatus) -> Optional[Incident]:
        incident = self.get_incident_by_id(incident_id)
        if incident:
            incident.status = status
            return incident
        return None

    def _auto_create_incident(self, alert: Alert, scan: ScanResponse):
        incident = Incident(
            id=str(uuid.uuid4()),
            title=alert.title,
            severity=alert.severity,
            risk_score=scan.risk_score,
            detection_source=alert.source,
            timeline=[{"time": str(datetime.now()), "event": "Threat detected by automated scanner"}],
            related_activities=[],
            threat_indicators=scan.threat_indicators,
            recommended_response=alert.recommended_action,
            status=AlertStatus.OPEN
        )
        self.incidents.append(incident)

    def _seed_demo_data(self):
        # Demo Alert
        self.alerts.append(Alert(
            id="demo-alert-1",
            title="Unauthorized sensitive access attempt",
            description="Agent agent_007 attempted to access restricted database backups multiple times.",
            severity=ThreatSeverity.HIGH,
            source="Internal Monitor",
            related_user="agent_007",
            timestamp=datetime.now(),
            status=AlertStatus.OPEN,
            recommended_action="Revoke access and initiate interview"
        ))

alert_service = AlertService()
