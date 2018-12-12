package fr.insa.lyon.pld.agile.algorithm;

import java.util.ArrayList;
import java.util.Iterator;

public class TSPSolverImplementation2 extends TSPSolverWorkerTemplate {

    
    /**
     * Constructor
     */
    public TSPSolverImplementation2() {
        super();
    }
    
    /**
     * Constructor
     * 
     * @param nodes the number of nodes to process
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     */
    public TSPSolverImplementation2(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        super(nodes, edgesCosts, nodesCosts);
    }

    /**
     * Creates an iterator to iterate in a certain order over a set of unexplored nodes
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     * @return an iterator over the unexplored nodes
     */
    @Override
    protected Iterator<Integer> iterator(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return new IteratorNearestNode(unexploredNodes, currentNode, edgesCosts, nodesCosts);
    }
    
    /**
     * This method computes a lower bound of branch costs to allow early branch cutting before the exploration.
     * 
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     * */
    @Override
    protected int startBound(ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return Integer.MAX_VALUE;
    }
    
    /**
     * This method computes a lower bound of branch costs to allow early branch cutting during the exploration.
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     * @return a lower bound for the cost of valid permutations of unexploredNodes starting with currentNode and ending with node 0
     */
    @Override
    protected int bound(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        // The lower bound to be returned
        int lowerBound = 0;
        
        // The lower bound is at least the cost of the minimum incoming branch
        int minimumIncomingBranch = Integer.MAX_VALUE;
        for (int i = 0; i < unexploredNodes.size(); ++i) {
            if(edgesCosts[currentNode][unexploredNodes.get(i)] < minimumIncomingBranch) {
                minimumIncomingBranch = edgesCosts[currentNode][unexploredNodes.get(i)];
            }
        }
        lowerBound += minimumIncomingBranch;
        
        // On top of this, let's add the cost of the minimum outgoing branch
        for (int i = 0; i < unexploredNodes.size(); ++i) {
            int minimumOutGoingBranch = edgesCosts[unexploredNodes.get(i)][0];
            for (int j = 0; j < unexploredNodes.size(); ++j) {
                if (i != j) {
                    if (edgesCosts[unexploredNodes.get(i)][unexploredNodes.get(j)] < minimumOutGoingBranch) {
                        minimumOutGoingBranch = edgesCosts[unexploredNodes.get(i)][unexploredNodes.get(j)];
                    }
                }
            }
            lowerBound += nodesCosts[unexploredNodes.get(i)];
            lowerBound += minimumOutGoingBranch;
        }
        return lowerBound;
    }

}
