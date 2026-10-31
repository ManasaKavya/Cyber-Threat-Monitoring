from abc import ABC, abstractmethod
from app.models.schemas import ScanResponse, ScanType

class ThreatProvider(ABC):
    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @abstractmethod
    async def scan_url(self, url: str) -> ScanResponse:
        pass

    @abstractmethod
    async def scan_ip(self, ip: str) -> ScanResponse:
        pass

    @abstractmethod
    async def scan_file(self, file_content: bytes, filename: str) -> ScanResponse:
        pass
