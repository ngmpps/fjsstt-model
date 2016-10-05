package at.ngmpps.fjsstt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ngmpps.fjsstt.model.problem.FJSSTTproblem;
import at.ngmpps.fjsstt.model.problem.Solution;

/**
 *
 * Proposed structure of a solution set. The examples in the javadoc refer to the two solution sets given in
 * https://github.com/ngmpps/scenario/blob/master/Loesung.md
 *
 */
public class SolutionSet {

    /**
     * Arbitrary name of the algorithm used (e.g. "Algorithm 1", "Algorithm 2")
     */
    private String name;
    
    /**
     * Id of the Problem for later reference; Its the hashCode of ProblemSet
     */
    private int problemId;

    /**
     * Problem configuration information, to understand what the solution is for 
     */
    private String problemFJS; 
    /**
     * Problem configuration information, to understand what the solution is for 
     */
    private String problemTransport;
    /**
     * Problem configuration information, to understand what the solution is for 
     */
    private String problemConfig;

    /**
     * A Map of Strings representing the solution, one entry per job.
     * The "Key" is the name of the job, the "Value" is the solution itself, e.g.
     *   "Job 0" -> (4,0,5) (13,9,4) (20,15,8) (22,29,3) (24,32,5) (19,48,6)
     *   "Job 1" -> (19,0,6) (27,13,6) (18,26,5) (13,31,4) (22,46,3) (1,53,3)
     *   ...
     *   (TODO: We can structure it even more as "Map<String, Solution>",
     *   where the type Solution would be a List<String> representing the steps of the solution.)
     */
    private Map<String, List<ScheduledOperation>> solution;

    /**
     * The known best feasible Solution
     */
    private Double minUpperBoundSolution;

    /**
     * The known best infeasible Solution
     */
    private Double maxLowerBoundSolution;


    public SolutionSet(){
    }
    
    public SolutionSet(ProblemSet p, FJSSTTproblem fjp, Solution minUpperBound, Solution maxLowerBound) {
   	 this("Solution for Problem with id " + p.hashCode(),
   			 p.hashCode(),
   			 p.getFjs(),
   			 p.getTransport(),
   			 p.getProperties(),
   			 new HashMap<String,List<ScheduledOperation>>(), // do solution object below
   			 minUpperBound!=null?minUpperBound.getObjectiveValue():Double.NEGATIVE_INFINITY,
				 maxLowerBound!=null?maxLowerBound.getObjectiveValue():Double.NEGATIVE_INFINITY);
   	 Solution s = minUpperBound;
   	 if(s==null)
   		 s=maxLowerBound;
        if (s != null) {
            for(int j=0;j<s.getOperationsBeginTimes().length;++j) {
                List<ScheduledOperation> jobschedule = new ArrayList<>();
                for(int o=0;o < s.getOperationsBeginTimes()[j].length && o < fjp.getProcessTimes()[j].length;++o) {
                    int machine = s.getOperationsMachineAssignments()[j][o];
                   int start = s.getOperationsBeginTimes()[j][o];
                   int end = start + fjp.getProcessTimes()[j][o][machine];
                   jobschedule.add(new ScheduledOperation(machine,j,o,start,end));
                }
                solution.put("Job"+j, jobschedule);
            }
        }
    }

    public SolutionSet(String name,
   		 				  int problemId,
   		 				  String problemFJS, 
   		 				  String problemTransport,
   		 				  String problemConfig, 
                       Map<String, List<ScheduledOperation>> solution,
                       Double minUpperBound,
                       Double maxLowerBound) {

        this.name = name;
        this.problemFJS = problemFJS;
        this.problemTransport = problemTransport;
        this.problemConfig = problemConfig;
        this.problemId = problemId;
        this.solution = solution;
        this.minUpperBoundSolution = minUpperBound;
        this.maxLowerBoundSolution = maxLowerBound;
    }

    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return hashCode of ProblemSet
     */
    public int getProblemId() {
       return problemId;
   }

    public String getProblemFJS() {
        return problemFJS;
    }
    
    public String getProblemTransport() {
       return problemTransport;
    }
    
    public String getProblemConfig() {
       return problemConfig;
    }

    public Map<String, List<ScheduledOperation>> getSolution() {
        return solution;
    }

    public Double getMinUpperBoundSolution() {
        return minUpperBoundSolution;
    }

    public Double getMaxLowerBoundSolution() {
       return maxLowerBoundSolution;
   }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setProblemId(int id){
   	 problemId = id;
    }

    public void setProblemFJS(String problemFJS) {
       this.problemFJS = problemFJS;
    }	
   
    public void setProblemTransport(String problemTransport) {
      this.problemTransport=problemTransport;
    }

    public void setProblemConfig(String problemConfig) {
       this.problemConfig=problemConfig;
    }

    public void setSolution(Map<String, List<ScheduledOperation>> solution) {
        this.solution = solution;
    }

    public void setMinUpperBoundSolution(Double obj) {
   	 this.minUpperBoundSolution = obj;
    }
    public void setMaxLowerBoundSolution(Double obj) {
   	 this.maxLowerBoundSolution = obj;
    }

    @Override
    public String toString() {
        return "SolutionSet{" +
                "name='" + name + '\'' +
                ", problemId='" + problemId + '\'' +
                ", problemFJS='" + problemFJS + '\'' +
                ", problemTransport='" + problemTransport + '\'' +
                ", problemConfig='" + problemConfig + '\'' +
                ", solution=\'" + solution + '\'' +
                ", minUpperBound=\'" + minUpperBoundSolution + '\'' +
                ", maxLowerBound=\'" + maxLowerBoundSolution + '\'' +
                '}';
    }
}
