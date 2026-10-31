import asyncio
import unittest
from unittest.mock import patch, MagicMock
from app.providers.abuseipdb_provider import AbuseIPDBProvider
from app.models.schemas import ScanType, ThreatClassification

class TestAbuseIPDBProvider(unittest.TestCase):
    def setUp(self):
        self.settings_patcher = patch('app.providers.abuseipdb_provider.settings')
        self.mock_settings = self.settings_patcher.start()
        self.mock_settings.ABUSEIPDB_API_KEY = "fake_abuse_key"

    def tearDown(self):
        self.settings_patcher.stop()

    @patch('httpx.AsyncClient.get')
    def test_scan_ip_malicious(self, mock_get):
        provider = AbuseIPDBProvider()
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            "data": {
                "ipAddress": "1.2.3.4",
                "abuseConfidenceScore": 95,
                "totalReports": 50,
                "domain": "malicious.com",
                "isp": "Bad ISP",
                "usageType": "Data Center"
            }
        }
        mock_get.return_value = mock_response

        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(provider.scan_ip("1.2.3.4"))

        self.assertEqual(result.classification, ThreatClassification.MALICIOUS)
        self.assertEqual(result.risk_score, 95)
        self.assertIn("Abuse Confidence: 95%", result.threat_indicators)
        print("AbuseIPDB IP Scan (Mocked): PASSED")

    @patch('httpx.AsyncClient.get')
    def test_scan_ip_unavailable(self, mock_get):
        provider = AbuseIPDBProvider()
        mock_response = MagicMock()
        mock_response.status_code = 401
        mock_get.return_value = mock_response

        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(provider.scan_ip("8.8.8.8"))

        self.assertEqual(result.metadata["status"], "unavailable")
        self.assertIn("API Error: 401", result.recommendation)
        print("AbuseIPDB Unavailable Fallback (Mocked): PASSED")

    def test_missing_api_key(self):
        self.mock_settings.ABUSEIPDB_API_KEY = ""
        provider = AbuseIPDBProvider()
        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(provider.scan_ip("8.8.8.8"))

        self.assertEqual(result.metadata["status"], "unavailable")
        self.assertEqual(result.recommendation, "Provider unavailable")
        print("AbuseIPDB Missing API Key (Mocked): PASSED")

if __name__ == "__main__":
    unittest.main()
