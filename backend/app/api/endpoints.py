from fastapi import APIRouter, File, UploadFile, HTTPException, Depends
from datetime import datetime
from typing import List
from app.models.schemas import (
    ScanRequest, ScanResponse, HealthResponse, ThreatHistoryResponse,
    ActivityLog, AdminRiskOverview, BehavioralRisk, Alert, Incident, AlertStatus, UserRole,
    EmployeeSummary
)
from app.services.threat_service import threat_service
from app.services.activity_service import activity_service
from app.services.alert_service import alert_service
from app.api.deps import get_current_user, RoleChecker

router = APIRouter()

# Role definitions
allow_any_auth = Depends(get_current_user)
require_admin = Depends(RoleChecker([UserRole.ADMIN]))

@router.get("/health", response_model=HealthResponse)
async def health_check():
    return HealthResponse(
        status="healthy",
        timestamp=datetime.now()
    )

# --- Scanning Endpoints (Authenticated Users) ---

@router.post("/scan/url", response_model=ScanResponse)
async def scan_url(request: ScanRequest, current_user=allow_any_auth):
    if not request.target:
        raise HTTPException(status_code=400, detail="URL target is required")
    res = await threat_service.scan_url(request.target)
    res.user_id = current_user.id # Tag result with user
    return res

@router.post("/scan/ip", response_model=ScanResponse)
async def scan_ip(request: ScanRequest, current_user=allow_any_auth):
    if not request.target:
        raise HTTPException(status_code=400, detail="IP target is required")
    res = await threat_service.scan_ip(request.target)
    res.user_id = current_user.id
    return res

@router.post("/scan/file", response_model=ScanResponse)
async def scan_file(file: UploadFile = File(...), current_user=allow_any_auth):
    content = await file.read()
    res = await threat_service.scan_file(content, file.filename)
    res.user_id = current_user.id
    return res

@router.get("/threats", response_model=ThreatHistoryResponse)
async def get_threats(current_user=allow_any_auth):
    # Only return user's own history
    all_threats = threat_service.get_history()
    user_threats = [t for t in all_threats if t.user_id == current_user.id]
    return ThreatHistoryResponse(threats=user_threats)

@router.get("/employee/summary", response_model=EmployeeSummary)
async def get_employee_summary(current_user=allow_any_auth):
    return activity_service.get_employee_summary(current_user.id)

# --- Activity & Admin Endpoints ---

@router.post("/activity")
async def log_activity(activity: ActivityLog, current_user=allow_any_auth):
    # Ensure user logs their own activity
    activity.user_id = current_user.id
    activity_service.log_activity(activity)
    return {"status": "success"}

@router.get("/admin/risk-overview", response_model=AdminRiskOverview)
async def get_admin_risk_overview(current_user=require_admin):
    return activity_service.get_risk_overview()

@router.get("/admin/activities", response_model=List[ActivityLog])
async def get_admin_activities(current_user=require_admin):
    return activity_service.get_all_activities()

@router.get("/admin/employee-risk", response_model=List[BehavioralRisk])
async def get_employee_risks(current_user=require_admin):
    return activity_service.get_employee_risks()

# --- Alert Endpoints (Admin) ---

@router.get("/alerts", response_model=List[Alert])
async def get_alerts(current_user=require_admin):
    return alert_service.get_all_alerts()

@router.get("/alerts/{alert_id}", response_model=Alert)
async def get_alert(alert_id: str, current_user=require_admin):
    alert = alert_service.get_alert_by_id(alert_id)
    if not alert:
        raise HTTPException(status_code=404, detail="Alert not found")
    return alert

@router.patch("/alerts/{alert_id}", response_model=Alert)
async def update_alert(alert_id: str, status: AlertStatus, current_user=require_admin):
    alert = alert_service.update_alert_status(alert_id, status)
    if not alert:
        raise HTTPException(status_code=404, detail="Alert not found")
    return alert

# --- Incident Endpoints (Admin) ---

@router.get("/incidents", response_model=List[Incident])
async def get_incidents(current_user=require_admin):
    return alert_service.get_all_incidents()

@router.get("/incidents/{incident_id}", response_model=Incident)
async def get_incident(incident_id: str, current_user=require_admin):
    incident = alert_service.get_incident_by_id(incident_id)
    if not incident:
        raise HTTPException(status_code=404, detail="Incident not found")
    return incident

@router.patch("/incidents/{incident_id}", response_model=Incident)
async def update_incident(incident_id: str, status: AlertStatus, current_user=require_admin):
    incident = alert_service.update_incident_status(incident_id, status)
    if not incident:
        raise HTTPException(status_code=404, detail="Incident not found")
    return incident
