package pack01;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import modelP.Problem;

public class Solution004 extends OperationListRepSolution {

	public Solution004(Problem problem) {
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
	//transform a chromosome represented by a string into a solution by scheduling and fitting operations
	//into slots where machine is idle if possible - but only if it is idle for long enough
	public int[][] turnChromosomeIntoSolution(ArrayList<Integer> chromosome, Problem p) {
		int machines = p.getNumberOfMachines();
		int jobs = p.getNumberOfJobs();
		
		int[][] solution = new int[machines][jobs];
		//initialize the array with -1 values which will mean no operation assigned on the machine
		for( int i=0; i<machines; i++ ) {
			for( int j=0; j<jobs; j++ ) {
				solution[i][j] = -1;
			}
		}
		 
		//3 variables below will store values for the current operation of the job that is being assigned to a machine
		int job = -1;
		int operation = -1;
		int opduration = -1;
		int machine = -1;
		//the list will store values for each job, they will be incremented each time an operation of a job is assigned
		ArrayList<Integer> operations = new ArrayList<Integer>();
		for( int i=0; i<jobs; i++ ) {
			operations.add(0);
		}
		
		//ArrayList of ArrayLists will store schedules for each machine
		//an operation is spread among the ArrayList elements according to the operation's duration value
		//gaps between operations have value -1
		ArrayList<ArrayList<Integer>> schedule = new ArrayList<ArrayList<Integer>>();
		for( int i=0; i<machines; i++ ) {
			schedule.add(new ArrayList<Integer>());
		}
		//ArrayList will store end points for each job after adding the last operation
		//the index of the ArrayList element indicated the job id
		ArrayList<Integer> jobends = new ArrayList<Integer>();
		for( int i=0; i<jobs; i++ ) {
			jobends.add(0);
		}
		//ArrayList will store the end points on machines after last operations assignement
		//the ArrayList element index indicates the machine id
		ArrayList<Integer> machineEndPoints = new ArrayList<Integer>();
		for( int i=0; i<machines; i++ ) {
			machineEndPoints.add(0);
		}
		
		//assign operations inside the loop
		for( int i=0; i<chromosome.size(); i++ ) {
			
			job = chromosome.get(i);
			operation = operations.get(job);
			opduration = p.getOperationDuration(job, operation);
			machine = p.getOperationMachineId(job, operation);
			
			//scan the machine for gaps/slots where it's idle and reqister their start and end point
			//the points will be stored in the ArrayList below - start as odd indexed and end as even endexed elements
			ArrayList<Integer> slots = new ArrayList<Integer>();
			for( int m=0; m<schedule.get(machine).size(); m++ ) {
				if( (m==0 && schedule.get(machine).get(0)==-1) || ( m!=0 && schedule.get(machine).get(m)==-1 && schedule.get(machine).get(m-1)!=-1 ) || (m!=0 && schedule.get(machine).get(m-1)==-1 && schedule.get(machine).get(m)!=-1) ) {
					slots.add(m);
				}
			}
			
			//check if there is a slot when the machine is idle and if the operation can be inserted there
			//an operation can be inserted into a free slot if the slot starts later than the last current job operation finishes
			//or slightly before and if the slot is long enough so the operation can fit 
			//or if the following operation on the machine would be only slightly moved
			boolean slot = false;
			int sstart = -1; 
			int send = -1;
			if( slots.size() > 0 ) {
				for( int j=slots.size()-2; j>=0; j=j-2 ) {
					if( slots.get(j+1)-slots.get(j) > opduration && slots.get(j) >= jobends.get(job) ) {
						slot = true;
						sstart = slots.get(j);
						send = slots.get(j+1);
						break;
					}
				}
			}
			
			//if there is a slot - schedule the operation in the slot
			if( slot ) {
				for( int k=sstart; k<sstart+opduration; k++ ) {
					schedule.get(machine).set(k, job);
				}
			//else - schedule it after the last operation on the machine
			} else {
			
				//check if the last operation of the current job ends later that the last operation on the machine
				//if yes - the will a gap on the machine when it's idle
				//the gap is represented with -1 values 
				if( jobends.get(job) > machineEndPoints.get(machine) ) {
					for( int k=0; k<(jobends.get(job)-machineEndPoints.get(machine)); k++ ) {
						schedule.get(machine).add(-1);
					}
				}
				//assign the operation on the machine
				for( int op=0; op<opduration; op++ ) {
					schedule.get(machine).add(job);
				}
			
			}
			
			//update the end point of last assigned operations of the current job
			//this is the size of the ArrayList of the schedule which is the machine 
			//where the current operation was assigned/scheduled to
			int currentJobUpdatedEndPoint = schedule.get(machine).size();
			jobends.set(job, currentJobUpdatedEndPoint);
			
			//update machine end point which is the size of the ArrayList representing the current machine
			int currentMachineEndPoint = schedule.get(machine).size();
			machineEndPoints.set(machine, currentMachineEndPoint);
			
			//increment the value of operations assigned for the appropriate job
			if( operations.get(job) < machines ) {
				int o = operations.get(job) + 1;
				operations.set(job, o);
			}
			
		}
		
		//turn the schedule into the solution
		for( int i=0; i<schedule.size(); i++ ) {
			Set<Integer> s = new LinkedHashSet<Integer>();
			s.addAll(schedule.get(i));
			ArrayList<Integer> m = new ArrayList<Integer>();
			m.addAll(s);
			for( int j=0; j<m.size(); j++ ) {
				for( int k=0; k<jobs; k++ ) {
					if( m.get(j)!=-1 ) {
						if( solution[i][k]==-1 ) {
							solution[i][k] = m.get(j);
							break;
						}
					}
				}
			}
		}
		
		return solution;
	}
	
}
