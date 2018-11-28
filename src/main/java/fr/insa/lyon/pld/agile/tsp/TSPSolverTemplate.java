package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class TSPSolverTemplate implements TSPSolver {

    private Integer[] bestPath;
    private Integer bestPathCost = 0;
    private Boolean timeLimitExceeded = false;
    
    @Override
    public final Boolean getTimeLimitExceeded() {
        return timeLimitExceeded;
    }

    public final void solve(int maximumTimeLimit, int nodes, int[][] edgesCosts, int[] nodesCosts) {
        timeLimitExceeded = false;
        long startTime = System.currentTimeMillis();
        
        // Base case
        if(nodes == 0) {
            bestPathCost = 0;
            return;
        }

        // At the beginning all nodes are unexplored
        ArrayList<Integer> unexploredNodes = new ArrayList<Integer>();
        for (int i = 1; i < nodes; i++) {
            unexploredNodes.add(i);
        }
    
        bestPathCost = startBound(unexploredNodes, edgesCosts, nodesCosts);
        bestPath = new Integer[nodes];

        // Start exploring from node 0
        ArrayList<Integer> exploredNodes = new ArrayList<Integer>(nodes);
        exploredNodes.add(0);
        branchAndBound(0, unexploredNodes, exploredNodes, 0, edgesCosts, nodesCosts,
                System.currentTimeMillis(), startTime);
        
        System.out.println("TIME = " + (System.currentTimeMillis() - startTime));
    }
    
    @Override
    public Integer getBestNode(int i) {
        if ((bestPath == null) || (i < 0) || (i >= bestPath.length)) {
            return null;
        }
        return bestPath[i];
    }
    
    @Override
    public Integer getBestCost() {
        return bestPathCost;
    }

    /**
     * This method computes a lower bound of branch costs to allow early branch cutting during the exploration.
     * This method must be overriden by subclasses.
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     * @return a lower bound for the cost of valid permutations of unexploredNodes starting with currentNode and ending with node 0
     */
    protected abstract int bound(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts);
    
    /**
     * 
     * */
    protected abstract int startBound(ArrayList<Integer> nonVus, int[][] cout, int[] duree);
    
    /**
     * This method must be overriden by subclasses.
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     * @return an iterator over the unexplored nodes
     */
    protected abstract Iterator<Integer> iterator(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts);

    /**
     * This method defines the template of a Branch And Bound algorithm for the TSP.
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param exploredNodes the list of the nodes that have already been explored (including currentNode)
     * @param currentCost the total cost of the explored nodes and edges so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     * @param startTime the time when the computation started
     * @param maximumTimeLimit the maximum amount of time allowed for the computation (in milliseconds)
     */
    private final void branchAndBound(int currentNode,
            ArrayList<Integer> unexploredNodes, ArrayList<Integer> exploredNodes,
            int currentCost, int[][] edgesCosts, int[] nodesCosts,
            long startTime, long maximumTimeLimit) {
        
        // Early exit: time limit exceeded
        if (System.currentTimeMillis() - startTime > maximumTimeLimit) {
            timeLimitExceeded = true;
            return;
        }
        
        // All nodes have been explored
        if (unexploredNodes.size() == 0) {
            // Complete the loop
            currentCost += edgesCosts[currentNode][0];
            // This is the best solution so far
            if (currentCost < bestPathCost) {
                exploredNodes.toArray(bestPath);
                bestPathCost = currentCost;
            }
        } else if (currentCost + bound(currentNode, unexploredNodes, edgesCosts, nodesCosts) < bestPathCost) {
            // Choose an order to iterate over the unexplored nodes
            Iterator<Integer> it = iterator(currentNode, unexploredNodes, edgesCosts, nodesCosts);
            while (it.hasNext()) {
                Integer nextNode = it.next();
                exploredNodes.add(nextNode);
                unexploredNodes.remove(nextNode);
                branchAndBound(nextNode, unexploredNodes, exploredNodes, currentCost
                        + edgesCosts[currentNode][nextNode] + nodesCosts[nextNode],
                        edgesCosts, nodesCosts, startTime, maximumTimeLimit);
                exploredNodes.remove(nextNode);
                unexploredNodes.add(nextNode);
            }
        }
    }

}
