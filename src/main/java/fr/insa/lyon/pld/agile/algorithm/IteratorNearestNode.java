package fr.insa.lyon.pld.agile.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class IteratorNearestNode implements Iterator<Integer> {

    private final Integer[] nodes;
    private Integer numberOfNodes;

    /**
     * Creates an iterator to iterate over a set of unexplored nodes.
     * 
     * The nodes will be iterated over in an order such that the cost between two consecutive nodes is locally minimized.
     * 
     * @param unexploredNodes the set of unexplored nodes
     */
    public IteratorNearestNode(Collection<Integer> unexploredNodes, int currentNode, int[][] edgesCosts, int[] nodesCost) {
        nodes = new Integer[unexploredNodes.size()];
        numberOfNodes = 0;
        
        // Set of temporary unexplored nodes
        ArrayList<Integer> candidateNodes = new ArrayList<Integer>(unexploredNodes);
        
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
            
            // Update the current path and the set of unexplored nodes
            currentNode = nextNode;
            nodes[numberOfNodes++] = nextNode;
            candidateNodes.remove(nextNode);
        }
    }

    @Override
    public boolean hasNext() {
        return numberOfNodes > 0;
    }

    @Override
    public Integer next() {
        return nodes[--numberOfNodes];
    }

    @Override
    public void remove() {}

}


