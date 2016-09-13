package at.ngmpps.fjsstt.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.model.problem.FJSSTTproblem;
import at.ngmpps.fjsstt.model.problem.FJSSTTproblem.Objective;

public class ProblemParser {
	static final Logger logger = LoggerFactory.getLogger(ProblemParser.class);

	static Pattern firstlinePattern = Pattern.compile("(\\d+)\\W(\\d+)");
	static Pattern operationsLinePattern = Pattern.compile("(\\d+)\\W(.+)(\\d+)\\W(\\d+)\\W(\\d+)");
	static Pattern operationsProcessesPattern = Pattern.compile("(\\d+)");
	static Pattern transportTimesLinePatter = Pattern.compile("(\\d+)");

	public static String PROBLEM_FILE_EXTENSION = "fjs";
	public static String TRANSPORT_FILE_EXTENSION = "transport";
	public static String CONFIG_FILE_EXTENSION = "properties";

	public static final String SEARCH_NR_TIME_SLOTS_KEY = "SubgradientSearch.NrTimeSlots";
	public static final String TRANSPORT_FILE_KEY = "SubgradientSearch.TransportFile";

	// fjs file to pares
	File problemFile;

	Properties configuration = new Properties();
	// File file;

	// Objective is to fill to be able to create a FJSSTT_problem
	int jobs;

	int machines;

	// perJob
	int[] operations;

	int maxOperations;

	// make timeslots in the problem the maxDueDate found in the files
	int timeslotsMaxDueDate;

	// The sets of alternative machines per operation. A key is an
	// integer tuple (job,operation), the corresponding value is the set
	// of alternative machines.
	HashMap<String, List<Integer>> altMachines;

	// The first index is the job, the second index is the operation,
	// and the third index is the machine.
	int[][][] processTimes;

	int[][] travelTimes;

	// per job
	int[] dueDates;

	// job priorities
	int[] jobWeights;

	// default objective function for parsed files
	Objective objective = Objective.TARDINESS;

	public ProblemParser() {
	}

	/**
	 * Based on the fjs File find also a .properties file with the same name and
	 * the transport file configured there.
	 * 
	 * @param file
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static FJSSTTproblem parseProblemWithProblemFile(final File file) throws URISyntaxException, IOException {
		final ProblemParser parse = new ProblemParser();
		return parse.parseProblemConfig(file);
	}

	/**
	 * Based on the fjs File find also a .properties file with the same name and
	 * the transport file configured there.
	 * 
	 * @param file
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ProblemParser parseProblemFileOnly(final File file) throws URISyntaxException, IOException {
		List<File> files = checkOrFindFile(file, PROBLEM_FILE_EXTENSION);
		if (files.size() > 0) {
			problemFile = files.get(0);
			parseProblemFile();
		}
		return this;
	}

	
	/**
	 * Based on the fjs File find also a .properties file with the same name and
	 * the transport file configured there.
	 * 
	 * @param filename
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static FJSSTTproblem parseProblemWithProblemFile(final String filename) throws URISyntaxException, IOException {
		return parseProblemWithProblemFile(new File(filename));
	}

	/**
	 * Based on the properties File (=configuration) find also a problem file
	 * (.fjs) with the same name and the transport file configured.
	 * 
	 * @param filename
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static FJSSTTproblem parseProblemWithConfigFile(final String filename) throws URISyntaxException, IOException {
		final ProblemParser parse = new ProblemParser();
		return parse.parseConfigProblem(new File(filename));
	}
	
	
	public static FJSSTTproblem parseFiles(String problemFile, String configFile, String transportFile) throws URISyntaxException, IOException {
		final ProblemParser parse = new ProblemParser();
			parse.parseProblemFileOnly(new File(problemFile));
			if(configFile!=null && !configFile.isEmpty()) {
				parse.parseConfigurationFile(new File(configFile));
			}
			if(transportFile!=null && !transportFile.isEmpty())
				parse.parseTransportTimes(new File(transportFile));
		return parse.getProblem();
	}
	/**
	 * use this method for generating a problem if you have the content of the required files (ie you don't need to load from disk)
	 * @param problemContent
	 * @param configContent
	 * @param transportContent
	 * @return
	 */
	public static FJSSTTproblem parseStrings(String problemContent, String configContent, String transportContent) {
		final ProblemParser parse = new ProblemParser();
		try {
			parse.parseProblem(problemContent);
			if(configContent!=null && !configContent.isEmpty())
				parse.parseConfiguration(configContent);
			if(transportContent!=null && !transportContent.isEmpty() )
				parse.parseTransportTimesString(transportContent);
		} catch(Exception e) {
			logger.error(e.getClass().getName()+": "+e.getMessage());
		}
		return parse.getProblem();
	}

