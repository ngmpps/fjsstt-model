package at.ngmpps.fjsstt.model.problem.subproblem;

import java.io.Serializable;
import java.util.Properties;

import at.ngmpps.fjsstt.factory.ProblemParser;

/**
 * Configuration parameters for the subproblem solver. A VNS solver requires
 * subproblem specific configuration, a DP solver requires no configuration.
 * 
 * @author ahaemm
 * 
 */
public class SubproblemSolverConfig implements Serializable {

	private static final long serialVersionUID = 3512198032457722173L;

	public static final String TYPE_KEY = "SubproblemSolver.type";
	public static final String TYPE_VNS = SubproblemSolverType.VariableNeighbourhoodSearch.name();//"VariableNeighbourhoodSearch";
	public static final String TYPE_DP = SubproblemSolverType.DynamicProgramming.name();//"DynamicProgramming";
	/**"EXECUTION_ITERATIONS_KEY replaces VNS_ITERATIONS_KEY"*/
	public static final String EXECUTION_ITERATIONS_KEY = "SubproblemSolver.Execution.Iterations";
	//public static final String VNS_ITERATIONS_KEY = "SubproblemSolver.VNSiterations";
	public static final String EXECUTION_TIME_KEY = "SubproblemSolver.Execution.Time";
	public static final String MAX_SHAKING_DISTANCE_KEY = "SubproblemSolver.MaxShakingDistance";
	public static final String LS_ITERATIONS_KEY = "SubproblemSolver.LS_iterations";
	public static final String LS_ALT_MACHINE_TRIES_KEY = "SubproblemSolver.LS_altMachine_tries";
	public static final String MIN_MAX_SLACK_KEY = "SubproblemSolver.MinMaxSlack";
	public static final String MIN_MAX_SHIFT_DISTANCE = "SubproblemSolver.MinMaxShiftDistance";

	/**
	 * available subproblem solver types are dynamic programming and variable
	 * neighbourhood search.
	 */
	private SubproblemSolverType type;
 
	String confString;
	/**
	 * VNS config parameter, only used if executiontime==-1 see {@link VNS_subproblem#executionIterations}
	 */
	private int executionIterations = -1;
	
	/**
	 * VNS config parameter; override iterations
	 */
	private int executionTime = -1;

	/**
	 * An array of job specific max slack values, indices are jobs. For further
	 * information see {@link VNS_subproblem#maxSlacks}
	 */
	private int[] maxSlacks;

	/**
	 * The maximum begin time of the first operation, when creating an initial
	 * random solution.
	 */
	// private int mMaxBeginTimeFirstOp;

	/**
	 * VNS config parameter, see {@link VNS_subproblem#maxShakingDistance}
	 */
	private int maxShakingDistance;

	/**
	 * VNS config parameter, see {@link VNS_subproblem#ls_iterations}
	 */
	private int ls_iterations;

	/**
	 * An array of job specific maximum shift distances, indices are jobs.
	 * Experiments revealed that it's useful that the shift values correspond to
	 * the max slack values. For further information see
	 * {@link VNS_subproblem#maxShiftDistances}
	 */
	private int[] maxShiftDistances;

	/**
	 * VNS config parameter, see {@link VNS_subproblem#ls_altMachine_tries}
	 */
	private int ls_altMachine_tries;

	public SubproblemSolverConfig(Properties config) {
		this(ProblemParser.getPropertyString(config, TYPE_KEY).toLowerCase().contains("dynamicprogramming")
				? SubproblemSolverType.DynamicProgramming : SubproblemSolverType.VariableNeighbourhoodSearch,
				ProblemParser.getPropertyInt(config, EXECUTION_ITERATIONS_KEY, -1), ProblemParser.getPropertyInt(config, EXECUTION_TIME_KEY, -1), ProblemParser.getPropertyInt(config, MIN_MAX_SLACK_KEY),
				ProblemParser.getPropertyInt(config, MAX_SHAKING_DISTANCE_KEY), ProblemParser.getPropertyInt(config, LS_ITERATIONS_KEY),
				ProblemParser.getPropertyInt(config, MIN_MAX_SHIFT_DISTANCE), ProblemParser.getPropertyInt(config, LS_ALT_MACHINE_TRIES_KEY));
	}
	
