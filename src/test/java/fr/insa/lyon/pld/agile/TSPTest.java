package fr.insa.lyon.pld.agile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.insa.lyon.pld.agile.tsp.TSP;
import fr.insa.lyon.pld.agile.tsp.TSP1;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

public class TSPTest {
    private static TSP tsp;
    private static TSP tsp1;

    @BeforeEach
    public void beforeEachTest() {
        tsp = new TSP1();
        tsp1 = new TSP1();
    }

    @Test
    public void testHeuristic() {
        int nb=10;
        int[][] cost = generateRandomCost(nb);
        int[] durr = generateRandomDurr(nb);
        tsp.chercheSolution(100000, nb, cost, durr);
        int val = tsp.getCoutMeilleureSolution();

        tsp1.disableBound();
        tsp1.chercheSolution(100000, nb, cost, durr);
        int val1 = tsp1.getCoutMeilleureSolution();

        assertEquals(val,val1);
    }
    
    @Test
    public void testSingleValue() {
        int nb=1;

	int[][] cost = generateRandomCost(nb);
	int[] durr = generateRandomDurr(nb);

        tsp.chercheSolution(100000, nb, cost, durr);
        int val = tsp.getCoutMeilleureSolution();

        tsp1.disableBound();
        tsp1.chercheSolution(100000, nb, cost, durr);
        int val1 = tsp1.getCoutMeilleureSolution();

        assertEquals(val,val1);     
    }

    @Test
    public void testEmptyValue() {
        int nb=0;

	int[][] cost = generateRandomCost(nb);
	int[] durr = generateRandomDurr(nb);

        tsp.chercheSolution(100000, nb, cost, durr);
        int val = tsp.getCoutMeilleureSolution();

        tsp1.disableBound();
        tsp1.chercheSolution(100000, nb, cost, durr);
        int val1 = tsp.getCoutMeilleureSolution();

        assertEquals(val,val1);      
    }

    private int[][] generateRandomCost(int nb){
        int[][] cost = new int[nb][nb];
        Random rand = new Random();

        for(int i=0;i<nb;++i){
            for(int j=0;j<nb;++j){
                cost[i][j]=rand.nextInt(1000);
            }
        }
        return cost;
    }

    private int[] generateRandomDurr(int nb){
        int[] durr = new int[nb];
        Random rand = new Random();
        for(int i=0;i<nb;++i){
            durr[i]=rand.nextInt(1000);
        }
        return durr;
    }
}
