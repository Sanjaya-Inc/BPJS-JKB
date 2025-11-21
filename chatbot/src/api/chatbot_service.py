import sys
import re
from typing import Dict, Any

from src.tool.tool_registry import ToolRegistry
from langchain_core.messages import SystemMessage, AIMessage, HumanMessage
from langchain_openai import ChatOpenAI
from langchain.agents import create_agent


class ChatbotService:
    def __init__(self):
        """Initialize the chatbot service with model, tools, and agent."""
        # Initialize the LLM model
        self.model = ChatOpenAI(
            model="qwen3-8B", 
            base_url="http://127.0.0.1:1234/v1", 
            api_key=""
        )
        
        # Initialize tools
        self.tool_registry = ToolRegistry()
        self.tools = self.tool_registry.get_tools()
        
        # Create agent executor
        self.agent_executor = create_agent(self.model, self.tools)
        
        # Define system message and chat history template
        self.base_chat_history = [
            SystemMessage(
                """
                You are an expert data analyst with enhanced RAG capabilities. You will answer user questions by create and executing Cypher queries to Database.

                You have 5 tools to help you create a correct, efficient, and precise Cypher query based on user questions.
                1. entity_extraction: Extracts medical entities and relationships from user input
                2. rag_enhanced_search: Performs semantic search across medical entities
                3. context_builder: Combines query, entities, and RAG results into enriched context
                4. schema_linking: Provides graph database schema definition
                5. execute_cypher: Executes Cypher queries against the graph database

                Step-by-step
                1. Extract medical entities from user question using entity_extraction
                2. Use rag_enhanced_search with extracted entities to find similar items in the database
                3. Use schema_linking to understand the database structure
                4. Use context_builder to create enriched context for query generation
                5. Finally, use execute_cypher with the enriched context to get precise results

                You can skip any tool if not needed.
                Summary the final answer based on the query result and provide clear explanation.
                """,
            ),
        ]

    def clean_llm_response(self, content: str) -> str:
        """Clean LLM response by removing thinking tags and unwanted content."""
        
        # 1. Remove <think>...</think> tags and their content
        content = re.sub(r'<think>.*?</think>', '', content, flags=re.DOTALL)
        
        # 2. Remove markdown code blocks
        # Note: It's safer to use replace or regex than hard slicing indices
        if content.startswith("```json"):
            content = content.replace("```json", "", 1)
        if content.startswith("```"):
            content = content.replace("```", "", 1)
        if content.endswith("```"):
            content = content[:-3]
            
        # 3. Remove any other XML-like tags
        content = re.sub(r'<[^>]+>', '', content)
        
        return content.strip()

    def process_question(self, user_input: str) -> Dict[str, Any]:
        """
        Process user question through the agent workflow and return structured response.
        
        Args:
            user_input: The user's question
            
        Returns:
            Dictionary containing the processed answer and metadata
        """
        try:
            # Prepare messages following the notebook pattern
            message = [
                HumanMessage(f"This is the question: {user_input}"),
                AIMessage("Understood. I will follow the workflow to answer the question."),
                HumanMessage("Detect what language is the question input and respond in the same language."),
                AIMessage("Understood. I will detect the input language and respond accordingly.")
            ]
            
            # Combine base chat history with current message
            final_messages = self.base_chat_history + message
            
            # Execute the agent
            response = self.agent_executor.invoke({
                "messages": final_messages,
            })
            
            # Extract raw content from response
            raw_content = response['messages'][-1].content
            
            # Clean the response
            final_output = self.clean_llm_response(raw_content)
            
            return {
                "answer": final_output,
                "status": "success",
                "metadata": {
                    "raw_response": raw_content,
                    "input_question": user_input,
                    "tools_used": len(self.tools)
                }
            }
            
        except Exception as e:
            return {
                "answer": f"Sorry, I encountered an error while processing your question: {str(e)}",
                "status": "error",
                "metadata": {
                    "error": str(e),
                    "input_question": user_input
                }
            }
