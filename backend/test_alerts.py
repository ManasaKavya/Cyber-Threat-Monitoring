import unittest
import uuid
from datetime import datetime
from app.models.schemas import ScanResponse, ScanType, ThreatClassification, ThreatSeverity, AlertStatus
from app.services.alert_service import alert_service
from app.services.threat_service import threat_service

class TestAlertSystem(unittest.TestCase):
    def test_alert_creation_on_critical_scan(self):
        # Clear current alerts
        alert_service.alerts = []

        # Mock a critical scan result
        scan = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator="malicious-payload.exe",
            indicator_type=ScanType.FILE,
            classification=ThreatClassification.MALICIOUS,
            risk_score=95,
            severity=ThreatSeverity.CRITICAL,
            threat_indicators=["Signature match"],
            recommendation="Quarantine",
            timestamp=datetime.now()
        )

        alert_service.create_alert_from_scan(scan)

        self.assertEqual(len(alert_service.alerts), 1)
        self.assertEqual(alert_service.alerts[0].severity, ThreatSeverity.CRITICAL)
        self.assertEqual(alert_service.alerts[0].status, AlertStatus.OPEN)
        print("Alert Creation (Critical Scan): PASSED")

    def test_incident_auto_creation(self):
        alert_service.incidents = []
        scan = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator="103.45.1.1",
            indicator_type=ScanType.IP,
            classification=ThreatClassification.MALICIOUS,
            risk_score=90,
            severity=ThreatSeverity.HIGH,
            threat_indicators=["Known botnet"],
            recommendation="Block",
            timestamp=datetime.now()
        )

        alert_service.create_alert_from_scan(scan)
        self.assertEqual(len(alert_service.incidents), 1)
        self.assertEqual(alert_service.incidents[0].severity, ThreatSeverity.HIGH)
        print("Incident Auto-Creation: PASSED")

    def test_alert_status_update(self):
        alert_id = "test-update-1"
        alert_service.alerts.append(ScanResponse( # Simplified for push
            scan_id=alert_id,
            indicator="test",
            indicator_type=ScanType.URL,
            classification=ThreatClassification.SAFE,
            risk_score=0,
            severity=ThreatSeverity.SAFE,
            threat_indicators=[],
            recommendation="",
            timestamp=datetime.now()
        ))
        # Re-adding properly
        from app.models.schemas import Alert
        a = Alert(
            id=alert_id,
            title="T",
            description="D",
            severity=ThreatSeverity.LOW,
            source="S",
            timestamp=datetime.now(),
            status=AlertStatus.OPEN,
            recommended_action="A"
        )
        alert_service.alerts = [a]

        res = alert_service.update_alert_status(alert_id, AlertStatus.RESOLVED)
        self.assertIsNotNone(res)
        self.assertEqual(res.status, AlertStatus.RESOLVED)
        print("Alert Status Update: PASSED")

if __name__ == "__main__":
    unittest.main()
