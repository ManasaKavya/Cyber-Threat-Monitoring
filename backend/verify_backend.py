from fastapi.testclient import TestClient
import sys
import os

# Add the app directory to sys.path
sys.path.append(os.path.join(os.path.dirname(__file__)))

from app.main import app

client = TestClient(app)

def get_token(email, password):
    res = client.post("/api/auth/login", json={"email": email, "password": password})
    return res.json()["access_token"]

def test_health():
    response = client.get("/api/health")
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"
    print("Health check: PASSED")

def test_scan_url():
    token = get_token("employee@trustguard.ai", "password123")
    headers = {"Authorization": f"Bearer {token}"}
    response = client.post("/api/scan/url", json={"target": "http://malicious.xyz"}, headers=headers)
    assert response.status_code == 200
    data = response.json()
    assert "classification" in data
    assert "severity" in data
    print("URL scan: PASSED")

def test_scan_ip():
    token = get_token("employee@trustguard.ai", "password123")
    headers = {"Authorization": f"Bearer {token}"}
    response = client.post("/api/scan/ip", json={"target": "103.45.1.1"}, headers=headers)
    assert response.status_code == 200
    data = response.json()
    assert "classification" in data
    print("IP scan: PASSED")

def test_scan_file():
    token = get_token("employee@trustguard.ai", "password123")
    headers = {"Authorization": f"Bearer {token}"}
    from io import BytesIO
    file_content = b"fake malicious content"
    file = {"file": ("malware.exe", BytesIO(file_content), "application/octet-stream")}
    response = client.post("/api/scan/file", files=file, headers=headers)
    assert response.status_code == 200
    data = response.json()
    assert "classification" in data
    assert "severity" in data
    print("File scan: PASSED")

def test_history():
    token = get_token("employee@trustguard.ai", "password123")
    headers = {"Authorization": f"Bearer {token}"}
    response = client.get("/api/threats", headers=headers)
    assert response.status_code == 200
    assert "threats" in response.json()
    print("History check: PASSED")

def test_admin_endpoints():
    token = get_token("admin@trustguard.ai", "adminsecure!")
    headers = {"Authorization": f"Bearer {token}"}

    # Test risk overview
    overview_res = client.get("/api/admin/risk-overview", headers=headers)
    assert overview_res.status_code == 200
    assert "org_risk_score" in overview_res.json()

    # Test employee risks
    employee_res = client.get("/api/admin/employee-risk", headers=headers)
    assert employee_res.status_code == 200
    assert isinstance(employee_res.json(), list)

    # Test activities
    activities_res = client.get("/api/admin/activities", headers=headers)
    assert activities_res.status_code == 200
    assert isinstance(activities_res.json(), list)
    print("Admin API checks: PASSED")

if __name__ == "__main__":
    try:
        test_health()
        test_scan_url()
        test_scan_ip()
        test_scan_file()
        test_history()
        test_admin_endpoints()
        print("\nAll backend verification tests PASSED successfully!")
    except Exception as e:
        print(f"\nVerification FAILED: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
