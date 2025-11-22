import pandas as pd
import json
from neo4j import GraphDatabase
import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))
from src.config import NEO4J_URI, NEO4J_AUTH

URI = NEO4J_URI
AUTH = NEO4J_AUTH

FILES = {
    "hospitals": "chatbot/data/actors/hospital.csv",
    "doctors": "chatbot/data/actors/doctors.csv", 
    "diagnoses": "chatbot/data/medical_ontology/master_diagnoses.csv",
    "procedures": "chatbot/data/medical_ontology/master_procedure.csv",
    "rules": "chatbot/data/medical_ontology/knowledge_rules.csv",
    "claims": "chatbot/data/evidence/claims_with_resume.csv" 
}

class BPJSGraphLoader:
    def __init__(self, uri, auth):
        self.driver = GraphDatabase.driver(uri, auth=auth)

    def close(self):
        self.driver.close()

    def clear_database(self):
        with self.driver.session() as session:
            session.run("MATCH (n) DETACH DELETE n")
            print("🧹 Database cleared.")

    def create_constraints(self):
        queries = [
            "CREATE CONSTRAINT IF NOT EXISTS FOR (h:Hospital) REQUIRE h.id IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (d:Doctor) REQUIRE d.id IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (diag:Diagnosis) REQUIRE diag.code IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (p:Procedure) REQUIRE p.code IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (c:Claim) REQUIRE c.id IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (patient:Patient) REQUIRE patient.name IS UNIQUE"
        ]
        with self.driver.session() as session:
            for q in queries:
                session.run(q)
        print("🔒 Constraints created.")

    # ---------------- LOADERS ----------------

    def load_medical_ontology(self):
        """Loads Diagnoses, Procedures, and Medical Rules"""
        print("📚 Loading Medical Ontology...")
        
        # 1. Load Diagnoses
        df_diag = pd.read_csv(FILES["diagnoses"])
        with self.driver.session() as session:
            for _, row in df_diag.iterrows():
                session.run("""
                    MERGE (d:Diagnosis {code: $code})
                    SET d.name = $name, 
                        d.avg_cost = toFloat($cost), 
                        d.severity = $severity
                """, code=row['icd10_code'], name=row['name'], cost=row['avg_cost'], severity=row['severity_level'])

        # 2. Load Procedures
        df_proc = pd.read_csv(FILES["procedures"])
        with self.driver.session() as session:
            for _, row in df_proc.iterrows():
                session.run("""
                    MERGE (p:Procedure {code: $code})
                    SET p.name = $name, 
                        p.avg_cost = toFloat($cost)
                """, code=row['proc_code'], name=row['name'], cost=row['avg_cost'])

        # 3. Load Knowledge Rules (Edges)
        df_rules = pd.read_csv(FILES["rules"])
        with self.driver.session() as session:
            for _, row in df_rules.iterrows():
                rel_type = row['relationship']
                # Note: Using F-string for rel_type is safe here because we control the CSV content.
                query = f"""
                    MATCH (d:Diagnosis {{code: $d_code}})
                    MATCH (p:Procedure {{code: $p_code}})
                    MERGE (d)-[:{rel_type}]->(p)
                """
                session.run(query, d_code=row['source_code'], p_code=row['target_code'])
        
        print(f"   - Loaded {len(df_diag)} Diagnoses, {len(df_proc)} Procedures, {len(df_rules)} Rules.")

    def load_infrastructure(self):
        """Loads Hospitals and Doctors with JSON parsing"""
        print("🏥 Loading Hospitals and Doctors...")

        # 1. Load Hospitals
        df_hos = pd.read_csv(FILES["hospitals"])
        with self.driver.session() as session:
            for _, row in df_hos.iterrows():
                # Create Hospital Node
                session.run("""
                    MERGE (h:Hospital {id: $id})
                    SET h.name = $name, 
                        h.class = $class_type,
                        h.location = point({latitude: toFloat($lat), longitude: toFloat($long)})
                """, id=row['hospital_id'], name=row['name'], class_type=row['class_type'], 
                   lat=row['latitude'], long=row['longitude'])
                
                # Parse Specialties JSON -> Create Edges
                try:
                    specialties = json.loads(row['specialties_json'])
                    for spec in specialties:
                        session.run("""
                            MATCH (h:Hospital {id: $id})
                            MERGE (s:Specialty {name: $spec})
                            MERGE (h)-[:HAS_SPECIALTY]->(s)
                        """, id=row['hospital_id'], spec=spec)
                except: pass # Handle empty or malformed json

                # Parse Facilities JSON -> Create Edges
                try:
                    facilities = json.loads(row['facilities_json'])
                    for fac in facilities:
                        session.run("""
                            MATCH (h:Hospital {id: $id})
                            MERGE (f:Facility {name: $fac})
                            MERGE (h)-[:HAS_FACILITY]->(f)
                        """, id=row['hospital_id'], fac=fac)
                except: pass

        # 2. Load Doctors
        df_doc = pd.read_csv(FILES["doctors"])
        with self.driver.session() as session:
            for _, row in df_doc.iterrows():
                session.run("""
                    MERGE (d:Doctor {id: $id})
                    SET d.name = $name, 
                        d.specialization = $spec
                """, id=row['doctor_id'], name=row['name'], spec=row['specialization'])
                
                # Link Doctor to Primary Hospital
                session.run("""
                    MATCH (d:Doctor {id: $did})
                    MATCH (h:Hospital {id: $hid})
                    MERGE (d)-[:WORKS_AT]->(h)
                """, did=row['doctor_id'], hid=row['primary_hospital_id'])

        print(f"   - Loaded {len(df_hos)} Hospitals and {len(df_doc)} Doctors.")

    def load_claims_and_resume(self):
        """Loads Claims with structured Medical Resume data"""
        print("📄 Loading Claims and Medical Resume Evidence...")
        
        df_claims = pd.read_csv(FILES["claims"])
        
        with self.driver.session() as session:
            for _, row in df_claims.iterrows():
                # 1. Create Basic Claim Node with Label (Status)
                session.run("""
                    MERGE (c:Claim {id: $cid})
                    SET c.total_cost = toFloat($cost),
                        c.status = $label,
                        c.date = date() 
                """, cid=row['claim_id'], cost=row['total_cost'], label=row['label'])

                # 2. Link Actors (Hospital, Doctor, ICD-10)
                session.run("""
                    MATCH (c:Claim {id: $cid})
                    MATCH (h:Hospital {id: $hid})
                    MATCH (d:Doctor {id: $did})
                    MATCH (diag:Diagnosis {code: $code})
                    MERGE (c)-[:SUBMITTED_AT]->(h)
                    MERGE (c)-[:SUBMITTED_BY]->(d)
                    MERGE (c)-[:CODED_AS]->(diag)
                """, cid=row['claim_id'], hid=row['hospital_id'], 
                   did=row['doctor_id'], code=row['diagnosis'])

                # 3. Parse Medical Resume JSON and Create Structured Data
                try:
                    resume_root = json.loads(row['medical_resume_json'])
                    
                    # Handle potential nesting: { "Medical_Resume": { ... } }
                    if "Medical_Resume" in resume_root:
                        data = resume_root["Medical_Resume"]
                    else:
                        data = resume_root

                    # Extract Fields
                    patient_name = data.get("Patient_Name", "Unknown")
                    primary_diag = data.get("Primary_Diagnosis", "")
                    secondary_diag = data.get("Secondary_Diagnosis", "")
                    primary_procedures = data.get("Primary_Procedure", [])
                    secondary_procedures = data.get("Secondary_Procedures", [])

                    # 4. Create Patient Node and Link to Claim
                    session.run("""
                        MATCH (c:Claim {id: $cid})
                        MERGE (p:Patient {name: $patient_name})
                        MERGE (c)-[:HAS_PATIENT]->(p)
                    """, cid=row['claim_id'], patient_name=patient_name)

                    # 5. Create Clinical Note with Diagnosis Information
                    session.run("""
                        MATCH (c:Claim {id: $cid})
                        CREATE (n:ClinicalNote)
                        SET n.primary_diagnosis_text = $primary_diag,
                            n.secondary_diagnosis_text = $secondary_diag
                        CREATE (c)-[:HAS_CLINICAL_NOTE]->(n)
                    """, cid=row['claim_id'], primary_diag=primary_diag, secondary_diag=secondary_diag)

                    # 6. Link PRIMARY Procedures to Claim
                    for proc_text in primary_procedures:
                        # First, try to find existing procedure by exact name match
                        result = session.run("""
                            MATCH (p:Procedure)
                            WHERE toLower(p.name) = toLower($proc_text)
                            RETURN p.name as name, p.code as code LIMIT 1
                        """, proc_text=proc_text)
                        
                        existing_proc = result.single()
                        if existing_proc:
                            # Link to existing procedure
                            session.run("""
                                MATCH (c:Claim {id: $cid})
                                MATCH (p:Procedure {name: $proc_name})
                                MERGE (c)-[:HAS_PRIMARY_PROCEDURE]->(p)
                            """, cid=row['claim_id'], proc_name=existing_proc['name'])
                        else:
                            # Create new procedure node with unique code based on name
                            unique_code = f"UNCODIFIED_{hash(proc_text) % 100000:05d}"
                            session.run("""
                                MATCH (c:Claim {id: $cid})
                                MERGE (p:Procedure {name: $proc_text})
                                ON CREATE SET p.code = $unique_code, p.avg_cost = 0.0
                                MERGE (c)-[:HAS_PRIMARY_PROCEDURE]->(p)
                            """, cid=row['claim_id'], proc_text=proc_text, unique_code=unique_code)

                    # 7. Link SECONDARY Procedures to Claim
                    for proc_text in secondary_procedures:
                        # First, try to find existing procedure by exact name match
                        result = session.run("""
                            MATCH (p:Procedure)
                            WHERE toLower(p.name) = toLower($proc_text)
                            RETURN p.name as name, p.code as code LIMIT 1
                        """, proc_text=proc_text)
                        
                        existing_proc = result.single()
                        if existing_proc:
                            # Link to existing procedure
                            session.run("""
                                MATCH (c:Claim {id: $cid})
                                MATCH (p:Procedure {name: $proc_name})
                                MERGE (c)-[:HAS_SECONDARY_PROCEDURE]->(p)
                            """, cid=row['claim_id'], proc_name=existing_proc['name'])
                        else:
                            # Create new procedure node with unique code based on name
                            unique_code = f"UNCODIFIED_{hash(proc_text) % 100000:05d}"
                            session.run("""
                                MATCH (c:Claim {id: $cid})
                                MERGE (p:Procedure {name: $proc_text})
                                ON CREATE SET p.code = $unique_code, p.avg_cost = 0.0
                                MERGE (c)-[:HAS_SECONDARY_PROCEDURE]->(p)
                            """, cid=row['claim_id'], proc_text=proc_text, unique_code=unique_code)

                except Exception as e:
                    print(f"⚠️ Error parsing JSON for Claim {row['claim_id']}: {e}")

        print(f"   - Loaded {len(df_claims)} Claims with Full Medical Resume Structure.")

# ---------------- EXECUTION ----------------
if __name__ == "__main__":
    try:
        loader = BPJSGraphLoader(URI, AUTH)
        
        loader.create_constraints()
        loader.clear_database() 
        
        loader.load_medical_ontology()
        loader.load_infrastructure()
        loader.load_claims_and_resume()
        
        print("\n✅ Graph Ingestion Complete!")
        loader.close()
    except Exception as e:
        print(f"\n❌ Error: {e}")
        print("Make sure Neo4j is running and your CSV file paths are correct.")
