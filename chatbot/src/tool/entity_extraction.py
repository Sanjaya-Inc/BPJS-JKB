import json
import re
from typing import Type
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool
from langchain_core.messages import SystemMessage, HumanMessage
from langchain_openai import ChatOpenAI

class EntityExtractionInput(BaseModel):
    """Input schema for the entity extraction tool."""
    user_query: str = Field(
        description="The user's natural language question to extract medical entities from."
    )

class EntityExtractionTool(BaseTool):
    name: str = "entity_extraction"
    description: str = """
    Extracts medical entities (diagnoses, procedures, hospitals, doctors) and their relationships 
    from user input using LLM analysis. Returns structured data for enhanced query processing.
    """
    args_schema: Type[BaseModel] = EntityExtractionInput

    def _clean_llm_response(self, content: str) -> str:
        """Clean LLM response by removing thinking tags and unwanted content."""
        # Remove <think>...</think> tags and their content
        content = re.sub(r'<think>.*?</think>', '', content, flags=re.DOTALL)
        
        # Remove markdown code blocks
        if content.startswith("```json"):
            content = content[7:]
        if content.startswith("```"):
            content = content[3:]
        if content.endswith("```"):
            content = content[:-3]
            
        # Remove any other XML-like tags that might be present
        content = re.sub(r'<[^>]+>', '', content)
        
        return content.strip()
    
    def _extract_json(self, content: str) -> str:
        """Extract JSON object from cleaned content."""
        # Try to find JSON object pattern
        json_pattern = r'\{.*\}'
        matches = re.findall(json_pattern, content, flags=re.DOTALL)
        
        if matches:
            # Return the largest match (most complete JSON)
            return max(matches, key=len)
        
        # If no JSON pattern found, return the content as-is
        return content

    def _run(self, user_query: str) -> str:
        llm = ChatOpenAI(
            model="qwen3-8B", 
            base_url="http://127.0.0.1:1234/v1", 
            api_key=""
        )
        
        system_instruction = """
        You are an expert medical entity extractor. Your goal is to analyze medical and insurance queries and extract structured information.
        
        Extract the following entities and return them strictly as a valid JSON object:
        {
            "entities": {
                "diagnoses": ["list of medical conditions/diseases mentioned"],
                "procedures": ["list of medical procedures mentioned"],
                "hospitals": ["list of hospitals/healthcare facilities mentioned"],
                "doctors": ["list of doctors/specialists mentioned"],
                "specialties": ["list of medical specialties mentioned"]
            },
            "relationships": [
                {
                    "subject": "entity1",
                    "relation": "relationship_type",
                    "object": "entity2"
                }
            ],
            "query_intent": "what is the user trying to find out (e.g., count, details, cost)",
            "language": "detected language code (e.g., en, id)",
            "keywords": ["important keywords for search (include synonyms)"]
        }

        Instructions:
        1. Include synonyms and related terms (e.g., if user says "heart attack", include "myocardial infarction").
        2. Detect both English and Indonesian medical terms.
        3. Do not include markdown formatting (like ```json). Just return the raw JSON string.
        4. CRITICAL: Do not include any thinking tags, reasoning, or explanations. Return ONLY the JSON object.
        5. Do not use <think> tags or any other XML-style tags in your response.
        """

        messages = [
            SystemMessage(content=system_instruction),
            HumanMessage(content=f"Analyze this query: {user_query}")
        ]

        try:
            response = llm.invoke(messages)
            content = response.content.strip()
            content = self._clean_llm_response(content)
            json_content = self._extract_json(content)
            
            result = json.loads(json_content)
            return json.dumps(result, indent=2)

        except json.JSONDecodeError as e:
            print(f"Warning: LLM response is not valid JSON. JSON Error: {str(e)}")
            print(f"Cleaned content that failed to parse: {content}")
            fallback = {
                "entities": {
                    "diagnoses": [],
                    "procedures": [],
                    "hospitals": [],
                    "doctors": [],
                    "specialties": []
                },
                "relationships": [],
                "query_intent": "general query",
                "language": "auto-detect",
                "keywords": [user_query],
                "raw_llm_response": response.content if 'response' in locals() else "Error",
                "parsing_error": str(e)
            }
            return json.dumps(fallback, indent=2)
                
        except Exception as e:
            error_response = {
                "error": f"Entity extraction failed: {str(e)}",
                "fallback_keywords": [user_query],
                "language": "auto-detect"
            }
            return json.dumps(error_response, indent=2)
