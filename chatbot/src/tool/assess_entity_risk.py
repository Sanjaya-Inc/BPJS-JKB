from typing import Type, Literal, Optional, Dict, Any
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool
from langchain_community.vectorstores import Neo4jVector
from langchain_openai import OpenAIEmbeddings
from neo4j import GraphDatabase
from src.config import NEO4J_URI, NEO4J_AUTH

# --- 1. Input Schema ---
class EntityRiskInput(BaseModel):
    entity_type: Literal["Doctor", "Hospital", "Claim"] = Field(
        description="The type of entity to analyze."
    )
    query: str = Field(
        description="The name or ID to look up (e.g., 'Dr. Budi', 'Hasan Sadikin', 'C100')."
    )

# --- 2. The Tool Class ---
class AssessEntityRiskTool(BaseTool):
    name: str = "assess_entity_risk"
    description: str = """
    ADVANCED RISK PROFILER. Use this to check Doctors, Hospitals, or Claims.
    Performs two types of analysis:
    1. FINANCIAL: Checks for Price Gouging (Z-Score/Normal Distribution).
    2. CLINICAL: Checks for Specialty Mismatches & Phantom Billing (Case Mix).
    """
    args_schema: Type[BaseModel] = EntityRiskInput
    
    # Database Config (imported from config)
    URI: str = NEO4J_URI
    AUTH: tuple = NEO4J_AUTH

    def _resolve_name_via_vector(self, entity_type: str, query: str) -> str:
        """Uses Vector Search to find the exact node name (e.g., 'Budi' -> 'Dr. Budi Hartono')."""
        try:
            index_map = {"Doctor": "doctor_entity_index", "Hospital": "hospital_entity_index"}
            if entity_type not in index_map:
                return query # Claims don't need vector resolution usually

            vector_store = Neo4jVector.from_existing_index(
                OpenAIEmbeddings(),
                url=self.URI,
                username=self.AUTH[0],
                password=self.AUTH[1],
                index_name=index_map[entity_type]
            )
            
            results = vector_store.similarity_search(query, k=1)
            if not results:
                return query # Fallback to original string
            
            # Extract exact name (assuming format "name: X\n...")
            return results[0].page_content.split("\n")[0].replace("name: ", "").strip()
        except:
            return query

    def _run(self, entity_type: str, query: str) -> str:
        driver = None
        try:
            # 1. Resolve Ambiguous Names
            search_term = self._resolve_name_via_vector(entity_type, query)
            
            driver = GraphDatabase.driver(self.URI, auth=self.AUTH)
            with driver.session() as session:
                
                # ==========================================
                # SCENARIO A: DOCTOR RISK PROFILE
                # ==========================================
                if entity_type == "Doctor":
                    cypher = """
                    MATCH (d:Doctor) WHERE d.name = $name OR d.id = $name
                    
                    // --- ENGINE 1: FINANCIAL (Z-Score) ---
                    MATCH (d)-[:SUBMITTED_BY]-(c:Claim)
                    WITH d, count(c) as total_vol, avg(c.total_cost) as doc_avg
                    
                    // Benchmark against ALL claims (or filter by specialty if data allows)
                    MATCH (all:Claim)
                    WITH d, total_vol, doc_avg, avg(all.total_cost) as global_avg, stDev(all.total_cost) as global_std
                    
                    WITH d, total_vol, doc_avg, global_avg, 
                         (doc_avg - global_avg) / global_std as z_score

                    // --- ENGINE 2: CLINICAL (Case Mix) ---
                    MATCH (d)-[:SUBMITTED_BY]-(c)-[:CODED_AS]->(diag:Diagnosis)
                    WITH d, total_vol, doc_avg, z_score, diag.name as disease, count(c) as disease_count
                    ORDER BY disease_count DESC LIMIT 3
                    
                    WITH d, total_vol, doc_avg, z_score, 
                         collect({name: disease, pct: (toFloat(disease_count)/total_vol)*100}) as top_diseases

                    RETURN 
                        d.name as Name,
                        d.specialization as Specialty,
                        total_vol as Volume,
                        doc_avg as Avg_Cost,
                        z_score as Z_Score,
                        top_diseases as Case_Mix
                    """
                    result = session.run(cypher, name=search_term).single()
                    if not result: return f"No data found for Doctor: {search_term}"
                    return self._format_doctor_report(result.data())

                # ==========================================
                # SCENARIO B: HOSPITAL RISK PROFILE
                # ==========================================
                elif entity_type == "Hospital":
                    cypher = """
                    MATCH (h:Hospital) WHERE h.name = $name OR h.id = $name
                    
                    // --- ENGINE 1: FINANCIAL ---
                    MATCH (h)-[:SUBMITTED_AT]-(c:Claim)
                    WITH h, count(c) as total_vol, avg(c.total_cost) as hosp_avg
                    
                    MATCH (all:Claim)
                    WITH h, total_vol, hosp_avg, avg(all.total_cost) as global_avg, stDev(all.total_cost) as global_std
                    WITH h, total_vol, hosp_avg, (hosp_avg - global_avg)/global_std as z_score
                    
                    // --- ENGINE 2: CLINICAL ---
                    MATCH (h)-[:SUBMITTED_AT]-(c)-[:CODED_AS]->(d:Diagnosis)
                    WITH h, total_vol, hosp_avg, z_score, d.name as disease, count(c) as d_count
                    ORDER BY d_count DESC LIMIT 3
                    
                    WITH h, total_vol, hosp_avg, z_score, 
                         collect({name: disease, pct: (toFloat(d_count)/total_vol)*100}) as top_diseases
                    
                    // Get Specialties
                    OPTIONAL MATCH (h)-[:HAS_SPECIALTY]->(s:Specialty)
                    
                    RETURN 
                        h.name as Name,
                        collect(DISTINCT s.name) as Specialties,
                        total_vol as Volume,
                        hosp_avg as Avg_Cost,
                        z_score as Z_Score,
                        top_diseases as Case_Mix
                    """
                    result = session.run(cypher, name=search_term).single()
                    if not result: return f"No data found for Hospital: {search_term}"
                    return self._format_hospital_report(result.data())

                # ==========================================
                # SCENARIO C: SPECIFIC CLAIM CHECK
                # ==========================================
                elif entity_type == "Claim":
                    # Simple lookup + Reference Price comparison
                    cypher = """
                    MATCH (c:Claim {id: $id})
                    MATCH (c)-[:CODED_AS]->(d:Diagnosis)
                    RETURN 
                        c.id as ID, c.total_cost as Cost, c.status as Status,
                        d.name as Diagnosis, d.avg_cost as Ref_Cost
                    """
                    result = session.run(cypher, id=query).single()
                    if not result: return f"Claim {query} not found."
                    data = result.data()
                    variance = ((data['Cost'] - data['Ref_Cost']) / data['Ref_Cost']) * 100
                    return f"CLAIM {data['ID']}: {data['Diagnosis']}. Cost: {data['Cost']:,} (Ref: {data['Ref_Cost']:,}). Variance: {variance:.1f}%."

        except Exception as e:
            return f"Analysis Error: {str(e)}"
        finally:
            if driver: driver.close()

    # --- 3. Helper Methods for Formatting (The "Analyst" Logic) ---
    
    def _format_doctor_report(self, data: Dict) -> str:
        # Financial Logic
        z_score = data['Z_Score']
        fin_flag = "✅ Normal"
        if z_score > 3: fin_flag = "🚨 EXTREME OUTLIER (Poss. Fraud Ring)"
        elif z_score > 1.5: fin_flag = "⚠️ High Cost"

        # Clinical Logic
        clinical_notes = ""
        for item in data['Case_Mix']:
            # Alert if Case Mix doesn't match Specialty (Simplified Logic)
            mismatch_alert = ""
            if data['Specialty'] == "GP" and "Surgery" in item['name']:
                mismatch_alert = "🚨 (SCOPE MISMATCH)"
            if item['pct'] > 60:
                mismatch_alert += " ⚠️ (Concentration Risk)"
            
            clinical_notes += f"- {item['name']}: {item['pct']:.1f}% {mismatch_alert}\n"

        return f"""
        👨‍⚕️ DOCTOR RISK REPORT: {data['Name']}
        -----------------------------------------
        • Specialty: {data['Specialty']}
        • Volume: {data['Volume']} claims
        
        💰 FINANCIAL PROFILE:
        • Avg Claim: {data['Avg_Cost']:,.0f} IDR
        • Z-Score: {z_score:.2f}
        • Status: {fin_flag}
        
        📊 CLINICAL CASE MIX (Top 3):
        {clinical_notes}
        """

    def _format_hospital_report(self, data: Dict) -> str:
        z_score = data['Z_Score']
        fin_flag = "✅ Normal"
        if z_score > 2.5: fin_flag = "🚨 STATISTICAL ANOMALY"
        
        clinical_notes = ""
        specialties = data['Specialties'] if data['Specialties'] else ["General"]
        
        for item in data['Case_Mix']:
            alert = ""
            # Alert if Single Disease Dominates
            if item['pct'] > 50: alert = "🚨 (SINGLE DISEASE FACTORY?)"
            clinical_notes += f"- {item['name']}: {item['pct']:.1f}% {alert}\n"

        return f"""
        🏥 HOSPITAL RISK REPORT: {data['Name']}
        -----------------------------------------
        • Specialties: {", ".join(specialties)}
        • Volume: {data['Volume']} claims
        
        💰 FINANCIAL PROFILE:
        • Avg Claim: {data['Avg_Cost']:,.0f} IDR
        • Z-Score: {z_score:.2f}
        • Status: {fin_flag}
        
        📊 DISEASE DISTRIBUTION:
        {clinical_notes}
        """
