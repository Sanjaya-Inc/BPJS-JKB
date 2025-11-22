import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from neo4j import GraphDatabase
from chatbot.src.config import NEO4J_URI, NEO4J_AUTH

URI = NEO4J_URI
AUTH = NEO4J_AUTH

class DiagnosisBenchmarkCalculator:
    def __init__(self, uri, auth):
        self.driver = GraphDatabase.driver(uri, auth=auth)

    def close(self):
        self.driver.close()

    def populate_market_benchmarks(self):
        """
        Populate market benchmark data for diagnosis nodes.
        CALCULATES market_avg_cost and market_std_dev from ACTUAL CLAIM HISTORY.
        """
        print("📊 Calculating real-world market benchmarks from Claims...")
        
        with self.driver.session() as session:
            # Update diagnosis nodes with calculated stats from actual claims
            result = session.run("""
                // 1. MATCH all claims with a valid cost
                MATCH (c:Claim)-[:CODED_AS]->(d:Diagnosis)
                WHERE c.total_cost IS NOT NULL AND c.total_cost > 0

                // 2. AGGREGATE: Calculate the Real Market Average and StdDev
                WITH d, 
                     avg(c.total_cost) AS calculated_avg, 
                     stDev(c.total_cost) AS calculated_std

                // 3. STORE: Save these real-world benchmarks
                SET d.market_avg_cost = calculated_avg
                SET d.market_std_dev = CASE 
                    WHEN calculated_std = 0 OR calculated_std IS NULL THEN calculated_avg * 0.15 
                    ELSE calculated_std 
                END
                RETURN count(d) as updated_count
            """)
            
            updated_count = result.single()['updated_count']
            print(f"   - Updated {updated_count} diagnosis nodes with calculated market benchmarks")
            
            if updated_count == 0:
                print("   ⚠️ Warning: No claims found to calculate benchmarks. Ensure claims are loaded first.")
            
        print("✅ Market benchmark calculation complete!")

    def calculate_diagnosis_benchmarks(self):
        """
        Execute the main Cypher query to calculate z-scores and outlier status for claims.
        """
        print("🧮 Calculating diagnosis benchmarks and z-scores...")
        
        cypher_query = """
        // 1. MATCH claims and their (now updated) Diagnosis benchmarks
        MATCH (c:Claim)-[:CODED_AS]->(d:Diagnosis)
        WHERE d.market_avg_cost IS NOT NULL AND c.total_cost IS NOT NULL

        // 2. CALCULATE the Z-Score
        // Formula: (Claim Cost - Market Avg) / Market StdDev
        WITH c, d, (c.total_cost - d.market_avg_cost) / d.market_std_dev AS z_score_val

        // 3. STORE the Score and a Human-Readable Status on the Claim node
        SET c.z_score = z_score_val
        SET c.outlier_status = CASE
            WHEN z_score_val > 2.0 THEN "HIGH_OUTLIER"    // > 2 Sigma (Flag for Fraud)
            WHEN z_score_val < -2.0 THEN "LOW_OUTLIER"   // < -2 Sigma (Suspiciously Low)
            ELSE "NORMAL_VARIANCE"
        END
        RETURN count(c) as processed_claims
        """
        
        with self.driver.session() as session:
            # First check how many eligible claims exist
            count_result = session.run("""
                MATCH (c:Claim)-[:CODED_AS]->(d:Diagnosis)
                WHERE d.market_avg_cost IS NOT NULL AND c.total_cost IS NOT NULL
                RETURN count(c) as eligible_claims
            """)
            
            eligible_claims = count_result.single()['eligible_claims']
            print(f"   - Found {eligible_claims} claims eligible for z-score calculation")
            
            if eligible_claims == 0:
                print("   ⚠️ Warning: No claims found with required market benchmark data")
                return
            
            # Execute the main calculation query
            result = session.run(cypher_query)
            processed_claims = result.single()['processed_claims']
            
            print(f"   - Processed {processed_claims} claims with z-scores and outlier status")
            
            # Get summary statistics
            stats_result = session.run("""
                MATCH (c:Claim)
                WHERE c.outlier_status IS NOT NULL
                RETURN 
                    c.outlier_status as status,
                    count(c) as count
                ORDER BY c.outlier_status
            """)
            
            print("   - Outlier Status Summary:")
            for record in stats_result:
                status = record['status']
                count = record['count']
                print(f"     • {status}: {count} claims")
        
        print("✅ Diagnosis benchmark calculation complete!")

    def verify_calculation_results(self):
        """
        Verify the calculation results and provide sample data for validation.
        """
        print("🔍 Verifying calculation results...")
        
        with self.driver.session() as session:
            # Sample some results for verification
            sample_result = session.run("""
                MATCH (c:Claim)-[:CODED_AS]->(d:Diagnosis)
                WHERE c.z_score IS NOT NULL
                RETURN 
                    c.id as claim_id,
                    c.total_cost as claim_cost,
                    d.name as diagnosis_name,
                    d.market_avg_cost as market_avg,
                    d.market_std_dev as market_std,
                    c.z_score as z_score,
                    c.outlier_status as status
                ORDER BY abs(c.z_score) DESC
                LIMIT 5
            """)
            
            print("   - Sample calculation results (Top 5 by absolute z-score):")
            for record in sample_result:
                claim_id = record['claim_id']
                claim_cost = record['claim_cost']
                diagnosis = record['diagnosis_name']
                market_avg = record['market_avg']
                z_score = round(record['z_score'], 2)
                status = record['status']
                
                print(f"     • Claim {claim_id}: {diagnosis}")
                print(f"       Cost: {claim_cost:,} vs Market: {market_avg:,}")
                print(f"       Z-Score: {z_score} → {status}")
        
        print("✅ Verification complete!")

def main():
    """
    Main execution function that orchestrates the entire benchmark calculation process.
    """
    try:
        print("🚀 Starting Diagnosis Benchmark Calculation Process...")
        print("=" * 60)
        
        calculator = DiagnosisBenchmarkCalculator(URI, AUTH)
        
        # Step 1: Populate market benchmark data
        calculator.populate_market_benchmarks()
        print()
        
        # Step 2: Calculate z-scores and outlier status
        calculator.calculate_diagnosis_benchmarks()
        print()
        
        # Step 3: Verify results
        calculator.verify_calculation_results()
        print()
        
        print("=" * 60)
        print("✅ Diagnosis Benchmark Calculation Process Complete!")
        print()
        print("📋 Summary:")
        print("   - Market benchmarks populated for all diagnoses")
        print("   - Z-scores calculated for all eligible claims")
        print("   - Outlier status assigned based on z-score thresholds:")
        print("     • HIGH_OUTLIER: Z-score > 2.0 (potential fraud)")
        print("     • LOW_OUTLIER: Z-score < -2.0 (suspiciously low)")
        print("     • NORMAL_VARIANCE: Z-score between -2.0 and 2.0")
        
        calculator.close()
        
    except Exception as e:
        print(f"\n❌ Error during benchmark calculation: {e}")
        print("Make sure Neo4j is running and the previous data loading scripts have been executed.")
        print("\nPrerequisites:")
        print("1. python3 data/upsert_initial_data.py")
        print("2. python3 data/setup_indicies.py")
        print("3. python3 data/calculate_diagnosis_benchmarks.py  (this script)")

if __name__ == "__main__":
    main()
