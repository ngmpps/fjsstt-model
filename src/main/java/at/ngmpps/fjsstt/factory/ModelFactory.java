package at.ngmpps.fjsstt.factory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ngmpps.fjsstt.model.MyBean;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.ScheduledOperation;
import at.ngmpps.fjsstt.model.SolutionSet;

public class ModelFactory {
	private static final Map<Integer, Float> myMap;

	static {
		Map<Integer, Float> aMap = new HashMap<>();
		aMap.put(1, 1.1f);
		aMap.put(2, 1.2f);
		myMap = Collections.unmodifiableMap(aMap);
	}

	private static final List<String> myList;

	static {
		List<String> aList = new ArrayList<>();
		aList.add("entry1");
		aList.add("entry2");
		myList = Collections.unmodifiableList(aList);
	}

	public static MyBean createBean(String uri) {
		final MyBean myBean = new MyBean(URI.create(uri), "mybean", "mydata", myList, myMap);
		return myBean;
	}

	public static ProblemSet createDummyProblemSet() {
		return new ProblemSet("dummyFJS", "dummyTransport", "dummyProp=xyz");
	}

	public static ProblemSet createProblemSet(String fjs, String transport, String properties) {
		return new ProblemSet(fjs, transport, properties);
	}

	public static ProblemSet createSrfgProblemSet() {
		final ProblemSet ps = new ProblemSet(
				"15 28\n" + "6 2 2 3 5 5 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 3 25 5 26 4 28 6 3 19 5 20 6 21 8 0 45 1\n"
						+ "6 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 19 5 20 6 21 8 2 14 4 15 5 3 22 4 23 3 24 6 2 2 3 5 5 0 35 2\n"
						+ "6 2 14 4 15 5 3 25 5 26 4 28 6 3 19 5 20 6 21 8 3 19 5 20 6 21 8 2 2 3 5 5 3 22 4 23 3 24 6 0 40 3\n"
						+ "6 2 14 4 15 5 3 19 5 20 6 21 8 2 2 3 5 5 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 22 4 23 3 24 6 0 50 2\n"
						+ "6 3 19 5 20 6 21 8 2 2 3 5 5 3 25 5 26 4 28 6 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 0 45 3\n"
						+ "7 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1\n"
						+ "7 2 1 2 6 3 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 55 2\n"
						+ "7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 2 27 3 5 4 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 3 7 7 8 8 9 6 0 55 1\n"
						+ "7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 27 3 5 4 3 7 7 8 8 9 6 0 50 3\n"
						+ "7 2 27 3 5 4 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1\n"
						+ "5 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 1\n"
						+ "5 3 19 5 20 6 21 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 0 25 2\n"
						+ "5 3 19 5 20 6 21 8 3 7 5 8 4 9 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 0 25 3\n"
						+ "5 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 4\n"
						+ "5 3 7 5 8 4 9 8 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 19 5 20 6 21 8 0 35 4",

				"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n"
						+ "0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n"
						+ "0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n"
						+ "22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n"
						+ "5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n"
						+ "22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n"
						+ "0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n"
						+ "0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n"
						+ "22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n"
						+ "8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18\n"
						+ "8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18\n"
						+ "4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n"
						+ "4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n"
						+ "13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\n"
						+ "11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\n"
						+ "5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n"
						+ "5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n"
						+ "7;7;7;15;6;15;7;7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15\n"
						+ "13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\n"
						+ "13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\n"
						+ "7;7;7;15;6;15;7;7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15\n"
						+ "4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n"
						+ "4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n"
						+ "11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\n"
						+ "4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n"
						+ "11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\n"
						+ "5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n"
						+ "22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n",

				"SimpleSearch.Alpha=2.0\n" + "#SubgradientSearch.TransportFile=\n" + "SubproblemSolver.LS_iterations=150\n"
						+ "SubproblemSolver.MinMaxSlack=5\n" + "SimpleSearch.UpperBoundary=138\n" + "SurrogateSearch.R=0.1\n"
						+ "SubgradientSearch.NrTimeSlots=800\n" + "SurrogateSearch.M=25\n"
						+ "SurrogateSearch.IterationsUntilFeasibilityRepair=2\n" + "SubproblemSolver.VNSiterations=100\n"
						+ "SubproblemSolver.MaxShakingDistance=3\n" + "SurrogateSearch.NoSubproblems=10\n"
						+ "SurrogateSearch.FixedInitialStepsize=true\n" + "SurrogateSearch.EstimatedOptimalDualCost=-1\n"
						+ "SurrogateSearch.InitialStepsize=0.2\n" + "SubgradientSearch.SearchType=Both\n"
						+ "SubproblemSolver.type=VariableNeighbourhoodSearch\n" + "SimpleSearch.IterationsUntilHalvingAlpha=20\n"
						+ "SubproblemSolver.LS_altMachine_tries=1\n" + "SurrogateSearch.NrRuns=10\n" + "SubproblemSolver.MinMaxShiftDistance=50\n"
						+ "SimpleSearch.IterationsUntilFeasibilityRepair=2\n");
		return ps;
	}

