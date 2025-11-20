from src.tool.schema_linking import SchemaLinkingTool
from src.tool.execute_chyper import ExecuteCypherTool
# Import your new tools
from src.tool.verify_claim_rules import VerifyClaimRulesTool
from src.tool.assess_entity_risk import AssessEntityRiskTool

class ToolRegistry:
    def __init__(self):
        self._tools = {
            "schema_linking": SchemaLinkingTool(),
            "execute_cypher": ExecuteCypherTool(),
            "verify_claim_rules": VerifyClaimRulesTool(), # Added
            "assess_entity_risk": AssessEntityRiskTool()  # Added
        }
    
    def get_tools(self):
        return list(self._tools.values())