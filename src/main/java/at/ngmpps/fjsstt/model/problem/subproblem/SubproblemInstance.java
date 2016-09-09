package at.ngmpps.fjsstt.model.problem.subproblem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.model.problem.FJSSTTproblem.Objective;

/**
 * Specifies a subproblem instance of the one job scheduling problem with
 * marginal cost for time units on machines.
 * 
 * @author ahaemm
 * 
 */

public class SubproblemInstance implements Serializable {

	private static final long serialVersionUID = -1870103761541777620L;

	static final Logger logger = LoggerFactory.getLogger(SubproblemInstance.class);

	/**
	 * The unique job ID.
	 */
	int jobID;

	/**
	 * The weight of the job.
	 */
	int jobWeight;

	/**
	 * The number of operations. The set of operations is {0,...,mOperations-1};
	 * It is assumed that the operations are to be processed in the order
	 * 0,...,mOperations - 1.
	 */
	int operations;

	/**
	 * The number of machines. The set of machines is {0,...,mMachines-1};
	 */
	int machines;

	/**
	 * The number of time units, indexed by {0,...,mTimeUnits - 1}. Each
	 * operation beginning time is defined as the beginning of the corresponding
	 * time unit, and each completion time the end of the time unit.
	 */
	int timeSlots;

	/**
	 * The sets of alternative machines per operation. A key is an operation, the
	 * corresponding value is the set of alternative machines.
	 */
	Map<Integer, List<Integer>> altMachines;

	/**
	 * The process times of operations on machines. The first index is the
	 * operation, the second index is the machine.
	 */
	int[][] processTimes;

	/**
	 * The travel times between machines. Both indices denote machines.
	 */
	int[][] travelTimes;

	/**
	 * The Lagrange multipliers, i.e. the marginal cost for time units on
	 * machines. The first index is the machine, the second index is the time
	 * unit.
	 */
	double[][] multipliers;

	/**
	 * The job's due date.
	 */
	int dueDate;

	int timezoneLength = 0;

	double timezoneFactor = 0;

	/**
	 * The optimisation objective, default is TARDINESS.
	 */
	Objective objective;

	public SubproblemInstance(final int id) {
		jobID = id;
		jobWeight = 1;
		objective = Objective.TARDINESS;
	}

	public SubproblemInstance(final int id, final int operations, final int machines, final int timeslots,
			final Map<Integer, List<Integer>> altMachines, final int[][] processTimes, final int[][] travelTimes, final int dueDate,
			final int jobWeight, final Objective objective) {
		this.jobID = id;
		this.operations = operations;
		this.machines = machines;
		this.timeSlots = timeslots;
		this.altMachines = altMachines;
		this.processTimes = processTimes;
		this.travelTimes = travelTimes;
		this.dueDate = dueDate;
		this.jobWeight = jobWeight;
		this.objective = objective;
		this.multipliers = new double[machines][timeSlots];
	}

	/**
	 * Calculates the total cost of a subproblem, when using the augmented price
	 * adjustment. The total cost is the sum of the Lagrangian multipliers and
	 * the sum of the decision variables of a time zone squared times a scaling
	 * factor.
	 * 
	 * @param objective
	 * @param bid
	 *           The given solution.
	 * @param multipliers
	 *           The given multiplier values. Indices are [machine][time slot].
	 * @param factor
	 *           The scaling factor for the augmented price update
	 * @param timezone
	 *           The number of slot which compromise a time zone
	 * @return
	 */
	public double calcAugmentedCost(final Objective objective, final Bid bid, final double[][] multipliers) {
		double cost = 0;
		this.multipliers = multipliers;

		// calculate machine utilisation costs, sum over all operations
		for (int op = 0; op < operations; op++) {
			// operation begin time
			final int op_beginTime = bid.getOptimumBeginTimes()[op];
			final int op_machine = bid.getOptimumMachines()[op];
			final int op_completionTime = op_beginTime + processTimes[op][op_machine] - 1;

			for (int k = op_beginTime; k <= op_completionTime; k++) {
				cost += multipliers[op_machine][k];
			}

			final int zones = processTimes[op][op_machine] / timezoneLength + 1;
			final int lastZone = processTimes[op][op_machine] % timezoneLength;

			for (int k = 1; k <= zones; k++) {
				if (k < zones) {
					cost += timezoneLength * timezoneLength * timezoneFactor;
				} else {
					cost += lastZone * lastZone * timezoneFactor;
				}
			}
		}

		// add job objective value
		final int lastOp_beginTime = bid.getOptimumBeginTimes()[operations - 1];
		final int lastOp_machine = bid.getOptimumMachines()[operations - 1];

		cost += this.calcObjectiveValue(objective, lastOp_beginTime, lastOp_machine);

		return cost;
	}

