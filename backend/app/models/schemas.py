from pydantic import BaseModel, EmailStr
from typing import List, Optional, Dict
from datetime import datetime
from enum import Enum

class ThreatClassification(str, Enum):
    SAFE = "SAFE"
    SUSPICIOUS = "SUSPICIOUS"
    MALICIOUS = "MALICIOUS"

class ThreatSeverity(str, Enum):
    SAFE = "SAFE"
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

class ScanType(str, Enum):
    URL = "URL"
    IP = "IP"
    FILE = "FILE"

class ActivityType(str, Enum):
    LOGIN = "LOGIN"
    SCAN_URL = "SCAN_URL"
    SCAN_IP = "SCAN_IP"
    SCAN_FILE = "SCAN_FILE"
    SENSITIVE_ACCESS = "SENSITIVE_ACCESS"
    LOGOUT = "LOGOUT"

class AlertStatus(str, Enum):
    OPEN = "OPEN"
    ACKNOWLEDGED = "ACKNOWLEDGED"
    RESOLVED = "RESOLVED"

class UserRole(str, Enum):
    EMPLOYEE = "EMPLOYEE"
    ADMIN = "ADMIN"

class User(BaseModel):
    id: str
    email: str
    role: UserRole
    is_active: bool = True

class Token(BaseModel):
    access_token: str
    token_type: str
    user: User

class LoginRequest(BaseModel):
    email: str
    password: str

class ScanRequest(BaseModel):
    target: str

class ScanResponse(BaseModel):
    scan_id: str
    indicator: str
    indicator_type: ScanType
    classification: ThreatClassification
    risk_score: int
    severity: Optional[ThreatSeverity] = ThreatSeverity.SAFE
    threat_indicators: List[str]
    recommendation: str
    confidence: float = 1.0
    timestamp: datetime
    metadata: Optional[Dict[str, str]] = {}
    user_id: Optional[str] = None # Added for history tracking

class ActivityLog(BaseModel):
    user_id: str
    activity_type: ActivityType
    timestamp: datetime = datetime.now()
    ip_address: Optional[str] = None
    resource: Optional[str] = None
    severity: ThreatSeverity = ThreatSeverity.SAFE
    metadata: Optional[Dict[str, str]] = {}

class BehavioralRisk(BaseModel):
    user_id: str
    anomaly_score: int
    severity: ThreatSeverity
    risk_factors: List[str]
    recommended_action: str

class AdminRiskOverview(BaseModel):
    org_risk_score: int
    active_threats: int
    critical_incidents: int
    employees_requiring_attention: int
    risk_distribution: Dict[str, int]
    open_alerts_count: int = 0
    critical_alerts_count: int = 0

class Alert(BaseModel):
    id: str
    title: str
    description: str
    severity: ThreatSeverity
    source: str
    related_user: Optional[str] = None
    timestamp: datetime
    status: AlertStatus
    recommended_action: str

class Incident(BaseModel):
    id: str
    title: str
    severity: ThreatSeverity
    risk_score: int
    detection_source: str
    timeline: List[Dict[str, str]]
    related_activities: List[ActivityLog]
    threat_indicators: List[str]
    recommended_response: str
    status: AlertStatus

class HealthResponse(BaseModel):
    status: str
    timestamp: datetime
    version: str = "1.0.0"

class EmployeeSummary(BaseModel):
    risk_score: int
    threats_count: int
    alerts_count: int
    scans_count: int
    recent_activities: List[ActivityLog]
    severity: ThreatSeverity

class ThreatHistoryResponse(BaseModel):
    threats: List[ScanResponse]
