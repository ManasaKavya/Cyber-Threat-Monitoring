import httpx
import uuid
from datetime import datetime
from app.models.schemas import ScanResponse, ScanType, ThreatClassification
from app.providers.base_provider import ThreatProvider
from app.core.config import settings

class AbuseIPDBProvider(ThreatProvider):
    def __init__(self):
        self.api_key = settings.ABUSEIPDB_API_KEY
        self.base_url = "https://api.abuseipdb.com/api/v2"
        self.headers = {
            "Key": self.api_key,
            "Accept": "application/json"
        }

    @property
    def name(self) -> str:
        return "AbuseIPDB"

    def _is_available(self) -> bool:
        return bool(self.api_key and self.api_key.strip())

    def _unavailable_response(self, indicator: str, indicator_type: ScanType, reason: str = "Provider unavailable") -> ScanResponse:
        return ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=indicator,
            indicator_type=indicator_type,
            classification=ThreatClassification.SAFE,
            risk_score=0,
            threat_indicators=[],
            recommendation=reason,
            timestamp=datetime.now(),
            metadata={"provider": self.name, "status": "unavailable"}
        )

    async def scan_url(self, url: str) -> ScanResponse:
        return self._unavailable_response(url, ScanType.URL, "AbuseIPDB does not support URL scanning")

    async def scan_ip(self, ip: str) -> ScanResponse:
        if not self._is_available():
            return self._unavailable_response(ip, ScanType.IP)

        params = {
            "ipAddress": ip,
            "maxAgeInDays": "90",
            "verbose": ""
        }

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.get(f"{self.base_url}/check", headers=self.headers, params=params)
                if response.status_code == 200:
                    data = response.json()["data"]
                    abuse_score = data.get("abuseConfidenceScore", 0)
                    total_reports = data.get("totalReports", 0)

                    classification = ThreatClassification.SAFE
                    if abuse_score > 75:
                        classification = ThreatClassification.MALICIOUS
                    elif abuse_score > 25:
                        classification = ThreatClassification.SUSPICIOUS

                    indicators = []
                    if abuse_score > 0:
                        indicators.append(f"Abuse Confidence: {abuse_score}%")
                    if total_reports > 0:
                        indicators.append(f"Total Reports: {total_reports}")

                    return ScanResponse(
                        scan_id=str(uuid.uuid4()),
                        indicator=ip,
                        indicator_type=ScanType.IP,
                        classification=classification,
                        risk_score=abuse_score,
                        threat_indicators=indicators,
                        recommendation="Block/Restric access" if classification == ThreatClassification.MALICIOUS else "Monitor activity",
                        timestamp=datetime.now(),
                        metadata={
                            "provider": self.name,
                            "domain": data.get("domain", "N/A"),
                            "isp": data.get("isp", "N/A"),
                            "usage_type": data.get("usageType", "N/A")
                        }
                    )
                else:
                    return self._unavailable_response(ip, ScanType.IP, f"API Error: {response.status_code}")
            except Exception as e:
                return self._unavailable_response(ip, ScanType.IP, f"Connection failed: {str(e)}")

    async def scan_file(self, file_content: bytes, filename: str) -> ScanResponse:
        return self._unavailable_response(filename, ScanType.FILE, "AbuseIPDB does not support file scanning")
