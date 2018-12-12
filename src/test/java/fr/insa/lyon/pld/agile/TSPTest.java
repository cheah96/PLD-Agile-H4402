package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.algorithm.TSPSolver;
import fr.insa.lyon.pld.agile.algorithm.TSPSolverImplementation1;
import fr.insa.lyon.pld.agile.algorithm.TSPSolverImplementation2;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TSPTest {
    private static TSPSolver tsp;
    private static TSPSolver tsp1;

    @BeforeEach
    public void beforeEachTest() {
        tsp = new TSPSolverImplementation1();
        tsp1 = new TSPSolverImplementation2();
    }

    @Test
    public void testHeuristic() {
        System.out.println("testHeuristics");
        int nb=20;
        int[][] cost = generateRandomCost(nb);
        int[] durr = generateRandomDurr(nb);
        
        tsp.solve(nb, cost, durr);
        int val = tsp.getBestCost();

        tsp1.solve(nb, cost, durr);
        int val1 = tsp1.getBestCost();

        assertEquals(val,val1);
    }
    
    @Test
    public void testSingleValue() {
        System.out.println("testSingleValue");
        int nb=1;

        int[][] cost = generateRandomCost(nb);
        int[] durr = generateRandomDurr(nb);

        tsp.solve(nb, cost, durr);
        int val = tsp.getBestCost();

        tsp1.solve(nb, cost, durr);
        int val1 = tsp1.getBestCost();

        assertEquals(val,val1);     
    }

    @Test
    public void testEmptyValue() {
        System.out.println("testEmptyValue");
        int nb=0;

        int[][] cost = generateRandomCost(nb);
        int[] durr = generateRandomDurr(nb);

        tsp.solve(nb, cost, durr);
        int val = tsp.getBestCost();

        tsp1.solve(nb, cost, durr);
        int val1 = tsp.getBestCost();

        assertEquals(val,val1);      
    }

    private int[][] generateRandomCost(int nb) {
        int[][] cost = new int[nb][nb];
        Random rand = new Random();

        for (int i=0;i<nb;++i) {
            for (int j=0;j<nb;++j) {
                cost[i][j]=rand.nextInt(1000);
            }
        }
        return cost;
    }

    private int[] generateRandomDurr(int nb) {
        int[] durr = new int[nb];
        Random rand = new Random();
        for (int i=0;i<nb;++i) {
            durr[i]=rand.nextInt(1000);
        }
        return durr;
    }
}
