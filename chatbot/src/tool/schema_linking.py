import json
from typing import Type
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool

class QuestionInput(BaseModel):
    """Input schema for the schema_linking tool."""
    question: str = Field(
        description="The user's natural language question to be linked to the graph database schema."
    )

class SchemaLinkingTool(BaseTool):
    name: str = "schema_linking"
    description: str = """
    Use this tool to retrieve the database schema (Nodes, Properties, and Relationships) 
    required to translate a natural language question into a Cypher query.
    Returns the graph structure, property data types, and sample values based on the uploaded CSV datasets.
    """
    args_schema: Type[BaseModel] = QuestionInput

    def _run(self, question: str) -> str:
        return """
        GRAPH DATABASE SCHEMA DEFINITION
        --------------------------------
        The database stores medical insurance claims, hospital infrastructure, doctors, and clinical knowledge rules.

        NODES & PROPERTIES (with Sample Values):
        1. Claim (from claims_with_resume.csv)
           - id (String): "C1001"
           - total_cost (Float): 65000000.0
           - label (String): "NORMAL" (e.g., "NORMAL", "FRAUD")
           - medical_resume (JSON String): "{\"Patient_Name\": \"Budi Santoso\", ...}" 

        2. Hospital (from hospital.csv)
           - id (String): "HOS001"
           - name (String): "RSUP Dr. Hasan Sadikin (RSHS)"
           - class (String): "Class A (National Referral)"
           - location (Point): point({latitude: -6.898169, longitude: 107.598406})
           - specialties (List<String>): ["Cardiology", "Oncology", ...]
           - facilities (List<String>): ["ICU", "NICU", "929 Beds", ...]

        3. Doctor (from doctors.csv)
           - id (String): "DOC001"
           - name (String): "Dr. Budi Hartono"
           - specialization (String): "Cardiologist"

        4. Diagnosis (ICD-10) (from master_diagnoses.csv)
           - code (String): "I21.9"
           - name (String): "Acute Myocardial Infarction (Heart Attack)"
           - avg_cost (Float): 65000000.0
           - severity (String): "High"

        5. Procedure (from master_procedure.csv)
           - code (String): "89.52"
           - name (String): "EKG (Electrocardiogram)"
           - avg_cost (Float): 175000.0

        6. ClinicalNote (Extracted from Claim.medical_resume)
           - text_raw (String): "Severe chest pain >3 hrs..."
           - primary_diagnosis_text (String): "Acute Myocardial Infarction"

        7. Specialty
           - name (String): "Cardiology"

        8. Facility
           - name (String): "ICU"

        RELATIONSHIP PATHS:
        -------------------
        1. Transactional (Claim History):
           (:Claim)-[:SUBMITTED_AT]->(:Hospital)  // via hospital_id
           (:Claim)-[:SUBMITTED_BY]->(:Doctor)    // via doctor_id
           (:Claim)-[:CODED_AS]->(:Diagnosis)     // via diagnosis (ICD-10 code)
           (:Claim)-[:INCLUDES_PROCEDURE]->(:Procedure) // via procedure code

        2. Infrastructure:
           (:Doctor)-[:WORKS_AT]->(:Hospital)     // via primary_hospital_id
           (:Hospital)-[:HAS_SPECIALTY]->(:Specialty) // parsed from specialties_json
           (:Hospital)-[:HAS_FACILITY]->(:Facility)   // parsed from facilities_json

        3. Medical Rules (Knowledge Graph from knowledge_rules.csv):
           (:Diagnosis)-[:REQUIRES]->(:Procedure)            // e.g., I21.9 REQUIRES 89.52
           (:Diagnosis)-[:TYPICALLY_TREATED_WITH]->(:Procedure) // e.g., I21.9 TYPICALLY_TREATED_WITH 37.22

        4. Evidence Trail (Unstructured Data):
           (:Claim)-[:HAS_CLINICAL_NOTE]->(:ClinicalNote)
           (:ClinicalNote)-[:MENTIONS]->(:Diagnosis) // Extracted entity linking
           (:ClinicalNote)-[:MENTIONS]->(:Procedure) // Extracted entity linking
        """