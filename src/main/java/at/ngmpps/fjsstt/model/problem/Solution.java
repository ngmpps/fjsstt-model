package at.ngmpps.fjsstt.model.problem;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import at.ngmpps.fjsstt.model.problem.subproblem.Bid;

/**
 * Datastructure representing a solution for a FJSSTT problem.
 * 
 * @author ahaemm
 * 
 */
public class Solution implements Serializable {

	private static final long serialVersionUID = -409137575428427517L;

	double objectiveValue;

	/**
	 * The job bids, each one representing the solution of a job-level
	 * subproblem. Indices are jobs.
	 */
	final Map<Integer, Bid> bids;

	/**
	 * The begin times of job operations.
	 */
	final Map<Integer, int[]> operationsBeginTimes;

	/**
	 * The machine assignments of job operations.
	 */
	final Map<Integer, int[]> operationsMachineAssignments;

	/**
	 * The iteration in which the solution was found.
	 */
	int iteration;

	/**
	 * The subgradient values resulting from mOperationsBeginTimes and
	 * mOperationsMachineAssignments.
	 */
	final int[][] subgradients;

	/**
	 * The vector of strictly positive Lagrange multipliers that led to the
	 * solution.
	 */
	final double[][] multipliers;

	/**
	 * Nr of Machines
	 */
	int machines;

	/**
	 * Nr of timeslots
	 */
	int timeslots;

	/**
	 * Max Nr of Operations per Job
	 */
	int maxOperationsPerJob;

	public Solution(final int machines, final int timeslots, final int maxOperationsPerJob) {
		this.machines = machines;
		this.timeslots = timeslots;
		this.maxOperationsPerJob = maxOperationsPerJob;

		operationsBeginTimes = new HashMap<Integer, int[]>();
		operationsMachineAssignments = new HashMap<Integer, int[]>();
		multipliers = new double[machines][timeslots];
		subgradients = new int[0][];
		bids = new HashMap<Integer,Bid>();
		objectiveValue = Double.NEGATIVE_INFINITY;
	}

	public Solution(final double objectiveValue, final int machines, final int timeslots, final int maxOperationsPerJob) {
		this(objectiveValue, machines, timeslots, maxOperationsPerJob, null, 0, new int[machines][timeslots]);
	}

	public Solution(final double objectiveValue, final int machines, final int timeslots, final int maxOperationsPerJob, final Map<Integer,Bid> bids,
			final int iteration, final int[][] subgradients) {
		this(objectiveValue, machines, timeslots, maxOperationsPerJob, bids, iteration, subgradients, null);
	}

	public Solution(final double objectiveValue, final int machines, final int timeslots, final int maxOperationsPerJob, final Map<Integer,Bid> bids,
			final int iteration, final int[][] subgradients, final double[][] multipliers) {
		this.objectiveValue = objectiveValue;
		this.bids = bids;
		this.iteration = iteration;
		this.subgradients = subgradients;
		this.machines = machines;
		this.timeslots = timeslots;
		this.maxOperationsPerJob = maxOperationsPerJob;

		operationsBeginTimes = new HashMap<Integer, int[]>();
		operationsMachineAssignments = new HashMap<Integer, int[]>();
		this.multipliers = new double[machines][timeslots];

		// for all jobs and operations: compile the arrays for optimal machine
		// assignments and completion times for operations
		if (this.bids != null) {
			for (final Bid bid : this.bids.values()) {
				final int job = bid.getJobID();
				final int[] machineAssignments = bid.getOptimumMachines();
				final int[] beginTimes = bid.getOptimumBeginTimes();

				operationsMachineAssignments.put(job, new int[machineAssignments.length]);
				operationsBeginTimes.put(job, new int[beginTimes.length]);

				System.arraycopy(machineAssignments, 0, operationsMachineAssignments.get(job), 0, machineAssignments.length);
				System.arraycopy(beginTimes, 0, operationsBeginTimes.get(job), 0, beginTimes.length);
			}
		}

		if (multipliers != null) {
			for (int m = 0; m < machines; m++) {
				System.arraycopy(multipliers[m], 0, this.multipliers[m], 0, multipliers[m].length);
			}
		}
	}

