from pydantic import BaseModel, Field
from typing import List, Optional

class Location(BaseModel):
    latitude: float
    longitude: float

class Hospital(BaseModel):
    hospital_id: str
    name: str
    class_type: Optional[str] = Field(None, alias="class_type")
    location: Optional[Location] = None
    specialties: List[str] = []
    facilities: List[str] = []

class HospitalResponse(BaseModel):
    data: List[Hospital]

class Doctor(BaseModel):
    doctor_id: str
    name: str
    specialization: str
    primary_hospital_id: str

class DoctorResponse(BaseModel):
    data: List[Doctor]

class Claim(BaseModel):
    claim_id: str
    doctor_id: str
    hospital_id: str
    diagnosis: str
    total_cost: float
    label: str
    medical_resume_json: Optional[str] = None

class ClaimResponse(BaseModel):
    data: List[Claim]