package at.ngmpps.fjsstt.model;

import java.util.HashMap;
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
    private Map<String, String> solution;

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
   	 this("Soltion for Problem with id " + p.hashCode(),
   			 p.getFjs(),
   			 p.getTransport(),
   			 p.getProperties(),
   			 new HashMap<String,String>(), // do solution object below
   			 minUpperBound!=null?minUpperBound.getObjectiveValue():Double.NEGATIVE_INFINITY,
				 maxLowerBound!=null?maxLowerBound.getObjectiveValue():Double.NEGATIVE_INFINITY);
   	 Solution s = minUpperBound;
   	 if(s==null)
   		 s=maxLowerBound;
   	 for(int i=0;i<s.getOperationsBeginTimes().length;++i) {
   		 StringBuilder sb = new StringBuilder();
   		 for(int j=0;j<s.getOperationsBeginTimes()[i].length;++j) {
   			 int maschine = s.getOperationsMachineAssignments()[i][j];
   			 //.append(j) do not need the operation just its begin tim
      		 sb.append("(").append(s.getOperationsBeginTimes()[i][j]).append(",")
      		 .append(maschine).append(",")
      		 .append(fjp.getProcessTimes()[i][j].length>maschine?fjp.getProcessTimes()[i][j][maschine]:"??").append(")");
   		 }
   		 solution.put("Job"+i, sb.toString());
   	 }
    }

    public SolutionSet(String name,
   		 				  String problemFJS, 
   		 				  String problemTransport,
   		 				  String problemConfig, 
                       Map<String, String> solution,
                       Double minUpperBound,
                       Double maxLowerBound) {

        this.name = name;
        this.problemFJS = problemFJS;
        this.problemTransport = problemTransport;
        this.problemConfig = problemConfig;
        this.solution = solution;
        this.minUpperBoundSolution = minUpperBound;
        this.maxLowerBoundSolution = maxLowerBound;
    }

    public String getName() {
        return name;
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

    public Map<String, String> getSolution() {
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

    public void setProblemFJS(String problemFJS) {
       this.problemFJS = problemFJS;
    }	
   
    public void setProblemTransport(String problemTransport) {
      this.problemTransport=problemTransport;
    }

    public void setProblemConfig(String problemConfig) {
       this.problemConfig=problemConfig;
    }

    public void setSolution(Map<String, String> solution) {
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
                ", problemFJS='" + problemFJS + '\'' +
                ", problemTransport='" + problemTransport + '\'' +
                ", problemConfig='" + problemConfig + '\'' +
                ", solution=\'" + solution + '\'' +
                ", minUpperBound=\'" + minUpperBoundSolution + '\'' +
                ", maxLowerBound=\'" + maxLowerBoundSolution + '\'' +
                '}';
    }
}
