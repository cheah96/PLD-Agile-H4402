package fr.insa.lyon.pld.agile.algorithm;

import java.util.ArrayList;

public interface TSPSolver {

    /**
     * Computes the shortest possible path such that every node of index i in the range [0, nodes-1] is visited exactly once.
     * 
     * @param nodes number of nodes in the TSP graph
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
     * @return the shortest possible path visiting all the nodes
     */
    public ArrayList<Integer> solve(int nodes, int[][] edgesCosts, int[] nodesCosts);

    /** 
     * After the solve method was called, a call to getBestCost will return the cost of the best solution computed by solve
     * 
     * @return the cost of the solution computed by solve
     */
    public Integer getBestCost();
}