	/**
	 * Constructor used by ListScheduling, so that it can return a real Solution
	 * and not just a String[][]. Problems: ListScheduling does not know in which
	 * iteration it is called, so it cannot set mIteration. Both bids and
	 * subgradients are not important for ListScheduling and - to my knowledge-
	 * need not be set.
	 */
	public Solution(final double objectiveValue, final Map<Integer, int[]> begTimes, final Map<Integer, int[]> machAss) {
		this(objectiveValue, begTimes, machAss, 0);
	}

	public Solution(final double objectiveValue, final Map<Integer, int[]> begTimes, final Map<Integer, int[]> machAss,
			final int iteration) {
		this.objectiveValue = objectiveValue;
		this.bids = null;
		this.iteration = iteration;

		this.operationsBeginTimes = begTimes;
		this.operationsMachineAssignments = machAss;
		this.subgradients = null;
		this.multipliers = null;
	}

	/**
	 * returns a new ID of a Job; need to add Operations, processTimes, dueDate and jobWeight for this individually
	 * @return
	 */
	public Integer addJob() {
		Integer newID = 0;
		for(Integer key :operationsBeginTimes.keySet())
			newID = key>=newID?key+1:newID;
		return newID;
	}
	public Integer addJob(int[] operationsBeginTimes, int[] operationsMachineAssignments) {
		Integer newID = addJob();
		this.operationsBeginTimes.put(newID, operationsBeginTimes);
		if(operationsBeginTimes.length>this.maxOperationsPerJob)
			maxOperationsPerJob=operationsBeginTimes.length;
		this.operationsMachineAssignments.put(newID, operationsMachineAssignments);
		return newID;
	}
	public void removeJob(Integer jobID)  {
		this.operationsBeginTimes.remove(jobID);
		this.operationsMachineAssignments.remove(jobID);
		// todo recalculate maxOperationsPerJob as the removed Job could have been the longest one.
	}
	
	public Solution clone() {
		return clone(0);
	}
	protected Solution clone(int reduce) {
		Solution result = null;
		if(reduce == 1) {
			result = new Solution(objectiveValue, machines, timeslots, maxOperationsPerJob, null, iteration, subgradients);
		} else if(reduce == 2) {
			result = new Solution(objectiveValue, machines, timeslots, maxOperationsPerJob, null, iteration, null);
		} else if(reduce == 3) {
			result = new Solution(objectiveValue, machines, timeslots, maxOperationsPerJob, null, iteration, null, null);
		} else //if (reduce == 0) 
			result = new Solution(objectiveValue, machines, timeslots, maxOperationsPerJob, bids, iteration, subgradients);
		
//		if (operationsBeginTimes != null)
//			for (int i : operationsBeginTimes.keySet())
//				for (int ii = 0; ii < operationsBeginTimes.get(i).length; ++ii)
//					result.setOperationsBeginTimes(i, ii, operationsBeginTimes.get(i)[ii]);
//
//		if (operationsMachineAssignments != null)
//			for (int i : operationsMachineAssignments.keySet())
//				for (int ii = 0; ii < operationsMachineAssignments.get(i).length; ++ii)
//					result.setOperationsMachineAssignments(i, ii, operationsMachineAssignments.get(i)[ii]);
		if (operationsBeginTimes != null) {
			for (int i : operationsBeginTimes.keySet()) {
				result.getOperationsBeginTimes().put(i, Arrays.copyOf(operationsBeginTimes.get(i), operationsBeginTimes.get(i).length));
			}
		}
		if (operationsMachineAssignments != null) {
			for (int i : operationsMachineAssignments.keySet()) {
				result.getOperationsMachineAssignments().put(i, Arrays.copyOf(operationsMachineAssignments.get(i), operationsMachineAssignments.get(i).length));
			}
		}
		return result;
	}
	
