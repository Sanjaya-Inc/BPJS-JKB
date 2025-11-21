# BPJS-JKB API

A FastAPI-based REST API for accessing BPJS-JKB data from a Neo4j graph database. The API provides endpoints to retrieve hospitals, doctors, and claims information with filtering capabilities.

## Features

- **GET /hospitals** - Retrieve hospital data with specialties and facilities
- **GET /doctors** - Retrieve doctor information with hospital associations
- **GET /claims** - Retrieve claims data with diagnosis and costs
- **Filtering Support** - Query parameters for filtering results
- **Neo4j Integration** - Direct queries to graph database
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