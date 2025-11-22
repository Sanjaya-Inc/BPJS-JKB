# BPJS-JKB API

A FastAPI-based REST API for accessing BPJS-JKB data from a Neo4j graph database. The API provides endpoints to retrieve hospitals, doctors, and claims information with filtering capabilities.

## Features

- **GET /hospitals** - Retrieve hospital data with specialties and facilities
- **GET /hospitals/{hospital_id}/analyze** - Analyze hospital claiming behavior using normal distribution
- **GET /doctors** - Retrieve doctor information with hospital associations
- **GET /claims** - Retrieve claims data with diagnosis and costs
- **GET /diagnoses** - Retrieve diagnosis information with filtering capabilities
- **GET /diagnoses/{diagnosis_id}** - Retrieve specific diagnosis details by ID or ICD-10 code
- **POST /claims/verify** - AI-powered fraud detection for insurance claims
- **POST /claims/verify-form** - AI-powered fraud detection for new claim form data
- **POST /chatbot/ask** - Natural language querying with RAG-enhanced search
- **Filtering Support** - Query parameters for filtering results
- **Neo4j Integration** - Direct queries to graph database
- **AI/ML Integration** - LLM-powered analysis and fraud detection
- **Statistical Analysis** - Normal distribution-based hospital behavior analysis
- **CSV-like JSON Response** - Simple, predictable response format
- **Comprehensive Testing** - Full test suite with mocking
- **Auto Documentation** - OpenAPI/Swagger docs at `/docs`

## Prerequisites

- Python 3.10+
- Neo4j Database running on `localhost:7687`
- Required Python packages (see `requirements.txt`)

## Installation

1. **Install dependencies:**
```bash
cd chatbot
pip3 install -r requirements.txt
```

2. **Configure Neo4j Connection:**

   The project uses centralized configuration management via `chatbot/src/config.py`. By default, it connects to:
   - URL: `neo4j://localhost:7687`
   - Username: `neo4j`
   - Password: `12345678`

   **To use different credentials**, set environment variables:
   ```bash
   export NEO4J_URI="neo4j://your-server:7687"
   export NEO4J_USERNAME="your-username"
   export NEO4J_PASSWORD="your-password"
   ```

   Or create a `.env` file in the project root:
   ```bash
   NEO4J_URI=neo4j://localhost:7687
   NEO4J_USERNAME=neo4j
   NEO4J_PASSWORD=your-secure-password
   ```

3. **Load data into Neo4j (if not already done):**
```bash
python3 data/upsert_initial_data.py
python3 data/setup_indicies.py
python3 data/calculate_diagnosis_benchmarks.py
```

## Running the API

```bash
cd chatbot
uvicorn src.api.main:app --host 0.0.0.0 --port 8000
```

The API will be available at `http://localhost:8000`

## API Documentation

Once the server is running, visit:
- **Interactive API docs:** `http://localhost:8000/docs`
- **ReDoc documentation:** `http://localhost:8000/redoc`

## Endpoints

### GET /hospitals/{hospital_id}/analyze

**Analyze hospital behavior on claiming using normal distribution.** This endpoint provides statistical analysis of a hospital's claiming patterns to identify potential anomalies.

**Path Parameters:**
- `hospital_id` (required): The hospital identifier (e.g., "HOS001")

**Analysis Process:**
The system performs a statistical analysis by:
1. **Claims Aggregation**: Groups all claims submitted to the specified hospital by diagnosis
2. **Normal Distribution Analysis**: Calculates Z-scores for each diagnosis to measure deviation from expected patterns
3. **Statistical Ranking**: Orders results by Z-score (highest deviations first)
4. **Anomaly Detection**: Identifies potential claiming anomalies based on statistical patterns

**Example Request:**
```bash
# Analyze claiming behavior for a specific hospital
curl "http://localhost:8000/hospitals/HOS001/analyze"
```

**Example Response:**
```json
{
  "data": [
    {
      "diagnosis_id": "D001",
      "diagnosis_name": "Acute Myocardial Infarction",
      "total_claims": 25,
      "z_score": 2.8
    },
    {
      "diagnosis_id": "D013", 
      "diagnosis_name": "Cerebral Infarction",
      "total_claims": 18,
      "z_score": 1.9
    },
    {
      "diagnosis_id": "D007",
      "diagnosis_name": "Pneumonia",
      "total_claims": 45,
      "z_score": 0.3
    }
  ],
  "hospital_id": "HOS001",
  "hospital_name": "RSUP Dr. Hasan Sadikin (RSHS)",
  "analysis_type": "claiming_behavior_normal_distribution"
}
```

