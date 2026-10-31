import hashlib
import uuid
from datetime import datetime
from typing import Dict, List
from app.models.schemas import ScanResponse, ScanType, ThreatClassification, ThreatSeverity

class SandboxService:
    async def perform_static_analysis(self, content: bytes, filename: str) -> ScanResponse:
        """
        Safe Sandbox Analysis: Performs deep static analysis on uploaded files without execution.
        """
        indicators = []
        risk_score = 0

        # 1. Basic Metadata
        file_size = len(content)
        sha256_hash = hashlib.sha256(content).hexdigest()

        # 2. Suspicious Extension Check
        suspicious_exts = {
            ".exe": 60, ".apk": 55, ".bat": 70, ".ps1": 75,
            ".vbs": 80, ".js": 40, ".scr": 85, ".pif": 90
        }

        ext = "." + filename.split(".")[-1].lower() if "." in filename else ""
        if ext in suspicious_exts:
            risk_score += suspicious_exts[ext]
            indicators.append(f"Suspicious file extension: {ext}")

        # 3. Size Heuristics
        if file_size < 100:
            risk_score += 10
            indicators.append("Unusually small file size")

        # 4. Suspicious Filename Patterns
        lower_name = filename.lower()
        if "invoice" in lower_name or "payment" in lower_name or "urgent" in lower_name:
            risk_score += 15
            indicators.append("Social engineering filename pattern")

        if lower_name.count(".") > 1:
            risk_score += 20
            indicators.append("Multiple file extensions detected")

        # 5. Magic Byte Detection
        if content.startswith(b"MZ"): # Windows Executable
            indicators.append("Windows Portable Executable (PE) format")
            if ext != ".exe":
                risk_score += 30
                indicators.append("Mismatched header: Executable disguised as non-exe")
            else:
                risk_score += 15 # Boost for confirmed EXE

        # Normalize score
        risk_score = min(100, risk_score)

        classification = ThreatClassification.SAFE
        severity = ThreatSeverity.SAFE

        if risk_score >= 80:
            classification = ThreatClassification.MALICIOUS
            severity = ThreatSeverity.CRITICAL
        elif risk_score >= 65:
            classification = ThreatClassification.MALICIOUS
            severity = ThreatSeverity.HIGH
        elif risk_score >= 35:
            classification = ThreatClassification.SUSPICIOUS
            severity = ThreatSeverity.MEDIUM
        elif risk_score >= 15:
            classification = ThreatClassification.SUSPICIOUS
            severity = ThreatSeverity.LOW

        return ScanResponse(
            scan_id=str(uuid.uuid4()),
            indicator=filename,
            indicator_type=ScanType.FILE,
            classification=classification,
            risk_score=risk_score,
            severity=severity,
            threat_indicators=indicators,
            recommendation="Sandbox analysis complete",
            confidence=0.9, # High confidence for static signals
            timestamp=datetime.now(),
            metadata={
                "provider": "Sandbox Static Analysis",
                "sha256": sha256_hash,
                "file_size": f"{file_size} bytes",
                "checks_performed": "extension,magic_bytes,filename"
            }
        )

sandbox_service = SandboxService()
