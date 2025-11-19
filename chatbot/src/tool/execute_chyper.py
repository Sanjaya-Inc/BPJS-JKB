import json
from neo4j import GraphDatabase
from langchain.tools import tool
from pydantic import BaseModel, Field
from typing import Type, List, Any
from langchain_core.tools import BaseTool

# 1. Neo4j Connection Details
URI = "neo4j://localhost:7687"
AUTH = ("neo4j", "12345678")

# 2. Define the Input Schema
class CypherInput(BaseModel):
    """Input schema for the execute_cypher tool."""
    
    cypher_query: str = Field(
        description="The exact Cypher query to execute. Ensure it is a READ-ONLY query (MATCH, RETURN, etc)."
    )

# 3. Helper: Custom JSON Serializer for Neo4j Types
# Standard json.dumps fails on Neo4j's Date, DateTime, and Point objects.
def neo4j_json_serializer(o: Any) -> Any:
    # Handle Neo4j Time/Date objects
    if hasattr(o, 'iso_format'):
        return o.iso_format()
    # Handle Neo4j Spatial Point objects (Latitude/Longitude)
    if hasattr(o, 'latitude') and hasattr(o, 'longitude'):
        return {"latitude": o.latitude, "longitude": o.longitude}
    # Handle Sets (often returned by collect())
    if isinstance(o, set):
        return list(o)
    return str(o)

# 4. Define the Tool
class ExecuteCypherTool(BaseTool):
    name: str = "execute_cypher"
    description: str = """
    Use this tool to execute a Cypher query against the graph database and retrieve the results.
    Input should be a valid Cypher string.
    Useful for answering questions like "How many...", "Find the path...", or "List all...".
    """
    args_schema: Type[BaseModel] = CypherInput

    def _run(self, cypher_query: str) -> str:
        driver = None
        try:
            # 1. Connect to the Database
            driver = GraphDatabase.driver(URI, auth=AUTH)
            
            with driver.session() as session:
                # 2. Execute the query using a read transaction
                # We wrap it in a lambda to use the retry logic of execute_read
                result = session.execute_read(
                    lambda tx: list(tx.run(cypher_query))
                )
                
                # 3. Process Results
                if not result:
                    return "Query executed successfully but returned no results."

                # Convert Neo4j records to native Python dictionaries
                # record.data() automatically converts Nodes and Relationships to dicts
                data = [record.data() for record in result]
                
                # 4. Return formatted JSON
                # We use the custom serializer to handle Dates and Points safely
                return json.dumps(data, default=neo4j_json_serializer, indent=2)

        except Exception as e:
            # Return the error message so the Agent knows the query failed and can retry
            return f"Cypher Execution Error: {str(e)}"
            
        finally:
            if driver:
                driver.close()