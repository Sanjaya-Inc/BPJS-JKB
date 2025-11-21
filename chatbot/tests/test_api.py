import pytest
import sys
import os
import json
from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient

# Add the src directory to Python path
sys.path.append(os.path.join(os.path.dirname(__file__), '..', 'src'))

from api.main import app, GraphDBService

# Create a test client
client = TestClient(app)

@pytest.fixture
def mock_db_service():
    """Mock GraphDBService for unit tests"""
    with patch('api.main.db_service') as mock_service:
        yield mock_service

@pytest.fixture
def sample_hospitals_data():
    """Sample hospital data matching CSV structure"""
    return [
        {
            "hospital_id": "HOS001",
            "name": "RSUP Dr. Hasan Sadikin (RSHS)",
            "class_type": "Class A (National Referral)",
            "latitude": -6.898169,
            "longitude": 107.598406,
            "specialties": ["Anestesi", "Bedah", "Internal Medicine", "Cardiology", "Oncology", "Radiology"],
            "facilities": ["IGD 24/7", "929 Beds", "NICU", "ICU", "Advanced Radiology", "Online Bed Monitoring"]
        },
        {
            "hospital_id": "HOS002",
            "name": "Santosa Hospital Bandung Central",
            "class_type": "Class A (Private)",
            "latitude": -6.952283,
            "longitude": 107.586310,
            "specialties": ["Santosa Brain Centre", "Santosa Heart Center", "Orthopedics", "Neurosurgery", "Oncology"],
            "facilities": ["CT-Scan 128 Slice", "MRI 1.5T", "Helipad", "ICU/HCU/CVCU", "Bank Darah"]
        }
    ]

@pytest.fixture
def sample_doctors_data():
    """Sample doctor data matching CSV structure"""
    return [
        {
            "doctor_id": "DOC001",
            "primary_hospital_id": "HOS002",
            "name": "Dr. Budi Hartono",
            "specialization": "Cardiologist"
        },
        {
            "doctor_id": "DOC002",
            "primary_hospital_id": "HOS001",
            "name": "Dr. Citra Dewi",
            "specialization": "General Surgeon"
        }
    ]

@pytest.fixture
def sample_claims_data():
    """Sample claims data matching CSV structure"""
    return [
        {
            "claim_id": "C1001",
            "doctor_id": "DOC001",
            "hospital_id": "HOS001",
            "diagnosis": "I21.9",
            "total_cost": 65000000,
            "label": "NORMAL",
            "medical_resume_json": "High fever (Day 4), Platelets 40,000. Gum bleeding (+). NS1 Positive."
        },
        {
            "claim_id": "C1002",
            "doctor_id": "DOC002",
            "hospital_id": "HOS002",
            "diagnosis": "K35.80",
            "total_cost": 32000000,
            "label": "NORMAL",
            "medical_resume_json": "Acute RLQ pain, Rebound tenderness (+). Leukocytosis 15k."
        }
    ]

class TestRootEndpoint:
    """Test the root endpoint"""
    
    def test_read_root(self):
        """Test the root endpoint returns correct information"""
        response = client.get("/")
        assert response.status_code == 200
        data = response.json()
        assert data["message"] == "BPJS-JKB API"
        assert data["version"] == "1.0.0"

class TestHospitalsEndpoint:
    """Test the hospitals endpoint"""
    
    def test_get_hospitals_success(self, mock_db_service, sample_hospitals_data):
        """Test successful retrieval of hospitals"""
        mock_db_service.get_hospitals.return_value = sample_hospitals_data
        
        response = client.get("/hospitals")
        assert response.status_code == 200
        
        data = response.json()
        assert "data" in data
        assert len(data["data"]) == 2
        assert data["data"][0]["hospital_id"] == "HOS001"
        assert data["data"][0]["name"] == "RSUP Dr. Hasan Sadikin (RSHS)"
        assert data["data"][0]["class_type"] == "Class A (National Referral)"
        assert isinstance(data["data"][0]["specialties"], list)
        assert isinstance(data["data"][0]["facilities"], list)
        
        mock_db_service.get_hospitals.assert_called_once_with(None, None)
    
    def test_get_hospitals_with_class_filter(self, mock_db_service, sample_hospitals_data):
        """Test hospitals endpoint with class type filter"""
        filtered_data = [sample_hospitals_data[0]]  # Only Class A (National Referral)
        mock_db_service.get_hospitals.return_value = filtered_data
        
        response = client.get("/hospitals?class_type=Class A")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 1
        assert data["data"][0]["class_type"] == "Class A (National Referral)"
        
        mock_db_service.get_hospitals.assert_called_once_with("Class A", None)
    
    def test_get_hospitals_with_specialty_filter(self, mock_db_service, sample_hospitals_data):
        """Test hospitals endpoint with specialty filter"""
        filtered_data = [sample_hospitals_data[0]]  # Only hospitals with Cardiology
        mock_db_service.get_hospitals.return_value = filtered_data
        
        response = client.get("/hospitals?specialty=Cardiology")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 1
        assert "Cardiology" in data["data"][0]["specialties"]
        
        mock_db_service.get_hospitals.assert_called_once_with(None, "Cardiology")
    
    def test_get_hospitals_empty_result(self, mock_db_service):
        """Test hospitals endpoint with no results"""
        mock_db_service.get_hospitals.return_value = []
        
        response = client.get("/hospitals")
        assert response.status_code == 200
        
        data = response.json()
        assert data["data"] == []

