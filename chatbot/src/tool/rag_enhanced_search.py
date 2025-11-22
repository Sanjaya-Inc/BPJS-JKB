import json
from typing import Type, Dict, List, Any, Optional
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool
from langchain_community.vectorstores import Neo4jVector
from langchain_openai import OpenAIEmbeddings
from neo4j import GraphDatabase
import numpy as np

import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..')))

from chatbot.src.config import NEO4J_URI, NEO4J_AUTH

class RagSearchInput(BaseModel):
    """Input schema for the RAG enhanced search tool."""
    extracted_entities: str = Field(
        description="JSON string containing extracted entities from the entity extraction tool"
    )

class SemanticRelationshipMapper:
    """Maps extracted relationships to graph database relationships using semantic similarity."""
    
    def __init__(self, embedding_model):
        self.embedding_model = embedding_model
        self._relationship_cache = {}
    
    def get_graph_relationships(self) -> List[str]:
        """Discover all relationship types from the graph database."""
        if self._relationship_cache:
            return self._relationship_cache.get("relationships", [])
            
        try:
            driver = GraphDatabase.driver(NEO4J_URI, auth=NEO4J_AUTH)
            with driver.session() as session:
                result = session.run("MATCH ()-[r]->() RETURN DISTINCT type(r) as rel_type")
                relationships = [record["rel_type"] for record in result]
                self._relationship_cache["relationships"] = relationships
                return relationships
        except Exception as e:
            print(f"Failed to discover graph relationships: {e}")
            return [":WORKS_AT", ":HAS_SPECIALTY", ":SUBMITTED_AT", ":CODED_AS", ":REQUIRES"]
    
    def map_relationship(self, extracted_relation: str, threshold: float = 0.6) -> List[str]:
        """Map extracted relationship to graph relationships using semantic similarity."""
        graph_relationships = self.get_graph_relationships()
        
        if not extracted_relation:
            return []
        
        try:
            # Get embedding for extracted relation
            extracted_embedding = self.embedding_model.embed_query(extracted_relation)
            
            similarities = []
            for graph_rel in graph_relationships:
                # Clean relationship name for embedding (remove : prefix)
                clean_rel = graph_rel.replace(":", "").replace("_", " ").lower()
                graph_embedding = self.embedding_model.embed_query(clean_rel)
                
                # Calculate cosine similarity
                similarity = np.dot(extracted_embedding, graph_embedding) / (
                    np.linalg.norm(extracted_embedding) * np.linalg.norm(graph_embedding)
                )
                similarities.append((graph_rel, similarity))
            
            # Return relationships above threshold, sorted by similarity
            matched_relations = [rel for rel, sim in similarities if sim >= threshold]
            return sorted(matched_relations, key=lambda x: next(sim for rel, sim in similarities if rel == x), reverse=True)
            
        except Exception as e:
            print(f"Semantic relationship mapping failed: {e}")
            return []


