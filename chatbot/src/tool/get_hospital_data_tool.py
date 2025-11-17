import json
from neo4j import GraphDatabase
from langchain.tools import tool
from pydantic import BaseModel, Field
from typing import Type
from langchain_core.tools import BaseTool

# 1. Neo4j Connection Details
URI = "neo4j://localhost:7687"
AUTH = ("neo4j", "12345678")

# 2. Define the Pydantic Input Schema
# This class defines the *exact* arguments the agent must provide to the tool.
class HospitalInfoInput(BaseModel):
    """Input schema for the get_hospital_info tool."""
    
    hospital_name: str = Field(
        description="The exact, full name of the hospital to get information for."
    )

class GetHospitalDataTool(BaseTool):
    name: str = "get_hospital_data"
    description: str = """
            Use this tool to get detailed information about a specific hospital,
            including its class, location, specialties, and facilities.
            """
    args_schema: type = HospitalInfoInput

    def _run(self, hospital_name: str) -> str:
        query = (
            """
            MATCH (h:Hospital {name: $hospital_name})
            // Collect specialties
            OPTIONAL MATCH (h)-[:OFFERS_SPECIALTY]->(s:Specialty)
            WITH h, collect(DISTINCT s.name) AS specialties
            // Collect facilities
            OPTIONAL MATCH (h)-[:HAS_FACILITY]->(f:Facility)
            WITH h, specialties, collect(DISTINCT f.name) AS facilities
            // Return all data in one row
            RETURN
                h.name AS name,
                h.class AS class,
                h.location AS location,
                specialties,
                facilities
            """
        )

        driver = None
        try:
            driver = GraphDatabase.driver(URI, auth=AUTH)
            with driver.session(database="neo4j") as session:
                # Use execute_read for a read-only transaction
                result = session.execute_read(
                    lambda tx: tx.run(query, hospital_name=hospital_name).single()
                )
                if not result:
                    return f"No hospital found with the name: {hospital_name}"
                # The result from Neo4j is a 'Record' object (like a dict)
                record_data = result.data()
                # The 'location' is a special Neo4j 'Point' object.
                # Let's make it a clean string for the agent.
                if record_data.get("location"):
                    loc = record_data["location"]
                    record_data["location"] = f"lat: {loc.latitude}, long: {loc.longitude}"
                # Format the dictionary as a clean JSON string for the agent
                # This is easier for an LLM to parse and understand.
                return json.dumps(record_data, indent=2, ensure_ascii=False)
        except Exception as e:
            return f"An error occurred while querying the database: {e}"
        finally:
            if driver:
                driver.close()