**Response Fields:**
- `data`: Array of diagnosis claiming analysis
  - `diagnosis_id`: Diagnosis identifier
  - `diagnosis_name`: Human-readable diagnosis name
  - `total_claims`: Total number of claims for this diagnosis at the hospital
  - `z_score`: Statistical Z-score indicating deviation from normal patterns
- `hospital_id`: Hospital identifier
- `hospital_name`: Hospital name for context
- `analysis_type`: Type of analysis performed

**Z-Score Interpretation:**
- **Z-Score > 2.0**: Significant positive deviation (potentially suspicious high claiming)
- **Z-Score 1.0-2.0**: Moderate positive deviation (above average claiming)
- **Z-Score -1.0 to 1.0**: Normal claiming pattern
- **Z-Score < -1.0**: Below average claiming pattern

**Use Cases:**
- **Fraud Detection**: Identify hospitals with unusual claiming patterns
- **Quality Assurance**: Monitor hospital performance and compliance
- **Resource Planning**: Understand hospital specialization patterns
- **Risk Assessment**: Evaluate hospitals for insurance risk management

### GET /hospitals

Retrieve hospital information with their specialties and facilities.

**Query Parameters:**
- `class_type` (optional): Filter by hospital class (e.g., "Class A")
- `specialty` (optional): Filter by specialty (e.g., "Cardiology")

**Example Requests:**
```bash
# Get all hospitals
curl "http://localhost:8000/hospitals"

# Filter by hospital class
curl "http://localhost:8000/hospitals?class_type=Class A"

# Filter by specialty
curl "http://localhost:8000/hospitals?specialty=Cardiology"
```

**Example Response:**
```json
{
  "data": [
    {
      "hospital_id": "HOS001",
      "name": "RSUP Dr. Hasan Sadikin (RSHS)",
      "class_type": "Class A (National Referral)",
      "latitude": -6.898169,
      "longitude": 107.598406,
      "specialties": ["Anestesi", "Bedah", "Internal Medicine"],
      "facilities": ["IGD 24/7", "929 Beds", "NICU"]
    }
  ]
}
```

### GET /doctors

Retrieve doctor information with their hospital associations.

**Query Parameters:**
- `specialization` (optional): Filter by doctor specialization
- `hospital_id` (optional): Filter by hospital ID

**Example Requests:**
```bash
# Get all doctors
curl "http://localhost:8000/doctors"

# Filter by specialization
curl "http://localhost:8000/doctors?specialization=Cardiologist"

# Filter by hospital
curl "http://localhost:8000/doctors?hospital_id=HOS001"
```

**Example Response:**
```json
{
  "data": [
    {
      "doctor_id": "DOC001",
      "primary_hospital_id": "HOS002",
      "name": "Dr. Budi Hartono",
      "specialization": "Cardiologist"
    }
  ]
}
```

### GET /claims

Retrieve claims information with diagnosis and cost details.

**Query Parameters:**
- `status` (optional): Filter by claim status ("NORMAL" or "FRAUD")
- `hospital_id` (optional): Filter by hospital ID
- `doctor_id` (optional): Filter by doctor ID

**Example Requests:**
```bash
# Get all claims
curl "http://localhost:8000/claims"

# Filter by status
curl "http://localhost:8000/claims?status=NORMAL"

# Filter by hospital
curl "http://localhost:8000/claims?hospital_id=HOS001"

# Filter by doctor
curl "http://localhost:8000/claims?doctor_id=DOC001"
```

**Example Response:**
```json
{
  "data": [
    {
      "claim_id": "C1001",
      "doctor_id": "DOC001",
      "hospital_id": "HOS001",
      "diagnosis": "I21.9",
      "total_cost": 65000000,
      "label": "NORMAL",
      "medical_resume_json": "Acute Myocardial Infarction. Chest pain radiating to left arm."
    }
  ]
}
```

### GET /diagnoses

Retrieve diagnosis information with filtering capabilities.

**Query Parameters:**
- `severity_level` (optional): Filter by severity level ("High", "Medium", "Low")
- `icd10_code` (optional): Filter by ICD-10 code pattern
- `name` (optional): Filter by diagnosis name (partial match)
- `min_cost` (optional): Filter by minimum average cost
- `max_cost` (optional): Filter by maximum average cost

