package at.ngmpps.fjsstt.model.problem;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.model.problem.subproblem.SubproblemInstance;

/**
 * Implements an instance of the flexible job shop scheduling problem with
 * travel time between machines, and with Lagrange multipliers for relaxed
 * machine capacity constraints. For a detailed problem description see the
 * NgMPPS technical report.
 * 
 * @author ahaemm
 * 
 */
public class FJSSTTproblem implements Serializable {

	private static final long serialVersionUID = 5108834322696705163L;

	final static Logger logger = LoggerFactory.getLogger(FJSSTTproblem.class);

	/**
	 * The available objective functions for a single job.
	 * 
	 * @author ahaemm
	 * 
	 */
	public enum Objective {
		COMPLETION_TIME, TARDINESS
	}
	
	/**
	 * The number of jobs. The set of jobs is {0,...,mJobs-1};
	 */
	final int jobs;

	/**
	 * The number of operations per job, array indices are jobs. The set of
	 * operations for job i is {0,...,mOperations[i]-1}; It is assumed that the
	 * operations are to be processed in the order 0,...,mOperations[i] - 1.
	 */
	final int operations[];

	/**
	 * The maximum number of operations of a job.
	 */
	final int maxOperations;

	/**
	 * The number of machines. The set of machines is {0,...,mMachines-1};
	 */
	final int machines;

	/**
	 * The number of time units, indexed by {0,...,mTimeUnits - 1}. Each
	 * operation beginning time is defined as the beginning of the corresponding
	 * time unit, and each completion time the end of the time unit.
	 */
	int timeSlots;

	/**
	 * The sets of alternative machines per operation. A key is an integer tuple
	 * (job,operation) in the form of "job-operation", the corresponding value is
	 * the *set* (i.e. every value is in there once) of alternative machines.
	 */
	final HashMap<String, List<Integer>> altMachines;

	/**
	 * The process times of operations on machines. The first index is the job,
	 * the second index is the operation, and the third index is the machine.
	 */
	final int[][][] processTimes;

	/**
	 * The travel times between machines. Both indices denote machines.
	 */
	int[][] travelTimes;

	/**
	 * The job's due dates, indices are jobs.
	 */
	final int[] dueDates;

	/**
	 * The objective function to be minimised on job level.
	 */
	public Objective objective;

	/**
	 * The job weights (for weighted tardiness / completion time objectives).
	 */
	int[] jobWeights;

	Properties configurations = null;

	int problemId = -1;


	
	/**
	 * Creates an instance of the FJSSTT problem.
	 * 
	 * @param jobs
	 * @param operations
	 * @param machines
	 * @param timeslots
	 *           multiplies timeslotes with factor 1.2
	 * @param altMachines
	 * @param processTimes
	 * @param travelTimes
	 * @param multipliers
	 * @param dueDates
	 * @param objective
	 */
	public FJSSTTproblem(final int jobs, final int[] operations, final int maxOperations, final int machines, final int timeslots,
			final HashMap<String, List<Integer>> altMachines, final int[][][] processTimes, final int[][] travelTimes, final int[] dueDates,
			final Objective objective, final int[] weights) {

		this.jobs = jobs;
		this.operations = operations;
		this.maxOperations = maxOperations;
		this.machines = machines;
		this.setTimeSlots(timeslots);
		this.altMachines = altMachines;
		this.processTimes = processTimes;
		if (travelTimes == null || travelTimes.length != machines)
			this.travelTimes = new int[machines][machines];
		else
			this.travelTimes = travelTimes;
		this.dueDates = dueDates;
		this.objective = objective;
		this.jobWeights = weights;
	}

	public FJSSTTproblem(final int jobs, final int[] operations, final int maxOperations, final int machines, final int timeslots,
			final HashMap<String, List<Integer>> altMachines, final int[][][] processTimes, final int[][] travelTimes, final int[] dueDates,
			final Objective objective, final int[] weights, final Properties configurations) {
		this(jobs, operations, maxOperations, machines, timeslots, altMachines, processTimes, travelTimes, dueDates, objective, weights);
		this.configurations = configurations;
	}

