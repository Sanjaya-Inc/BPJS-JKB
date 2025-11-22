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

    def get_diagnoses(self, severity_level: Optional[str], icd10_code: Optional[str], 
                     name: Optional[str], min_cost: Optional[float], max_cost: Optional[float]) -> List[Dict]:
        query = "MATCH (d:Diagnosis)"
        conditions = []
        params = {}

        if severity_level:
            conditions.append("d.severity_level = $severity_level")
            params["severity_level"] = severity_level
        
        if icd10_code:
            conditions.append("d.icd10_code CONTAINS $icd10_code")
            params["icd10_code"] = icd10_code
            
        if name:
            conditions.append("toLower(d.name) CONTAINS toLower($name)")
            params["name"] = name
            
        if min_cost:
            conditions.append("d.avg_cost >= $min_cost")
            params["min_cost"] = min_cost
            
        if max_cost:
            conditions.append("d.avg_cost <= $max_cost")
            params["max_cost"] = max_cost

        if conditions:
            query += " WHERE " + " AND ".join(conditions)

        query += """
        RETURN d.node_id AS diagnosis_id,
               d.icd10_code AS icd10_code,
               d.name AS name,
               d.avg_cost AS avg_cost,
               d.severity_level AS severity_level
        ORDER BY d.node_id
        """
        return self.run_query(query, params)

    def get_diagnosis_by_id(self, diagnosis_id: str) -> List[Dict]:
        query = """
        MATCH (d:Diagnosis)
        WHERE d.node_id = $diagnosis_id OR d.icd10_code = $diagnosis_id
        RETURN d.node_id AS diagnosis_id,
               d.icd10_code AS icd10_code,
               d.name AS name,
               d.avg_cost AS avg_cost,
               d.severity_level AS severity_level
        """
        return self.run_query(query, {"diagnosis_id": diagnosis_id})

    def analyze_hospital_claiming_behavior(self, hospital_id: str) -> Dict:
        """Analyze hospital behavior on claiming using normal distribution."""
        query = """
        MATCH (h:Hospital {id: $hospital_id})<-[:SUBMITTED_AT]-(c:Claim)-[:CODED_AS]->(d:Diagnosis)
        WHERE d.market_avg_cost IS NOT NULL
        RETURN 
            h.name as hospital_name,
            d.code as diagnosis_id,
            d.name as diagnosis_name, 
            count(c) as total_claims,      
            avg(c.z_score) as z_score
        ORDER BY z_score DESC
        """
        results = self.run_query(query, {"hospital_id": hospital_id})
        
        # Extract hospital name from first result (all rows have same hospital)
        hospital_name = results[0]["hospital_name"] if results else "Unknown Hospital"
        
        return {
            "hospital_name": hospital_name,
            "analysis_data": results
        }
