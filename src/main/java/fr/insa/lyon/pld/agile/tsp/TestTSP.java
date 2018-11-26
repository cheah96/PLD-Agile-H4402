package fr.insa.lyon.pld.agile.tsp;

import java.util.Random;

public class TestTSP {
    public static void main(String[] args) {
	if(testTSP()){
	    System.out.println("true");
	}
	else {
	    System.out.println("false");
	}
    }
    public static boolean testTSP() {

	if(!testHeuristic()) {
	    return false;
	}
	/*if(!testBogusCostDuration()) {
	    return false;
	}
	if(!testSingleValue()) {
	    return false;
	}
	if(!testEmptyValue()) {
	    return false;
	}*/
	return true;
    }
    public static boolean testHeuristic() {
	int nb=10;

	int[][] cost = generateRandomCost(nb);
	int[] durr = generateRandomDurr(nb);

	TSP tsp = new TSP1();
	tsp.chercheSolution(100000, nb, cost, durr);
	int val = tsp.getCoutMeilleureSolution();

	TSP tsp1 = new TSP1();
	tsp1.disableBound();
	tsp1.chercheSolution(100000, nb, cost, durr);
	int val1 = tsp1.getCoutMeilleureSolution();
	
	System.out.println("val="+val);
	System.out.println("val1="+val1);
	
	if(val!=val1) {
	    return false;
	}

	return true;
    }

    public static boolean testBogusCostDuration() {
	int nb=10;

	int[][] cost = generateBogusCost(nb);
	int[] durr = generateBogusDurr(nb);

	TSP tsp = new TSP1();
	tsp.chercheSolution(100000, nb, cost, durr);
	int val = tsp.getCoutMeilleureSolution();

	TSP tsp1 = new TSP1();
	tsp1.disableBound();
	tsp1.chercheSolution(100000, nb, cost, durr);
	int val1 = tsp1.getCoutMeilleureSolution();

	if(val!=val1) {
	    return false;
	}

	return true;
    }
    public static boolean testSingleValue() {
	int nb=1;

	int[][] cost = generateBogusCost(nb);
	int[] durr = generateBogusDurr(nb);

	TSP tsp = new TSP1();
	tsp.chercheSolution(100000, nb, cost, durr);
	int val = tsp.getCoutMeilleureSolution();

	TSP tsp1 = new TSP1();
	tsp1.disableBound();
	tsp1.chercheSolution(100000, nb, cost, durr);
	int val1 = tsp1.getCoutMeilleureSolution();

	if(val!=val1) {
	    return false;
	}

	return true;
    }

    public static boolean testEmptyValue() {
	int nb=0;

	int[][] cost = generateBogusCost(nb);
	int[] durr = generateBogusDurr(nb);

	TSP tsp = new TSP1();
	tsp.chercheSolution(100000, nb, cost, durr);
	int val = tsp.getCoutMeilleureSolution();

	TSP tsp1 = new TSP1();
	tsp1.disableBound();
	tsp1.chercheSolution(100000, nb, cost, durr);
	int val1 = tsp1.getCoutMeilleureSolution();

	if(val!=val1) {
	    return false;
	}

	return true;
    }

    public static int[][] generateRandomCost(int nb){
	int[][] cost = new int[nb][nb];
	Random rand = new Random();

	for(int i=0;i<nb;++i){
	    for(int j=0;j<nb;++j){
		cost[i][j]=rand.nextInt(1000);
	    }
	}
	return cost;
    }

    public static int[] generateRandomDurr(int nb){
	int[] durr = new int[nb];
	Random rand = new Random();
	for(int i=0;i<nb;++i){
	    durr[i]=rand.nextInt(1000);
	}
	return durr;
    }

    public static int[][] generateBogusCost(int nb){
	int[][] cost = new int[nb][nb];
	Random rand = new Random();

	for(int i=0;i<nb;++i){
	    for(int j=0;j<nb;++j){
		int randomInt = rand.nextInt(4);
		if(randomInt==0) {
		    cost[i][j]=rand.nextInt(2000)-1000;
		}
		else if(randomInt==1) {
		    cost[i][j]=Integer.MAX_VALUE;
		}
		else if(randomInt==2) {
		    cost[i][j]=Integer.MIN_VALUE;
		}
	    }
	}
	return cost;
    }

    public static int[] generateBogusDurr(int nb){
	int[] durr = new int[nb];
	Random rand = new Random();
	for(int i=0;i<nb;++i){
	    int randomInt = rand.nextInt(4);
	    if(randomInt==0) {
		durr[i]=rand.nextInt(2000)-1000;
	    }
	    else if(randomInt==1) {
		durr[i]=Integer.MAX_VALUE;
	    }
	    else if(randomInt==2) {
		durr[i]=Integer.MIN_VALUE;
	    }
	}
	return durr;
    }

}