	/**
	 * Calculates average maximum slacks between operations of a job, and before
	 * the first operation. These slacks are job specific.
	 * 
	 * @return An integer array of average maximum slacks. Indices are jobs.
	 */
	public int[] calcAverageMaxSlacks() {
		int[] averageMaxSlacks = new int[jobs];
		for (int job = 0; job < this.jobs; job++) {
			int jobSlack = Math.max(0, dueDates[job] - this.calcMinJobCompletionTime(job));
			Double averageMaxSlack = (double) jobSlack / (operations[job]);
			averageMaxSlacks[job] = averageMaxSlack.intValue();
		}
		return averageMaxSlacks;
	}

	/**
	 * Calculates a lower bound on makespan for a FJSS problem (no travel times
	 * are considered, machine capacity constraints are neglected). This means
	 * that for a job, all operations are assigned to machines minimising process
	 * time, and the begin time for operation (j+1) = completion time for
	 * operation (j) + 1.
	 */
	public int calcLBmakespanFJSS() {
		int minMakespan = -1;
		int[] jobMinCompletionTimes = new int[this.getJobs()];
		for (int job = 0; job < this.getJobs(); job++) {
			int jobMinProcessTime = 0;

			for (int op = 0; op < operations[job]; op++) {
				List<Integer> opAltMachines = getAltMachines(job, op);
				int opMinProcessTime = Integer.MAX_VALUE;
				for (int machine : opAltMachines) {
					if (processTimes[job][op][machine] < opMinProcessTime) {
						opMinProcessTime = processTimes[job][op][machine];
					}
				}
				jobMinProcessTime += opMinProcessTime;
			}
			jobMinCompletionTimes[job] = jobMinProcessTime - 1;
		}

		// identify maximum over the minimum job completion times: this is the
		// makespan
		for (int compTime : jobMinCompletionTimes) {
			if (compTime > minMakespan) {
				minMakespan = compTime;
			}
		}
		return minMakespan;
	}

	/**
	 * Implements a dynamic programming algorithm to determine the minimum
	 * completion time of a job, considering different process times on machines
	 * and travel times between machines.
	 * 
	 * @param job
	 *           The job under consideration.
	 * @return The minimum completion time of the job.
	 */
	public int calcMinJobCompletionTime(int job) {
		int jobCompTime = Integer.MAX_VALUE;

		/*
		 * v[j][m] is the minimum completion time of operation j assigned to
		 * machine m.
		 */
		int[][] v = new int[operations[job]][machines];

		// first stage: calculate v values for operation 0
		List<Integer> opAltMachines = getAltMachines(job, 0);
		for (int opMachine : opAltMachines) {
			v[0][opMachine] = processTimes[job][0][opMachine] - 1;
		}

		// iterative stages
		for (int op = 1; op < operations[job]; op++) {
			int previousOp = op - 1;
			opAltMachines = this.altMachines.get(job + "-" + op);
			for (int opMachine : opAltMachines) {
				// v[op][opMachine] is minimium over machine(op-1)
				// {v[op-1][machine(op-1)] + travel time
				// (machine(op-1),opMachine) + process time of op on opMachine
				v[op][opMachine] = Integer.MAX_VALUE;
				List<Integer> previousOpAltMachines = getAltMachines(job, previousOp);
				for (int previousOpMachine : previousOpAltMachines) {
					int new_v = v[op - 1][previousOpMachine] + travelTimes[previousOpMachine][opMachine] + processTimes[job][op][opMachine];
					if (new_v < v[op][opMachine]) {
						v[op][opMachine] = new_v;
					}
				}
			}
		}

		// last stage, determine minimum v value for last operation, which is the
		// minimum job completion time
		int lastOp = this.operations[job] - 1;
		opAltMachines = this.altMachines.get(job + "-" + lastOp);
		for (int lastOpMachine : opAltMachines) {
			if (v[lastOp][lastOpMachine] < jobCompTime) {
				jobCompTime = v[lastOp][lastOpMachine];
			}
		}
		return jobCompTime;
	}

