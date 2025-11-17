import json
from neo4j import GraphDatabase
import os # Import os to build a reliable file path

# 1. Neo4j Connection Details (from your prompt)
URI = "neo4j://localhost:7687"  # Default Neo4j Bolt port
AUTH = ("neo4j", "12345678")

# 2. Helper function to create constraints and indexes
# This is BEST PRACTICE: It ensures no duplicate nodes and speeds up queries.
def create_constraints_and_indexes(tx):
    print("Creating constraints and indexes...")
    # Make hospital names unique
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (h:Hospital) REQUIRE h.name IS UNIQUE;")
    # Make specialty names unique
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (s:Specialty) REQUIRE s.name IS UNIQUE;")
    # Make facility names unique
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (f:Facility) REQUIRE f.name IS UNIQUE;")
    # Create a spatial index on hospital locations for fast geographic queries
    tx.run("CREATE SPATIAL INDEX IF NOT EXISTS FOR (h:Hospital) ON (h.location);")

# 3. Define the Cypher query to import one hospital object
# This query creates the graph structure you designed.
def create_hospital_graph(tx, hospital_record):
    
    # This single query:
    # 1. Creates the :Hospital node and sets its properties
    # 2. Unwinds the 'specialties' list, creates :Specialty nodes, and links them
    # 3. Unwinds the 'facilities' list, creates :Facility nodes, and links them
    query = """
    // 1. Create or merge the :Hospital node
    MERGE (h:Hospital {name: $record.name})
    SET 
        h.class = $record.class,
        // Create a spatial 'point' for the location
        h.location = point({
            latitude: $record.location.latitude, 
            longitude: $record.location.longitude
        })

    // 2. Process all specialties for this hospital
    WITH h, $record.specialties AS specialtyNames
    UNWIND specialtyNames AS specName
    MERGE (s:Specialty {name: specName})
    MERGE (h)-[:OFFERS_SPECIALTY]->(s)

    // 3. Process all facilities for this hospital
    // We use 'WITH DISTINCT h' to regroup back to the single hospital
    // after the previous UNWIND, so we don't create duplicate facilities.
    WITH DISTINCT h, $record.facilities AS facilityNames
    UNWIND facilityNames AS facName
    MERGE (f:Facility {name: facName})
    MERGE (h)-[:HAS_FACILITY]->(f)
    """
    tx.run(query, record=hospital_record)

# 4. Main function to drive the script
def main():
    
    # --- File Path Logic ---
    # Gets the directory where this script is running (e.g., /.../src/)
    script_dir = os.path.dirname(__file__) 
    # Goes up one level (to the project root) and then into 'data/hospital.json'
    json_file_path = os.path.join(script_dir, '..', 'data', 'hospital.json')
    # This makes the path robust: ../data/hospital.json
    # ---
    
    # Load the JSON data
    try:
        with open(json_file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
            # Access the list inside the 'hospitals' key
            hospital_list = data.get('hospitals')
            if hospital_list is None:
                print("Error: 'hospitals' key not found in JSON file.")
                return
                
    except FileNotFoundError:
        print(f"Error: hospital.json not found at path: {json_file_path}")
        print("Please make sure your 'hospital.json' file is inside the 'data' folder.")
        return
    except json.JSONDecodeError as e:
        print(f"Error: Could not decode hospital.json. Check for syntax errors (like trailing commas).")
        print(f"Details: {e}")
        return

    # Connect to Neo4j
    driver = None
    try:
        driver = GraphDatabase.driver(URI, auth=AUTH)
        driver.verify_connectivity()
        print(f"Connected to Neo4j at {URI} successfully.")
        
        # Create a session
        with driver.session(database="neo4j") as session:
            # Run the constraint setup first
            try:
                session.execute_write(create_constraints_and_indexes)
                print("Constraints and indexes are set.")
            except Exception as e:
                print(f"Warning: Could not set constraints. (This might be fine if they exist). {e}")

            # Loop through each hospital in the JSON list and load it
            print(f"Starting to load {len(hospital_list)} hospitals...")
            count = 0
            for hospital_obj in hospital_list:
                session.execute_write(create_hospital_graph, hospital_obj)
                count += 1
                print(f"  Loaded: {hospital_obj.get('name')}")
            
            print(f"\nSuccessfully loaded data for {count} hospitals.")

    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        if driver:
            driver.close()
            print("Connection to Neo4j closed.")

if __name__ == "__main__":
    main()