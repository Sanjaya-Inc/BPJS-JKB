from pydantic import BaseModel, Field
from typing import List, Optional, Any

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

# Chatbot schemas
class QuestionRequest(BaseModel):
    question: str = Field(..., description="User's question input", min_length=1)

class ChatbotResponse(BaseModel):
    answer: str = Field(..., description="Processed answer from the chatbot")
    status: str = Field(default="success", description="Response status")
    metadata: Optional[dict] = Field(default=None, description="Additional metadata from processing")

# Claim Verification schemas
class ClaimVerificationRequest(BaseModel):
    claim_id: str = Field(..., description="The claim ID to verify", min_length=1)

class ClaimVerificationResponse(BaseModel):
    claim_id: str = Field(..., description="The verified claim ID")
    validation_result: str = Field(..., description="Validation result: FRAUD or NORMAL")
    confidence_score: int = Field(..., description="Confidence score between 0-100%")
    detail_claim_data: dict = Field(..., description="Detailed claim data from the database")
    explanation: str = Field(..., description="Detailed explanation of the validation")
    status: str = Field(default="success", description="Response status")