	public SubproblemInstance createSubproblem(int job) {

		final int operations = getOperations()[job];
		final int machines = getMachines();
		final int timeslots = getTimeSlots();

		// set the operations' alternative machines

		// the mapping of alternative machines to operations for the job
		// subproblem
		final HashMap<Integer, List<Integer>> altMachinesMapping = new HashMap<Integer, List<Integer>>();

		// loop over job operations
		for (int j = 0; j < getOperations()[job]; j++) {
			// the set of alternative machines for operation (i,j)
			final List<Integer> altMachines = getAltMachines(job, j); // FIXME:
																							// debug
			altMachinesMapping.put(j, altMachines);
		}

		// set the operations' process times on machines

		final int[][] processTimes_subproblem = new int[getOperations()[job]][getMachines()];
		final int[][][] processTimes_problem = getProcessTimes();
		for (int j = 0; j < getOperations()[job]; j++) {
			System.arraycopy(processTimes_problem[job][j], 0, processTimes_subproblem[j], 0, getMachines());
		}

		final int[][] travelTimes = getTravelTimes();
		final int dueDate = getDueDates()[job];
		final int jobWeight = getJobWeights()[job];

		return new SubproblemInstance(job, operations, machines, timeslots, altMachinesMapping, processTimes_subproblem, travelTimes, dueDate,
				jobWeight, getObjective());
	}

	/**
	 * 
	 * NOTE: all subproblems share the same array of Lagrange multipliers.
	 * 
	 */
	public SubproblemInstance[] createSubproblems() {
		// removed parameter final FJSSTT_problem problem
		// -> now using mProblem

		final SubproblemInstance[] subproblems = new SubproblemInstance[getJobs()];

		// loop over all jobs
		for (int job = 0; job < getJobs(); job++) {
			subproblems[job] = createSubproblem(job);
		}

		return subproblems;
	}

