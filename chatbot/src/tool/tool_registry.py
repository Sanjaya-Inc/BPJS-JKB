from langchain_core.utils.function_calling import convert_to_openai_function
from src.tool.get_hospital_data_tool import GetHospitalDataTool

class ToolRegistry:
    def __init__(self):
        """Initialize and register tool instances."""

        self._tools = {
            "get_hospital_data_tool": GetHospitalDataTool(),
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
