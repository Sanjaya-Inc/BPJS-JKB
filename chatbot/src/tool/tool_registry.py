from src.tool.schema_linking import SchemaLinkingTool
from src.tool.execute_chyper import ExecuteCypherTool
from src.tool.entity_extraction import EntityExtractionTool
from src.tool.rag_enhanced_search import RagEnhancedSearchTool
from src.tool.context_builder import ContextBuilderTool

class ToolRegistry:
    def __init__(self):
        self._tools = {
            "schema_linking": SchemaLinkingTool(),
            "execute_cypher": ExecuteCypherTool(),
            "entity_extraction": EntityExtractionTool(),
            "rag_enhanced_search": RagEnhancedSearchTool(),
            "context_builder": ContextBuilderTool()
        }
    
    def get_tools(self):
        return list(self._tools.values())