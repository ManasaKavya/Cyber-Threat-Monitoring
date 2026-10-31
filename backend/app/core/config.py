from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import List

class Settings(BaseSettings):
    PROJECT_NAME: str = "TrustGuardAI API"
    API_V1_STR: str = "/api"

    # CORS
    BACKEND_CORS_ORIGINS: List[str] = ["*"]

    # Provider Keys (Secrets - to be set in .env)
    VIRUSTOTAL_API_KEY: str = ""
    ABUSEIPDB_API_KEY: str = ""

    # Security
    SECRET_KEY: str = "supersecretsecuritykeyforcybertrustguardai" # Should be changed in .env
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 # 1 day

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=True
    )

settings = Settings()
