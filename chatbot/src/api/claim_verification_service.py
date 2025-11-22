import sys
import os
import re
from typing import Dict, Any

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..')))

from chatbot.src.tool.execute_chyper import ExecuteCypherTool
from langchain_core.messages import SystemMessage, AIMessage, HumanMessage
from langchain_openai import ChatOpenAI
from langchain.agents import create_agent
from langchain_core.callbacks import BaseCallbackHandler


class ToolExecutionPrinter(BaseCallbackHandler):
    """Custom callback handler to log tool executions during claim verification."""
    
    def on_tool_start(self, serialized, input_str, **kwargs):
        """Run when a tool starts running."""
        tool_name = serialized.get("name")
        print(f"[VERIFY_LOG] 🛠️  Agent is entering tool: {tool_name}")
        print(f"[VERIFY_LOG]    Input args: {input_str}")

    def on_tool_end(self, output, **kwargs):
        """Run when a tool ends running."""
        print(f"[VERIFY_LOG] ✅ Tool execution finished.")


class ClaimVerificationService:
    def __init__(self):
        """Initialize the claim verification service with model, tools, and agent."""
        # Initialize the LLM model (same configuration as notebook)
        self.model = ChatOpenAI(
            model="qwen3-8B", 
            base_url="http://127.0.0.1:1234/v1", 
            api_key=""
        )
        
        # Initialize tools (same as notebook)
        self.tools = [ExecuteCypherTool()]
        
        # Create agent executor
        self.agent_executor = create_agent(self.model, self.tools)
        
        # Define system message and chat history (exact copy from notebook)
        self.base_chat_history = [
            SystemMessage(
                """
                You are an expert Medical Fraud Detection AI Agent powered by a Knowledge Graph.
                Your goal is to validate insurance claims against medical rules.

                You have 1 tools to help you analyze the claim is Fraudlent or not.
                1. execute_cypher: Executes Cypher queries against the graph database

                Rule : 
                1. When the claim data from the graph database already has a Status (FRAUD / NORMAL). It means it has been validated before. You should return the existing Status without re-validation.
                2. Use Indonesian language for all responses.

                Output Formaat: 
                Claim ID: <claim_id>
                Validation Result: <FRAUD/NORMAL>
                Confidence Score: <0-100%>
                Detail Claim Data: <detailed claim data>
                Explanation: <detailed explanation of the validation>
                """,
            ),
        ]
        
        # Golden Cypher queries from the notebook
        self.golden_cypher_for_get_claim_data = """
MATCH (c:Claim {id: <claim_id>})
OPTIONAL MATCH (c)-[:HAS_PATIENT]->(patient:Patient)
OPTIONAL MATCH (c)-[:HAS_PRIMARY_PROCEDURE]->(pp:Procedure)
OPTIONAL MATCH (c)-[:HAS_SECONDARY_PROCEDURE]->(sp:Procedure)
OPTIONAL MATCH (c)-[:HAS_CLINICAL_NOTE]->(note:ClinicalNote)
OPTIONAL MATCH (c)-[:SUBMITTED_AT]->(hospital:Hospital)
OPTIONAL MATCH (c)-[:SUBMITTED_BY]->(doctor:Doctor)
OPTIONAL MATCH (c)-[:CODED_AS]->(diagnosis:Diagnosis)
RETURN c.id, c.total_cost, c.status,
       patient.name as patient_name,
       hospital.name as hospital_name,
       doctor.name as doctor_name,
       diagnosis.name as diagnosis_name,
       collect(DISTINCT pp.name) as primary_procedures,
       collect(DISTINCT sp.name) as secondary_procedures,
       note.primary_diagnosis_text,
       note.secondary_diagnosis_text
        """
        
        self.golden_cypher_to_get_price_procedure_diagnose_based_on_claim_id = """
MATCH (c:Claim {id: <claim_id>})
OPTIONAL MATCH (c)-[:HAS_PRIMARY_PROCEDURE]->(pp:Procedure)
OPTIONAL MATCH (c)-[:HAS_SECONDARY_PROCEDURE]->(sp:Procedure)
OPTIONAL MATCH (c)-[:CODED_AS]->(diagnosis:Diagnosis)
WITH c, diagnosis, 
     collect(DISTINCT pp) as primary_procs,
     collect(DISTINCT sp) as secondary_procs
RETURN 
    c.id,
    c.total_cost,
    diagnosis.name,
    diagnosis.avg_cost as diagnosis_cost,
    reduce(total = 0, proc IN primary_procs | total + COALESCE(proc.avg_cost, 0)) as primary_procs_total,
    reduce(total = 0, proc IN secondary_procs | total + COALESCE(proc.avg_cost, 0)) as secondary_procs_total
        """
        
        self.golden_query_to_get_diagnose_and_procedure_relation = """
MATCH (d:Diagnosis {code: <diagnosis_id>})-[r]->(p:Procedure)
WITH d, count(r) AS procedure_count,
     collect({procedure: p.name, relationship: type(r), cost: p.avg_cost}) AS procedures
RETURN d.code AS ICD10_Code,
       d.name AS Diagnosis_Name,
       d.severity AS Severity,
       d.avg_cost AS Diagnosis_Cost,
       procedure_count AS Number_of_Procedures,
       procedures AS Associated_Procedures;
        """
        
        self.golden_query_get_specialisties_doctor = """
MATCH (d:Doctor {id: <doctor_id>})
RETURN d.id AS doctor_id,
       d.name AS doctor_name,
       d.specialization AS specialization;
        """
        
        self.golden_query_get_specialties_and_facilities_hospital = """
MATCH (h:Hospital {id: <hospital_id>})
OPTIONAL MATCH (h)-[:HAS_SPECIALTY]->(s:Specialty)
OPTIONAL MATCH (h)-[:HAS_FACILITY]->(f:Facility)
RETURN h.id AS hospital_id,
       h.name AS hospital_name,
       h.class AS hospital_class,
       h.location AS location,
       collect(DISTINCT s.name) AS specialties,
       collect(DISTINCT f.name) AS facilities;
        """
        
        # Additional queries for form verification (from notebook)
        self.golden_query_get_procedure_costs = """
MATCH (p:Procedure)
WHERE p.name IN ['<primary_procedure>', '<secondary_procedure>']
RETURN p.name AS procedure_name,
       p.avg_cost AS avg_cost
        """

        self.golden_query_get_diagnosis_cost = """
MATCH (d:Diagnosis {code: '<diagnosis_id>'})
RETURN d.code AS diagnosis_code,
       d.name AS diagnosis_name,
       d.avg_cost AS avg_cost
        """

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

    def verify_form_data(self, form_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Verify form input data using the exact logic from dani-verify-claim-form.ipynb notebook.
        
        Args:
            form_data: Dictionary containing form input data
            
        Returns:
            Dictionary containing the verification result and metadata
        """
        try:
            # Format form input for display (exact copy from notebook)
            form_summary = f"""
Hospital ID: {form_data['hospital_id']}
Doctor ID: {form_data['doctor_id']}
Diagnosis ID: {form_data['diagnosa_id']}
Total Cost: {form_data['total_cost']:,}
Primary Procedure: {form_data['primary_procedure']}
Secondary Procedure: {form_data.get('secondary_procedure', 'None')}
Diagnosis Text: {form_data['diagnosis_text']}
"""

            # Prepare messages following the notebook pattern (exact copy from notebook)
            message = [
                HumanMessage(f"This is a medical claim form with the following data: {form_summary}"),
                HumanMessage(f"""

    Please validate this claim form data following these steps. Be objective and allow for reasonable operational variances.



    **Form Data Details:**

    - Hospital ID: {form_data['hospital_id']}

    - Doctor ID: {form_data['doctor_id']}

    - Diagnosis ID: {form_data['diagnosa_id']}

    - Total Cost: {form_data['total_cost']:,}

    - Primary Procedure: {form_data['primary_procedure']}

    - Secondary Procedure: {form_data.get('secondary_procedure', 'None')}

    - Diagnosis Text: {form_data['diagnosis_text']}



    **Validation Steps (Apply these rules strictly in order)**:



    1. **Procedure Consistency**: 

       Check if the procedures are clinically appropriate for the diagnosis.

       - *Logic*: Use the relation from {self.golden_query_to_get_diagnose_and_procedure_relation} (replace '<diagnosis_id>' with '{form_data['diagnosa_id']}').

       - *Guidance*: If the procedures are standard diagnostic tools for the diagnosis (e.g., CT Scan/MRI for Stroke), it is a MATCH.



    2. **Cost Analysis (The 20% Rule)**: 

       Compare the Form's Total Cost vs. Ground Truth (Sum of Diagnosis Avg Cost + Procedure Avg Costs).

       - *Logic*: Calculate the deviation: `(Form_Cost - Ground_Truth) / Ground_Truth`.

       - *Guidance*: 

          - If deviation is **< 20%**: Consider this **NORMAL** operational variance (e.g., room upgrades, extra meds). Do NOT flag as fraud based on cost alone.

          - If deviation is **> 20%**: Flag as **FRAUD** (Cost significantly inflated).

       - Use these queries to get ground truth:

         * Diagnosis cost: {self.golden_query_get_diagnosis_cost} (replace '<diagnosis_id>' with '{form_data['diagnosa_id']}')

         * Procedure costs: {self.golden_query_get_procedure_costs} (replace '<primary_procedure>' with '{form_data['primary_procedure']}' and '<secondary_procedure>' with '{form_data.get('secondary_procedure', '')}')



    3. **Doctor Qualification (GP Exception)**: 

       Check if the doctor is qualified.

       - *Logic*: Use {self.golden_query_get_specialisties_doctor} (replace '<doctor_id>' with '{form_data['doctor_id']}').

       - *Guidance*: 

          - **GPs (General Practitioners)** are VALID for initial diagnoses, consultations, and ordering standard scans (like MRI/CT), even for complex conditions like Stroke. 

          - Flag as **FRAUD** only if there is a **hard contradiction** (e.g., a Pediatrician performing Major Surgery, or an Ophthalmologist treating Heart Attack).



    4. **Hospital Capability**: 

       Check if the hospital has relevant facilities.

       - *Logic*: Use {self.golden_query_get_specialties_and_facilities_hospital} (replace '<hospital_id>' with '{form_data['hospital_id']}').

       - *Guidance*: Look for broad keyword matches. For example, if Diagnosis is "Stroke", facilities like "ICU", "Neurology", or "Internal Medicine" are sufficient evidence of capability.



    5. **Final Verdict**:

       Based on the above, determine FRAUD or NORMAL.

       - Provide a confidence score (0-100%).

       - Provide the Form Data Summary.

       - **Explanation**: You MUST explicitly state the cost deviation percentage in your explanation (e.g., "Cost is 4.5% higher, which is within the acceptable 20% variance").

    """)
            ]
            
            # Combine base chat history with current message
            final_messages = self.base_chat_history + message
            
            # Create callback handler instance
            callback_handler = ToolExecutionPrinter()
            
            # Execute the agent with logging
            print(f"[VERIFY_LOG] Processing form data verification")
            response = self.agent_executor.invoke(
                {"messages": final_messages},
                config={"callbacks": [callback_handler]}
            )
            
            # Extract raw content from response
            raw_content = response['messages'][-1].content
            
            # Clean the response
            final_output = self.clean_llm_response(raw_content)
            
            # Parse the structured output to extract components
            return self._parse_form_verification_output(final_output, form_data, raw_content, form_summary)
            
        except Exception as e:
            return {
                "form_data_summary": f"Error processing form data: {form_data}",
                "validation_result": "ERROR",
                "confidence_score": 0,
                "detail_analysis": f"Error during verification: {str(e)}",
                "explanation": f"Error during verification: {str(e)}",
                "status": "error",
                "metadata": {
                    "error": str(e),
                    "input_form_data": form_data
                }
            }

    def verify_claim(self, claim_id: str) -> Dict[str, Any]:
        """
        Verify a claim by claim ID using the exact logic from the notebook.
        
        Args:
            claim_id: The claim ID to verify
            
        Returns:
            Dictionary containing the verification result and metadata
        """
        try:
            # Prepare messages following the notebook pattern
            message = [
                HumanMessage(f"This is Claim ID: {claim_id}"),
                HumanMessage(f"""
    Please follow these steps to analyze the claim. Be objective and allow for reasonable operational variances.

    1. **Data Retrieval**: 
       Execute {self.golden_cypher_for_get_claim_data} to get all relevant data. 
       - If the status is already "NORMAL" or "FRAUD", return it directly with 100% confidence. (No further validation needed.)
       - If status is null/NaN, proceed to validation steps below.

    2. **Validation Logic (Apply these rules strictly in order)**:

       a. **Procedure Consistency**: 
          Check if the procedure is clinically appropriate for the diagnosis.
          - *Logic*: Use the relation from {self.golden_query_to_get_diagnose_and_procedure_relation}.
          - *Guidance*: If the procedure is a standard diagnostic tool for the diagnosis (e.g., MRI for Stroke), it is a MATCH.

       b. **Cost Analysis (The 20% Rule)**: 
          Compare the Claim's Total Cost vs. Ground Truth (Sum of Diagnosis Avg Cost + Procedure Avg Cost).
          - *Logic*: Calculate the deviation: `(Claim_Cost - Ground_Truth) / Ground_Truth`.
          - *Guidance*: 
             - If deviation is **< 20%**: Consider this **NORMAL** operational variance (e.g., room upgrades, extra meds). Do NOT flag as fraud based on cost alone.
             - If deviation is **> 20%**: Flag as **FRAUD** (Cost significantly inflated).
          - Ground Truth Query: {self.golden_cypher_to_get_price_procedure_diagnose_based_on_claim_id} (or calculate manually using averages).

       c. **Doctor Qualification (GP Exception)**: 
          Check if the doctor is qualified.
          - *Logic*: Use {self.golden_query_get_specialisties_doctor}.
          - *Guidance*: 
             - **GPs (General Practitioners)** are VALID for initial diagnoses, consultations, and ordering standard scans (like MRI/CT), even for complex conditions like Stroke. 
             - Flag as **FRAUD** only if there is a **hard contradiction** (e.g., a Pediatrician performing Major Surgery, or an Ophthalmologist treating Heart Attack).

       d. **Hospital Capability**: 
          Check if the hospital has relevant facilities.
          - *Logic*: Use {self.golden_query_get_specialties_and_facilities_hospital}.
          - *Guidance*: Look for broad keyword matches. For example, if Diagnosis is "Stroke", facilities like "ICU", "Neurology", or "Internal Medicine" are sufficient evidence of capability.

    3. **Final Verdict**:
       Based on the above, determine FRAUD or NORMAL.
       - Provide a confidence score (0-100%).
       - Provide the Detailed Claim Data.
       - **Explanation**: You MUST explicitly state the cost deviation percentage in your explanation (e.g., "Cost is 4.5% higher, which is within the acceptable 20% variance").
       - Use Indonesian Langauge for all responses.
    """)
            ]
            
            # Combine base chat history with current message
            final_messages = self.base_chat_history + message
            
            # Create callback handler instance
            callback_handler = ToolExecutionPrinter()
            
            # Execute the agent with logging
            print(f"[VERIFY_LOG] Processing claim verification for: {claim_id}")
            response = self.agent_executor.invoke(
                {"messages": final_messages},
                config={"callbacks": [callback_handler]}
            )
            
            # Extract raw content from response
            raw_content = response['messages'][-1].content
            
            # Clean the response
            final_output = self.clean_llm_response(raw_content)
            
            # Parse the structured output to extract components
            return self._parse_verification_output(final_output, claim_id, raw_content)
            
        except Exception as e:
            return {
                "claim_id": claim_id,
                "validation_result": "ERROR",
                "confidence_score": 0,
                "detail_claim_data": {},
                "explanation": f"Error during verification: {str(e)}",
                "status": "error",
                "metadata": {
                    "error": str(e),
                    "input_claim_id": claim_id
                }
            }

    def _parse_verification_output(self, output: str, claim_id: str, raw_content: str) -> Dict[str, Any]:
        """
        Parse the LLM output to extract structured verification results.
        
        Args:
            output: Cleaned output from the LLM
            claim_id: The original claim ID
            raw_content: Raw response for debugging
            
        Returns:
            Structured verification result
        """
        try:
            # Default values
            validation_result = "UNKNOWN"
            confidence_score = 0
            detail_claim_data = {}
            explanation = output
            
            # Extract validation result (FRAUD or NORMAL)
            if "FRAUD" in output.upper():
                validation_result = "FRAUD"
            elif "NORMAL" in output.upper():
                validation_result = "NORMAL"
            
            # Extract confidence score
            confidence_match = re.search(r'confidence.*?(\d+)', output, re.IGNORECASE)
            if confidence_match:
                confidence_score = int(confidence_match.group(1))
            
            # Try to extract structured data if present
            # This is a simplified parser - could be enhanced based on actual output format
            lines = output.split('\n')
            for line in lines:
                if 'detail claim data' in line.lower():
                    # Try to extract any JSON-like data
                    continue
            
            return {
                "claim_id": claim_id,
                "validation_result": validation_result,
                "confidence_score": confidence_score,
                "detail_claim_data": detail_claim_data,
                "explanation": explanation,
                "status": "success",
                "metadata": {
                    "raw_response": raw_content,
                    "input_claim_id": claim_id,
                    "parsed_successfully": True
                }
            }
            
        except Exception as e:
            return {
                "claim_id": claim_id,
                "validation_result": "ERROR",
                "confidence_score": 0,
                "detail_claim_data": {},
                "explanation": f"Error parsing verification result: {str(e)}\n\nRaw output: {output}",
                "status": "error",
                "metadata": {
                    "error": str(e),
                    "input_claim_id": claim_id,
                    "raw_response": raw_content,
                    "parsed_successfully": False
                }
            }

    def _parse_form_verification_output(self, output: str, form_data: Dict[str, Any], raw_content: str, form_summary: str) -> Dict[str, Any]:
        """
        Parse the LLM output for form verification to extract structured results.
        
        Args:
            output: Cleaned output from the LLM
            form_data: Original form input data
            raw_content: Raw response for debugging
            form_summary: Formatted form summary
            
        Returns:
            Structured form verification result
        """
        try:
            # Default values
            validation_result = "UNKNOWN"
            confidence_score = 0
            detail_analysis = output
            explanation = output
            
            # Extract validation result (FRAUD or NORMAL)
            if "FRAUD" in output.upper():
                validation_result = "FRAUD"
            elif "NORMAL" in output.upper():
                validation_result = "NORMAL"
            
            # Extract confidence score
            confidence_match = re.search(r'confidence.*?(\d+)', output, re.IGNORECASE)
            if confidence_match:
                confidence_score = int(confidence_match.group(1))
            
            return {
                "form_data_summary": form_summary.strip(),
                "validation_result": validation_result,
                "confidence_score": confidence_score,
                "detail_analysis": detail_analysis,
                "explanation": explanation,
                "status": "success",
                "metadata": {
                    "raw_response": raw_content,
                    "input_form_data": form_data,
                    "parsed_successfully": True
                }
            }
            
        except Exception as e:
            return {
                "form_data_summary": form_summary.strip(),
                "validation_result": "ERROR",
                "confidence_score": 0,
                "detail_analysis": f"Error parsing verification result: {str(e)}",
                "explanation": f"Error parsing verification result: {str(e)}\n\nRaw output: {output}",
                "status": "error",
                "metadata": {
                    "error": str(e),
                    "input_form_data": form_data,
                    "raw_response": raw_content,
                    "parsed_successfully": False
                }
            }
