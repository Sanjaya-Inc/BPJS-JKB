import logging
import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..')))

from contextlib import asynccontextmanager
from fastapi import FastAPI, Depends, Query
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional, AsyncGenerator

# Imports
from chatbot.src.database import db
from .repository import HealthcareRepository
from .schemas import HospitalResponse, DoctorResponse, ClaimResponse, DiagnosisResponse, QuestionRequest, ChatbotResponse, ClaimVerificationRequest, ClaimVerificationResponse, ClaimFormVerificationRequest, ClaimFormVerificationResponse, HospitalAnalysisResponse
from .chatbot_service import ChatbotService
from .claim_verification_service import ClaimVerificationService

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncGenerator:
    """
    Handles the startup and shutdown logic for the application.
    Code before 'yield' runs on startup.
    Code after 'yield' runs on shutdown.
    """
    logger.info("Starting up...")
    db.connect()
    
    yield
    
    logger.info("Shutting down...")
    db.close()

# --- App Definition ---
app = FastAPI(
    title="BPJS-JKB API",
    description="API for accessing hospitals, doctors, and claims data from Neo4j GraphDB",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/api-docs",
    lifespan=lifespan 
)

# CORS Middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Dependency Injection ---
def get_repository():
    """
    Creates a repository instance with an active session.
    Closes session automatically after request is done.
    """
    session = db.get_session()
    try:
        yield HealthcareRepository(session)
    finally:
        session.close()

# Global chatbot service instance
_chatbot_service = None

def get_chatbot_service():
    """
    Creates or returns the singleton chatbot service instance.
    """
    global _chatbot_service
    if _chatbot_service is None:
        _chatbot_service = ChatbotService()
    return _chatbot_service

# Global claim verification service instance
_verification_service = None

def get_verification_service():
    """
    Creates or returns the singleton claim verification service instance.
    """
    global _verification_service
    if _verification_service is None:
        _verification_service = ClaimVerificationService()
    return _verification_service

# --- Routes ---

@app.get("/", tags=["Health"])
def read_root():
    return {"message": "BPJS-JKB API", "version": "1.0.0"}

@app.get("/hospitals", response_model=HospitalResponse, tags=["Hospitals"])
def get_hospitals(
    class_type: Optional[str] = Query(None, description="Filter by hospital class type"),
    specialty: Optional[str] = Query(None, description="Filter by specialty"),
    repo: HealthcareRepository = Depends(get_repository)
):
    results = repo.get_hospitals(class_type, specialty)
    return {"data": results}

@app.get("/doctors", response_model=DoctorResponse, tags=["Doctors"])
def get_doctors(
    specialization: Optional[str] = Query(None, description="Filter by doctor specialization"),
    hospital_id: Optional[str] = Query(None, description="Filter by hospital ID"),
    repo: HealthcareRepository = Depends(get_repository)
):
    results = repo.get_doctors(specialization, hospital_id)
    return {"data": results}

@app.get("/claims", response_model=ClaimResponse, tags=["Claims"])
def get_claims(
    status: Optional[str] = Query(None, description="Filter by claim status (NORMAL/FRAUD)"),
    hospital_id: Optional[str] = Query(None, description="Filter by hospital ID"),
    doctor_id: Optional[str] = Query(None, description="Filter by doctor ID"),
    repo: HealthcareRepository = Depends(get_repository)
):
    results = repo.get_claims(status, hospital_id, doctor_id)
    return {"data": results}

@app.get("/diagnoses", response_model=DiagnosisResponse, tags=["Diagnoses"])
def get_diagnoses(
    severity_level: Optional[str] = Query(None, description="Filter by severity level (High/Medium/Low)"),
    icd10_code: Optional[str] = Query(None, description="Filter by ICD-10 code pattern"),
    name: Optional[str] = Query(None, description="Filter by diagnosis name"),
    min_cost: Optional[float] = Query(None, description="Filter by minimum cost"),
    max_cost: Optional[float] = Query(None, description="Filter by maximum cost"),
    repo: HealthcareRepository = Depends(get_repository)
):
    results = repo.get_diagnoses(severity_level, icd10_code, name, min_cost, max_cost)
    return {"data": results}

@app.get("/diagnoses/{diagnosis_id}", response_model=DiagnosisResponse, tags=["Diagnoses"])
def get_diagnosis_by_id(
    diagnosis_id: str,
    repo: HealthcareRepository = Depends(get_repository)
):
    results = repo.get_diagnosis_by_id(diagnosis_id)
    return {"data": results}

