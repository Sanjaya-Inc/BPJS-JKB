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

class Diagnosis(BaseModel):
    diagnosis_id: str
    icd10_code: str
    name: str
    avg_cost: float
    severity_level: str

class DiagnosisResponse(BaseModel):
    data: List[Diagnosis]

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

# Form Verification schemas
class ClaimFormVerificationRequest(BaseModel):
    hospital_id: str = Field(..., description="Hospital ID", min_length=1)
    doctor_id: str = Field(..., description="Doctor ID", min_length=1)
    diagnosa_id: str = Field(..., description="Diagnosis ID (ICD-10 code)", min_length=1)
    total_cost: float = Field(..., description="Total cost of the claim", gt=0)
    primary_procedure: str = Field(..., description="Primary medical procedure", min_length=1)
    secondary_procedure: Optional[str] = Field(None, description="Secondary medical procedure")
    diagnosis_text: str = Field(..., description="Diagnosis description text", min_length=1)

class ClaimFormVerificationResponse(BaseModel):
    form_data_summary: str = Field(..., description="Summary of the input form data")
    validation_result: str = Field(..., description="Validation result: FRAUD or NORMAL")
    confidence_score: int = Field(..., description="Confidence score between 0-100%")
    detail_analysis: str = Field(..., description="Detailed analysis of the validation")
    explanation: str = Field(..., description="Detailed explanation of the validation")
    status: str = Field(default="success", description="Response status")
    metadata: Optional[dict] = Field(default=None, description="Additional metadata from processing")

# Hospital Analysis schemas
class HospitalAnalysisItem(BaseModel):
    diagnosis_id: str = Field(..., description="Diagnosis node ID")
    diagnosis_name: str = Field(..., description="Diagnosis name")
    total_claims: int = Field(..., description="Total number of claims for this diagnosis")
    z_score: float = Field(..., description="Z-score indicating deviation from normal claiming patterns")

class HospitalAnalysisResponse(BaseModel):
    data: List[HospitalAnalysisItem]
    hospital_id: str = Field(..., description="Hospital ID")
    hospital_name: str = Field(..., description="Hospital name")
    analysis_type: str = Field(default="claiming_behavior_normal_distribution", description="Type of analysis performed")
