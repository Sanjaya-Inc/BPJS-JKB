from langchain_community.vectorstores import Neo4jVector
from langchain_openai import OpenAIEmbeddings
from neo4j import GraphDatabase

URI = "neo4j://localhost:7687"
AUTH = ("neo4j", "12345678")

def clear_existing_indices():
    """Clear all existing vector indices and embedding properties"""
    print("🧹 Clearing existing vector indices...")
    
    # Connect to Neo4j
    driver = GraphDatabase.driver(URI, auth=AUTH)
    
    try:
        with driver.session() as session:
            # Drop existing vector indices
            index_names = [
                "diagnosis_rules_index",
                "procedure_concept_index", 
                "doctor_entity_index",
                "hospital_entity_index"
            ]
            
            for index_name in index_names:
                try:
                    session.run(f"DROP INDEX {index_name} IF EXISTS")
                    print(f"   - Dropped index: {index_name}")
                except Exception as e:
                    print(f"   - Index {index_name} doesn't exist or already dropped")
            
            # Remove embedding properties from all nodes to ensure clean state
            session.run("MATCH (n) WHERE n.embedding IS NOT NULL REMOVE n.embedding")
            print("   - Cleared all embedding properties from nodes")
            
    finally:
        driver.close()
    
    print("✅ Index clearing complete!")

def create_all_indices():
    # Clear existing indices first
    
    embedding_model = OpenAIEmbeddings(
        model="text-embedding-qwen3-embedding-4b",                  # Must match the model name loaded in your local server
        openai_api_base="http://127.0.0.1:1234/v1", # Point to local server
        openai_api_key="",        # usually 'lm-studio' or 'dummy' for local
        check_embedding_ctx_length=False   # Important: Prevents errors with some local models
    )
    
    config = [
        # Index Name              # Node Label    # Properties to Embed
        ("diagnosis_rules_index", "Diagnosis",    ["name", "code"]),
        ("procedure_concept_index","Procedure",   ["name", "code"]),
        ("doctor_entity_index",   "Doctor",       ["name", "id"]),
        ("hospital_entity_index", "Hospital",     ["name", "id"])
    ]

    print("⏳ Starting Index Creation...")
    for index_name, label, props in config:
        print(f"   - Indexing {label}...")
        Neo4jVector.from_existing_graph(
            embedding=embedding_model,
            url=URI,
            username=AUTH[0],
            password=AUTH[1],
            index_name=index_name,
            node_label=label,
            text_node_properties=props,
            embedding_node_property="embedding",
        )
    print("✅ All Indices Created!")

if __name__ == "__main__":
    clear_existing_indices()
    create_all_indices()
