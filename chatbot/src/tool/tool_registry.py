import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..')))

from chatbot.src.tool.schema_linking import SchemaLinkingTool
from chatbot.src.tool.execute_chyper import ExecuteCypherTool
from chatbot.src.tool.entity_extraction import EntityExtractionTool
from chatbot.src.tool.rag_enhanced_search import RagEnhancedSearchTool

class ToolRegistry:
    def __init__(self):
        self._tools = {
            "schema_linking": SchemaLinkingTool(),
            "execute_cypher": ExecuteCypherTool(),
            "entity_extraction": EntityExtractionTool(),
            "rag_enhanced_search": RagEnhancedSearchTool(),
        }
    
    def get_tools(self):
        return list(self._tools.values())
