package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import java.util.Iterator;

public class TSPSolverImplementation1 extends TSPSolverWorkerTemplate {

    public TSPSolverImplementation1() {
        super();
    }
    
    public TSPSolverImplementation1(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        super(nodes, edgesCosts, nodesCosts);
    }

    @Override
    protected Iterator<Integer> iterator(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return new IteratorSeq(unexploredNodes);
    }
    
    @Override
    protected int startBound(ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        // The start lower bound to be returned
        int lowerBound = 0;
        
        // Set of temporary unexplored nodes
        ArrayList<Integer> candidateNodes = new ArrayList<Integer>(unexploredNodes);
        
        // First node to be explored
        Integer currentNode = 0;
        
        // Greedily compute the best path exploring each node exactly once
        while (!candidateNodes.isEmpty()) {
            // Find the next unexplored node that has the lowest cost from the current node
            int minCost = edgesCosts[currentNode][candidateNodes.get(0)];
            Integer nextNode = candidateNodes.get(0);
            for (int i = 1; i < candidateNodes.size(); ++i) {
                if (edgesCosts[currentNode][candidateNodes.get(i)] < minCost) {
                    minCost = edgesCosts[currentNode][candidateNodes.get(i)];
                    nextNode = candidateNodes.get(i);
                }
            }
            
            // Update the lower bound
            lowerBound += edgesCosts[currentNode][nextNode] + nodesCosts[nextNode];
            
            // Update the set of unexplored nodes
            currentNode = nextNode;
            candidateNodes.remove(nextNode);
        }
        
        lowerBound += edgesCosts[currentNode][0];
        return lowerBound;
    }
    
    @Override
    protected int bound(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        // The lower bound to be returned
        int lowerBound = 0;
        
        // The lower bound is at least the cost of the minimum incoming branch
        int minimumIncomingBranch = Integer.MAX_VALUE;
        for(int i = 0; i < unexploredNodes.size(); ++i) {
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
                    if(edgesCosts[unexploredNodes.get(i)][unexploredNodes.get(j)] < minimumOutGoingBranch) {
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
