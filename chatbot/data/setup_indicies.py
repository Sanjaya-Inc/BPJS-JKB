from langchain_community.vectorstores import Neo4jVector
from langchain_openai import OpenAIEmbeddings

URI = "neo4j://localhost:7687"
AUTH = ("neo4j", "12345678")

def create_all_indices():
    embedding = OpenAIEmbeddings()
    
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
            embedding=embedding,
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
    create_all_indices()