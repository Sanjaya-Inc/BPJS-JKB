from typing import List, Dict, Optional
from neo4j import Session
from fastapi import HTTPException

class HealthcareRepository:
    def __init__(self, session: Session):
        self.session = session

    def run_query(self, query: str, params: Dict = None) -> List[Dict]:
        """Execute a read transaction and return a list of dictionaries."""
        try:
            result = self.session.execute_read(
                lambda tx: list(tx.run(query, params or {}))
            )
            return [record.data() for record in result]
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")

    def get_hospitals(self, class_type: Optional[str], specialty: Optional[str]) -> List[Dict]:
        query = """
        MATCH (h:Hospital)
        OPTIONAL MATCH (h)-[:HAS_SPECIALTY]->(s:Specialty)
        OPTIONAL MATCH (h)-[:HAS_FACILITY]->(f:Facility)
        """
        
        conditions = []
        params = {}

        if class_type:
            conditions.append("h.class CONTAINS $class_type")
            params["class_type"] = class_type
        
        if specialty:
            conditions.append("s.name CONTAINS $specialty")
            params["specialty"] = specialty

        if conditions:
            query += " WHERE " + " AND ".join(conditions)

        query += """
        WITH h, 
             COLLECT(DISTINCT s.name) AS specialties,
             COLLECT(DISTINCT f.name) AS facilities
        RETURN h.id AS hospital_id,
               h.name AS name,
               h.class AS class_type,
               {latitude: h.location.latitude, longitude: h.location.longitude} AS location,
               specialties,
               facilities
        ORDER BY h.id
        """
        return self.run_query(query, params)

    def get_doctors(self, specialization: Optional[str], hospital_id: Optional[str]) -> List[Dict]:
        query = "MATCH (d:Doctor)-[:WORKS_AT]->(h:Hospital)"
        conditions = []
        params = {}

        if specialization:
            conditions.append("d.specialization CONTAINS $specialization")
            params["specialization"] = specialization
        
        if hospital_id:
            conditions.append("h.id = $hospital_id")
            params["hospital_id"] = hospital_id

        if conditions:
            query += " WHERE " + " AND ".join(conditions)

        query += """
        RETURN d.id AS doctor_id,
               h.id AS primary_hospital_id,
               d.name AS name,
               d.specialization AS specialization
        ORDER BY d.id
        """
        return self.run_query(query, params)

    def get_claims(self, status: Optional[str], hospital_id: Optional[str], doctor_id: Optional[str]) -> List[Dict]:
        query = """
        MATCH (c:Claim)-[:SUBMITTED_AT]->(h:Hospital)
        MATCH (c)-[:SUBMITTED_BY]->(d:Doctor)
        MATCH (c)-[:CODED_AS]->(diag:Diagnosis)
        OPTIONAL MATCH (c)-[:HAS_CLINICAL_NOTE]->(cn:ClinicalNote)
        """
        
        conditions = []
        params = {}

        if status:
            conditions.append("c.status = $status")
            params["status"] = status
        if hospital_id:
            conditions.append("h.id = $hospital_id")
            params["hospital_id"] = hospital_id
        if doctor_id:
            conditions.append("d.id = $doctor_id")
            params["doctor_id"] = doctor_id

        if conditions:
            query += " WHERE " + " AND ".join(conditions)

        query += """
        RETURN c.id AS claim_id,
               d.id AS doctor_id,
               h.id AS hospital_id,
               diag.code AS diagnosis,
               c.total_cost AS total_cost,
               c.status AS label,
               cn.text_raw AS medical_resume_json
        ORDER BY c.id
        """
        return self.run_query(query, params)