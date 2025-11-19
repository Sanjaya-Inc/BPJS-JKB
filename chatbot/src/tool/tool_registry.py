from langchain_core.utils.function_calling import convert_to_openai_function
from src.tool.execute_chyper import ExecuteCypherTool
from src.tool.schema_linking import SchemaLinkingTool

class ToolRegistry:
    def __init__(self):
        """Initialize and register tool instances."""

        self._tools = {
            "schema_linking": SchemaLinkingTool(),
            "execute_cypher": ExecuteCypherTool(),
        }

        self._functions = [
            convert_to_openai_function(tool) for tool in self._tools.values()
        ]

    def get_tools(self):
        """Retrieve all registered tools as a list of BaseTool objects."""
        # Return the list of tool instances, not the dictionary
        return list(self._tools.values())

    def get_functions(self):
        """Retrieve all registered LangChain-compatible functions."""
        return self._functions
