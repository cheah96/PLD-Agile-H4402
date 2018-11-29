package fr.insa.lyon.pld.agile.tsp;

public abstract class TSPSolverFactory {
    
    public static TSPSolver getSolver(int nodesCount) {
	if(nodesCount<=20) {
	    return new TSPSolverImplementation2();
	}
	else {
	    return new TSPSolverImplementation2();
	}
	
    }
}