@app.get("/hospitals/{hospital_id}/analyze", response_model=HospitalAnalysisResponse, tags=["Hospitals"])
def analyze_hospital_claiming_behavior(
    hospital_id: str,
    repo: HealthcareRepository = Depends(get_repository)
):
    """
    Analyze hospital behavior on claiming using normal distribution.
    
    This endpoint analyzes a hospital's claiming patterns by examining:
    - Claims submitted to the specified hospital  
    - Diagnoses associated with those claims (with diagnosis IDs)
    - Z-scores indicating deviation from normal claiming patterns
    - Results ordered by Z-score (highest deviation first)
    
    The analysis uses normal distribution to identify potential anomalies in claiming behavior.
    Higher Z-scores indicate greater deviation from expected claiming patterns.
    
    Returns both diagnosis details and hospital information for comprehensive analysis.
    """
    from fastapi import HTTPException
    
    result = repo.analyze_hospital_claiming_behavior(hospital_id)
    
    if not result["analysis_data"]:
        raise HTTPException(
            status_code=404, 
            detail=f"No claiming data found for hospital {hospital_id}"
        )
    
    return {
        "data": result["analysis_data"],
        "hospital_id": hospital_id,
        "hospital_name": result["hospital_name"],
        "analysis_type": "claiming_behavior_normal_distribution"
    }

@app.post("/chatbot/ask", response_model=ChatbotResponse, tags=["Chatbot"])
def ask_chatbot(
    request: QuestionRequest,
    chatbot_service: ChatbotService = Depends(get_chatbot_service)
):
    """
    Process a user question through the chatbot agent workflow.
    
    This endpoint replicates the functionality from the dani-chatbot.ipynb notebook,
    using the same agent-based approach with entity extraction, RAG search, 
    context building, schema linking, and Cypher query execution.
    """
    result = chatbot_service.process_question(request.question)
    return ChatbotResponse(**result)

@app.post("/claims/verify", response_model=ClaimVerificationResponse, tags=["Claims"])
def verify_claim(
    request: ClaimVerificationRequest,
    verification_service: ClaimVerificationService = Depends(get_verification_service)
):
    """
    Verify a claim for fraud detection using AI-powered medical knowledge graph analysis.
    
    This endpoint replicates the functionality from the dani-verify-claim-id.ipynb notebook,
    implementing the same multi-step validation process:
    
    1. Data Retrieval: Fetches claim data from Neo4j knowledge graph
    2. Validation Logic: 
       - Procedure consistency check
       - Cost analysis (20% variance rule)  
       - Doctor qualification validation
       - Hospital capability verification
    3. Final Verdict: Returns FRAUD/NORMAL with confidence score and detailed explanation
    
    The validation follows medical rules and allows for reasonable operational variances.
    """
    result = verification_service.verify_claim(request.claim_id)
    return ClaimVerificationResponse(**result)

@app.post("/claims/verify-form", response_model=ClaimFormVerificationResponse, tags=["Claims"])
def verify_claim_form(
    request: ClaimFormVerificationRequest,
    verification_service: ClaimVerificationService = Depends(get_verification_service)
):
    """
    Verify new claim form data for fraud detection using AI-powered medical knowledge graph analysis.
    
    This endpoint replicates the functionality from the dani-verify-claim-form.ipynb notebook,
    implementing the same multi-step validation process for raw form input data:
    
    1. Procedure Consistency: Validates procedures are appropriate for the diagnosis
    2. Cost Analysis: Applies 20% variance rule comparing form cost vs ground truth
    3. Doctor Qualification: Validates doctor specialization (with GP exception rules)
    4. Hospital Capability: Ensures hospital has required facilities for the diagnosis
    5. Final Verdict: Returns FRAUD/NORMAL with confidence score and detailed explanation
    
    Unlike /claims/verify which works with existing claim IDs, this endpoint processes 
    new form data before it becomes a claim in the database.
    
    Example form input:
    {
        "hospital_id": "H001",
        "doctor_id": "D001", 
        "diagnosa_id": "I63",
        "total_cost": 15000000,
        "primary_procedure": "CT Scan",
        "secondary_procedure": "MRI",
        "diagnosis_text": "Cerebral infarction"
    }
    """
    # Convert Pydantic model to dictionary for service method
    form_data = {
        "hospital_id": request.hospital_id,
        "doctor_id": request.doctor_id,
        "diagnosa_id": request.diagnosa_id,
        "total_cost": request.total_cost,
        "primary_procedure": request.primary_procedure,
        "secondary_procedure": request.secondary_procedure,
        "diagnosis_text": request.diagnosis_text
    }
    
    result = verification_service.verify_form_data(form_data)
    return ClaimFormVerificationResponse(**result)
