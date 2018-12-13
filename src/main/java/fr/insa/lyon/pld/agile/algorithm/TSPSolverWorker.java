package fr.insa.lyon.pld.agile.algorithm;

import java.util.ArrayList;
import javax.swing.SwingWorker;

public abstract class TSPSolverWorker extends SwingWorker<ArrayList<Integer>, ArrayList<Integer>> implements TSPSolver {
    protected int nodes;
    protected int[][] edgesCosts;
    protected int[] nodesCosts;

    /**
     * Default constructor
     */
    public TSPSolverWorker() {
        this(0, null, null);
    }
    
    /**
     * Constructor
     * 
     * @param nodes number of nodes in the TSP graph
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
     */
    public TSPSolverWorker(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        this.nodes = nodes;
        this.edgesCosts = edgesCosts;
        this.nodesCosts = nodesCosts;
    }

    /**
     * This method sets the number of nodes in the TSP
     * 
     * @param nodes the number of nodes
     */
    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    /**
     * This method sets the edges costs for the TSP
     * 
     * @param edgesCosts the edges costs
     */
    public void setEdgesCosts(int[][] edgesCosts) {
        this.edgesCosts = edgesCosts;
    }

    /**
     * This method sets the nodes costs for the TSP
     * 
     * @param nodesCosts the nodes costs
     */
    public void setNodesCosts(int[] nodesCosts) {
        this.nodesCosts = nodesCosts;
    }
    
    /**
     * This method is called in a background process by the SwingWorker execute method
     */
    @Override
    protected ArrayList<Integer> doInBackground() throws Exception {
        return solve();
    }
    
    /**
     * This method solves a TSP.
     * 
     * @param nodes number of nodes in the TSP graph
     * @param edgesCosts edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 &le; i &lt; nodes and 0 &le; j &lt; nodes
     * @param nodesCosts nodesCosts[i] is the time spent visiting node i, such that 0 &le; i &lt; nodes
     * @return the best path for this TSP
     */
    @Override
    public final ArrayList<Integer> solve(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        this.nodes = nodes;
        this.edgesCosts = edgesCosts;
        this.nodesCosts = nodesCosts;
        return solve();
    }
    
    /**
     * This method solves the TSP.
     * This method must be overriden by subclasses.
     * 
     * @return the best path for the TSP.
     */
    public abstract ArrayList<Integer> solve();
}
