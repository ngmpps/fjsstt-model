package at.ngmpps.fjsstt.model.problem;

import java.io.Serializable;
import java.util.Arrays;

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
	final Bid[] bids;

	/**
	 * The completion times of job operations.
	 */
	final int[][] operationsBeginTimes;

	/**
	 * The machine assignments of job operations.
	 */
	final int[][] operationsMachineAssignments;

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
	 * Nr of Jobs
	 */
	int jobs;
	
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
	

	public Solution(final int machines, final int timeslots, final int jobs, final int maxOperationsPerJob) {
		this.jobs = jobs;
		this.machines = machines;
		this.timeslots = timeslots;
		this.maxOperationsPerJob = maxOperationsPerJob;

		operationsBeginTimes = new int[jobs][maxOperationsPerJob];
		operationsMachineAssignments = new int[jobs][maxOperationsPerJob];
		multipliers = new double[machines][timeslots];
		subgradients = new int[0][];
		bids = new Bid[0];
		objectiveValue = Double.NEGATIVE_INFINITY;
	}

	public Solution(final double objectiveValue, final int machines, final int timeslots, final int jobs, final int maxOperationsPerJob) {
		this(objectiveValue, machines, timeslots, jobs, maxOperationsPerJob, null, 0, new int[machines][timeslots]);
	}

	public Solution(final double objectiveValue, final int machines, final int timeslots, final int jobs, final int maxOperationsPerJob, final Bid[] bids, final int iteration,
			final int[][] subgradients) {
		this(objectiveValue, machines, timeslots, jobs, maxOperationsPerJob, bids, iteration, subgradients, null);
	}

	public Solution(final double objectiveValue, final int machines, final int timeslots, final int jobs, final int maxOperationsPerJob, final Bid[] bids, final int iteration,
			final int[][] subgradients, final double[][] multipliers) {
		this.objectiveValue = objectiveValue;
		this.bids = bids;
		this.iteration = iteration;
		this.subgradients = subgradients;
		this.jobs = jobs;
		this.machines = machines;
		this.timeslots = timeslots;
		this.maxOperationsPerJob = maxOperationsPerJob;

		this.operationsBeginTimes = new int[jobs][maxOperationsPerJob];
		this.operationsMachineAssignments = new int[jobs][maxOperationsPerJob];
		this.multipliers = new double[machines][timeslots];

		// for all jobs and operations: compile the arrays for optimal machine
		// assignments and completion times for operations
		if (this.bids != null) {
			for (final Bid bid : this.bids) {
				final int job = bid.getJobID();
				final int[] machineAssignments = bid.getOptimumMachines();
				final int[] beginTimes = bid.getOptimumBeginTimes();

				System.arraycopy(machineAssignments, 0, this.operationsMachineAssignments[job], 0, machineAssignments.length);
				System.arraycopy(beginTimes, 0, this.operationsBeginTimes[job], 0, beginTimes.length);
			}
		} 

		if (multipliers != null) {
			for (int m = 0; m < machines; m++) {
				System.arraycopy(multipliers[m], 0, multipliers[m], 0, multipliers[m].length);
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
	public Solution(final double objectiveValue, final int[][] begTimes, final int[][] machAss) {
		this(objectiveValue, begTimes, machAss, 0);
	}

	public Solution(final double objectiveValue, final int[][] begTimes, final int[][] machAss,
			final int iteration) {
		this.objectiveValue = objectiveValue;
		this.bids = null;
		this.iteration = iteration;

		this.operationsBeginTimes = begTimes;
		this.operationsMachineAssignments = machAss;
		this.subgradients = null;
		this.multipliers = null;
	}

	public Solution clone() {
		Solution result = new Solution(objectiveValue, machines, timeslots, jobs, maxOperationsPerJob, bids, iteration, subgradients);
		if (operationsBeginTimes != null)
			for (int i = 0; i < operationsBeginTimes.length; ++i)
				for (int ii = 0; ii < operationsBeginTimes[i].length; ++ii)
					result.setOperationsBeginTimes(i, ii, operationsBeginTimes[i][ii]);

		if (operationsMachineAssignments != null)
			for (int i = 0; i < operationsMachineAssignments.length; ++i)
				for (int ii = 0; ii < operationsMachineAssignments[i].length; ++ii)
					result.setOperationsMachineAssignments(i, ii, operationsMachineAssignments[i][ii]);
		return result;
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
		for (int i = 0; i < jobs; i++) {
			for (int j = 0; j < operationsMachineAssignments[i].length; j++) {
				if (this.operationsMachineAssignments[i][j] != sol.getOperationsMachineAssignments()[i][j])
					return false;
				if (this.operationsBeginTimes[i][j] != sol.getOperationsBeginTimes()[i][j])
					return false;
			}
		}
		return true;
	}

	/**
	 * @return the mBids
	 */
	public Bid[] getBids() {
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
	 * @return the mOperationsCompTimes
	 */
	public int[][] getOperationsBeginTimes() {
		return operationsBeginTimes;
	}

	/**
	 * @return the mOperationsMachineAssignments
	 */
	public int[][] getOperationsMachineAssignments() {
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

	public void setOperationsBeginTimes(final int job, final int op, final int time) {
		operationsBeginTimes[job][op] = time;
	}

	public void setOperationsMachineAssignments(final int job, final int op, final int time) {
		operationsMachineAssignments[job][op] = time;
	}

	@Override
	public String toString() {
		return "Solution{" +
				", objectiveValue=" + objectiveValue +
				", bids=" + Arrays.toString(bids) +
				", operationsBeginTimes=" + Arrays.toString(operationsBeginTimes) +
				", operationsMachineAssignments=" + Arrays.toString(operationsMachineAssignments) +
				", iteration=" + iteration +
				", subgradients=" + Arrays.toString(subgradients) +
				", multipliers=" + Arrays.toString(multipliers) +
				'}';
	}
}
