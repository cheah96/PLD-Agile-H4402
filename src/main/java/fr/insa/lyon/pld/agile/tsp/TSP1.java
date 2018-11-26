package fr.insa.lyon.pld.agile.tsp;

import java.util.ArrayList;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {

    @Override
    protected Iterator<Integer> iterator(Integer sommetCrt, ArrayList<Integer> nonVus, int[][] cout, int[] duree) {
	return new IteratorSeq(nonVus, sommetCrt);
    }

    @Override
    protected int bound(Integer sommetCourant, ArrayList<Integer> nonVus, int[][] cout, int[] duree) {
	int value=0;
	
	int minimumIncomingBranch=Integer.MAX_VALUE;
	for(int i=0;i<nonVus.size();++i){
	    if(cout[sommetCourant][nonVus.get(i)]<minimumIncomingBranch){
		minimumIncomingBranch=cout[sommetCourant][nonVus.get(i)];
	    }
	}
	value+=minimumIncomingBranch;
	
	for(int i=0;i<nonVus.size();++i){
	    int minimumOutGoingBranch=cout[nonVus.get(i)][0];
	    for(int j=0;j<nonVus.size();++j){
		if(i!=j){
		    if(cout[nonVus.get(i)][nonVus.get(j)]<minimumOutGoingBranch){
			minimumOutGoingBranch=cout[nonVus.get(i)][nonVus.get(j)];
		    }
		}
	    }
	    value+=duree[nonVus.get(i)];
	    value+=minimumOutGoingBranch;
	}
	return value;
    }
}