package fr.insa.lyon.pld.agile.algorithm;

public abstract class TSPSolverFactory {
    
    public static TSPSolverWorkerTemplate getSolver(int nodes, int[][] edgesCosts, int[] nodesCosts) {
        if (nodes <= 20) {
            return new TSPSolverImplementation2(nodes, edgesCosts, nodesCosts);
        } else {
            return new TSPSolverImplementation2(nodes, edgesCosts, nodesCosts);
        }
    }
    
    public static TSPSolverWorkerTemplate getSolver(int nodes) {
        return getSolver(nodes, null, null);
    }
}