	/**
	 * Checks if the Given File is OK or if not searches for that file
	 * 
	 * @param fileOrFolderPath
	 *           may be the path to a file or to a folder, if the latter then the
	 *           fileExtension is used to get a list of files
	 * @param fileExtension
	 * @return
	 */
	public static List<File> checkOrFindFile(final File fileOrFolderPath, final String fileExtension) {
		List<File> files = new ArrayList<File>();
		if (fileOrFolderPath != null) {
			File found = fileOrFolderPath;
			if (!found.canRead()) {
				// :( ... use class loader to resolve that file
				final URL file = ProblemParser.class.getClassLoader().getResource(found.getAbsolutePath().toString());
				if (file != null) {
					try {
						found = new File(file.toURI());
						if (!found.canRead())
							found = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			if (found != null && found.isDirectory()) {
				// we received a directory. try to find the right file in this
				// directory
				try {
					for (File subfile : found.listFiles()) {
						if (!subfile.isDirectory()) {
							String problemName = subfile.toString();
							if (problemName.endsWith(fileExtension)) {
								files.add(subfile);
							}
						}
					}
				} catch (Exception e) {

				}
			} else if (found != null && found.isFile()) {
				files.add(found);
			}
		}
		return files;
	}

	public static List<File> findConfigurationFiles(File problemFilePath) throws IOException {
		return findFiles(problemFilePath, CONFIG_FILE_EXTENSION);
	}

	/**
	 * Takes a File or Folder as input and searches for files in the same folder
	 * with the given extensions for the first case (File) search for files with
	 * given extension and where the name starts equal to the given file
	 * WT1.fjs,properties -> WT1a.properties, WT1b.properties
	 * 
	 * @param problemFilePath
	 * @param someOtherFileExtension
	 * @return
	 * @throws IOException
	 */
	public static List<File> findFiles(File fileOrPath, String someOtherFileExtension) throws IOException {
		String FilePathName = null;
		File folder = null;
		ArrayList<File> resultFiles = new ArrayList<File>();
		File problemFilePath = fileOrPath;
		if (problemFilePath != null && !problemFilePath.canRead()) {
			// this checks the filename and eventually uses the classpathloader
			List<File> found = checkOrFindFile(problemFilePath, someOtherFileExtension);
			if (found != null && found.size() > 0)
				problemFilePath = found.get(0);
		}

		if (problemFilePath != null && problemFilePath.canRead()) {
			// its a file using that format name.ext
			if (problemFilePath.toString().lastIndexOf(".") > problemFilePath.toString().lastIndexOf(File.separatorChar)) {
				// all transport files starting with the same name
				FilePathName = problemFilePath.toString().substring(0, problemFilePath.toString().lastIndexOf('.'));
				// search in this folder for files that start with FilePathName and
				// end with .transport
				folder = problemFilePath.getParentFile();
			} else if (problemFilePath.isDirectory()) {
				// its a Folder!
				folder = problemFilePath;
				// all transport files in this folder
				FilePathName = problemFilePath.toString();
			}
			// we have selected WT1.fjs and want to find WT1a.properties &&
			// WT1b.properties, but not WT2.properties
			// alse
			// we have selected WT1a.properties and want to find WT1.fjs but not
			// WT2.fjs
			if (folder != null) {
				int substring = FilePathName.length();
				int folderCharPos = Math.max(FilePathName.lastIndexOf(File.separatorChar), 0);
				for (int s = substring; s > folderCharPos; s--) {
					for (File file : folder.listFiles()) {
						String fn = file.toString();
						if (fn.endsWith(someOtherFileExtension) && fn.startsWith(FilePathName))
							resultFiles.add(file);
					}
					if (resultFiles.isEmpty())
						FilePathName = FilePathName.substring(0, s - 1);
					else
						s = folderCharPos;
				}
			}
		}

		return resultFiles;
	}

	public static List<File> findProblemFiles(File problemFilePath) throws IOException {
		return findFiles(problemFilePath, ProblemParser.PROBLEM_FILE_EXTENSION);
	}

	public static List<File> findTransportFiles(File problemFilePath) throws IOException {
		return findFiles(problemFilePath, TRANSPORT_FILE_EXTENSION);
	}
	public static boolean getPropertyBool(Properties config, String key){
		return getPropertyBool(config, key, null);
	}
	public static boolean getPropertyBool(Properties config, String key, Boolean defaultval) {
		if (config.containsKey(key)) {
			String prop = config.getProperty(key);
			if (prop != null && !prop.isEmpty()) {
					return Boolean.parseBoolean(trimm(config.getProperty(key)));
			}
		}
		logger.error("Property Key {} not found or empty in Config {}.", key, config.toString());
		if(defaultval!=null)
			return defaultval;
		return false;
	}

	public static double getPropertyDouble(Properties config, String key){
		return getPropertyDouble(config,key,null);
	}
	public static double getPropertyDouble(Properties config, String key, Double defaultVal) {
		if (config.containsKey(key)) {
			String prop = config.getProperty(key);
			if (prop != null && !prop.isEmpty()) {
				return Double.parseDouble(trimm(prop));
			}
		}
		logger.error("Property Key {} not found or empty in Config {}.", key, config.toString());
		if(defaultVal!=null)
			return defaultVal;
		return 0.0;
	}
	public static int getPropertyInt(Properties config, String key) {
		return getPropertyInt(config,key,null);
	}
	public static int getPropertyInt(Properties config, String key,Integer defaultVal) {
		if (config.containsKey(key)) {

			String prop = config.getProperty(key);
			if (prop != null && !prop.isEmpty()) {
				return Integer.parseInt(trimm(prop));
			}
		}
		logger.error("Property Key {} not found or emtpy in Config {}.", key, config.toString());
		if(defaultVal!=null)
			return defaultVal;
		return 0;
	}
	public static String getPropertyString(Properties config, String key) {
		return getPropertyString(config,key,null);
	}
	public static String getPropertyString(Properties config, String key, String defaultVal) {
		if (config.containsKey(key)) {
			String prop = config.getProperty(key);
			if (prop != null && !prop.isEmpty()) {
				return trimm(config.getProperty(key));
			}
		}
		logger.error("Property Key {} not found or empty in Config {}.", key, config.toString());
		if(defaultVal!=null)
			return defaultVal;
		return "";
	}

	public static String trimm(String string) {
		// break at first non space char
		int i, ii;
		for (i = string.length() - 1; i > 0; --i)
			if (string.charAt(i) != ' ')
				break;
		for (ii = 0; ii < i; ++ii)
			if (string.charAt(ii) != ' ')
				break;
		return string.substring(ii, i + 1);
	}

	public FJSSTTproblem getProblem() {
		FJSSTTproblem problem = new FJSSTTproblem(jobs, operations, maxOperations, machines, timeslotsMaxDueDate, altMachines, processTimes, travelTimes,
				dueDates, objective, jobWeights, configuration);
		if(configuration!=null && configuration.containsKey(ProblemParser.SEARCH_NR_TIME_SLOTS_KEY))
			problem.setTimeSlots(Integer.parseInt(configuration.getProperty(ProblemParser.SEARCH_NR_TIME_SLOTS_KEY)));
		return problem;
	}

	public boolean getPropertyBool(String key) {
		return getPropertyBool(key, null);
	}
	public boolean getPropertyBool(String key, Boolean defaultVal) {
		return getPropertyBool(configuration, key, defaultVal);
	}

	public double getPropertyDouble(String key) {
		return getPropertyDouble(key, null);
	}
	
	public double getPropertyDouble(String key, Double defaultVal) {
		return getPropertyDouble(configuration, key, defaultVal);
	}

	public int getPropertyInt(String key) {
		return getPropertyInt(key, null);
	}

	public int getPropertyInt(String key, Integer defaultVal) {
		return getPropertyInt(configuration, key, defaultVal);
	}

	public String getPropertyString(String key) {
		return getPropertyString(key, null);
	}

	public String getPropertyString(String key, String defaultVal) {
		return getPropertyString(configuration, key, defaultVal);
	}

	/**
	 * Checks if the configuration File (.properties) is readable and parses it;
	 * additionally it searches for fjs problems files and takes one of the found
	 * files with a similar name. The config file is used to check if it contains
	 * the link to the transport file (SubgradientSearch.TRANSPORT_FILE_KEY); if
	 * so its parsed
	 * 
	 * @return
	 */
	public FJSSTTproblem parseConfigProblem(File configFile) {
		try {
			parseConfigurationFile(configFile);
			List<File> problemFiles = findProblemFiles(configFile);
			if (problemFiles != null && problemFiles.size() > 0) {
				problemFile = problemFiles.get(0);
				parseProblemFile();
			}
			if (configuration != null) {
				parseTransportTimes();
			} else {
				List<File> transportFiles = findTransportFiles(configFile);
				if (transportFiles != null && transportFiles.size() > 0) {
					parseTransportTimes(transportFiles.get(0));
				}
			}
			return getProblem(); 
		} catch (Exception io) {
			// its ok if something happens here. we still have the problem
			io.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if the problemFile is readable and parses it; additionally it
	 * searches for properties files (the config) and takes one of the found
	 * files. That file is used to check if it contains the link to the transport
	 * file (SubgradientSearch.TRANSPORT_FILE_KEY); if so its parsed
	 * 
	 * @return
	 */
	public FJSSTTproblem parseProblemConfig(File problemFjsFile) {
		List<File> files = checkOrFindFile(problemFile, PROBLEM_FILE_EXTENSION);
		if (files.size() > 0) {
			problemFile = files.get(0);
			try {
				parseProblemFile();
				try {
					List<File> multiplePropertiesFiles = findConfigurationFiles(problemFile);
					if (multiplePropertiesFiles != null && multiplePropertiesFiles.size() > 0) {
						parseConfigurationFile(multiplePropertiesFiles.get(0));
						parseTransportTimes();
					}
				} catch (Exception io) {
					// its ok if something happens here. we still have the problem
					io.printStackTrace();
				}
				return getProblem();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return null;
	}

	protected File parseProblemFile() throws IOException {
		if (problemFile != null && problemFile.canRead()) {
			BufferedReader reader = new BufferedReader(new FileReader(problemFile));
			parseProblem(reader);
			reader.close();
		}
		return problemFile;
	}
	
	public String parseProblem(String prob) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(prob));
		parseProblem(reader);
		reader.close();
		return prob;
	}
	
	public void parseProblem(BufferedReader reader) throws IOException {
			// key == int[](job,operation)
			altMachines = new HashMap<String, List<Integer>>();

			// read files line per line
			

			String currentLine = reader.readLine();

			// first line has 2 numbers: mJobs mMachines
			final Matcher firstline = ProblemParser.firstlinePattern.matcher(currentLine);
			firstline.matches();
			jobs = Integer.parseInt(firstline.group(1));
			machines = Integer.parseInt(firstline.group(2));

			dueDates = new int[jobs];
			operations = new int[jobs];
			jobWeights = new int[jobs];

			// to find max, we init this with 0
			timeslotsMaxDueDate = 0;
			maxOperations = 0;

			processTimes = new int[jobs][][];

			// one line per job
			for (int j = 0; j < jobs; ++j) {
				currentLine = reader.readLine();
				final Matcher operationsLine = ProblemParser.operationsLinePattern.matcher(currentLine);
				operationsLine.matches();
				operations[j] = new Integer(operationsLine.group(1));
				maxOperations = maxOperations > operations[j] ? maxOperations : operations[j];

				// main stuff in the form of: #Machines/Op (machine,processtime)
				// (machine,processtime)... #Machines/Op times
				// e.g.: 3 1 2 2 3 4 2 : 3 machines for this operation: machine
				// 1 needs 2, machine 2 needs 3, machine 4 needs 2
				// !!! need to adjust machineID: id=1 here its 0
				final String operationsForJob = operationsLine.group(2);

				// TODO releaseTime is not used; keep this line to keep the
				// semantics of this value
				final int releaseTime = new Integer(operationsLine.group(3));

				// there is a semantic difference between L. Mönch's DueDate
				// (time point)
				// vs our time-slot based point of view. Addition our first
				// time-slot == 0.
				// hence we reduce the dueDates parsed by 1
				dueDates[j] = new Integer(operationsLine.group(4)) - 1;
				timeslotsMaxDueDate = timeslotsMaxDueDate > dueDates[j] ? timeslotsMaxDueDate : dueDates[j];

				// priorities
				jobWeights[j] = new Integer(operationsLine.group(5));

				// parse line
				final Matcher operationsProcessTimes = ProblemParser.operationsProcessesPattern.matcher(operationsForJob);

				// parse per job per operation
				processTimes[j] = new int[operations[j]][];
				for (int o = 0; o < operations[j]; ++o) {
					// in last array we have all machines. but not all are
					// possible (=0)
					processTimes[j][o] = new int[machines];

					// how many tuples (machine, processingtime) do we have for
					// the
					// operation o
					operationsProcessTimes.find();
					final int altMachinesForOpCount = new Integer(operationsProcessTimes.group());
					final List<Integer> altMachinesForOp = new ArrayList<Integer>();
					for (int machineIdx = 0; machineIdx < altMachinesForOpCount; machineIdx++) {
						operationsProcessTimes.find();
						int machine = new Integer(operationsProcessTimes.group());
						// machine 1 in file is machine 0 here!
						machine--;
						altMachinesForOp.add(machine);
						operationsProcessTimes.find();
						final int time = new Integer(operationsProcessTimes.group());
						processTimes[j][o][machine] = time;
					}
					final String key = j + "-" + o;
					altMachines.put(key, altMachinesForOp);
				}
			}
			
	}

	/**
	 * parse the .properties file with the configuration (search for it, if not
	 * there)
	 * 
	 * @param properties
	 * @return
	 * @throws IOException
	 */
	public Properties parseConfigurationFile(File properties) throws IOException {
		// parse propertiesfile ([problem].conf) and add to problem
		List<File> found = checkOrFindFile(properties, CONFIG_FILE_EXTENSION);
		if (found.size() > 0)
			properties = found.get(0);
		if (properties != null && properties.canRead()) {
			configuration = new Properties();
			configuration.load(new FileReader(properties));
			return configuration;
		} else {
			configuration = null;
		}
		return null;
	}
	
	public Properties parseConfiguration(String properties) throws IOException {
		configuration = new Properties();
		configuration.load(new StringReader(properties));
		return configuration;
	}

	/**
	 * If a configuration is given, use the transport file there; else search for
	 * a transport file with a similar name as the problem file
	 * 
	 * @return
	 * @throws IOException
	 */
	public File parseTransportTimes() throws IOException {
		File file = null;
		if (configuration != null && configuration.containsKey(TRANSPORT_FILE_KEY)) {
			String tpfile = configuration.getProperty(TRANSPORT_FILE_KEY);
			if (tpfile != null && !tpfile.isEmpty() && !tpfile.equals(" ") && !tpfile.equals("  ") && !tpfile.equals("   ")) {
				List<File> transpFile = findFiles(new File(problemFile.getParentFile() + File.separator + tpfile), TRANSPORT_FILE_EXTENSION);
				if (transpFile != null && transpFile.size() > 0) {
					file = transpFile.get(0);
					parseTransportTimes(file);
				}
			}
		} else if (configuration == null) {
			List<File> transports = findTransportFiles(problemFile);
			if (transports != null && !transports.isEmpty())
				parseTransportTimes(transports.get(0));
		}
		return file;
	}

	public void parseTransportTimes(String pathToFile) throws IOException {
		List<File> transpFile = findFiles(new File(pathToFile), TRANSPORT_FILE_EXTENSION);
		if (transpFile != null && transpFile.size() > 0) {
			File file = transpFile.get(0);
			parseTransportTimes(file);
		}
	}
	
	public void parseTransportTimes(File transportFile) throws IOException {

		if (transportFile != null) {
			// try also travel time
			BufferedReader reader = new BufferedReader(new FileReader(transportFile));
			parseTransportTimes(reader);
			reader.close();
		}
	}
	public void parseTransportTimesString(String transportString) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(transportString));
		parseTransportTimes(reader);
		reader.close();
	}

	public void parseTransportTimes(BufferedReader reader) throws IOException {
		boolean initTravelTimes = false;
		// try also travel time
		travelTimes = new int[machines][];
		String currentLine = reader.readLine();
		if(currentLine!=null) {
			for (int machine = 0; currentLine != null && machine < machines; machine++) {
				initTravelTimes = true;
				// one line per machine
				travelTimes[machine] = new int[machines];
				final Matcher transportTimesLine = ProblemParser.transportTimesLinePatter.matcher(currentLine);
				for (int othermachine = 0; othermachine < machines; ++othermachine) {
					if (transportTimesLine.find()) {
						travelTimes[machine][othermachine] = Integer.parseInt(transportTimesLine.group());
					}
				}
				currentLine = reader.readLine();
			}
		}
		
		if (!initTravelTimes) {
			// we don't have times: init w/ 0
			travelTimes = new int[machines][];
			for (int i = 0; i < travelTimes.length; ++i) {
				travelTimes[i] = new int[machines];
				for (int ii = 0; ii < travelTimes[i].length; ++ii) {
					travelTimes[i][ii] = 0;
				}
			}
		}
	}
	
	
}
