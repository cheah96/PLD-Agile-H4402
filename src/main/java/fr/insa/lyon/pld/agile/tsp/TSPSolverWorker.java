package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import javax.swing.SwingWorker;

/**
 *
 * @author paul
 */
public abstract class TSPSolverWorker extends SwingWorker<ArrayList<Integer>, ArrayList<Integer>> implements TSPSolver {
    protected int nodes;
    protected int[][] edgesCosts;
    protected int[] nodesCosts;

    public TSPSolverWorker() {
        this(0, null, null);
    }
    
    public TSPSolverWorker(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        this.nodes = nodes;
        this.edgesCosts = edgesCosts;
        this.nodesCosts = nodesCosts;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public void setEdgesCosts(int[][] edgesCosts) {
        this.edgesCosts = edgesCosts;
    }

    public void setNodesCosts(int[] nodesCosts) {
        this.nodesCosts = nodesCosts;
    }
    
    @Override
    protected ArrayList<Integer> doInBackground() throws Exception {
        return solve();
    }
    
    @Override
    public final ArrayList<Integer> solve(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        this.nodes = nodes;
        this.edgesCosts = edgesCosts;
        this.nodesCosts = nodesCosts;
        return solve();
    }
    
    public abstract ArrayList<Integer> solve();
}