	/**
	 * in certain experiments we need one configuration file but both types of Subproblem Solvers.
	 * 
	 * @param type
	 * 	overrides the value given in the properties.
	 * @param config
	 * 
	 */
	public SubproblemSolverConfig(SubproblemSolverType type, Properties config) {
		this(type,
				ProblemParser.getPropertyInt(config, EXECUTION_ITERATIONS_KEY, -1), ProblemParser.getPropertyInt(config, EXECUTION_TIME_KEY, -1), ProblemParser.getPropertyInt(config, MIN_MAX_SLACK_KEY),
				ProblemParser.getPropertyInt(config, MAX_SHAKING_DISTANCE_KEY), ProblemParser.getPropertyInt(config, LS_ITERATIONS_KEY),
				ProblemParser.getPropertyInt(config, MIN_MAX_SHIFT_DISTANCE), ProblemParser.getPropertyInt(config, LS_ALT_MACHINE_TRIES_KEY));
	}

	/**
	 * Use this constructor for DP (no config params needed), or for VNS with
	 * default configuration.
	 * 
	 * @param type
	 */
	public SubproblemSolverConfig(SubproblemSolverType type) {
		this.type = type;
		this.executionIterations = 50; 
		this.executionTime = 11;
		this.maxSlacks = new int[100]; // create default maxSlack values (10) for
													// 100 jobs
		for (int i = 0; i < this.maxSlacks.length; i++) {
			this.maxSlacks[i] = 10;
		}
		this.maxShakingDistance = 3;
		this.ls_iterations = 50;
		this.maxShiftDistances = new int[100]; // create default max shift
															// distance values (10) for 100
															// jobs
		for (int i = 0; i < this.maxShiftDistances.length; i++) {
			this.maxShiftDistances[i] = 10;
		}
		this.ls_altMachine_tries = 1;
		
		confString = "" + executionIterations + ", " + maxSlacks + ", " + maxShakingDistance + ", " + ls_iterations
				+ ", " + maxShiftDistances + ", " + ls_altMachine_tries;
	}

	public SubproblemSolverConfig(SubproblemSolverType type, int ExecutionIterations, int ExecutionTime, int maxSlack, int mMaxShakingDistance, int mLS_iterations,
			int maxShiftDistance, int mLS_altMachine_tries) {
		this(type, ExecutionIterations, ExecutionTime, new int[100], mMaxShakingDistance, mLS_iterations, new int[100], mLS_altMachine_tries, maxSlack,
				maxShiftDistance);
		confString = "" + ExecutionIterations + ", " + ExecutionTime + ", " + maxSlack + ", " + mMaxShakingDistance + ", " + mLS_iterations
				+ ", " + maxShiftDistance + ", " + mLS_altMachine_tries;
	}

	/**
	 * Job specific values for max slack as well as max shift distance are
	 * provided as parameters.
	 * 
	 * @param type
	 * @param VNSiterations
	 * @param mMaxSlack
	 * @param mMaxShakingDistance
	 * @param mLS_iterations
	 * @param mMaxShiftDistance
	 * @param mLS_altMachine_tries
	 * @param minMaxSlack
	 *           The minimum value for max slack.
	 * @param minMaxShiftDistance
	 *           The minimum value for max shift distance.
	 * 
	 */
	public SubproblemSolverConfig(SubproblemSolverType type, int ExecutionIterations, int ExecutionTime, int[] maxSlacks, int mMaxShakingDistance, int mLS_iterations,
			int[] maxShiftDistances, int mLS_altMachine_tries, int minMaxSlack, int minMaxShiftDistance) {
		super();
		this.type = type;
		this.executionIterations = ExecutionIterations;
		this.executionTime = ExecutionTime;
		this.maxSlacks = maxSlacks;
		for (int i = 0; i < this.maxSlacks.length; i++) {
			if (this.maxSlacks[i] < minMaxSlack) {
				this.maxSlacks[i] = minMaxSlack;
			}
		}
		this.maxShakingDistance = mMaxShakingDistance;
		this.ls_iterations = mLS_iterations;
		this.maxShiftDistances = maxShiftDistances;
		for (int i = 0; i < this.maxShiftDistances.length; i++) {
			if (this.maxShiftDistances[i] < minMaxShiftDistance) {
				this.maxShiftDistances[i] = minMaxShiftDistance;
			}
		}
		this.ls_altMachine_tries = mLS_altMachine_tries;
	}

	public int getExecutionIterations() {
		return executionIterations;
	}

	public int getExecutionTime() {
		return executionTime;
	}

	public int getLS_altMachine_tries() {
		return ls_altMachine_tries;
	}

	public int getLS_iterations() {
		return ls_iterations;
	}

	public int getMaxShakingDistance() {
		return maxShakingDistance;
	}

	public int[] getMaxShiftDistances() {
		return maxShiftDistances;
	}

	public int[] getMaxSlacks() {
		return maxSlacks;
	}

	public SubproblemSolverType getType() {
		return type;
	}
	
	public String getConfigString(){
		return confString;
	}

}
