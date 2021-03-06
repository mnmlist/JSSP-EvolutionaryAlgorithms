package pack01;

import java.util.*;

import modelP.Problem;

//machine representation solution with a single point of crossover
public class Solution001 extends MachineListRepSolution {

	public Solution001(Problem problem) {
		super(problem);
	}

	@Override
	public ArrayList<int[][]> createNewSolutions(ArrayList<int[][]> parentSolutions, Problem p) {
		ArrayList<int[][]> newSolutions = new ArrayList<int[][]>();
		
		ArrayList<int[]> parent1 = this.makeChromosome( parentSolutions.get(0), p );
		ArrayList<int[]> parent2 = this.makeChromosome( parentSolutions.get(1), p );
		
		ArrayList<ArrayList<int[]>> parents = new ArrayList<ArrayList<int[]>>();
		parents.add(parent1);
		parents.add(parent2);
		
		//one crossover point is generated by passing the boolean parameter of value true
		ArrayList<Integer> crossoverPoints = this.selectCrossoverPoints( true, parent1.size() );
		
		ArrayList<ArrayList<int[]>> childrenGenomes = this.crossover( parents, crossoverPoints );
		
		int[][] child1 = this.turnChromosomeIntoSolution( childrenGenomes.get(0) );
		int[][] child2 = this.turnChromosomeIntoSolution( childrenGenomes.get(1) );
		
		newSolutions.add(child1);
		newSolutions.add(child2);
		
		return newSolutions;
	}
	

}
