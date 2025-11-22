import logging
import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from neo4j import GraphDatabase
from chatbot.src.config import NEO4J_URI, NEO4J_AUTH

logger = logging.getLogger(__name__)

class Neo4jDatabase:
    def __init__(self):
        self._driver = None

    def connect(self):
        """Initialize the Neo4j driver."""
        try:
            self._driver = GraphDatabase.driver(NEO4J_URI, auth=NEO4J_AUTH)
            logger.info("Connected to Neo4j Database.")
        except Exception as e:
            logger.error(f"Failed to connect to Neo4j: {e}")
            raise

    def close(self):
        """Close the Neo4j driver."""
        if self._driver:
            self._driver.close()
            logger.info("Neo4j connection closed.")

    def get_session(self):
        """Helper to get a session. Useful for dependency injection."""
        if not self._driver:
            self.connect()
        return self._driver.session()

db = Neo4jDatabase()
