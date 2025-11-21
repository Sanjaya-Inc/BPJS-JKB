import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI, Depends, Query
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional, AsyncGenerator

# Imports
from src.database import db
from .repository import HealthcareRepository
from .schemas import HospitalResponse, DoctorResponse, ClaimResponse

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
    redoc_url="/redoc",
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