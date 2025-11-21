import json
from typing import Type, Dict, List
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool

class ContextBuilderInput(BaseModel):
    """Input schema for the context builder tool."""
    original_query: str = Field(
        description="The original user query"
    )
    extracted_entities: str = Field(
        description="JSON string containing extracted entities"
    )
    rag_results: str = Field(
        description="JSON string containing RAG search results"
    )

class ContextBuilderTool(BaseTool):
    name: str = "context_builder"
    description: str = """
    Builds enriched context by combining user query, extracted entities, and RAG search results.
    Creates structured context that helps generate more precise Cypher queries.
    """
    args_schema: Type[BaseModel] = ContextBuilderInput

    def _build_entity_context(self, entities: Dict) -> str:
        """Build context string from extracted entities"""
        context_parts = []
        
        for entity_type, entity_list in entities.items():
            if entity_list:
                context_parts.append(f"- {entity_type.title()}: {', '.join(entity_list)}")
        
        return "\n".join(context_parts) if context_parts else "No specific entities detected"

    def _build_rag_context(self, rag_results: Dict) -> str:
        """Build context string from RAG search results"""
        context_parts = []
        
        # Get summary info
        summary = rag_results.get("summary", {})
        total_results = summary.get("total_results", 0)
        high_relevance = summary.get("high_relevance_results", 0)
        
        context_parts.append(f"RAG Search found {total_results} results ({high_relevance} high relevance)")
        
        # Add top matches
        top_matches = summary.get("top_matches", [])
        if top_matches:
            context_parts.append("\nTop Related Entities:")
            for i, match in enumerate(top_matches[:3], 1):
                entity_type = match.get("entity_type", "unknown")
                content = match.get("content", "")
                score = match.get("similarity_score", 0)
                context_parts.append(f"{i}. {entity_type}: {content} (similarity: {score:.3f})")
        
        # Add specific diagnosis matches if available
        diagnosis_results = rag_results.get("search_results", {}).get("diagnoses", [])
        high_relevance_diagnoses = [d for d in diagnosis_results if d.get("relevance") == "high"]
        
        if high_relevance_diagnoses:
            context_parts.append(f"\nHigh Relevance Diagnoses Found:")
            for diag in high_relevance_diagnoses[:3]:
                content = diag.get("content", "")
                metadata = diag.get("metadata", {})
                context_parts.append(f"- {content}")
                if metadata:
                    context_parts.append(f"  Metadata: {metadata}")
        
        return "\n".join(context_parts) if context_parts else "No relevant RAG results found"

    def _generate_query_hints(self, entities: Dict, rag_results: Dict, original_query: str) -> List[str]:
        """Generate hints for more precise Cypher query generation"""
        hints = []
        
        # Intent-based hints
        query_intent = rag_results.get("query_metadata", {}).get("query_intent", "")
        if "count" in query_intent.lower() or "berapa" in original_query.lower():
            hints.append("This appears to be a counting/statistics query - use COUNT() aggregation")
        
        # Entity-specific hints
        diagnoses = entities.get("diagnoses", [])
        if diagnoses:
            hints.append("Query involves medical diagnoses - consider using :Diagnosis nodes and ICD codes")
        
        procedures = entities.get("procedures", [])
        if procedures:
            hints.append("Query involves medical procedures - consider :Procedure nodes")
        
        hospitals = entities.get("hospitals", [])
        if hospitals:
            hints.append("Query involves hospitals - consider :Hospital nodes and location data")
        
        # Language hint
        language = rag_results.get("query_metadata", {}).get("language", "")
        if language == "id":
            hints.append("Respond in Indonesian language")
        elif language == "en":
            hints.append("Respond in English language")
        
        # RAG-based hints
        top_matches = rag_results.get("summary", {}).get("top_matches", [])
        if top_matches:
            top_match = top_matches[0]
            if top_match.get("similarity_score", 0) > 0.8:
                content = top_match.get("content", "")
                hints.append(f"High confidence match found: {content}")
        
        return hints

    def _run(self, original_query: str, extracted_entities: str, rag_results: str) -> str:
        try:
            # Parse input JSON
            entities_data = json.loads(extracted_entities)
            rag_data = json.loads(rag_results)
            
            entities = entities_data.get("entities", {})
            
            # Build enriched context
            enriched_context = {
                "original_query": original_query,
                "query_metadata": {
                    "intent": entities_data.get("query_intent", "unknown"),
                    "language": entities_data.get("language", "auto-detect"),
                    "keywords": entities_data.get("keywords", [])
                },
                "extracted_entities_summary": self._build_entity_context(entities),
                "rag_insights": self._build_rag_context(rag_data),
                "query_generation_hints": self._generate_query_hints(entities, rag_data, original_query),
                "enhanced_system_message": self._build_enhanced_system_message(entities_data, rag_data, original_query)
            }
            
            return json.dumps(enriched_context, indent=2)
            
        except json.JSONDecodeError as e:
            return json.dumps({
                "error": f"JSON parsing error: {str(e)}",
                "fallback_context": {
                    "original_query": original_query,
                    "message": "Using basic context due to parsing error"
                }
            })
        except Exception as e:
            return json.dumps({
                "error": f"Context building failed: {str(e)}",
                "fallback_context": {
                    "original_query": original_query,
                    "message": "Using basic context due to error"
                }
            })

    def _build_enhanced_system_message(self, entities_data: Dict, rag_data: Dict, original_query: str) -> str:
        """Build enhanced system message for the agent"""
        
        base_message = """
        You are an expert data analyst with enhanced context about the user's query.
        
        QUERY ANALYSIS:
        """
        
        # Add entity information
        entities = entities_data.get("entities", {})
        if any(entities.values()):
            base_message += f"\n\nEXTRACTED ENTITIES:\n"
            for entity_type, entity_list in entities.items():
                if entity_list:
                    base_message += f"- {entity_type.title()}: {', '.join(entity_list)}\n"
        
        # Add RAG insights
        top_matches = rag_data.get("summary", {}).get("top_matches", [])
        if top_matches:
            base_message += f"\n\nRELEVANT DATABASE ENTITIES (from semantic search):\n"
            for match in top_matches[:3]:
                content = match.get("content", "")
                score = match.get("similarity_score", 0)
                base_message += f"- {content} (similarity: {score:.3f})\n"
        
        # Add query hints
        query_intent = entities_data.get("query_intent", "")
        language = entities_data.get("language", "")
        
        base_message += f"\n\nQUERY CONTEXT:"
        base_message += f"\n- Intent: {query_intent}"
        base_message += f"\n- Language: {language}"
        base_message += f"\n- Original Query: '{original_query}'"
        
        base_message += """

        INSTRUCTIONS:
        1. Use the schema_linking tool to get database schema
        2. Create Cypher queries that leverage the extracted entities and RAG insights
        3. If RAG found high-similarity matches, prioritize those entities in your query
        4. Respond in the same language as the user's question
        5. Use the enhanced context above to create more precise and relevant queries
        """
        
        return base_message