class TestDoctorsEndpoint:
    """Test the doctors endpoint"""
    
    def test_get_doctors_success(self, mock_db_service, sample_doctors_data):
        """Test successful retrieval of doctors"""
        mock_db_service.get_doctors.return_value = sample_doctors_data
        
        response = client.get("/doctors")
        assert response.status_code == 200
        
        data = response.json()
        assert "data" in data
        assert len(data["data"]) == 2
        assert data["data"][0]["doctor_id"] == "DOC001"
        assert data["data"][0]["name"] == "Dr. Budi Hartono"
        assert data["data"][0]["specialization"] == "Cardiologist"
        assert data["data"][0]["primary_hospital_id"] == "HOS002"
        
        mock_db_service.get_doctors.assert_called_once_with(None, None)
    
    def test_get_doctors_with_specialization_filter(self, mock_db_service, sample_doctors_data):
        """Test doctors endpoint with specialization filter"""
        filtered_data = [sample_doctors_data[0]]  # Only Cardiologist
        mock_db_service.get_doctors.return_value = filtered_data
        
        response = client.get("/doctors?specialization=Cardiologist")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 1
        assert data["data"][0]["specialization"] == "Cardiologist"
        
        mock_db_service.get_doctors.assert_called_once_with("Cardiologist", None)
    
    def test_get_doctors_with_hospital_filter(self, mock_db_service, sample_doctors_data):
        """Test doctors endpoint with hospital filter"""
        filtered_data = [sample_doctors_data[1]]  # Only doctors at HOS001
        mock_db_service.get_doctors.return_value = filtered_data
        
        response = client.get("/doctors?hospital_id=HOS001")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 1
        assert data["data"][0]["primary_hospital_id"] == "HOS001"
        
        mock_db_service.get_doctors.assert_called_once_with(None, "HOS001")

class TestClaimsEndpoint:
    """Test the claims endpoint"""
    
    def test_get_claims_success(self, mock_db_service, sample_claims_data):
        """Test successful retrieval of claims"""
        mock_db_service.get_claims.return_value = sample_claims_data
        
        response = client.get("/claims")
        assert response.status_code == 200
        
        data = response.json()
        assert "data" in data
        assert len(data["data"]) == 2
        assert data["data"][0]["claim_id"] == "C1001"
        assert data["data"][0]["doctor_id"] == "DOC001"
        assert data["data"][0]["hospital_id"] == "HOS001"
        assert data["data"][0]["diagnosis"] == "I21.9"
        assert data["data"][0]["total_cost"] == 65000000
        assert data["data"][0]["label"] == "NORMAL"
        
        mock_db_service.get_claims.assert_called_once_with(None, None, None)
    
    def test_get_claims_with_status_filter(self, mock_db_service, sample_claims_data):
        """Test claims endpoint with status filter"""
        normal_claims = [claim for claim in sample_claims_data if claim["label"] == "NORMAL"]
        mock_db_service.get_claims.return_value = normal_claims
        
        response = client.get("/claims?status=NORMAL")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 2
        for claim in data["data"]:
            assert claim["label"] == "NORMAL"
        
        mock_db_service.get_claims.assert_called_once_with("NORMAL", None, None)
    
    def test_get_claims_with_hospital_filter(self, mock_db_service, sample_claims_data):
        """Test claims endpoint with hospital filter"""
        filtered_data = [sample_claims_data[0]]  # Only claims from HOS001
        mock_db_service.get_claims.return_value = filtered_data
        
        response = client.get("/claims?hospital_id=HOS001")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 1
        assert data["data"][0]["hospital_id"] == "HOS001"
        
        mock_db_service.get_claims.assert_called_once_with(None, "HOS001", None)
    
    def test_get_claims_with_doctor_filter(self, mock_db_service, sample_claims_data):
        """Test claims endpoint with doctor filter"""
        filtered_data = [sample_claims_data[1]]  # Only claims from DOC002
        mock_db_service.get_claims.return_value = filtered_data
        
        response = client.get("/claims?doctor_id=DOC002")
        assert response.status_code == 200
        
        data = response.json()
        assert len(data["data"]) == 1
        assert data["data"][0]["doctor_id"] == "DOC002"
        
        mock_db_service.get_claims.assert_called_once_with(None, None, "DOC002")