	private static final Map<String, List<ScheduledOperation>> mySolution;

	static {
		Map<String, List<ScheduledOperation>> aSolution = new HashMap<>();
		List<ScheduledOperation> job0 = new ArrayList<ScheduledOperation>();
		//"Job 0", "(4,0,5) (13,9,4) (20,15,8) (22,29,3) (24,32,5) (19,48,6)"
		job0.add(new ScheduledOperation(4,0,0,0,5));
		job0.add(new ScheduledOperation(13,0,1,9,13));
		job0.add(new ScheduledOperation(20,0,2,15,23));
		job0.add(new ScheduledOperation(22,0,3,29,32));
		job0.add(new ScheduledOperation(24,0,4,32,37));
		job0.add(new ScheduledOperation(19,0,5,48,54));
		aSolution.put("Job 0", job0);
		
		//"Job 1", "(19,0,6) (27,13,6) (18,26,5) (13,31,4) (22,46,3) (1,53,3)"
		List<ScheduledOperation> job1 = new ArrayList<ScheduledOperation>();
		job1.add(new ScheduledOperation(19,1,0,0,6));
		job1.add(new ScheduledOperation(27,1,1,13,19));
		job1.add(new ScheduledOperation(18,1,2,26,31));
		job1.add(new ScheduledOperation(13,1,3,31,35));
		job1.add(new ScheduledOperation(22,1,4,46,49));
		job1.add(new ScheduledOperation(1,1,5,53,56));
		aSolution.put("Job 1", job1);
		
		//"Job 2", "(14,0,5) (25,5,4) (20,23,8) (18,33,5) (4,41,5) (21,51,4)"
		List<ScheduledOperation> job2 = new ArrayList<ScheduledOperation>();
		job2.add(new ScheduledOperation(14,2,0,0,5));
		job2.add(new ScheduledOperation(25,2,1,5,9));
		job2.add(new ScheduledOperation(20,2,2,23,31));
		job2.add(new ScheduledOperation(18,2,3,33,38));
		job2.add(new ScheduledOperation(4,2,4,41,46));
		job2.add(new ScheduledOperation(21,2,5,51,55));
		aSolution.put("Job 2", job2);
		
		
		//"Job 3", "(13,5,4) (18,9,5) (4,17,5) (19,25,6) (24,42,5) (21,47,4)"
		List<ScheduledOperation> job3 = new ArrayList<ScheduledOperation>();
		job3.add(new ScheduledOperation(13,3,0,5,9));
		job3.add(new ScheduledOperation(18,3,1,9,14));
		job3.add(new ScheduledOperation(4,3,2,17,22));
		job3.add(new ScheduledOperation(19,3,3,25,31));
		job3.add(new ScheduledOperation(24,3,4,42,47));
		job3.add(new ScheduledOperation(21,3,5,47,51));
		aSolution.put("Job 3", job3);

		
		//"Job 4", "(18,0,5) (4,8,5) (25,19,4) (14,23,5) (20,35,8) (22,49,3)"
		List<ScheduledOperation> job4 = new ArrayList<ScheduledOperation>();
		job4.add(new ScheduledOperation(18,4,0,0,5));
		job4.add(new ScheduledOperation(4,4,1,8,13));
		job4.add(new ScheduledOperation(25,4,2,19,23));
		job4.add(new ScheduledOperation(14,4,3,23,28));
		job4.add(new ScheduledOperation(20,4,4,35,43));
		job4.add(new ScheduledOperation(22,4,5,49,52));
		aSolution.put("Job 4", job4);
		
		
		//"Job 5", "(11,0,3) (24,3,6) (26,13,3) (0,21,2) (2,33,6) (6,40,7) (14,58,4)"
		List<ScheduledOperation> job5 = new ArrayList<ScheduledOperation>();
		job5.add(new ScheduledOperation(11,5,0,0,3));
		job5.add(new ScheduledOperation(24,5,1,3,9));
		job5.add(new ScheduledOperation(26,5,2,13,16));
		job5.add(new ScheduledOperation(0,5,3,21,23));
		job5.add(new ScheduledOperation(2,5,4,33,39));
		job5.add(new ScheduledOperation(6,5,5,40,47));
		job5.add(new ScheduledOperation(14,5,6,58,62));
		aSolution.put("Job 5", job5);
		
		
		//"Job 6", "(0,0,2) (11,6,3) (24,9,6) (26,19,3) (2,27,6) (6,33,7) (14,51,4)"
		List<ScheduledOperation> job6 = new ArrayList<ScheduledOperation>();
		job6.add(new ScheduledOperation(0,6,0,0,2));
		job6.add(new ScheduledOperation(11,6,1,6,9));
		job6.add(new ScheduledOperation(24,6,2,9,15));
		job6.add(new ScheduledOperation(26,6,3,19,22));
		job6.add(new ScheduledOperation(2,6,4,27,33));
		job6.add(new ScheduledOperation(6,6,5,33,40));
		job6.add(new ScheduledOperation(14,6,6,51,55));
		aSolution.put("Job 6", job6);
		
		
		//"Job 7", "(14,5,4) (2,20,6) (0,26,2) (26,33,3) (11,40,3) (27,49,4) (8,53,6)"
		List<ScheduledOperation> job7 = new ArrayList<ScheduledOperation>();
		job7.add(new ScheduledOperation(0,7,0,5,9));
		job7.add(new ScheduledOperation(11,7,1,20,26));
		job7.add(new ScheduledOperation(24,7,2,26,28));
		job7.add(new ScheduledOperation(26,7,3,33,36));
		job7.add(new ScheduledOperation(2,7,4,40,43));
		job7.add(new ScheduledOperation(6,7,5,49,53));
		job7.add(new ScheduledOperation(14,7,6,53,59));
		aSolution.put("Job 7", job7);
		
		
		//"Job 8", "(13,0,5) (3,12,7) (5,19,3) (27,22,4) (11,32,3) (26,39,3) (6,47,7)"
		List<ScheduledOperation> job8 = new ArrayList<ScheduledOperation>();
		job8.add(new ScheduledOperation(13,8,0,0,5));
		job8.add(new ScheduledOperation(3,8,1,12,19));
		job8.add(new ScheduledOperation(5,8,2,19,22));
		job8.add(new ScheduledOperation(27,8,3,22,26));
		job8.add(new ScheduledOperation(11,8,4,32,35));
		job8.add(new ScheduledOperation(26,8,5,39,42));
		job8.add(new ScheduledOperation(6,8,6,47,54));
		aSolution.put("Job 8", job8);
		
		
		//"Job 9", "(26,0,3) (25,9,5) (11,18,3) (5,27,3) (3,30,7) (8,37,6) (13,50,5)"
		List<ScheduledOperation> job9 = new ArrayList<ScheduledOperation>();
		job9.add(new ScheduledOperation(26,9,0,0,3));
		job9.add(new ScheduledOperation(25,9,1,9,14));
		job9.add(new ScheduledOperation(11,9,2,18,21));
		job9.add(new ScheduledOperation(5,9,3,27,30));
		job9.add(new ScheduledOperation(3,9,4,30,37));
		job9.add(new ScheduledOperation(8,9,5,37,43));
		job9.add(new ScheduledOperation(13,9,6,50,55));
		aSolution.put("Job 9", job9);
		
		
		//"Job 10", "(9,0,8) (9,53,9) (10,62,9) (7,79,4) (20,90,8)"
		List<ScheduledOperation> job10 = new ArrayList<ScheduledOperation>();
		job10.add(new ScheduledOperation(9,10,0,0,8));
		job10.add(new ScheduledOperation(9,10,1,53,62));
		job10.add(new ScheduledOperation(10,10,2,62,71));
		job10.add(new ScheduledOperation(7,10,3,79,83));
		job10.add(new ScheduledOperation(20,10,4,90,98));
		aSolution.put("Job 10", job10);
		
		
		//"Job 11", "(18,14,5) (10,36,9) (9,45,8) (10,53,8) (7,69,4)"
		List<ScheduledOperation> job11 = new ArrayList<ScheduledOperation>();
		job11.add(new ScheduledOperation(18,11,0,14,19));
		job11.add(new ScheduledOperation(10,11,1,36,45));
		job11.add(new ScheduledOperation(9,11,2,45,53));
		job11.add(new ScheduledOperation(10,11,3,53,61));
		job11.add(new ScheduledOperation(7,11,4,69,73));
		aSolution.put("Job 11", job11);
		
		
		//"Job 12", "(20,0,8) (7,15,4) (9,27,10) (9,37,8) (10,45,8)"
		List<ScheduledOperation> job12 = new ArrayList<ScheduledOperation>();
		job12.add(new ScheduledOperation(20,12,0,0,8));
		job12.add(new ScheduledOperation(7,12,1,15,19));
		job12.add(new ScheduledOperation(9,12,2,27,37));
		job12.add(new ScheduledOperation(9,12,3,37,45));
		job12.add(new ScheduledOperation(10,12,4,45,53));
		aSolution.put("Job 12", job12);
		
		
		//"Job 13", "(10,0,9) (9,9,8) (9,17,9) (7,34,4) (20,45,8)"
		List<ScheduledOperation> job13 = new ArrayList<ScheduledOperation>();
		job13.add(new ScheduledOperation(10,13,0,0,9));
		job13.add(new ScheduledOperation(9,13,1,9,17));
		job13.add(new ScheduledOperation(9,13,2,17,26));
		job13.add(new ScheduledOperation(7,13,3,34,38));
		job13.add(new ScheduledOperation(20,13,4,45,53));
		aSolution.put("Job 13", job13);		
		
		
		//"Job 14", "(7,0,4) (10,12,7) (10,19,8) (10,27,9) (18,48,5)"
		List<ScheduledOperation> job14 = new ArrayList<ScheduledOperation>();
		job14.add(new ScheduledOperation(7,14,0,0,4));
		job14.add(new ScheduledOperation(10,14,1,12,19));
		job14.add(new ScheduledOperation(10,14,2,19,27));
		job14.add(new ScheduledOperation(10,14,3,27,36));
		job14.add(new ScheduledOperation(18,14,4,48,53));
		aSolution.put("Job 14", job14);

		
		mySolution = Collections.unmodifiableMap(aSolution);
	}

	public static SolutionSet createSrfgSolutionSet(ProblemSet problem) {
		final SolutionSet ss = new SolutionSet("Algorithmus 1", problem.getFjs(), problem.getTransport(), problem.getProperties(), mySolution,
				537.5, 434.4);
		return ss;
	}

	public static SolutionSet noSolutionSet() {
		return null;
	}

	public static SolutionSet emptySolutionSet() {
		// rem values for config make no sense
		final SolutionSet ss = new SolutionSet("noname", "", "", "", null, -1.0,-1.0);
		return ss;
	}
}
