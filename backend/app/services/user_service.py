from typing import List, Optional
import uuid
from app.models.schemas import User, UserRole, LoginRequest
from app.core.security import get_password_hash, verify_password

class UserService:
    def __init__(self):
        # In-memory storage for users
        self.users = {}
        self._seed_users()

    def get_user_by_email(self, email: str) -> Optional[dict]:
        return self.users.get(email.lower())

    def authenticate_user(self, email: str, password: str) -> Optional[User]:
        user_data = self.get_user_by_email(email)
        if not user_data:
            return None
        if not verify_password(password, user_data["hashed_password"]):
            return None
        return User(
            id=user_data["id"],
            email=user_data["email"],
            role=user_data["role"]
        )

    def _seed_users(self):
        # Default Employee
        self.users["employee@trustguard.ai"] = {
            "id": "user_emp_001",
            "email": "employee@trustguard.ai",
            "hashed_password": get_password_hash("password123"),
            "role": UserRole.EMPLOYEE
        }
        # Default Admin
        self.users["admin@trustguard.ai"] = {
            "id": "user_adm_001",
            "email": "admin@trustguard.ai",
            "hashed_password": get_password_hash("adminsecure!"),
            "role": UserRole.ADMIN
        }

user_service = UserService()
