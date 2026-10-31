import unittest
import uuid
from datetime import datetime
from app.models.schemas import ScanResponse, ScanType, ThreatClassification, ThreatSeverity
from app.services.risk_engine import risk_engine

class TestRiskEngine(unittest.TestCase):
    def test_weighted_risk_malicious_dominance(self):
        # Result 1: Heuristic says SUSPICIOUS
        h_res = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator="test.xyz",
            indicator_type=ScanType.URL,
            classification=ThreatClassification.SUSPICIOUS,
            risk_score=45,
            threat_indicators=["Heuristic trigger"],
            recommendation="Review",
            timestamp=datetime.now(),
            metadata={"provider": "Internal Heuristic Engine"}
        )

        # Result 2: VirusTotal says MALICIOUS (High Weight)
        v_res = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator="test.xyz",
            indicator_type=ScanType.URL,
            classification=ThreatClassification.MALICIOUS,
            risk_score=95,
            threat_indicators=["VT: 15/70 detections"],
            recommendation="Block",
            timestamp=datetime.now(),
            metadata={"provider": "VirusTotal"}
        )

        analysis = risk_engine.calculate_final_risk([h_res, v_res])

        self.assertEqual(analysis["classification"], ThreatClassification.MALICIOUS)
        self.assertGreaterEqual(analysis["risk_score"], 70)
        self.assertIn(analysis["severity"], [ThreatSeverity.HIGH, ThreatSeverity.CRITICAL])
        print("Risk Engine Weighted Merge: PASSED")

    def test_unavailable_provider_fallback(self):
        h_res = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator="safe.com",
            indicator_type=ScanType.URL,
            classification=ThreatClassification.SAFE,
            risk_score=10,
            threat_indicators=[],
            recommendation="Safe",
            timestamp=datetime.now(),
            metadata={"provider": "Internal Heuristic Engine"}
        )

        v_res = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator="safe.com",
            indicator_type=ScanType.URL,
            classification=ThreatClassification.SAFE,
            risk_score=0,
            threat_indicators=[],
            recommendation="N/A",
            timestamp=datetime.now(),
            metadata={"provider": "VirusTotal", "status": "unavailable"}
        )

        analysis = risk_engine.calculate_final_risk([h_res, v_res])
        self.assertEqual(analysis["classification"], ThreatClassification.SAFE)
        self.assertLess(analysis["risk_score"], 20)
        print("Risk Engine Unavailable Fallback: PASSED")

if __name__ == "__main__":
    unittest.main()
