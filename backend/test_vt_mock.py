import asyncio
import unittest
from unittest.mock import patch, MagicMock
from app.providers.virustotal_provider import VirusTotalProvider
from app.models.schemas import ScanType, ThreatClassification

class TestVirusTotalProvider(unittest.TestCase):
    def setUp(self):
        # Patch settings to ensure API key is present for tests
        self.settings_patcher = patch('app.providers.virustotal_provider.settings')
        self.mock_settings = self.settings_patcher.start()
        self.mock_settings.VIRUSTOTAL_API_KEY = "fake_key"
        self.provider = VirusTotalProvider()

    def tearDown(self):
        self.settings_patcher.stop()

    @patch('httpx.AsyncClient.get')
    def test_scan_url_malicious(self, mock_get):
        # Mock successful VT response
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            "data": {
                "attributes": {
                    "last_analysis_stats": {
                        "malicious": 10,
                        "suspicious": 2,
                        "harmless": 50,
                        "undetected": 10
                    }
                }
            }
        }
        mock_get.return_value = mock_response

        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(self.provider.scan_url("http://malicious-example.com"))

        self.assertEqual(result.classification, ThreatClassification.MALICIOUS)
        self.assertGreater(result.risk_score, 10)
        self.assertIn("VT Detections: 10 malicious", result.threat_indicators)
        print("VT URL Scan (Mocked): PASSED")

    @patch('httpx.AsyncClient.get')
    def test_scan_url_unavailable(self, mock_get):
        mock_response = MagicMock()
        mock_response.status_code = 403 # Forbidden/Invalid Key
        mock_get.return_value = mock_response

        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(self.provider.scan_url("http://example.com"))

        self.assertEqual(result.recommendation, "Provider unavailable")
        self.assertEqual(result.metadata["status"], "unavailable")
        print("VT Unavailable Fallback (Mocked): PASSED")

if __name__ == "__main__":
    unittest.main()
