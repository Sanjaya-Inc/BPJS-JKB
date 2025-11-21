import json
from typing import Type, Dict, List, Any
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool
from langchain_community.vectorstores import Neo4jVector
from langchain_openai import OpenAIEmbeddings
from src.config import NEO4J_URI, NEO4J_AUTH

class RagSearchInput(BaseModel):
    """Input schema for the RAG enhanced search tool."""
    extracted_entities: str = Field(
        description="JSON string containing extracted entities from the entity extraction tool"
    )

class RagEnhancedSearchTool(BaseTool):
    name: str = "rag_enhanced_search"
    description: str = """
    Performs enhanced RAG search across multiple entity types using extracted entities.
    Searches for similar diagnoses, procedures, and other medical entities in the vector database.
    """
    args_schema: Type[BaseModel] = RagSearchInput
    
    def _get_embedding_model(self):
        """Get the embedding model instance"""
        return OpenAIEmbeddings(
            model="text-embedding-qwen3-embedding-4b",
            openai_api_base="http://127.0.0.1:1234/v1",
            openai_api_key="",
            check_embedding_ctx_length=False
        )

    def _search_diagnosis_entities(self, query_terms: List[str]) -> List[Dict]:
        """Search for diagnosis entities using existing vector index"""
        try:
            vector_store = Neo4jVector.from_existing_graph(
                embedding=self._get_embedding_model(),
                url=NEO4J_URI,
                username=NEO4J_AUTH[0],
                password=NEO4J_AUTH[1],
                index_name="diagnosis_rules_index",
                node_label="Diagnosis",
                text_node_properties=["name", "code"],
                embedding_node_property="embedding",
            )
            
            results = []
            for term in query_terms:
                if term.strip():
                    search_results = vector_store.similarity_search_with_score(term, k=3)
                    for doc, score in search_results:
                        results.append({
                            "entity_type": "diagnosis",
                            "search_term": term,
                            "content": doc.page_content,
                            "metadata": doc.metadata,
                            "similarity_score": score,
                            "relevance": "high" if score > 0.8 else "medium" if score > 0.6 else "low"
                        })
            
            return results
            
        except Exception as e:
            return [{"error": f"Diagnosis search failed: {str(e)}"}]

    def _search_procedure_entities(self, query_terms: List[str]) -> List[Dict]:
        """Search for procedure entities using existing procedure_concept_index"""
        try:
            vector_store = Neo4jVector.from_existing_graph(
                embedding=self._get_embedding_model(),
                url=NEO4J_URI,
                username=NEO4J_AUTH[0],
                password=NEO4J_AUTH[1],
                index_name="procedure_concept_index",
                node_label="Procedure",
                text_node_properties=["name", "code"],
                embedding_node_property="embedding",
            )
            
            results = []
            for term in query_terms:
                if term.strip():
                    search_results = vector_store.similarity_search_with_score(term, k=3)
                    for doc, score in search_results:
                        results.append({
                            "entity_type": "procedure",
                            "search_term": term,
                            "content": doc.page_content,
                            "metadata": doc.metadata,
                            "similarity_score": score,
                            "relevance": "high" if score > 0.8 else "medium" if score > 0.6 else "low"
                        })
            
            return results
            
        except Exception as e:
            return [{"error": f"Procedure search failed: {str(e)}"}]

    def _search_hospital_entities(self, query_terms: List[str]) -> List[Dict]:
        """Search for hospital entities using existing hospital_entity_index"""
        try:
            vector_store = Neo4jVector.from_existing_graph(
                embedding=self._get_embedding_model(),
                url=NEO4J_URI,
                username=NEO4J_AUTH[0],
                password=NEO4J_AUTH[1],
                index_name="hospital_entity_index",
                node_label="Hospital",
                text_node_properties=["name", "id"],
                embedding_node_property="embedding",
            )
            
            results = []
            for term in query_terms:
                if term.strip():
                    search_results = vector_store.similarity_search_with_score(term, k=3)
                    for doc, score in search_results:
                        results.append({
                            "entity_type": "hospital",
                            "search_term": term,
                            "content": doc.page_content,
                            "metadata": doc.metadata,
                            "similarity_score": score,
                            "relevance": "high" if score > 0.8 else "medium" if score > 0.6 else "low"
                        })
            
            return results
            
        except Exception as e:
            return [{"error": f"Hospital search failed: {str(e)}"}]

    def _search_doctor_entities(self, query_terms: List[str]) -> List[Dict]:
        """Search for doctor entities using existing doctor_entity_index"""
        try:
            vector_store = Neo4jVector.from_existing_graph(
                embedding=self._get_embedding_model(),
                url=NEO4J_URI,
                username=NEO4J_AUTH[0],
                password=NEO4J_AUTH[1],
                index_name="doctor_entity_index",
                node_label="Doctor",
                text_node_properties=["name", "id"],
                embedding_node_property="embedding",
            )
            
            results = []
            for term in query_terms:
                if term.strip():
                    search_results = vector_store.similarity_search_with_score(term, k=3)
                    for doc, score in search_results:
                        results.append({
                            "entity_type": "doctor",
                            "search_term": term,
                            "content": doc.page_content,
                            "metadata": doc.metadata,
                            "similarity_score": score,
                            "relevance": "high" if score > 0.8 else "medium" if score > 0.6 else "low"
                        })
            
            return results
            
        except Exception as e:
            return [{"error": f"Doctor search failed: {str(e)}"}]

    def _run(self, extracted_entities: str) -> str:
        try:
            # Parse the extracted entities JSON
            entities_data = json.loads(extracted_entities)
            entities = entities_data.get("entities", {})
            
            # Collect all search terms
            all_search_terms = []
            diagnosis_terms = entities.get("diagnoses", [])
            procedure_terms = entities.get("procedures", [])
            hospital_terms = entities.get("hospitals", [])
            doctor_terms = entities.get("doctors", [])
            specialty_terms = entities.get("specialties", [])
            keywords = entities_data.get("keywords", [])
            
            # Combine all terms for comprehensive search
            all_search_terms.extend(diagnosis_terms)
            all_search_terms.extend(procedure_terms) 
            all_search_terms.extend(hospital_terms)
            all_search_terms.extend(doctor_terms)
            all_search_terms.extend(specialty_terms)
            all_search_terms.extend(keywords)
            
            # Remove duplicates and empty strings
            all_search_terms = list(set([term.strip() for term in all_search_terms if term.strip()]))
            
            # Perform searches across different entity types
            search_results = {
                "query_metadata": {
                    "original_entities": entities,
                    "query_intent": entities_data.get("query_intent", "unknown"),
                    "language": entities_data.get("language", "auto-detect"),
                    "total_search_terms": len(all_search_terms)
                },
                "search_results": {
                    "diagnoses": self._search_diagnosis_entities(diagnosis_terms + keywords),
                    "procedures": self._search_procedure_entities(procedure_terms),
                    "hospitals": self._search_hospital_entities(hospital_terms),
                    "doctors": self._search_doctor_entities(doctor_terms + specialty_terms),
                },
                "summary": {
                    "total_results": 0,
                    "high_relevance_results": 0,
                    "top_matches": []
                }
            }
            
            # Calculate summary statistics
            all_results = []
            for entity_type, results in search_results["search_results"].items():
                all_results.extend(results)
            
            search_results["summary"]["total_results"] = len(all_results)
            search_results["summary"]["high_relevance_results"] = len([r for r in all_results if r.get("relevance") == "high"])
            
            # Get top 3 matches across all entity types
            valid_results = [r for r in all_results if "similarity_score" in r and r["similarity_score"] > 0]
            top_matches = sorted(valid_results, key=lambda x: x["similarity_score"], reverse=True)[:3]
            search_results["summary"]["top_matches"] = top_matches
            
            return json.dumps(search_results, indent=2)
            
        except json.JSONDecodeError:
            return json.dumps({
                "error": "Invalid JSON in extracted_entities parameter",
                "fallback_search": "Unable to perform enhanced search"
            })
        except Exception as e:
            return json.dumps({
                "error": f"RAG enhanced search failed: {str(e)}",
                "fallback_search": "Consider using basic search"
            })
