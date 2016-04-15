package pack01;

import java.util.*;

import modelP.JSSP;
import modelP.Problem;

public class RunSolutions {

	public static void main(String[] args) {
		
		Problem p = JSSP.getProblem(86);//41,99,73
		
		Solution s = new Solution001( p );
		//Solution s = new Solution002( p );
		//Solution s = new Solution003( p );
		//Solution s = new Solution004( p );
		
		int populationSize = 1000;
		ArrayList<int[][]> population = new ArrayList<int[][]>();
		for( int i=0; i<populationSize; i++ ) {
			population.add( JSSP.getRandomSolution(p) );
		}
		//show minimum and average fitness in the newly created population
		int m = s.getMinFitnessInPopulation(population, p);
		int av = s.averagePopulationFitness(population, p);
		System.out.println( "av = " + av );
		System.out.println( "minimum = " + m );
		
		ArrayList<int[][]> parents = new ArrayList<int[][]>();
		ArrayList<int[][]> children = new ArrayList<int[][]>();
		ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
		
		//to measure time taken for the the loop where population is manipulated
		long startTime = System.nanoTime();
		
		for( int i=0; ; i++ ) {
			
			parents = s.parentsRandomly(population, p);
			
			children = s.createNewSolutions(parents, p);
			
			toBeRemoved = s.selectToRemoveRandomly(population, p);
			//toBeRemoved = s.selectToRemoveByFitness(population, p);
			
			population.remove(toBeRemoved.get(1));
			population.remove(toBeRemoved.get(0));
			
			population.addAll(children);
			
			if( JSSP.getFitness(children.get(0), p)<m ) m = JSSP.getFitness(children.get(0), p);
			else if( JSSP.getFitness(children.get(1), p)<m ) m = JSSP.getFitness(children.get(1), p);
			
			long present = System.nanoTime();
			long d = (long) ((present - startTime)/1000000000.0);
			if( d>=180 ) break;
		}
		
		long endTime = System.nanoTime();
		
		System.out.println("after");
		//m = s.getMinFitnessInPopulation(population, p);
		//av = s.averagePopulationFitness(population, p);
		//System.out.println( "av = " + av );
		System.out.println( "minimum = " + m );
		
		long duration = (long) ((endTime - startTime)/1000000000.0);
		System.out.println( duration/60 + "min " + duration%60 + "s");
		System.out.println( duration + "s" );
		
	}

}
