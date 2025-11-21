import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI, Depends, Query
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional, AsyncGenerator

# Imports
from src.database import db
from .repository import HealthcareRepository
from .schemas import HospitalResponse, DoctorResponse, ClaimResponse, QuestionRequest, ChatbotResponse
from .chatbot_service import ChatbotService

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
