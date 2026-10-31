import uuid
from typing import List, Dict
from datetime import datetime
from app.models.schemas import ScanResponse, ThreatClassification, ThreatSeverity, ScanType
from app.providers.mock_provider import MockThreatProvider
from app.providers.virustotal_provider import VirusTotalProvider
from app.providers.abuseipdb_provider import AbuseIPDBProvider
from app.services.risk_engine import risk_engine
from app.services.sandbox_service import sandbox_service
from app.services.alert_service import alert_service

class ThreatService:
    def __init__(self):
        self.heuristic_engine = MockThreatProvider()
        self.vt_provider = VirusTotalProvider()
        self.abuse_provider = AbuseIPDBProvider()
        self.history: List[ScanResponse] = []

    def _orchestrate_risk(self, indicator: str, indicator_type: ScanType, heuristic: ScanResponse, external_results: List[ScanResponse]) -> ScanResponse:
        all_results = [heuristic] + external_results

        # Calculate combined risk using the new Risk Engine
        risk_analysis = risk_engine.calculate_final_risk(all_results)

        # Combine metadata
        combined_metadata = heuristic.metadata.copy()
        for res in external_results:
            if res.metadata.get("status") != "unavailable":
                combined_metadata.update(res.metadata)

        combined_metadata["orchestration"] = "AI Risk Engine (Phase 5)"

        final_res = ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=indicator,
            indicator_type=indicator_type,
            classification=risk_analysis["classification"],
            risk_score=risk_analysis["risk_score"],
            severity=risk_analysis["severity"],
            threat_indicators=risk_analysis["threat_indicators"],
            recommendation=risk_analysis["recommendation"],
            confidence=risk_analysis["confidence"],
            timestamp=datetime.now(),
            metadata=combined_metadata
        )

        # Phase 7: Create alert if critical
        alert_service.create_alert_from_scan(final_res)

        return final_res

    async def scan_url(self, url: str) -> ScanResponse:
        h_result = await self.heuristic_engine.scan_url(url)
        v_result = await self.vt_provider.scan_url(url)

        result = self._orchestrate_risk(url, ScanType.URL, h_result, [v_result])
        self.history.append(result)
        return result

    async def scan_ip(self, ip: str) -> ScanResponse:
        h_result = await self.heuristic_engine.scan_ip(ip)
        v_result = await self.vt_provider.scan_ip(ip)
        a_result = await self.abuse_provider.scan_ip(ip)

        result = self._orchestrate_risk(ip, ScanType.IP, h_result, [v_result, a_result])
        self.history.append(result)
        return result

    async def scan_file(self, content: bytes, filename: str) -> ScanResponse:
        # Heuristics
        h_result = await self.heuristic_engine.scan_file(content, filename)
        # VirusTotal
        v_result = await self.vt_provider.scan_file(content, filename)
        # Sandbox Static Analysis (Phase 5)
        s_result = await sandbox_service.perform_static_analysis(content, filename)

        result = self._orchestrate_risk(filename, ScanType.FILE, h_result, [v_result, s_result])
        self.history.append(result)
        return result

    def get_history(self) -> List[ScanResponse]:
        return self.history[-20:]

threat_service = ThreatService()
