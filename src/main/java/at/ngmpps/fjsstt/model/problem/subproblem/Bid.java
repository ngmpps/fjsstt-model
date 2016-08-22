package at.ngmpps.fjsstt.model.problem.subproblem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data structure for a job's bid in an auction. A bid consists of the job
 * ID, the price and a list of tuples (assigned machine, time slot) with time
 * slots occupied by the job operations. The price is the optimal cost of a one
 * job scheduling problem. Furthermore the bid holds the optimal machine
 * assignments and beginning times for the operations. So a bid is a
 * comprehensive representation of a solution for a one job scheduling problem.
 * 
 * @author ahaemm
 * 
 */

public class Bid implements Serializable {

	private static final long serialVersionUID = 8193409735520951959L;

	/**
	 * Logger configuration in NgMPPS/src/logback.xml, e.g. to switch between
	 * info / debug level.
	 */
	final static Logger mLogger = LoggerFactory.getLogger(Bid.class);

	/**
	 * The unique ID of the job issuing the bid.
	 */
	final private int jobID;

	/**
	 * The price the job is willing to pay. This is exactly the minimum total
	 * cost computed for the single job's scheduling problem.
	 */
	private double price;

	/**
	 * The list of occupied time slots. An entry is a tuple (machine, time slot).
	 */
	private final List<int[]> occupiedTimeSlots;

	/**
	 * The optimum machine assignments. Indices are operations.
	 */
	private final int[] optimumMachines;

	/**
	 * The optimum beginning times for operations. Indices are operations.
	 */
	private final int[] optimumBeginTimes;

	public Bid(final int id, final double price, final int[] optimumMachines, final int[] optimumBeginTimes) {
		this.jobID = id;
		this.price = price;
		this.occupiedTimeSlots = new ArrayList<int[]>();
		this.optimumBeginTimes = optimumBeginTimes;
		this.optimumMachines = optimumMachines;
	}
	
	public Bid(final int id, final double price, final int[] optimumMachines, final int[] optimumBeginTimes, final int[][] processTimes, final int operations) {
		this(id,price,optimumMachines,optimumBeginTimes);
		// loop over operations
		for (int j = 0; j < operations; j++) {

			// the optimal machine for operation j
			final int optMachine = optimumMachines[j];

			// loop over occupied time slots
			for (int t = optimumBeginTimes[j]; t <= optimumBeginTimes[j] + processTimes[j][optMachine]
					- 1; t++) {
				final int[] tuple = { optMachine, t };
				getOccupiedTimeSlots().add(tuple);
			}
		}

	}

	public int getJobID() {
		return jobID;
	}

	public List<int[]> getOccupiedTimeSlots() {
		return occupiedTimeSlots;
	}

	/**
	 * @return the mOptimumBeginTimes
	 */
	public int[] getOptimumBeginTimes() {
		return optimumBeginTimes;
	}

	/**
	 * @return the mOptimumMachines
	 */
	public int[] getOptimumMachines() {
		return optimumMachines;
	}

	public double getPrice() {
		return price;
	}

	public void print(String string) {
		mLogger.info("Printing the bid \"" + string + "\" for subproblem / job with index " + jobID);
		mLogger.info("Kosten: " + this.price);
		mLogger.info("operation, machine");
		for (int i = 0; i < optimumMachines.length; i++) {
			mLogger.info(i + ", " + optimumMachines[i]);
		}
		mLogger.info("operation, beginning time");
		for (int i = 0; i < optimumBeginTimes.length; i++) {
			mLogger.info(i + ", " + optimumBeginTimes[i]);
		}
	}

	/**
	 * @param mPrice
	 *           the mPrice to set
	 */
	public void setPrice(final double mPrice) {
		this.price = mPrice;
	}

}
