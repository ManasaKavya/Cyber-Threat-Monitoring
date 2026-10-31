import unittest
from datetime import datetime
from app.models.schemas import ActivityLog, ActivityType, ThreatSeverity, ThreatClassification
from app.services.behavioral_engine import behavioral_engine
from app.services.activity_service import ActivityService

class TestBehavioralMonitoring(unittest.TestCase):
    def test_risk_scoring_high_volume(self):
        uid = "test_user"
        activities = []
        # Create 15 safe activities
        for i in range(15):
            activities.append(ActivityLog(
                user_id=uid,
                activity_type=ActivityType.SCAN_URL,
                severity=ThreatSeverity.SAFE
            ))

        risk = behavioral_engine.analyze_user_activity(uid, activities)
        # Should have at least MEDIUM risk due to abnormal volume
        self.assertGreaterEqual(risk.anomaly_score, 20)
        print("Behavioral Engine (Volume): PASSED")

    def test_risk_scoring_malicious_pattern(self):
        uid = "attacker"
        activities = [
            ActivityLog(user_id=uid, activity_type=ActivityType.SCAN_FILE, severity=ThreatSeverity.CRITICAL),
            ActivityLog(user_id=uid, activity_type=ActivityType.SCAN_FILE, severity=ThreatSeverity.CRITICAL),
            ActivityLog(user_id=uid, activity_type=ActivityType.SCAN_FILE, severity=ThreatSeverity.CRITICAL)
        ]

        risk = behavioral_engine.analyze_user_activity(uid, activities)
        self.assertEqual(risk.severity, ThreatSeverity.MEDIUM) # 40 points for 3 malicious
        print("Behavioral Engine (Malicious Pattern): PASSED")

    def test_activity_service_aggregation(self):
        service = ActivityService()
        service.activities = [] # Clear demo data

        service.log_activity(ActivityLog(user_id="u1", activity_type=ActivityType.LOGIN))
        service.log_activity(ActivityLog(user_id="u1", activity_type=ActivityType.SCAN_URL, severity=ThreatSeverity.CRITICAL))

        overview = service.get_risk_overview()
        self.assertEqual(overview.active_threats, 1)
        self.assertEqual(len(service.get_employee_risks()), 1)
        print("Activity Service Aggregation: PASSED")

if __name__ == "__main__":
    unittest.main()