**Example Requests:**
```bash
# Get all diagnoses
curl "http://localhost:8000/diagnoses"

# Filter by severity level
curl "http://localhost:8000/diagnoses?severity_level=High"

# Filter by ICD-10 code pattern
curl "http://localhost:8000/diagnoses?icd10_code=I21"

# Filter by diagnosis name
curl "http://localhost:8000/diagnoses?name=heart"

# Filter by cost range
curl "http://localhost:8000/diagnoses?min_cost=10000000&max_cost=50000000"

# Combine multiple filters
curl "http://localhost:8000/diagnoses?severity_level=High&min_cost=30000000"
```

**Example Response:**
```json
{
  "data": [
    {
      "diagnosis_id": "D001",
      "icd10_code": "I21.9",
      "name": "Acute Myocardial Infarction (Heart Attack)",
      "avg_cost": 65000000,
      "severity_level": "High"
    },
    {
      "diagnosis_id": "D013",
      "icd10_code": "I63.9",
      "name": "Cerebral Infarction (Stroke)",
      "avg_cost": 55000000,
      "severity_level": "High"
    }
  ]
}
```

### GET /diagnoses/{diagnosis_id}

Retrieve specific diagnosis details by diagnosis ID or ICD-10 code.

**Path Parameters:**
- `diagnosis_id`: Either the diagnosis node ID (e.g., "D001") or ICD-10 code (e.g., "I21.9")

**Example Requests:**
```bash
# Get diagnosis by node ID
curl "http://localhost:8000/diagnoses/D001"

# Get diagnosis by ICD-10 code
curl "http://localhost:8000/diagnoses/I21.9"
```

**Example Response:**
```json
{
  "data": [
    {
      "diagnosis_id": "D001",
      "icd10_code": "I21.9",
      "name": "Acute Myocardial Infarction (Heart Attack)",
      "avg_cost": 65000000,
      "severity_level": "High"
    }
  ]
}
```

### POST /claims/verify

**AI-powered fraud detection for insurance claims.** This endpoint uses machine learning and medical knowledge graph analysis to detect potentially fraudulent claims.

**Request Body:**
```json
{
  "claim_id": "C1043"
}
```

**Validation Process:**
The system performs a multi-step analysis:
1. **Data Retrieval**: Fetches claim data from the knowledge graph
2. **Procedure Consistency**: Verifies procedures are appropriate for the diagnosis
3. **Cost Analysis**: Applies the 20% variance rule for cost validation
4. **Doctor Qualification**: Validates doctor specialization for the procedure
5. **Hospital Capability**: Ensures hospital has required facilities

**Example Request:**
```bash
curl -X POST "http://localhost:8000/claims/verify" \
     -H "Content-Type: application/json" \
     -d '{"claim_id": "C1043"}'
```

**Example Response:**
```json
{
  "claim_id": "C1043",
  "validation_result": "FRAUD",
  "confidence_score": 85,
  "detail_claim_data": {
    "patient_name": "John Doe",
    "hospital_name": "General Hospital",
    "doctor_name": "Dr. Smith",
    "diagnosis_name": "Stroke",
    "total_cost": 15000.0,
    "procedures": ["MRI Brain", "CT Scan"]
  },
  "explanation": "Cost is 25% higher than expected ground truth (12000.0), exceeding the 20% variance threshold. Doctor qualification verified. Hospital has required neurology facilities. Flagged as FRAUD due to cost inflation.",
  "status": "success"
}
```

**Response Fields:**
- `claim_id`: The verified claim identifier
- `validation_result`: Either "FRAUD", "NORMAL", or "ERROR"
- `confidence_score`: Confidence level (0-100%)
- `detail_claim_data`: Detailed claim information from database
- `explanation`: Human-readable explanation of the analysis
- `status`: API response status

### POST /claims/verify-form

**AI-powered fraud detection for new claim form data.** This endpoint verifies raw form input data before it becomes a claim in the database, using the same validation logic as the claim verification endpoint.

**Request Body:**
```json
{
  "hospital_id": "H001",
  "doctor_id": "D001", 
  "diagnosa_id": "I63",
  "total_cost": 15000000,
  "primary_procedure": "CT Scan",
  "secondary_procedure": "MRI",
  "diagnosis_text": "Cerebral infarction"
}
```

**Validation Process:**
The system performs the same multi-step analysis as claim verification:
1. **Procedure Consistency**: Validates procedures are appropriate for the diagnosis
2. **Cost Analysis**: Applies 20% variance rule comparing form cost vs ground truth
3. **Doctor Qualification**: Validates doctor specialization (with GP exception rules)
4. **Hospital Capability**: Ensures hospital has required facilities for the diagnosis
5. **Final Verdict**: Returns FRAUD/NORMAL with confidence score and detailed explanation

