package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author paul
 */
public abstract class TSPSolverWorkerTemplate extends TSPSolverWorker {   
    private ArrayList<Integer> bestPath = null;
    private Integer bestPathCost = 0;

    public TSPSolverWorkerTemplate() {
        super();
    }
    
    public TSPSolverWorkerTemplate(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        super(nodes, edgesCosts, nodesCosts);
    }

    public final ArrayList<Integer> solve() {
        // Base case
        if(nodes == 0) {
            bestPathCost = 0;
            return new ArrayList<>();
        }

        // At the beginning all nodes are unexplored
        ArrayList<Integer> unexploredNodes = new ArrayList<>();
        for (int i = 1; i < nodes; i++) {
            unexploredNodes.add(i);
        }
    
        bestPathCost = startBound(unexploredNodes, edgesCosts, nodesCosts);
        bestPath = null;

        // Start exploring from node 0
        ArrayList<Integer> exploredNodes = new ArrayList<>(nodes);
        exploredNodes.add(0);
        branchAndBound(0, unexploredNodes, exploredNodes, 0, edgesCosts, nodesCosts);
        return bestPath;
    }

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
    protected abstract int startBound(ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts);
    
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
     */
    private final void branchAndBound(int currentNode,
            ArrayList<Integer> unexploredNodes, ArrayList<Integer> exploredNodes,
            int currentCost, int[][] edgesCosts, int[] nodesCosts) {
        
        // Early exit
        if (isCancelled()) {
            return;
        }
        
        // All nodes have been explored
        if (unexploredNodes.isEmpty()) {
            // Complete the loop
            currentCost += edgesCosts[currentNode][0];
            // This is the best solution so far
            if (currentCost < bestPathCost) {
                bestPath = new ArrayList<>(exploredNodes);
                bestPathCost = currentCost;
                publish(bestPath);
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
                        edgesCosts, nodesCosts);
                exploredNodes.remove(nextNode);
                unexploredNodes.add(nextNode);
            }
        }
    }

    @Override
    protected void process(List<ArrayList<Integer>> list) {
        List<Integer> bestPath = list.get(list.size()-1);
        firePropertyChange("intermediateBestPath", null, bestPath);
    }

    @Override
    protected void done() {
        firePropertyChange("finalBestPath", null, bestPath);
    }
}
