import pandas as pd
import json
from neo4j import GraphDatabase

# ---------------- CONFIGURATION ----------------
# Update these with your Neo4j credentials
URI = "neo4j://localhost:7687"  # Default Neo4j Bolt port
AUTH = ("neo4j", "12345678")

# File Paths (Ensure these match your saved CSV filenames)
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
        """Wipes the database clean before loading (Optional)"""
        with self.driver.session() as session:
            session.run("MATCH (n) DETACH DELETE n")
            print("🧹 Database cleared.")

    def create_constraints(self):
        """Creates unique constraints to prevent duplicates and speed up queries"""
        queries = [
            "CREATE CONSTRAINT IF NOT EXISTS FOR (h:Hospital) REQUIRE h.id IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (d:Doctor) REQUIRE d.id IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (diag:Diagnosis) REQUIRE diag.code IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (p:Procedure) REQUIRE p.code IS UNIQUE",
            "CREATE CONSTRAINT IF NOT EXISTS FOR (c:Claim) REQUIRE c.id IS UNIQUE"
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
                # Dynamic relationship creation using apoc or simple string formatting (safe here as inputs are controlled)
                rel_type = row['relationship']
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
                specialties = json.loads(row['specialties_json'])
                for spec in specialties:
                    session.run("""
                        MATCH (h:Hospital {id: $id})
                        MERGE (s:Specialty {name: $spec})
                        MERGE (h)-[:HAS_SPECIALTY]->(s)
                    """, id=row['hospital_id'], spec=spec)

                # Parse Facilities JSON -> Create Edges
                facilities = json.loads(row['facilities_json'])
                for fac in facilities:
                    session.run("""
                        MATCH (h:Hospital {id: $id})
                        MERGE (f:Facility {name: $fac})
                        MERGE (h)-[:HAS_FACILITY]->(f)
                    """, id=row['hospital_id'], fac=fac)

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
        """Loads Claims and parses the complex Clinical Resume JSON"""
        print("📄 Loading Claims and Clinical Evidence...")
        
        df_claims = pd.read_csv(FILES["claims"])
        
        with self.driver.session() as session:
            for _, row in df_claims.iterrows():
                # 1. Create Basic Claim Node
                session.run("""
                    CREATE (c:Claim {id: $cid})
                    SET c.sep_no = $sep,
                        c.total_cost = toFloat($cost),
                        c.date = date()  // Assuming current date for demo
                """, cid=row['claim_id'], sep=row['sep_no'], cost=row['total_cost'])

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
                   did=row['doctor_id'], code=row['icd10_primary'])

                # 3. Parse Clinical Resume JSON (The "Evidence")
                resume_json = json.loads(row['medical_resume_json'])
                
                # Create Clinical Note Node
                session.run("""
                    MATCH (c:Claim {id: $cid})
                    CREATE (n:ClinicalNote)
                    SET n.text_raw = $notes,
                        n.primary_diagnosis_text = $d_prim
                    CREATE (c)-[:HAS_CLINICAL_NOTE]->(n)
                """, cid=row['claim_id'], notes=resume_json.get('notes', ''), 
                   d_prim=resume_json.get('d_prim', ''))

                # Extract Procedures from JSON list and link to Ontology
                # NOTE: In a real app, this would use Vector Search/LLM. 
                # Here we do simple exact/partial matching for the demo.
                procedures_text = resume_json.get('proc', [])
                for proc_txt in procedures_text:
                    # Try to find a matching Procedure in our Ontology
                    session.run("""
                        MATCH (c:Claim {id: $cid})-[:HAS_CLINICAL_NOTE]->(n:ClinicalNote)
                        
                        // 1. Create an 'MentionedEntity' for the raw text
                        CREATE (e:MentionedEntity {text: $p_text, type: 'Procedure'})
                        CREATE (n)-[:MENTIONS]->(e)
                        
                        // 2. Attempt to link to standardized Procedure (Simple fuzzy match logic)
                        WITH e
                        MATCH (p:Procedure)
                        WHERE toLower(e.text) CONTAINS toLower(p.name) OR toLower(p.name) CONTAINS toLower(e.text)
                        MERGE (e)-[:MAPS_TO]->(p)
                    """, cid=row['claim_id'], p_text=proc_txt)

        print(f"   - Loaded {len(df_claims)} Claims with Evidence.")

# ---------------- EXECUTION ----------------
if __name__ == "__main__":
    try:
        loader = BPJSGraphLoader(URI, AUTH)
        
        loader.create_constraints()
        loader.clear_database() # Uncomment to wipe DB before starting
        
        loader.load_medical_ontology()
        loader.load_infrastructure()
        loader.load_claims_and_resume()
        
        print("\n✅ Graph Ingestion Complete!")
        loader.close()
    except Exception as e:
        print(f"\n❌ Error: {e}")
        print("Make sure Neo4j is running and your CSV file paths are correct.")