package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import java.util.Iterator;

public class TSPSolverImplementation0 extends TSPSolverWorkerTemplate {

    public TSPSolverImplementation0() {
        super();
    }
    
    public TSPSolverImplementation0(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        super(nodes, edgesCosts, nodesCosts);
    }

    @Override
    protected Iterator<Integer> iterator(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return new IteratorSeq(unexploredNodes);
    }
    
    @Override
    protected int startBound(ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return Integer.MAX_VALUE;
    }
    
    @Override
    protected int bound(Integer currentNode, ArrayList<Integer> unexploredNodes, int[][] edgesCosts, int[] nodesCosts) {
        return 0;
    }

}
