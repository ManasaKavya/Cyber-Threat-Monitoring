import base64
import httpx
import uuid
from datetime import datetime
from typing import Optional
from app.models.schemas import ScanResponse, ScanType, ThreatClassification
from app.providers.base_provider import ThreatProvider
from app.core.config import settings

class VirusTotalProvider(ThreatProvider):
    def __init__(self):
        self.api_key = settings.VIRUSTOTAL_API_KEY
        self.base_url = "https://www.virustotal.com/api/v3"
        self.headers = {
            "x-apikey": self.api_key,
            "Accept": "application/json"
        }

    @property
    def name(self) -> str:
        return "VirusTotal"

    def _is_available(self) -> bool:
        return bool(self.api_key and self.api_key.strip())

    def _unavailable_response(self, indicator: str, indicator_type: ScanType) -> ScanResponse:
        return ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=indicator,
            indicator_type=indicator_type,
            classification=ThreatClassification.SAFE,
            risk_score=0,
            threat_indicators=[],
            recommendation="Provider unavailable",
            timestamp=datetime.now(),
            metadata={"provider": self.name, "status": "unavailable"}
        )

    async def scan_url(self, url: str) -> ScanResponse:
        if not self._is_available():
            return self._unavailable_response(url, ScanType.URL)

        # VT requires URLs to be base64 encoded (no padding) for the /urls/{id} endpoint
        url_id = base64.urlsafe_b64encode(url.encode()).decode().strip("=")

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.get(f"{self.base_url}/urls/{url_id}", headers=self.headers)
                if response.status_code == 200:
                    data = response.json()["data"]["attributes"]
                    stats = data["last_analysis_stats"]
                    malicious = stats.get("malicious", 0)
                    suspicious = stats.get("suspicious", 0)
                    total = sum(stats.values())

                    risk_score = int((malicious + suspicious * 0.5) / total * 100) if total > 0 else 0
                    classification = ThreatClassification.SAFE
                    if malicious > 3:
                        classification = ThreatClassification.MALICIOUS
                    elif malicious > 0 or suspicious > 0:
                        classification = ThreatClassification.SUSPICIOUS

                    return ScanResponse(
                        scan_id=str(uuid.uuid4()),
                        indicator=url,
                        indicator_type=ScanType.URL,
                        classification=classification,
                        risk_score=risk_score,
                        threat_indicators=[f"VT Detections: {malicious} malicious"],
                        recommendation="Block" if classification == ThreatClassification.MALICIOUS else "Review",
                        timestamp=datetime.now(),
                        metadata={"provider": self.name, "stats": str(stats)}
                    )
                else:
                    return self._unavailable_response(url, ScanType.URL)
            except Exception:
                return self._unavailable_response(url, ScanType.URL)

    async def scan_ip(self, ip: str) -> ScanResponse:
        if not self._is_available():
            return self._unavailable_response(ip, ScanType.IP)

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.get(f"{self.base_url}/ip_addresses/{ip}", headers=self.headers)
                if response.status_code == 200:
                    data = response.json()["data"]["attributes"]
                    stats = data["last_analysis_stats"]
                    malicious = stats.get("malicious", 0)

                    risk_score = int(malicious / sum(stats.values()) * 100) if sum(stats.values()) > 0 else 0
                    classification = ThreatClassification.MALICIOUS if malicious > 5 else ThreatClassification.SAFE

                    return ScanResponse(
                        scan_id=str(uuid.uuid4()),
                        indicator=ip,
                        indicator_type=ScanType.IP,
                        classification=classification,
                        risk_score=risk_score,
                        threat_indicators=[f"VT Malicious count: {malicious}"],
                        recommendation="Action needed" if classification == ThreatClassification.MALICIOUS else "None",
                        timestamp=datetime.now(),
                        metadata={"provider": self.name, "country": data.get("country", "Unknown")}
                    )
                return self._unavailable_response(ip, ScanType.IP)
            except Exception:
                return self._unavailable_response(ip, ScanType.IP)

    async def scan_file(self, file_content: bytes, filename: str) -> ScanResponse:
        # For simplicity and speed, VT file analysis usually starts with checking the hash
        import hashlib
        sha256_hash = hashlib.sha256(file_content).hexdigest()

        if not self._is_available():
            return self._unavailable_response(filename, ScanType.FILE)

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.get(f"{self.base_url}/files/{sha256_hash}", headers=self.headers)
                if response.status_code == 200:
                    data = response.json()["data"]["attributes"]
                    stats = data["last_analysis_stats"]
                    malicious = stats.get("malicious", 0)

                    risk_score = int(malicious / sum(stats.values()) * 100) if sum(stats.values()) > 0 else 0
                    classification = ThreatClassification.MALICIOUS if malicious > 2 else ThreatClassification.SAFE

                    return ScanResponse(
                        scan_id=str(uuid.uuid4()),
                        indicator=filename,
                        indicator_type=ScanType.FILE,
                        classification=classification,
                        risk_score=risk_score,
                        threat_indicators=[f"VT Engine Detections: {malicious}"],
                        recommendation="Quarantine" if classification == ThreatClassification.MALICIOUS else "Safe",
                        timestamp=datetime.now(),
                        metadata={"provider": self.name, "hash": sha256_hash}
                    )
                return self._unavailable_response(filename, ScanType.FILE)
            except Exception:
                return self._unavailable_response(filename, ScanType.FILE)
