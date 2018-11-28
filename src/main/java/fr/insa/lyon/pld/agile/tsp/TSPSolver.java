package fr.insa.lyon.pld.agile.tsp;

public interface TSPSolver {

    /**
     * Computes the shortest possible path such that every node of index i in the range [0, nodes-1] is visited exactly once.
     * 
     * @param timeLimit maximum execution time limit for the solve method (in milliseconds)
     * @param nodes number of nodes in the TSP graph
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     */
    public void solve(int maximumTimeLimit, int nodes, int[][] edgesCosts, int[] nodesCost);
    
    /**
     * @return true if solve returned prematurely because the Time Limit was reached before the entire search space could be explored
     */
    public Boolean getTimeLimitExceeded();

    /**
     * @param i the index of a node in the best round computed by solve
     * @return the node at the i-th position in the best round computed by solve
     */
    public Integer getBestNode(int i);

    /** 
     * @return the cost (in minutes) of the solution computed by solve
     */
    public Integer getBestCost();
}