	/**
	 * clone without bids, subgradients, multipliers
	 * @return
	 */
	public Solution cloneReducedSize() {
		return clone(3);
	}

	/**
	 * Checks if two solutions are equal. The check only considers machine
	 * assignments and begin times.
	 * 
	 * @param sol
	 *           The solution to be compared to.
	 * @return True if the solutions are equal.
	 */
	public boolean equals(Solution sol) {
		for (int i : operationsMachineAssignments.keySet()) {
			if (!sol.getOperationsMachineAssignments().containsKey(i))
				return false;
			if (operationsMachineAssignments.get(i).length != sol.getOperationsBeginTimes().get(i).length)
				return false;
			for (int j = 0; j < operationsMachineAssignments.get(i).length; j++) {
				if (this.operationsMachineAssignments.get(i)[j] != sol.getOperationsMachineAssignments().get(i)[j])
					return false;
				if (this.operationsBeginTimes.get(i)[j] != sol.getOperationsBeginTimes().get(i)[j])
					return false;
			}
		}
		return true;
	}

	/**
	 * @return the mBids
	 */
	public Map<Integer,Bid> getBids() {
		return bids;
	}

	/**
	 * @return the mIteration
	 */
	public int getIteration() {
		return iteration;
	}

	public double[][] getMultipliers() {
		return multipliers;
	}

	/**
	 * @return the mObjectiveValue
	 */
	public double getObjectiveValue() {
		return objectiveValue;
	}

	/**
	 * @return the start time per Job per Operation
	 */
	public Map<Integer, int[]> getOperationsBeginTimes() {
		return operationsBeginTimes;
	}

	/**
	 * @return the assigned Machine per Job per Operation
	 */
	public Map<Integer, int[]> getOperationsMachineAssignments() {
		return operationsMachineAssignments;
	}

	/**
	 * @return the mSubgradients
	 */
	public int[][] getSubgradients() {
		return subgradients;
	}

	public void setIteration(int bestvaluefoundiniteration) {
		iteration = bestvaluefoundiniteration;
	}

	public void setObjectiveValue(double newvalue) {
		objectiveValue = newvalue;
	}
	

	public void setOperationsBeginTimes(final Integer job, final int op, final int time) {
		setArrayValueInMap(operationsBeginTimes, job, op, time);
	}

	public void setOperationsMachineAssignments(final Integer job, final int op, final int time) {
		setArrayValueInMap(operationsMachineAssignments, job, op, time);
	}


	protected void setArrayValueInMap(final Map<Integer, int[]> myMap, final Integer mapKey, final int arrayIdx, final int value) {
		if (!myMap.containsKey(mapKey)) {
			int[] x = new int[arrayIdx + 1];
			x[arrayIdx] = value;
			myMap.put(mapKey, x);
		} else if (myMap.get(mapKey).length < (arrayIdx + 1)) {
			int[] x = Arrays.copyOf(myMap.get(mapKey), arrayIdx + 1);
			x[arrayIdx] = value;
			myMap.put(mapKey, x);
		} else
			myMap.get(mapKey)[arrayIdx] = value;
	}

	@Override
	public String toString() {
		return "Solution{" + ", objectiveValue=" + objectiveValue 
				+ ", bids=" + (bids!=null ? bids.toString() : "null") 
				+ ", operationsBeginTimes=" + (operationsBeginTimes!=null ? operationsBeginTimes.toString() : "null") 
				+ ", operationsMachineAssignments=" + (operationsMachineAssignments!=null ? operationsMachineAssignments.toString() : "null") 
				+ ", iteration=" + iteration 
				+ ", subgradients=" + (subgradients != null ? Arrays.toString(subgradients) : "null") 
				+ ", multipliers=" + (multipliers != null ? Arrays.toString(multipliers):"null") 
				+ '}';
	}
}
