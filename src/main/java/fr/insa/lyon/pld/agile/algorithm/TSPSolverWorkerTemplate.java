package fr.insa.lyon.pld.agile.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TSPSolverWorkerTemplate extends TSPSolverWorker {   
    private ArrayList<Integer> bestPath = null;
    private Integer bestPathCost = 0;
    private Double progress = 0.0;

    /**
     * Default constructor
     */
    public TSPSolverWorkerTemplate() {
        super();
    }
    
    /**
     * Constructor
     * 
     * @param nodes the number of nodes to process
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
     */
    public TSPSolverWorkerTemplate(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        super(nodes, edgesCosts, nodesCosts);
    }
    
    /**
     * This method solves the TSP
     * 
     * @return the best path for the TSP
     */
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
        progress = 0.0;

        // Start exploring from node 0
        ArrayList<Integer> exploredNodes = new ArrayList<>(nodes);
        exploredNodes.add(0);
        branchAndBound(0, unexploredNodes, exploredNodes, 0, edgesCosts, nodesCosts);
        return bestPath;
    }

    /** 
     * After the solve method was called, a call to getBestCost will return the cost of the best solution computed by solve
     * 
     * @return the cost of the solution computed by solve
     */
    public Integer getBestCost() {
        return bestPathCost;
    }
    
    /**
     * This method computes a lower bound of branch costs to allow early branch cutting during the exploration.
     * This method must be overriden by subclasses.
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
     * @return a lower bound for the cost of valid permutations of unexploredNodes starting with currentNode and ending with node 0
     */
    protected abstract int bound(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts);
    
    /**
     * This method computes a lower bound of branch costs to allow early branch cutting before the exploration.
     * 
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
     * */
    protected abstract int startBound(ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts);
    
    /**
     * This method must be overriden by subclasses.
     * 
     * @param currentNode the last explored node
     * @param unexploredNodes the list of the nodes that have not been explored so far
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
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
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
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
            if(exploredNodes.size() <= 3) {
                updateProgress(exploredNodes.size() - 1, unexploredNodes.size());
            }
            
            // Complete the loop
            currentCost += edgesCosts[currentNode][0];
            // This is the best solution so far
            if (currentCost < bestPathCost) {
                bestPath = new ArrayList<>(exploredNodes);
                bestPathCost = currentCost;
                publish(bestPath);
            }
        } else if (currentCost + bound(currentNode, unexploredNodes, edgesCosts, nodesCosts) < bestPathCost) {
            if(exploredNodes.size() == 3) {
                updateProgress(exploredNodes.size() - 1, unexploredNodes.size());
            }
            
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
        } else if(exploredNodes.size() <= 3) {
            updateProgress(exploredNodes.size() - 1, unexploredNodes.size());
        }
    }
    
    /**
     * This method update the best intermediate path during the TSP computation
     * 
     * @param list the current best path
     */
    @Override
    protected void process(List<ArrayList<Integer>> list) {
        List<Integer> bestPath = list.get(list.size()-1);
        firePropertyChange("intermediateBestPath", null, bestPath);
    }
    
    /**
     * This method updates the progress during the TSP computation
     * 
     * @param exploredNodesCount number of explored nodes in the current state of the computation
     * @param unexploredNodesCount number of unexplored nodes in the current state of the computation
     */
    protected void updateProgress(int exploredNodesCount, int unexploredNodesCount) {
        if (exploredNodesCount <= 0) {
            return;
        }
        
        long statesCount = 1;
        for(int k = 0; k < exploredNodesCount; k++) {
            statesCount *= (exploredNodesCount + unexploredNodesCount - k);
        }
        
        double newProgress = progress + 1.0 / statesCount;
        setProgress((int)(newProgress*100));
        progress = newProgress;
    }
}
