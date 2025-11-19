import json
from typing import Type
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool

# 1. Define the Input Schema
class QuestionInput(BaseModel):
    """Input schema for the schema_linking tool."""
    question: str = Field(
        description="The user's natural language question to be linked to the graph database schema."
    )

# 2. Define the Tool
class SchemaLinkingTool(BaseTool):
    name: str = "schema_linking"
    description: str = """
    Use this tool to retrieve the database schema (Nodes, Properties, and Relationships) 
    required to translate a natural language question into a Cypher query.
    Returns the graph structure, property data types, and sample values.
    """
    args_schema: Type[BaseModel] = QuestionInput

    def _run(self, question: str) -> str:
      return """
        GRAPH DATABASE SCHEMA DEFINITION
        --------------------------------
        The database stores medical insurance claims, infrastructure data, and clinical evidence.

        NODES & PROPERTIES (with Sample Values):
        1. Claim
           - id (String): "C100"
           - sep_no (String): "SEP001"
           - total_cost (Float): 15500000.0
           - date (Date): "2023-11-14"
        
        2. Hospital
           - id (String): "HOS001"
           - name (String): "RSUP Dr. Hasan Sadikin"
           - class (String): "Class A"
           - location (Point): point({latitude: -6.89, longitude: 107.59})
        
        3. Doctor
           - id (String): "DOC001"
           - name (String): "Dr. Budi Hartono"
           - specialization (String): "Cardiologist"
        
        4. Diagnosis (ICD-10)
           - code (String): "I21.9"
           - name (String): "Acute Myocardial Infarction"
           - avg_cost (Float): 15000000.0
           - severity (String): "High"
        
        5. Procedure
           - code (String): "89.52"
           - name (String): "EKG (Electrocardiogram)"
           - avg_cost (Float): 150000.0
        
        6. ClinicalNote (Unstructured Evidence)
           - text_raw (String): "Patient has chest pain..."
           - primary_diagnosis_text (String): "Acute MI"
        
        7. MentionedEntity (Extracted Text)
           - text (String): "PCI Stent"
           - type (String): "Procedure"
        
        8. Specialty
           - name (String): "Cardiology"
        
        9. Facility
           - name (String): "ICU"

        RELATIONSHIP PATHS:
        -------------------
        1. Transactional (Claim History):
           (:Claim)-[:SUBMITTED_AT]->(:Hospital)
           (:Claim)-[:SUBMITTED_BY]->(:Doctor)
           (:Claim)-[:CODED_AS]->(:Diagnosis)
        
        2. Evidence Trail (Connecting Text to Ontology):
           (:Claim)-[:HAS_CLINICAL_NOTE]->(:ClinicalNote)
           (:ClinicalNote)-[:MENTIONS]->(:MentionedEntity)
           (:MentionedEntity)-[:MAPS_TO]->(:Procedure)
        
        3. Infrastructure:
           (:Doctor)-[:WORKS_AT]->(:Hospital)
           (:Hospital)-[:HAS_SPECIALTY]->(:Specialty)
           (:Hospital)-[:HAS_FACILITY]->(:Facility)
        
        4. Medical Rules (Ontology):
           (:Diagnosis)-[:REQUIRES]->(:Procedure)
           (:Diagnosis)-[:TYPICALLY_TREATED_WITH]->(:Procedure)
        """