**Key Differences from `/claims/verify`:**
- Works with **raw form data** instead of existing claim IDs
- Validates **new claims** before they enter the database
- Uses form input fields directly for validation
- Same validation logic but different data source

**Example Request:**
```bash
curl -X POST "http://localhost:8000/claims/verify-form" \
     -H "Content-Type: application/json" \
     -d '{
       "hospital_id": "H001",
       "doctor_id": "D001", 
       "diagnosa_id": "I63",
       "total_cost": 15000000,
       "primary_procedure": "CT Scan",
       "secondary_procedure": "MRI",
       "diagnosis_text": "Cerebral infarction"
     }'
```

**Example Response:**
```json
{
  "form_data_summary": "Hospital ID: H001\nDoctor ID: D001\nDiagnosis ID: I63\nTotal Cost: 15,000,000\nPrimary Procedure: CT Scan\nSecondary Procedure: MRI\nDiagnosis Text: Cerebral infarction",
  "validation_result": "NORMAL",
  "confidence_score": 92,
  "detail_analysis": "Procedure consistency verified: CT Scan and MRI are appropriate for stroke diagnosis. Cost deviation is 8.5% above ground truth, within acceptable 20% variance. Doctor qualification verified for general practitioner. Hospital has required neurology facilities.",
  "explanation": "Form data validated successfully. Cost is 8.5% higher than expected ground truth (13,850,000), which is within the acceptable 20% variance. All validation checks passed.",
  "status": "success"
}
```

**Response Fields:**
- `form_data_summary`: Summary of the input form data
- `validation_result`: Either "FRAUD", "NORMAL", or "ERROR"
- `confidence_score`: Confidence level (0-100%)
- `detail_analysis`: Detailed analysis of the validation
- `explanation`: Human-readable explanation of the validation
- `status`: API response status
- `metadata`: Additional processing information

**Request Fields:**
- `hospital_id` (required): Hospital identifier
- `doctor_id` (required): Doctor identifier  
- `diagnosa_id` (required): Diagnosis ID (ICD-10 code)
- `total_cost` (required): Total cost of the claim (must be > 0)
- `primary_procedure` (required): Primary medical procedure
- `secondary_procedure` (optional): Secondary medical procedure
- `diagnosis_text` (required): Diagnosis description text

### POST /chatbot/ask

Natural language interface for querying the medical database using RAG-enhanced search.

**Request Body:**
```json
{
  "question": "How many stroke cases were treated at RSUP Dr. Hasan Sadikin last month?"
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8000/chatbot/ask" \
     -H "Content-Type: application/json" \
     -d '{"question": "Show me all cardiologists at Class A hospitals"}'
```

**Example Response:**
```json
{
  "answer": "Based on the database query, there are 5 cardiologists working at Class A hospitals: Dr. Ahmad (HOS001), Dr. Sari (HOS002)...",
  "status": "success",
  "metadata": {
    "tools_used": 4,
    "input_question": "Show me all cardiologists at Class A hospitals"
  }
}
```

## Testing

Run the test suite:
```bash
cd chatbot
python3 -m pytest tests/test_api.py -v
```

## Database Schema

The API queries a Neo4j graph database with the following node types:
- **Hospital**: Healthcare facilities with specialties and facilities
- **Doctor**: Medical professionals associated with hospitals
- **Claim**: Insurance claims with diagnosis and cost information
- **Diagnosis**: ICD-10 diagnosis codes
- **Procedure**: Medical procedure codes
- **Specialty**: Hospital specializations
- **Facility**: Hospital facilities and equipment

## Configuration

### Environment Variables

The application supports the following environment variables for configuration:

| Variable | Description | Default |
|----------|-------------|---------|
| `NEO4J_URI` | Neo4j database connection URI | `neo4j://localhost:7687` |
| `NEO4J_USERNAME` | Neo4j database username | `neo4j` |
| `NEO4J_PASSWORD` | Neo4j database password | `12345678` |

### Configuration Files

- **`chatbot/src/config.py`** - Centralized configuration management
- **`.env`** (optional) - Environment variables file (add to `.gitignore`)

## Development

### Project Structure
```
chatbot/
├── src/
│   ├── config.py           # Centralized configuration
│   ├── api/
│   │   └── main.py         # FastAPI application
│   └── tool/               # Analysis tools
├── tests/
│   └── test_api.py         # Test suite
├── data/                   # Neo4j data loading scripts
├── notebook/               # Jupyter notebooks
├── requirements.txt        # Python dependencies
└── README.md              # This file
```
