import unittest
from fastapi.testclient import TestClient
from app.main import app
from app.models.schemas import UserRole

client = TestClient(app)

class TestAuthentication(unittest.TestCase):
    def test_login_success_employee(self):
        response = client.post("/api/auth/login", json={
            "email": "employee@trustguard.ai",
            "password": "password123"
        })
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIn("access_token", data)
        self.assertEqual(data["user"]["role"], UserRole.EMPLOYEE)
        print("Employee Login: PASSED")

    def test_login_success_admin(self):
        response = client.post("/api/auth/login", json={
            "email": "admin@trustguard.ai",
            "password": "adminsecure!"
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["user"]["role"], UserRole.ADMIN)
        print("Admin Login: PASSED")

    def test_login_invalid(self):
        response = client.post("/api/auth/login", json={
            "email": "admin@trustguard.ai",
            "password": "wrongpassword"
        })
        self.assertEqual(response.status_code, 401)
        print("Invalid Login: PASSED")

    def test_rbac_admin_endpoint_denied_for_employee(self):
        # 1. Login as employee
        login_res = client.post("/api/auth/login", json={
            "email": "employee@trustguard.ai",
            "password": "password123"
        })
        token = login_res.json()["access_token"]

        # 2. Try to access admin endpoint
        headers = {"Authorization": f"Bearer {token}"}
        response = client.get("/api/admin/risk-overview", headers=headers)
        self.assertEqual(response.status_code, 403)
        print("RBAC (Employee denied Admin): PASSED")

    def test_rbac_admin_endpoint_allowed_for_admin(self):
        # 1. Login as admin
        login_res = client.post("/api/auth/login", json={
            "email": "admin@trustguard.ai",
            "password": "adminsecure!"
        })
        token = login_res.json()["access_token"]

        # 2. Access admin endpoint
        headers = {"Authorization": f"Bearer {token}"}
        response = client.get("/api/admin/risk-overview", headers=headers)
        self.assertEqual(response.status_code, 200)
        print("RBAC (Admin allowed Admin): PASSED")

if __name__ == "__main__":
    unittest.main()