	/**
	 * Create random travel times "mTravelTimes" between machines for a
	 * production network consisting of 3 shops. Machines are randomly assigned
	 * to shops, similar to the drawing process for the soccer worldcup. Travel
	 * time within a shop is 0. Travel time between 2 shops is a random number
	 * within given limits. The triangle inequality for travel times is
	 * satisfied. Travel times are symmetric.
	 * 
	 * @param lowerLimit
	 *           The lower limit for a travel time.
	 * @param upperLimit
	 *           The upper limit for a travel time.
	 * @param pwA
	 */
	public void createTravelTimes(double lowerLimit, double upperLimit, PrintWriter pw) {

		Random rng = new Random();

		// an array entry is the assigned shop, indices are machine
		int shopAssignments[] = new int[machines];

		ArrayList<Integer> machineList = new ArrayList<Integer>();

		for (int i = 0; i < this.machines; i++) {
			machineList.add(i);
		}

		int shop = 0;
		while (machineList.size() > 0) {
			// draw random machine and assign to "shop"
			int randomIndex = rng.nextInt(machineList.size());
			int randomMachine = machineList.get(randomIndex);
			machineList.remove(randomIndex);
			shopAssignments[randomMachine] = shop;
			// shops.get(shop).add(randomMachine);
			shop++;
			if (shop == 3)
				shop = 0;
		}

		// create random travel times between shops, satisfying the triangle
		// inequality
		boolean triangleInequalSatisfied = false;
		int[][] travelTimesShops = new int[3][3];

		while (!triangleInequalSatisfied) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (i != j && j > i) {
						double travelTime = lowerLimit + (upperLimit - lowerLimit) * rng.nextDouble();
						travelTimesShops[i][j] = Math.round((float) travelTime);
						travelTimesShops[j][i] = travelTimesShops[i][j]; // symmetric
																							// travel
																							// times
					}
				}
			}
			// check triangle inequality for the three connections
			if (travelTimesShops[0][2] < travelTimesShops[0][1] + travelTimesShops[1][2]
					&& travelTimesShops[0][1] < travelTimesShops[0][2] + travelTimesShops[2][1]
					&& travelTimesShops[1][2] < travelTimesShops[1][0] + travelTimesShops[0][2])
				triangleInequalSatisfied = true;
		}

		// Create matrix of travel times between machines
		for (int i = 0; i < machines; i++) {
			for (int j = 0; j < machines; j++) {
				if (i != j && j > i) {
					int shop_i = shopAssignments[i];
					int shop_j = shopAssignments[j];
					if (shop_i == shop_j)
						travelTimes[i][j] = 0;
					else
						travelTimes[i][j] = travelTimesShops[shop_i][shop_j];
					travelTimes[j][i] = travelTimes[i][j];
				}
			}
		}
		logger.debug("");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < machines; i++) {
			sb.append("" + shopAssignments[i]);
			sb.append(",");
		}
		logger.debug(sb.toString());
		printMatrix(travelTimesShops);
		printMatrix(travelTimes, pw);
	}

	public List<Integer> getAltMachines(int job, int op) {
		return altMachines.get(job + "-" + op);
	}

	public Properties getConfigurations() {
		return configurations;
	}

	public int[] getDueDates() {
		return dueDates;
	}

	public int getJobs() {
		return jobs;
	}

	/**
	 * @return the mJobWeights
	 */
	public int[] getJobWeights() {
		return jobWeights;
	}

	public int getMachines() {
		return machines;
	}

	/**
	 * @return the mMaxOperations
	 */
	public int getMaxOperations() {
		return maxOperations;
	}

	/**
	 * @return the mObjective
	 */
	public Objective getObjective() {
		return objective;
	}

	public int[] getOperations() {
		return operations;
	}

	public int[][][] getProcessTimes() {
		return processTimes;
	}

	public int getTimeSlots() {
		return timeSlots;
	}

	public int[][] getTravelTimes() {
		return travelTimes;
	}
	
	public void setProblemId(int id){
		problemId = id;
	}
	
	/**
	 * returns the prblem ID; if non is set, one is generated on the fly
	 * @return
	 */
	public int getProblemId(){
		if(problemId==-1)
			problemId = super.hashCode();
		return problemId;
	}

	public void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			logger.debug("");
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < matrix[i].length; j++) {
				sb.append("" + matrix[i][j]);
				sb.append(" ");
			}
			logger.debug(sb.toString());
		}
	}

	public void printMatrix(int[][] matrix, PrintWriter pw) {
		for (int i = 0; i < matrix.length; i++) {
			logger.debug("");
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < matrix[i].length; j++) {
				sb.append(matrix[i][j]);
				pw.print(matrix[i][j]);
				sb.append(" ");
				pw.print(" ");
			}
			pw.println();
			logger.debug(sb.toString());
		}
		System.out.println();
	}

	/**
	 * @param mJobWeights
	 *           the mJobWeights to set
	 */
	public void setJobWeights(final int[] mJobWeights) {
		this.jobWeights = mJobWeights;
	}

	/**
	 * @param mObjective
	 *           the mObjective to set
	 */
	public void setObjective(final Objective mObjective) {
		this.objective = mObjective;
	}

	public void setTimeSlots(final int timeslots) {
		this.timeSlots = timeslots;
	}

	/**
	 * @param mTravelTimes
	 *           the mTravelTimes to set
	 */
	public void setTravelTimes(final int[][] mTravelTimes) {
		this.travelTimes = mTravelTimes;
	}
}
