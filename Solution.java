package pack01;

import java.util.*;

import modelP.JSSP;
import modelP.Problem;

public abstract class Solution {
	
	private Problem p;
	
	public Solution(Problem problem) {
		p = problem;
	}
	
	//selects a set of randomly chosen solutions from the population which is represented as an ArrayList of int-arrays
	//where int[0] is the fintes of the chosen solution and int[1] is the idex of the solution in the population
	public ArrayList<int[]> selectSample(ArrayList<int[][]> population, Problem p, int sampleSize) {
		ArrayList<int[]> sample = new ArrayList<int[]>();
		Random r = new Random();
		
		for( int i=0; i<sampleSize;  ) {
			int n = r.nextInt(population.size());	//choose a random index from the population
			
			int[] s = { JSSP.getFitness(population.get(n), p), n }; //create a pair consisting of the fitness of the solution and it's index in the population
			if( !sample.contains(s) ) {			//add the pair to the ArrayList if it wasn't chosen before
				sample.add(s);
				i++;
			}
		}
		
		return sample;
	}
	
	//selects two solutions from the sample as parents for crossover
	//the parents are chosen as the ones of the best (the lowest) fitness in the sample
	public ArrayList<int[][]> parentsByFitnessFromSample(ArrayList<int[][]> population, ArrayList<int[]> sample, Problem p) {
		ArrayList<int[][]> parents = new ArrayList<int[][]>();
		
		int f1=999999, f2=999999, id1=0, id2=0;
		
		//iterate over the sample and pick two solutions from the population that have the best fitness (the lowest values)
		for( int i=0; i<sample.size(); i++ ) {
			if( sample.get(i)[0]<f1 ) {
				f1 = sample.get(i)[0];
				id1 = sample.get(i)[1];
			}
			if( sample.get(i)[0]<f2 && sample.get(i)[1]!=id1 ) {
				f2 = sample.get(i)[0];
				id2 = sample.get(i)[1];
			}
		}
		
		parents.add(population.get(id1));
		parents.add(population.get(id2));
		
		return parents;
	}
	
	//get two solutions from the population that have the lowest fitness value for crossover
	public ArrayList<int[][]> parentsByFitnessFromPopulation(ArrayList<int[][]> population, Problem p) {
		ArrayList<int[][]> parents = new ArrayList<int[][]>();
		
		//find the ones with the lowest fitness in the pool and make them parents
		int a=-1, b=-1, fa=999, fb=999;
		for( int i=0; i<population.size(); i++ ) {
			if( JSSP.getFitness(population.get(i), p)<fa ) {
				fa = JSSP.getFitness(population.get(i), p);
				a = i;
			}
			if( JSSP.getFitness(population.get(i), p)<fb && a!=i ) {
				fb = JSSP.getFitness(population.get(i), p);
				b = i;
			}
		}
		
		int[][] p1 = population.get(a);
		int[][] p2 = population.get(b);
		parents.add(p1);
		parents.add(p2);
		
		return parents;
	}
	
	//select randomly two solution from population for crossover
	public ArrayList<int[][]> parentsRandomly(ArrayList<int[][]> population, Problem p) {
		ArrayList<int[][]> parents = new ArrayList<int[][]>();
		
		Random r = new Random();
		
		int a = r.nextInt( population.size() );
		int b = r.nextInt( population.size() );
		
		int[][] p1 = population.get(a);
		int[][] p2 = population.get(b);
		
		parents.add(p1);
		parents.add(p2);
		
		return parents;
	}
	
	//select ids of two solutions in the population that have the worst fitness to be removed
	public ArrayList<Integer> selectToRemoveByFitness(ArrayList<int[][]> population, Problem p) {
		ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
		
		int f1=0, f2=0, id1=0, id2=0;
		for( int[][] i : population ) {
			if( JSSP.getFitness(i, p)>f1 ) {
				f1 = JSSP.getFitness(i, p);
				id1 = population.indexOf(i);
			}
			if( JSSP.getFitness(i, p)>f2 && JSSP.getFitness(i, p)!=f1 ) {
				f2=JSSP.getFitness(i, p);
				id2=population.indexOf(i);
			}
		}
		
		toBeRemoved.add(id1);
		toBeRemoved.add(id2);
		Collections.sort(toBeRemoved);
		
		return toBeRemoved;
	}
	
	//select randomly two solutions from the population to be removed
	public ArrayList<Integer> selectToRemoveRandomly(ArrayList<int[][]> population, Problem p) {
		ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
		
		Random r = new Random();
		
		int a = r.nextInt( population.size() );
		int b = -1;
		do {
			b = r.nextInt( population.size() );
		} while ( b==a );
		
		toBeRemoved.add(a);
		toBeRemoved.add(b);
		Collections.sort(toBeRemoved);
		
		return toBeRemoved;
	}
	
	//(crossover)  ?abstract? 
	abstract public ArrayList<int[][]> createNewSolutions(ArrayList<int[][]> parents, Problem p);
	
	//turn a solution into a genome of appropriate representation
	abstract public Object makeChromosome(int[][] solution, Problem problem);
	
	//select crossover point
	public ArrayList<Integer> selectCrossoverPoints(boolean one, int parentSize) {
		ArrayList<Integer> points = new ArrayList<Integer>();
		Random r = new Random();
		
		//how many points: 1-3
		int n = 0;
		if( one ) {
			n = 1;
			int point = r.nextInt(parentSize-1);
			points.add(point);
		} else {
			n = r.nextInt(2) + 2;
			
			//select crossover points as indexes in the genome
			for( int i=0; i<n;  ) {
				int point = r.nextInt(parentSize-1);
				if( !points.contains(point) ) {
					points.add(point);
					i++;
				}
			}
		}
		Collections.sort(points);
		
		return points;
	}
	
	//get the average fitness in the population
	public int averagePopulationFitness(ArrayList<int[][]> population, Problem p) {
		int average = 0;
		
		for( int i=0; i<population.size(); i++ ) {
			average += JSSP.getFitness(population.get(i), p);
		}
		average = average/population.size();
		
		return average;
	}
	
	//find the value of the minimum (the best) fitness in the population
	public int getMinFitnessInPopulation(ArrayList<int[][]> population, Problem p) {
		int min = 9999;
		
		for( int i=0; i<population.size(); i++ ) {
			if( JSSP.getFitness(population.get(i), p)<min ) {
				min = JSSP.getFitness(population.get(i), p);
			}
		}
		
		return min;
	}
	
}
