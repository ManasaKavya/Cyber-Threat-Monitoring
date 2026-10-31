import uuid
from datetime import datetime
from app.models.schemas import ScanResponse, ScanType, ThreatClassification
from app.providers.base_provider import ThreatProvider

class MockThreatProvider(ThreatProvider):
    @property
    def name(self) -> str:
        return "Internal Heuristic Engine"

    async def scan_url(self, url: str) -> ScanResponse:
        is_malicious = ".xyz" in url or ".top" in url
        risk_score = 85 if is_malicious else 15
        return ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=url,
            indicator_type=ScanType.URL,
            classification=ThreatClassification.MALICIOUS if is_malicious else ThreatClassification.SAFE,
            risk_score=risk_score,
            threat_indicators=["Suspicious TLD"] if is_malicious else [],
            recommendation="Block access" if is_malicious else "Safe to proceed",
            timestamp=datetime.now(),
            metadata={"provider": self.name}
        )

    async def scan_ip(self, ip: str) -> ScanResponse:
        is_malicious = ip.startswith("103.") or ip.startswith("45.")
        risk_score = 75 if is_malicious else 10
        return ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=ip,
            indicator_type=ScanType.IP,
            classification=ThreatClassification.MALICIOUS if is_malicious else ThreatClassification.SAFE,
            risk_score=risk_score,
            threat_indicators=["Known malicious range"] if is_malicious else [],
            recommendation="Monitor traffic" if is_malicious else "Normal status",
            timestamp=datetime.now(),
            metadata={"provider": self.name}
        )

    async def scan_file(self, file_content: bytes, filename: str) -> ScanResponse:
        is_malicious = filename.endswith(".exe") or filename.endswith(".apk")
        risk_score = 90 if is_malicious else 5
        return ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=filename,
            indicator_type=ScanType.FILE,
            classification=ThreatClassification.MALICIOUS if is_malicious else ThreatClassification.SAFE,
            risk_score=risk_score,
            threat_indicators=["Executable attachment"] if is_malicious else [],
            recommendation="Do not open" if is_malicious else "Low risk file",
            timestamp=datetime.now(),
            metadata={"provider": self.name, "file_size": str(len(file_content))}
        )