	/**
	 * Calculates the total cost of a subproblem with a given solution (encoded
	 * in a bid) and with given Lagrange multiplier values. Total cost is the sum
	 * of objective value (e.g. tardiness) and utilisation costs of machines.
	 * 
	 * @param bid
	 *           The given solution.
	 * @param multipliers
	 *           The given multiplier values. Indices are [machine][time slot].
	 * @return The total cost.
	 */
	public double calcCost(final Objective objective, final Bid bid, final double[][] multipliers) {
		double cost = 0;
		this.multipliers = multipliers;

		// calculate machine utilisation costs, sum over all operations
		for (int op = 0; op < operations; op++) {
			// operation begin time
			final int op_beginTime = bid.getOptimumBeginTimes()[op];
			final int op_machine = bid.getOptimumMachines()[op];
			final int op_completionTime = op_beginTime + processTimes[op][op_machine] - 1;

			for (int k = op_beginTime; k <= op_completionTime; k++) {
				cost += multipliers[op_machine][k];
			}
		}

		// add job objective value
		final int lastOp_beginTime = bid.getOptimumBeginTimes()[operations - 1];
		final int lastOp_machine = bid.getOptimumMachines()[operations - 1];

		cost += this.calcObjectiveValue(objective, lastOp_beginTime, lastOp_machine);

		return cost;
	}

	/**
	 * Calculate the objective value as function of the last operation's
	 * completion time.
	 * 
	 * @param objective
	 *           The objective function for a single job. Completion time or
	 *           tardiness.
	 * @param beginTime
	 *           The beginning time of the last operation, i.e. the operation
	 *           (mOperation - 1).
	 * @param machine
	 *           The machine assigned to the last operation.
	 * 
	 * @return The objective value. The default value is the last operation's
	 *         completion time.
	 */
	public double calcObjectiveValue(final Objective objective, final int beginTime, final int machine) {

		// the completion time of the last operation.
		final double compTime = beginTime + this.processTimes[operations - 1][machine] - 1;

		switch (objective) {

		case COMPLETION_TIME:

			return compTime * jobWeight;

		case TARDINESS:

			if (dueDate <= 0) {
				logger.warn("WARNING: job due date not set, but objective is tardiness. Returning job completion time.");
				return compTime;
			}

			return Math.max(0, compTime - dueDate) * jobWeight;
		}

		return compTime;

	}

	public void calcRandomMultipliers() {
		calcRandomMultipliers(300);
	}

	public void calcRandomMultipliers(int seed) {
		this.calcRandomMultipliers(seed, 5);
	}

	/**
	 * Calculates random values for Lagrange multipliers from the interval
	 * (0,upperBound].
	 * 
	 * @param seed
	 * @param upperBound
	 *           The upper bound of the interval.
	 */
	public void calcRandomMultipliers(int seed, double upperBound) {
		Random rng = new Random(seed);

		final double[][] lambda = new double[getMachines()][getTimeSlots()];
		for (int m = 0; m < getMachines(); m++) {
			for (int t = 0; t < getTimeSlots(); t++) {
				lambda[m][t] = rng.nextDouble() * upperBound;
			}
		}
		setMultipliers(lambda);
	}

	public Map<Integer, List<Integer>> getAltMachines() {
		return altMachines;
	}

	public int getDueDate() {
		return dueDate;
	}

	public int getJobID() {
		return jobID;
	}

	public int getJobWeight() {
		return jobWeight;
	}

	public int getMachines() {
		return machines;
	}

	public double[][] getMultipliers() {
		return multipliers;
	}

	public Objective getObjective() {
		return objective;
	}

	public int getOperations() {
		return operations;
	}

	public int[][] getProcessTimes() {
		return processTimes;
	}

	public int getTimeSlots() {
		return timeSlots;
	}

	public double getTimezoneFactor() {
		return timezoneFactor;
	}

	public int getTimezoneLength() {
		return timezoneLength;
	}

	public int[][] getTravelTimes() {
		return travelTimes;
	}

	public void printPrices() {
		for (int m = 0; m < machines; m++) {
			String row = new String();
			for (int k = 0; k < timeSlots; k++) {
				row = row + "[ " + multipliers[m][k] + " ]";
			}
			logger.debug(row);
		}
	}

	public void setAltMachines(HashMap<Integer, List<Integer>> altMachines) {
		this.altMachines = altMachines;
	}

	public void setDueDate(int dueDate) {
		this.dueDate = dueDate;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public void setJobWeight(int jobWeight) {
		this.jobWeight = jobWeight;
	}

	public void setMachines(int machines) {
		this.machines = machines;
	}

	public void setMultipliers(double[][] multipliers) {
		this.multipliers = multipliers;
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}

	public void setOperations(int operations) {
		this.operations = operations;
	}

	public void setProcessTimes(int[][] processTimes) {
		this.processTimes = processTimes;
	}

	public void setTimeSlots(int timeSlots) {
		this.timeSlots = timeSlots;
	}

	public void setTimezoneFactor(double timezoneFactor) {
		this.timezoneFactor = timezoneFactor;
	}

	public void setTimezoneLength(int timezoneLength) {
		this.timezoneLength = timezoneLength;
	}

	public void setTravelTimes(int[][] travelTimes) {
		this.travelTimes = travelTimes;
	}

}