class RagEnhancedSearchTool(BaseTool):
    name: str = "rag_enhanced_search"
    description: str = """
    Performs intelligent RAG search based only on extracted entities.
    Only searches relevant entity types and uses semantic relationship mapping.
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
    
    def _get_relationship_mapper(self):
        """Get the relationship mapper instance"""
        if not hasattr(self, '_relationship_mapper'):
            self._relationship_mapper = SemanticRelationshipMapper(self._get_embedding_model())
        return self._relationship_mapper

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
                    search_results = vector_store.similarity_search_with_score(term, k=1)
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
                    search_results = vector_store.similarity_search_with_score(term, k=1)
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
                    search_results = vector_store.similarity_search_with_score(term, k=1)
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
                    search_results = vector_store.similarity_search_with_score(term, k=1)
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

    def _search_entity_with_filters(self, entity_type: str, search_terms: List[str], min_similarity: float = 0.6) -> List[Dict]:
        """Generic entity search with similarity filtering"""
        if entity_type == "diagnosis":
            results = self._search_diagnosis_entities(search_terms)
        elif entity_type == "procedure":
            results = self._search_procedure_entities(search_terms)
        elif entity_type == "hospital":
            results = self._search_hospital_entities(search_terms)
        elif entity_type == "doctor":
            results = self._search_doctor_entities(search_terms)
        else:
            return []
        
        # Filter by minimum similarity and remove errors
        filtered_results = []
        for result in results:
            if "error" not in result and result.get("similarity_score", 0) >= min_similarity:
                filtered_results.append({
                    "name": self._extract_name_from_content(result.get("content", "")),
                    "similarity_score": result.get("similarity_score"),
                    "metadata": result.get("metadata", {}),
                    "search_term": result.get("search_term")
                })
        
        return filtered_results
    
    def _extract_name_from_content(self, content: str) -> str:
        """Extract clean name from content"""
        lines = content.strip().split('\n')
        for line in lines:
            if line.startswith('name: '):
                return line.replace('name: ', '').strip()
        return content.strip()

    def _run(self, extracted_entities: str) -> str:
        try:
            # Parse the extracted entities JSON
            entities_data = json.loads(extracted_entities)
            entities = entities_data.get("entities", {})
            relationships = entities_data.get("relationships", [])
            keywords = entities_data.get("keywords", [])
            
            # Smart search: Only search entity types that have content
            search_results = {}
            
            # Search hospitals if specified
            if entities.get("hospitals"):
                hospital_terms = entities["hospitals"] + [kw for kw in keywords if "hospital" in kw.lower() or "rumah sakit" in kw.lower()]
                search_results["hospitals"] = self._search_entity_with_filters("hospital", hospital_terms)
            
            # Search diagnoses if specified
            if entities.get("diagnoses"):
                search_results["diagnoses"] = self._search_entity_with_filters("diagnosis", entities["diagnoses"])
            
            # Search procedures if specified
            if entities.get("procedures"):
                search_results["procedures"] = self._search_entity_with_filters("procedure", entities["procedures"])
            
            # Search doctors if specified
            if entities.get("doctors"):
                search_results["doctors"] = self._search_entity_with_filters("doctor", entities["doctors"])
            
            # Search specialties as doctors if specified
            if entities.get("specialties"):
                search_results["doctors"] = search_results.get("doctors", []) + self._search_entity_with_filters("doctor", entities["specialties"])
            
            # Handle relationships for secondary searches
            for relationship in relationships:
                subject = relationship.get("subject")
                relation = relationship.get("relation")
                obj = relationship.get("object")
                
                if relation and subject and obj:
                    # Use semantic relationship mapper
                    mapped_relations = self._get_relationship_mapper().map_relationship(relation)
                    
                    # If we found hospitals and relationship mentions doctors, search for related doctors
                    if "hospitals" in search_results and "doctor" in obj.lower() and mapped_relations:
                        # Add doctors related through the semantic relationship
                        doctor_keywords = [kw for kw in keywords if "dokter" in kw.lower() or "doctor" in kw.lower()]
                        if doctor_keywords:
                            related_doctors = self._search_entity_with_filters("doctor", doctor_keywords)
                            if related_doctors:
                                search_results.setdefault("related_doctors", []).extend(related_doctors)
            
            # Clean output structure - only return what was found
            final_results = {}
            for entity_type, results in search_results.items():
                if results:  # Only include non-empty results
                    # Sort by similarity score and take top 1 (most similar only)
                    sorted_results = sorted(results, key=lambda x: x.get("similarity_score", 0), reverse=True)[:1]
                    final_results[entity_type] = sorted_results
            
            print(f"[RAG Search] Simplified search result: {json.dumps(final_results, indent=2)}")
            
            return json.dumps(final_results, indent=2)
            
        except json.JSONDecodeError:
            return json.dumps({
                "error": "Invalid JSON in extracted_entities parameter"
            })
        except Exception as e:
            return json.dumps({
                "error": f"RAG enhanced search failed: {str(e)}"
            })
