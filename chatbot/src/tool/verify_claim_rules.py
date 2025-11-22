from typing import Type, Optional
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool
from langchain_community.vectorstores import Neo4jVector
from langchain_openai import OpenAIEmbeddings
import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..')))

from neo4j import GraphDatabase
from chatbot.src.config import NEO4J_URI, NEO4J_AUTH

# --- CHANGE 1: Update Input to accept Cost ---
class VerifyClaimInput(BaseModel):
    diagnosis_query: str = Field(
        description="The diagnosis name or code (e.g., 'Heart Attack', 'I21.9')."
    )
    claimed_cost: Optional[float] = Field(
        default=None,
        description="The total cost submitted in the claim. Essential for UPCODING checks."
    )

class VerifyClaimRulesTool(BaseTool):
    name: str = "verify_claim_rules"
    description: str = """
    Validates a claim against medical rules.
    Checks for:
    1. Required Procedures (Medical Necessity)
    2. Cost Variance (Upcoding/Fraud Detection)
    """
    args_schema: Type[BaseModel] = VerifyClaimInput
    
    URI: str = NEO4J_URI
    AUTH: tuple = NEO4J_AUTH

    def _run(self, diagnosis_query: str, claimed_cost: Optional[float] = None) -> str:
        driver = None
        try:
            # 1. Vector Search (Unchanged)
            vector_store = Neo4jVector.from_existing_index(
                OpenAIEmbeddings(),
                url=self.URI,
                username=self.AUTH[0],
                password=self.AUTH[1],
                index_name="diagnosis_rules_index"
            )
            results = vector_store.similarity_search(diagnosis_query, k=1)
            if not results:
                return f"Diagnosis not found for '{diagnosis_query}'."
            
            matched_text = results[0].page_content
            clean_text = matched_text.split("\n")[0].replace("name: ", "").strip()

            # --- CHANGE 2: Golden Cypher for Upcoding ---
            # We pass the claimed_cost into the query to let Neo4j do the math.
            cypher_query = """
            MATCH (d:Diagnosis)
            WHERE d.name CONTAINS $text OR d.code CONTAINS $text
            
            OPTIONAL MATCH (d)-[:REQUIRES]->(req:Procedure)
            
            // Logic: Handling Cost Comparison
            WITH d, collect(req.name) as required_procs, $input_cost as input_cost
            
            // Calculate Variance only if input_cost is provided
            WITH d, required_procs, input_cost,
                 CASE 
                    WHEN input_cost IS NOT NULL 
                    THEN ((input_cost - d.avg_cost) / d.avg_cost) * 100 
                    ELSE 0 
                 END as variance_pct
            
            RETURN 
                d.name as Name,
                d.code as Code,
                d.avg_cost as Ref_Cost,
                required_procs as Required,
                variance_pct as Variance,
                CASE 
                    WHEN input_cost IS NULL THEN "NO_COST_PROVIDED"
                    WHEN variance_pct > 50 THEN "HIGH_RISK_UPCODING"
                    WHEN variance_pct > 20 THEN "MODERATE_RISK"
                    ELSE "NORMAL"
                END as Status
            """
            
            driver = GraphDatabase.driver(self.URI, auth=self.AUTH)
            with driver.session() as session:
                # Pass the cost to the query
                result = session.run(cypher_query, text=clean_text, input_cost=claimed_cost).single()
                
                if not result:
                    return "Error retrieving rules."
                
                data = result.data()
                
                # --- CHANGE 3: Format the Output ---
                cost_analysis = ""
                if claimed_cost:
                    cost_analysis = f"""
                💰 COST ANALYSIS:
                   - Claimed: {claimed_cost:,.0f}
                   - Reference: {data['Ref_Cost']:,.0f}
                   - Variance: +{data['Variance']:.1f}%
                   - 🚨 VERDICT: {data['Status']} (Threshold: >20% is Risk)
                    """

                return f"""
                🏥 MEDICAL RULES:
                - Diagnosis: {data['Name']} ({data['Code']})
                - Required Procedures: {", ".join(data['Required'])}
                {cost_analysis}
                """

        except Exception as e:
            return f"Error: {str(e)}"
        finally:
            if driver: driver.close()
