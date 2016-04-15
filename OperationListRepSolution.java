package pack01;

import java.util.*;

import modelP.Problem;

public abstract class OperationListRepSolution extends Solution {

	public OperationListRepSolution(Problem problem) {
		super(problem);
	}
	
	//turn a solution int[][] into a chromosome that is operations representation 
	//ArrayList<Integer> storing integers as job ids as many times as many operations the job has 
	public ArrayList<Integer> makeChromosome(int[][] solution, Problem problem) {
		ArrayList<Integer> chromosome = new ArrayList<Integer>();
		
		int machines = problem.getNumberOfMachines();
		int jobs = problem.getNumberOfJobs();
		
		for( int i=0; i<machines; i++ ) {
			for( int j=0; j<jobs; j++ ) {
				chromosome.add( solution[i][j] );
			}
		}
		
		return chromosome;
	}
	
	//create new solutions by crossover
	public ArrayList<ArrayList<Integer>> crossover(ArrayList<ArrayList<Integer>> parents, ArrayList<Integer> points, Problem p) {
		ArrayList<ArrayList<Integer>> children = new ArrayList<ArrayList<Integer>>();
		Random r = new Random();
		
		ArrayList<Integer> p1 = parents.get(0);
		ArrayList<Integer> p2 = parents.get(1);
		
		ArrayList<Integer> c1 = new ArrayList<Integer>();
		ArrayList<Integer> c2 = new ArrayList<Integer>();
		
		int counter = 0;
		for( int i=0; i<p1.size(); i++ ) {
			if( counter==0 || counter%2==0 ) {
				c1.add(p1.get(i));
				c2.add(p2.get(i));
			} else {
				c1.add(p2.get(i));
				c2.add(p1.get(i));
			}
			if( points.size()>=counter+1 && i==points.get(counter) ) counter++;
		}
		
		//even out
		ArrayList<Integer> child1 = evenOut(c1, p);
		ArrayList<Integer> child2 = evenOut(c2, p);
		
		//mutate with probability of 0.3 - separately for each genome 
		int mp1 = r.nextInt(10);
		if( mp1<3 ) {
			ArrayList<Integer> chld1 = mutate( child1 );
			children.add(chld1);
		} else {
			children.add(child1);
		}
		
		int mp2 = r.nextInt(10);
		if( mp2<3 ) {
			ArrayList<Integer> chld2 = mutate( child2 );
			children.add(chld2);
		} else {
			children.add(child2);
		}
		
		return children;
	}
	
	public static ArrayList<Integer> evenOut(ArrayList<Integer> list, Problem p) {
		ArrayList<Integer> chromosome = list;
		
		//the arraylist will store info about how many times a job occurrs in the list
		//that is how many operations are there in the list to determine whether there's to few of one
		//or too many of another one
		ArrayList<Integer> operations = new ArrayList<Integer>();
		
		for( int i=0; i<p.getNumberOfJobs(); i++ ) {
			operations.add(0);
			for( int j=0; j<chromosome.size(); j++ ) {
				if( chromosome.get(j)==i ) {
					int o = operations.get(i) + 1;
					operations.set(i, o);
				}
			}
		}
		
		//the loop will go on until there is the same amount of each of the values - that is
		//each job number occurs the same number of times in the chromosome
		while( !isEven(chromosome, p) ) {
			
			int low = 0, high = 0;
			for( int i=0; i<operations.size(); i++ ) {
				if( operations.get(i)>p.getNumberOfMachines() ) high = i;
				if( operations.get(i)<p.getNumberOfMachines() ) low = i;
			}
			
			//find the index of the last operation in list which is redundant
			int indexH = -1;
			for( int i=chromosome.size()-1; i>=0; i-- ) {
				if( chromosome.get(i)==high ) {
					indexH = i;
					break;
				}
			}
			
			//change the redundant operation to the value of job which operations are too few in the chromosome
			chromosome.set(indexH, low);
			
			//reset the operations list to update the number of operations of each job in the chromosome
			operations.clear();
			for( int i=0; i<p.getNumberOfJobs(); i++ ) {
				operations.add(0);
				for( int j=0; j<chromosome.size(); j++ ) {
					if( chromosome.get(j)==i ) {
						int o = operations.get(i) + 1;
						operations.set(i, o);
					}
				}
			}
		}
		
		return chromosome;
	}
	
	public static boolean isEven(ArrayList<Integer> chromosome, Problem p) {
		boolean even = true;
		
		int machines = p.getNumberOfMachines();
		int jobs = p.getNumberOfJobs();
		
		for( int i=0; i<jobs; i++ ) {
			int counter = 0;
			
			for( int j=0; j<chromosome.size(); j++ ) {
				
				if( chromosome.get(j)==i ) {
					counter++;
				}
				
				if( counter>machines ) {
					even = false;
					break;
				}
			
			}
		}
		
		return even;
	}
	
	//the method body will vary depending on the way operations are scheduled on machines
	//in string representation there are 3 ways of doing that
	abstract public int[][] turnChromosomeIntoSolution(ArrayList<Integer> chromosome, Problem p);
		
	//mutate chromosome by choosing randomly two points in the chromosome 
	//and swapping the list elements at those indexes
	public ArrayList<Integer> mutate(ArrayList<Integer> chromosome) {
		ArrayList<Integer> mutatedChromosome = chromosome;
		
		Random r = new Random();
		
		int a = r.nextInt(chromosome.size()-1);
		int b = -1;
		do {
			b = r.nextInt(chromosome.size()-1);
		} while( b==a );
		
		int valueA = chromosome.get(a);
		int valueB = chromosome.get(b);
		
		mutatedChromosome.set(a, valueB);
		mutatedChromosome.set(b, valueA);
		
		return mutatedChromosome;
	}
	
}
