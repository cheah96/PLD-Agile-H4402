package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import java.util.Iterator;

public class TSPSolverImplementation2 extends TSPSolverTemplate {

    @Override
    protected Iterator<Integer> iterator(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return new IteratorNearestNode(unexploredNodes, currentNode, edgesCosts, nodesCosts);
    }
    
    @Override
    protected int startBound(ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return Integer.MAX_VALUE;
    }
    
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
