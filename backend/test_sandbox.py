import unittest
import asyncio
from app.services.sandbox_service import sandbox_service
from app.models.schemas import ThreatClassification, ThreatSeverity

class TestSandboxService(unittest.TestCase):
    def test_static_analysis_executable(self):
        # Mock content of an EXE file
        content = b"MZ" + b"\x00" * 100
        filename = "malware.exe"

        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(sandbox_service.perform_static_analysis(content, filename))

        self.assertEqual(result.classification, ThreatClassification.MALICIOUS)
        self.assertEqual(result.severity, ThreatSeverity.HIGH) # 60 (.exe) + 15 (MZ) = 75 (HIGH)
        self.assertIn("Windows Portable Executable (PE) format", result.threat_indicators)
        print("Sandbox Static Analysis (EXE): PASSED")

    def test_static_analysis_suspicious_double_extension(self):
        content = b"Some document content" # 21 bytes
        filename = "invoice.pdf.js"

        loop = asyncio.get_event_loop()
        result = loop.run_until_complete(sandbox_service.perform_static_analysis(content, filename))

        self.assertIn("Multiple file extensions detected", result.threat_indicators)
        self.assertEqual(result.classification, ThreatClassification.MALICIOUS)
        # 40 (.js) + 15 (invoice) + 20 (multi) + 10 (small size) = 85 (CRITICAL)
        self.assertEqual(result.severity, ThreatSeverity.CRITICAL)
        print("Sandbox Static Analysis (Double Ext): PASSED")

if __name__ == "__main__":
    unittest.main()
