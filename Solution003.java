package pack01;

import java.util.ArrayList;

import modelP.Problem;

public class Solution003 extends OperationListRepSolution {

	public Solution003(Problem problem) {
		super(problem);
	}

	@Override
	public ArrayList<int[][]> createNewSolutions(ArrayList<int[][]> parentSolutions, Problem p) {
		ArrayList<int[][]> children = new ArrayList<int[][]>();
		
		Object parent1 = this.makeChromosome(parentSolutions.get(0), p);
		Object parent2 = this.makeChromosome(parentSolutions.get(1), p);
		
		ArrayList<ArrayList<Integer>> parents = new ArrayList<ArrayList<Integer>>();
		parents.add((ArrayList<Integer>) parent1);
		parents.add((ArrayList<Integer>) parent2);
		
		ArrayList<Integer> points = this.selectCrossoverPoints(true, parents.get(0).size());
		
		ArrayList<ArrayList<Integer>> childrenChromosomes = this.crossover(parents, points, p);
		
		int[][] child1 = this.turnChromosomeIntoSolution(childrenChromosomes.get(0), p);
		int[][] child2 = this.turnChromosomeIntoSolution(childrenChromosomes.get(1), p);
		
		children.add(child1);
		children.add(child2);
		
		return children;
	}

	@Override
	public int[][] turnChromosomeIntoSolution(ArrayList<Integer> chromosome, Problem p) {
		int machines = p.getNumberOfMachines();
		int jobs = p.getNumberOfJobs();
		
		int[][] solution = new int[machines][jobs];
		//initialize the array with -1
		for( int i=0; i<machines; i++ ) {
			for( int j=0; j<jobs; j++ ) {
				solution[i][j] = -1;
			}
		}
		
		//3 variables below will store values for the current operation of the job that is being assigned to a machine
		int job = -1;
		int operation = -1;
		int machine = -1;
		//the list will store values for each job, they will be incremented each time an operation of a job is assigned
		//this way it will be registered which operation of the job is currently being scheduled
		ArrayList<Integer> operations = new ArrayList<Integer>();
		for( int i=0; i<jobs; i++ ) {
			operations.add(0);
		}
		
		//assign operations inside the loop
		for( int i=0; i<chromosome.size(); i++ ) {
			
			job = chromosome.get(i);
			operation = operations.get(job);
			machine = p.getOperationMachineId(job, operation);
		
			//assign the operation on a particular machine in the first available slot
			for( int j=0; j<jobs; j++ ) {
				if( solution[machine][j]==-1 ) {
					solution[machine][j]=job;
					break;
				}
			}
			
			//increment the value of operations assigned for the appropriate job
			if( operations.get(job)<machines ) {
				int o = operations.get(job) + 1;
				operations.set(job, o);
			}
			
		}
		
		return solution;
	}
	
}
