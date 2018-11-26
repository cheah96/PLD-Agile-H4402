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
		
		double som=0;
		int minZero=Integer.MAX_VALUE;
		for(int i=0;i<nonVus.size();++i){
			int min1=Integer.MAX_VALUE;
			int min2=Integer.MAX_VALUE;
			for(int j=0;j<nonVus.size();++j){
				if(i!=j){
					if(cout[nonVus.get(i)][nonVus.get(j)]<min1){
						min1=cout[nonVus.get(i)][nonVus.get(j)];
					}
					if(cout[nonVus.get(j)][nonVus.get(i)]<min2){
						min2=cout[nonVus.get(j)][nonVus.get(i)];
					}
				}
			}
			
			if(cout[nonVus.get(i)][0]<minZero){
				minZero=cout[nonVus.get(i)][0];
			}
			
			som+=min1+min2;
			value+=duree[i];
		}
		value+=Math.floor(som/2.0);
		value+=minZero;
		return value;
	}
}
