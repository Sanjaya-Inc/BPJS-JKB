import json
from typing import Type
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool


class SchemaLinkingTool(BaseTool):
    name: str = "schema_linking"
    description: str = """
      Provides the graph database schema definition, including nodes, properties, and relationship paths.
    """

    def _run(self, question: str) -> str:
        return """
        BPJS GRAPH DATABASE SCHEMA
        ==========================
        Enhanced medical insurance claims database with structured medical resume data.

        CORE NODES & PROPERTIES:
        ------------------------
        
        1. Claim (Primary Entity)
           - id (String): "C1001", "C1002"
           - total_cost (Float): 65000000.0
           - status (String): "NORMAL", "FRAUD", null
           - date (Date): Claim submission date

        2. Patient (From medical_resume_json)
           - name (String): "Budi Santoso", "Siti Aminah"
           
        3. Hospital (Infrastructure)
           - id (String): "HOS001", "HOS002"
           - name (String): "RSUP Dr. Hasan Sadikin (RSHS)"
           - class (String): "Class A (National Referral)"
           - location (Point): Geospatial coordinates

        4. Doctor (Medical Staff)
           - id (String): "DOC001", "DOC002"  
           - name (String): "Dr. Budi Hartono"
           - specialization (String): "Cardiologist", "Surgeon"

        5. Diagnosis (ICD-10 Codes)
           - code (String): "I21.9", "K35.80", "A90"
           - name (String): "Acute Myocardial Infarction"
           - avg_cost (Float): Expected treatment cost
           - severity (String): "High", "Medium", "Low"

        6. Procedure (Medical Procedures)
           - code (String): "89.52", "37.22", "UNCODIFIED"
           - name (String): "PCI (Angiography/Stent)", "IV Heparin"
           - avg_cost (Float): Procedure cost
           * UNCODIFIED: New procedures from medical resume text

        7. ClinicalNote (Medical Documentation)
           - primary_diagnosis_text (String): Clinical primary diagnosis
           - secondary_diagnosis_text (String): Supporting clinical findings

        8. Specialty (Hospital Capabilities)
           - name (String): "Cardiology", "Oncology"

        9. Facility (Hospital Equipment)
           - name (String): "ICU", "CT Scan", "MRI"

        RELATIONSHIP STRUCTURE:
        ----------------------
        
        CLAIM RELATIONSHIPS:
        (:Claim)-[:SUBMITTED_AT]->(:Hospital)           // Where claim filed
        (:Claim)-[:SUBMITTED_BY]->(:Doctor)             // Submitting doctor
        (:Claim)-[:CODED_AS]->(:Diagnosis)              // Primary ICD-10 code
        (:Claim)-[:HAS_PATIENT]->(:Patient)             // Patient info
        (:Claim)-[:HAS_PRIMARY_PROCEDURE]->(:Procedure) // Main procedures
        (:Claim)-[:HAS_SECONDARY_PROCEDURE]->(:Procedure) // Support procedures
        (:Claim)-[:HAS_CLINICAL_NOTE]->(:ClinicalNote)  // Clinical docs

        INFRASTRUCTURE:
        (:Doctor)-[:WORKS_AT]->(:Hospital)
        (:Hospital)-[:HAS_SPECIALTY]->(:Specialty)
        (:Hospital)-[:HAS_FACILITY]->(:Facility)

        MEDICAL RULES:
        (:Diagnosis)-[:REQUIRES]->(:Procedure)
        (:Diagnosis)-[:TYPICALLY_TREATED_WITH]->(:Procedure)

        KEY QUERIES FOR MEDICAL RESUME DATA:
        -----------------------------------
        
        1. GET COMPLETE MEDICAL RESUME FOR CLAIM:
        MATCH (c:Claim {id: "C1001"})
        OPTIONAL MATCH (c)-[:HAS_PATIENT]->(patient:Patient)
        OPTIONAL MATCH (c)-[:HAS_PRIMARY_PROCEDURE]->(pp:Procedure)
        OPTIONAL MATCH (c)-[:HAS_SECONDARY_PROCEDURE]->(sp:Procedure)  
        OPTIONAL MATCH (c)-[:HAS_CLINICAL_NOTE]->(note:ClinicalNote)
        OPTIONAL MATCH (c)-[:SUBMITTED_AT]->(hospital:Hospital)
        OPTIONAL MATCH (c)-[:SUBMITTED_BY]->(doctor:Doctor)
        OPTIONAL MATCH (c)-[:CODED_AS]->(diagnosis:Diagnosis)
        RETURN c.id, c.total_cost, c.status,
               patient.name as patient_name,
               hospital.name as hospital_name,
               doctor.name as doctor_name,
               diagnosis.name as diagnosis_name,
               collect(DISTINCT pp.name) as primary_procedures,
               collect(DISTINCT sp.name) as secondary_procedures,
               note.primary_diagnosis_text,
               note.secondary_diagnosis_text

        2. GET ALL PROCEDURES (PRIMARY + SECONDARY):
        MATCH (c:Claim {id: "C1001"})-[r:HAS_PRIMARY_PROCEDURE|HAS_SECONDARY_PROCEDURE]->(p:Procedure)
        RETURN type(r) as procedure_type, p.name, p.code, p.avg_cost
        ORDER BY procedure_type, p.name

        3. FIND CLAIMS BY PROCEDURE:
        MATCH (c:Claim)-[r:HAS_PRIMARY_PROCEDURE|HAS_SECONDARY_PROCEDURE]->(p:Procedure)
        WHERE p.name CONTAINS "PCI"
        RETURN c.id, c.status, type(r) as procedure_type, p.name

        4. MEDICAL COMPLIANCE CHECK:
        MATCH (c:Claim)-[:CODED_AS]->(d:Diagnosis)
        MATCH (c)-[:HAS_PRIMARY_PROCEDURE]->(actual:Procedure)
        OPTIONAL MATCH (d)-[:REQUIRES]->(required:Procedure)
        RETURN c.id, d.name, actual.name, required.name
        WHERE required IS NOT NULL AND actual.name <> required.name
        """