class TestErrorHandling:
    """Test error handling scenarios"""
    
    def test_database_connection_error(self, mock_db_service):
        """Test handling of database connection errors"""
        from fastapi import HTTPException
        mock_db_service.get_hospitals.side_effect = HTTPException(status_code=500, detail="Database connection error")
        
        response = client.get("/hospitals")
        assert response.status_code == 500
        data = response.json()
        assert "detail" in data
    
    def test_invalid_query_parameters(self):
        """Test handling of invalid query parameters"""
        # The API should still work with unexpected parameters
        response = client.get("/hospitals?invalid_param=test")
        assert response.status_code == 200

class TestResponseFormat:
    """Test response format compliance"""
    
    def test_hospitals_response_format(self, mock_db_service, sample_hospitals_data):
        """Test that hospitals response matches expected CSV-like format"""
        mock_db_service.get_hospitals.return_value = sample_hospitals_data
        
        response = client.get("/hospitals")
        data = response.json()
        
        # Check main structure
        assert "data" in data
        assert isinstance(data["data"], list)
        
        # Check individual hospital format
        hospital = data["data"][0]
        required_fields = ["hospital_id", "name", "class_type", "latitude", "longitude", "specialties", "facilities"]
        for field in required_fields:
            assert field in hospital
        
        # Check data types
        assert isinstance(hospital["hospital_id"], str)
        assert isinstance(hospital["name"], str)
        assert isinstance(hospital["class_type"], str)
        assert isinstance(hospital["latitude"], (int, float))
        assert isinstance(hospital["longitude"], (int, float))
        assert isinstance(hospital["specialties"], list)
        assert isinstance(hospital["facilities"], list)
    
    def test_doctors_response_format(self, mock_db_service, sample_doctors_data):
        """Test that doctors response matches expected CSV-like format"""
        mock_db_service.get_doctors.return_value = sample_doctors_data
        
        response = client.get("/doctors")
        data = response.json()
        
        # Check main structure
        assert "data" in data
        assert isinstance(data["data"], list)
        
        # Check individual doctor format
        doctor = data["data"][0]
        required_fields = ["doctor_id", "primary_hospital_id", "name", "specialization"]
        for field in required_fields:
            assert field in doctor
        
        # Check data types
        assert isinstance(doctor["doctor_id"], str)
        assert isinstance(doctor["primary_hospital_id"], str)
        assert isinstance(doctor["name"], str)
        assert isinstance(doctor["specialization"], str)
    
    def test_claims_response_format(self, mock_db_service, sample_claims_data):
        """Test that claims response matches expected CSV-like format"""
        mock_db_service.get_claims.return_value = sample_claims_data
        
        response = client.get("/claims")
        data = response.json()
        
        # Check main structure
        assert "data" in data
        assert isinstance(data["data"], list)
        
        # Check individual claim format
        claim = data["data"][0]
        required_fields = ["claim_id", "doctor_id", "hospital_id", "diagnosis", "total_cost", "label"]
        for field in required_fields:
            assert field in claim
        
        # Check data types
        assert isinstance(claim["claim_id"], str)
        assert isinstance(claim["doctor_id"], str)
        assert isinstance(claim["hospital_id"], str)
        assert isinstance(claim["diagnosis"], str)
        assert isinstance(claim["total_cost"], (int, float))
        assert isinstance(claim["label"], str)
