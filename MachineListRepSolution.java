package pack01;

import java.util.*;

import modelP.JSSP;
import modelP.Problem;

public abstract class MachineListRepSolution extends Solution {
	
	Problem p;
	
	public MachineListRepSolution( Problem problem ) {
		super( problem );
	}

	@Override
	public ArrayList<int[]> makeChromosome(int[][] solution, Problem p) {
		ArrayList<int[]> chromosome = new ArrayList<int[]>();
		int machines = solution.length;
				
		//transform parentSolution into chromosome that is into ArrayLists of int arrays that represent machines
		for( int i=0; i<machines; i++ ) {
			int[] machine = solution[i];
			
			chromosome.add(machine);
		}
		
		return chromosome;
	}
	
	public ArrayList<ArrayList<int[]>> crossover(ArrayList<ArrayList<int[]>> parents, ArrayList<Integer> points) {
		ArrayList<ArrayList<int[]>> children = new ArrayList<ArrayList<int[]>>();
		Random r = new Random();
		
		ArrayList<int[]> parent1 = parents.get(0);
		ArrayList<int[]> parent2 = parents.get(1);
		
		ArrayList<int[]> child1 = new ArrayList<int[]>();
		ArrayList<int[]> child2 = new ArrayList<int[]>();
		
		int counter = 0;
		for( int i=0; i<parent1.size(); i++ ) {
			if( i!=points.get(counter) ) {
				
				child1.add(parent1.get(i));
				child2.add(parent2.get(i));
			} else {
				child1.add(parent2.get(i));
				child2.add(parent1.get(i));
			}
			if( i==points.get(counter) && points.size()>counter+1 ) counter++;
		}
		
		//mutatation with probability of 0.3 - separately for each chromosome
		int mp1 = r.nextInt(10);
		if( mp1<3 ) {
			//mutate
			ArrayList<int[]> c1 = mutate(child1);
			children.add(c1);
		} else {
			children.add(child1);
		}
		int mp2 = r.nextInt(10);
		if( mp2<3 ) {
			ArrayList<int[]> c2 = mutate(child2);
			children.add(c2);
		} else {
			children.add(child2);
		}
		
		return children;
	}

	
	public int[][] turnChromosomeIntoSolution(ArrayList<int[]> chromosome) {
		int machines = chromosome.size();
		int jobs = chromosome.get(0).length;
		
		// int array of size 		machines	/	jobs
		int[][] solution = new int[machines][jobs];
		
		for( int i=0; i<chromosome.size(); i++ ) {
			int[] machine = chromosome.get(i);
			solution[i] = machine;
		}
		
		return solution;
	}

	//in machine representation chromosomes genes two genes mutate  
	//that is two machines change their places 
	public static ArrayList<int[]> mutate(ArrayList<int[]> chromosome) {
		ArrayList<int[]> mutatedChromosome = new ArrayList<int[]>();
		Random r = new Random();
		
		//select two random indexes the list representing the chromosome
		int index1 = r.nextInt(chromosome.size());
		int index2 = -1;
		do {
			index2 = r.nextInt(chromosome.size());
		} while( index2==index1 );
		
		for( int j=0; j<chromosome.size(); j++ ) {
			if( j==index1 ) {
				mutatedChromosome.add(chromosome.get(index2));
			} else if( j==index2 ) {
				mutatedChromosome.add(chromosome.get(index1));
			} else {
				mutatedChromosome.add(chromosome.get(j));
			}
		}
		
		return mutatedChromosome;
	}
	
}
