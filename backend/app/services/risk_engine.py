from typing import List, Dict
from app.models.schemas import ThreatClassification, ThreatSeverity, ScanResponse, ScanType

class RiskEngine:
    def calculate_final_risk(self, results: List[ScanResponse]) -> Dict:
        """
        Intelligent Risk Engine: Combines multiple security signals using a weighted scoring logic.
        """
        if not results:
            return self._default_risk()

        total_weight = 0
        weighted_score = 0
        all_indicators = []
        confidence_sum = 0

        # Weights for different providers/signals
        weights = {
            "Internal Heuristic Engine": 0.3,
            "VirusTotal": 0.5,
            "AbuseIPDB": 0.5,
            "Sandbox Static Analysis": 0.4
        }

        for res in results:
            provider_name = res.metadata.get("provider", "Unknown")
            if res.metadata.get("status") == "unavailable":
                continue

            weight = weights.get(provider_name, 0.2)

            # Boost weight if malicious
            if res.classification == ThreatClassification.MALICIOUS:
                weight *= 1.2

            total_weight += weight
            weighted_score += (res.risk_score * weight)
            all_indicators.extend(res.threat_indicators)
            confidence_sum += res.confidence * weight

        if total_weight == 0:
            # Fallback to the first available heuristic if all external failed
            return self._map_to_engine_result(results[0].risk_score, results[0].threat_indicators, 0.5)

        final_score = min(100, int(weighted_score / total_weight))
        final_confidence = round(confidence_sum / total_weight, 2)

        return self._map_to_engine_result(final_score, list(set(all_indicators)), final_confidence)

    def _map_to_engine_result(self, score: int, indicators: List[str], confidence: float) -> Dict:
        severity = ThreatSeverity.SAFE
        classification = ThreatClassification.SAFE

        if score >= 90:
            severity = ThreatSeverity.CRITICAL
            classification = ThreatClassification.MALICIOUS
        elif score >= 70:
            severity = ThreatSeverity.HIGH
            classification = ThreatClassification.MALICIOUS
        elif score >= 40:
            severity = ThreatSeverity.MEDIUM
            classification = ThreatClassification.SUSPICIOUS
        elif score >= 15:
            severity = ThreatSeverity.LOW
            classification = ThreatClassification.SUSPICIOUS

        recommendation = "Safe to proceed"
        if severity == ThreatSeverity.CRITICAL:
            recommendation = "TERMINATE ACCESS IMMEDIATELY"
        elif severity == ThreatSeverity.HIGH:
            recommendation = "Block and report incident"
        elif severity == ThreatSeverity.MEDIUM:
            recommendation = "Requires manual investigation"
        elif severity == ThreatSeverity.LOW:
            recommendation = "Proceed with caution"

        return {
            "risk_score": score,
            "severity": severity,
            "classification": classification,
            "threat_indicators": indicators,
            "recommendation": recommendation,
            "confidence": confidence
        }

    def _default_risk(self) -> Dict:
        return {
            "risk_score": 0,
            "severity": ThreatSeverity.SAFE,
            "classification": ThreatClassification.SAFE,
            "threat_indicators": [],
            "recommendation": "No data available",
            "confidence": 0.0
        }

risk_engine = RiskEngine()
