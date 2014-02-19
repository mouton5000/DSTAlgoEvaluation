import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.GFLACAlgorithm;
import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.ShPAlgorithm;
import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.SteinerArborescenceApproximationAlgorithm;
import graphTheory.generators.steinLib.STPDirectedGenerator;
import graphTheory.generators.steinLib.STPGenerator;
import graphTheory.generators.steinLib.STPUndirectedGenerator;
import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.graph.UndirectedGraph;
import graphTheory.graphDrawer.EnergyAnalogyGraphDrawer;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.instances.steiner.classic.SteinerUndirectedInstance;
import graphTheory.steinLib.STPTranslator;
import graphTheory.steinLib.SteinLibInstancesGroups;
import graphTheory.utils.FileManager;

import java.awt.Color;
import java.io.File;
import java.util.HashSet;

/**
 * 
 * @author Watel Dimitri
 * 
 */
public class Main {

	public static void main(String[] args) {

		
		// Run this method to compute a small SteinerDirectedInstance, solve it and
		// display it on the screen.
//		exampleCreateInstanceAndDisplay();
		
		// Run one of those methods to compute a small SteinerUndirectedInstance
		// and transform it into a directed one : bidirected, acyclic or strongly connected
//		exampleTransformUndirectedIntoBidirected();
//		exampleTransformUndirectedIntoAcyclic();
//		exampleTransformUndirectedIntoStronglyConnected();
		
		// Run one of those methods to transform all the instances describe with the
		// STP format in the directory SteinLib/B into directed instances (bidirected
		// acyclic or strongly connected) and store them into their own directory.
//		exampleCreateBidirectedInstances();
//		exampleCreateAcyclicInstances();
//		exampleCreateSronglyConnectedInstances();
		
		// Run one of those methods to compute the GFLACAlgorithm over the previous
		// generated instances and show the results on standart output.
//		exampleLaunchBidirTest();
//		exampleLaunchAcyclicTest();
//		exampleLaunchStronglyTest();
		
	}

	/*------------------------------------------------------------------------
	 * 
	 * Example on how to use the Graph and SteinerDirectedInstance classes.
	 * 
	 *------------------------------------------------------------------------
	 */

	/**
	 * Create a Steiner instance and run an approximation over it. Finally draw
	 * the instance and the returned solution on the screen.
	 */
	public static void exampleCreateInstanceAndDisplay() {

		DirectedGraph dg = new DirectedGraph();

		// Add 5 vertices : 1, 2, 3, 4 and 5
		for (int i = 1; i <= 5; i++)
			dg.addVertice(i);

		// Add arcs
		dg.addDirectedEdges(1, 2, 3); // Add an arc (1,2) and an arc (1,3)
		dg.addDirectedEdges(2, 4, 5); // Add an arc (2,4) and an arc (2,5)
		dg.addDirectedEdges(3, 1, 5);
		dg.addDirectedEdges(4, 5);

		SteinerDirectedInstance sdi = new SteinerDirectedInstance(dg);
		sdi.setRoot(1); // Set the node 1 as the root of the instance
		sdi.setRequiredNodes(4, 5); // Set the nodes 4 and 5 as the terminals
		sdi.setCost(3, 5, 5); // Set the cost of the arc (3,5) as 5
		sdi.setCost(4, 5, 3); // Set the cost of the arc (4,5) as 3
		sdi.setCost(2, 5, 2); // Set the cost of the arc (2,5) as 2
		// Every other cost is the default cost: 1

		// Create an algorithm to solve or approximate the instance
		ShPAlgorithm alg = new ShPAlgorithm();
		alg.setInstance(sdi);
		alg.compute();

		// Display the solution, the cost of the solution and the time needed to
		// compute them
		System.out.println("Returned solution : " + alg.getArborescence());
		System.out.println("Cost: " + alg.getCost());
		System.out.println("Running Time: " + alg.getTime() + " ms");

		// Display the graph on the screen

		// We display the returned solution in red
		for (Arc a : alg.getArborescence())
			sdi.getGraph().setColor(a, Color.RED);
		// We now display the graph and the cost of the edges on the screen
		new EnergyAnalogyGraphDrawer(sdi.getGraph(), sdi.getCosts());
	}

	
	/*------------------------------------------------------------------------
	 * 
	 * Examples on how to transform an undirected steiner instance into a directed instance.
	 * 
	 *------------------------------------------------------------------------
	 */
	
	
	/**
	 * Transform a small undirected instance into a bidirected Steiner instance.
	 */
	public static void exampleTransformUndirectedIntoBidirected(){
		UndirectedGraph ug = new UndirectedGraph();

		// Add 5 vertices : 1, 2, 3, 4 and 5
		for (int i = 1; i <= 5; i++)
			ug.addVertice(i);

		// Add edges
		ug.addUndirectedEdges(1, 2, 3); // Add an edge (1,2) and an edge (1,3)
		ug.addUndirectedEdges(2, 3, 4, 5); // Add edges (2,3) (2,4) (2,5)

		SteinerUndirectedInstance sui = new SteinerUndirectedInstance(ug);
		sui.setRequiredNodes(1, 4, 5); // Set the nodes 1, 4 and 5 as the terminals
		sui.setCost(1, 3, 2); // Set the cost of (1,3) to 2
		// Every other cost is the default cost: 1
		
		// Transformation
		SteinerDirectedInstance sdi = SteinerDirectedInstance.getSymetrizedGraphFromUndirectedInstance(sui);
		
		// We now display the graph and the cost of the arcs on the screen
		new EnergyAnalogyGraphDrawer(sdi.getGraph(), sdi.getCosts());
	}

	
	
	/**
	 * Transform a small undirected instance into an directed acyclic Steiner instance.
	 */
	public static void exampleTransformUndirectedIntoAcyclic(){
		UndirectedGraph ug = new UndirectedGraph();

		// Add 5 vertices : 1, 2, 3, 4 and 5
		for (int i = 1; i <= 5; i++)
			ug.addVertice(i);

		// Add edges
		ug.addUndirectedEdges(1, 2, 3); // Add an edge (1,2) and an edge (1,3)
		ug.addUndirectedEdges(2, 3, 4, 5); // Add edges (2,3) (2,4) (2,5)

		SteinerUndirectedInstance sui = new SteinerUndirectedInstance(ug);
		sui.setRequiredNodes(1, 4, 5); // Set the nodes 1, 4 and 5 as the terminals
		sui.setCost(1, 3, 2); // Set the cost of (1,3) to 2
		// Every other cost is the default cost: 1

		// We describe the optimal solution. This could have been done
		// using an exact algorithm to solve Steiner
		HashSet<Arc> arborescence = new HashSet<Arc>();
		arborescence.add(new Arc(1,2,false));
		arborescence.add(new Arc(2,4,false));
		arborescence.add(new Arc(2,5,false));

		
		// Transformation
		SteinerDirectedInstance sdi = SteinerDirectedInstance.getAcyclicGraphFromUndirectedInstance(sui, arborescence);
		
		// We now display the graph and the cost of the arcs on the screen
		new EnergyAnalogyGraphDrawer(sdi.getGraph(), sdi.getCosts());
	}

	
	/**
	 * Transform a small undirected instance into a directed strongly connected Steiner instance.
	 */
	public static void exampleTransformUndirectedIntoStronglyConnected(){
		UndirectedGraph ug = new UndirectedGraph();

		// Add 5 vertices : 1, 2, 3, 4 and 5
		for (int i = 1; i <= 5; i++)
			ug.addVertice(i);

		// Add edges
		ug.addUndirectedEdges(1, 2, 3); // Add an edge (1,2) and an edge (1,3)
		ug.addUndirectedEdges(2, 3, 4, 5); // Add edges (2,3) (2,4) (2,5)

		SteinerUndirectedInstance sui = new SteinerUndirectedInstance(ug);
		sui.setRequiredNodes(1, 4, 5); // Set the nodes 1, 4 and 5 as the terminals
		sui.setCost(1, 3, 2); // Set the cost of (1,3) to 2
		// Every other cost is the default cost: 1

		// We describe the optimal solution. This could have been done
		// using an exact algorithm to solve Steiner
		HashSet<Arc> arborescence = new HashSet<Arc>();
		arborescence.add(new Arc(1,2,false));
		arborescence.add(new Arc(2,4,false));
		arborescence.add(new Arc(2,5,false));

		
		// Transformation
		SteinerDirectedInstance sdi = SteinerDirectedInstance.getRandomGraphStronglyConnectedFromUndirectedInstance(sui, arborescence);
		
		// We now display the graph and the cost of the arcs on the screen
		new EnergyAnalogyGraphDrawer(sdi.getGraph(), sdi.getCosts());
	}
	
	/*-------------------------------------------------------------------------------
	 * 
	 * 
	 * Example on how to generate the benchmarks of directed instances from a benchmark of undirected instances.
	 * 
	 * We assume:
	 * - that all the undirected instances of the SteinLib Benchmark were downloaded at http://steinlib.zib.de/ with the STP format and
	 * stored in a main SteinLib directory. For example, here, we assume this directory is "SteinLib/".
	 * - that each instance category has its own subdirectory. For example here, we test the algorithms over the
	 * category "B" from the "Sparse Complete Other" main category of instances (see http://http://steinlib.zib.de/testset.php).
	 * We downloaded the B instances in a subFolder named "SteinLib/B/"
	 * - that the results of category "B"  are in the folder "SteinLib/Results/B.results".
	 * 
	 * (Notice one can currently find all the results for all the categories in the directory "/SteinLibOptimalSolutions/")
	 * 
	 * -------------------------------------------------------------------------------
	 */

	public static void exampleCreateBidirectedInstances() {
		String steinLibMainDir = "SteinLib/";
		String steinLibSubDir = "B/";
		String steinLibTargetMainDir = "SteinLibBidir/";

		createBidirectedInstances(steinLibMainDir, steinLibSubDir,
				steinLibTargetMainDir);
	}

	public static void exampleCreateAcyclicInstances() {
		String steinLibMainDir = "SteinLib/";
		String steinLibSubDir = "B/";
		String steinLibTargetMainDir = "SteinLibAcyclic/";

		createAcyclicInstances(steinLibMainDir, steinLibSubDir,
				steinLibTargetMainDir);
	}
	
	public static void exampleCreateSronglyConnectedInstances() {
		String steinLibMainDir = "SteinLib/";
		String steinLibSubDir = "B/";
		String steinLibTargetMainDir = "SteinLibStrongly/";

		createStronglyConnectedInstances(steinLibMainDir, steinLibSubDir,
				steinLibTargetMainDir);
	}
	
	/**
	 * From undirected instances in "steinLibMainDir/steinLibSubDir", create
	 * bidirected instances, and store them with the STP format in the directory
	 * "steinLibTargetMainDir/steinLibSubDir".
	 * <p>
	 * If the original instance name is ABC01.stp, the name of the computed
	 * instance is ABC01bd.stp
	 * 
	 * 
	 * @param steinLibMainDir
	 * @param steinLibSubDir
	 * @param steinLibTargetMainDir
	 */
	public static void createBidirectedInstances(String steinLibMainDir,
			String steinLibSubDir, String steinLibTargetMainDir) {
		File f = new File(steinLibMainDir + steinLibSubDir);
		String name = f.listFiles()[0].getName();
		name = name.substring(0, name.length() - 4); 
		SteinLibInstancesGroups slig = SteinLibInstancesGroups.getGroup(name);

		// File containing for each instance the  optimal solution cost
		String resultFilePath = slig.getResultFileName();

		// Make the target directories if they do not exists.
		File mkdir = new File(steinLibTargetMainDir+steinLibSubDir);
		mkdir.mkdirs();
		mkdir = new File(steinLibTargetMainDir+"Results");
		mkdir.mkdirs();
		
		STPUndirectedGenerator gen = new STPUndirectedGenerator(steinLibMainDir
				+ steinLibSubDir, steinLibMainDir + resultFilePath);

		FileManager writeOptimalSolutionsValues = new FileManager();
		writeOptimalSolutionsValues.openErase(steinLibTargetMainDir+resultFilePath);
		for (int i = 0; i < gen.getNumberOfInstances(); i++) {
			SteinerUndirectedInstance sui = gen.generate();
			SteinerDirectedInstance sdi = SteinerDirectedInstance
					.getSymetrizedGraphFromUndirectedInstance(sui);
			
			String instanceName = sui.getGraph().getParam(
					STPGenerator.OUTPUT_NAME_PARAM_NAME)
			+ "bd";

			Integer instanceOptimumValue = sui.getGraph().getParamInteger(
					STPGenerator.OUTPUT_OPTIMUM_VALUE_PARAM_NAME);
			
			writeOptimalSolutionsValues.writeln(instanceName+" "+instanceOptimumValue);
			
			STPTranslator.translateSteinerGraph(
					sdi,
					steinLibTargetMainDir
							+ steinLibSubDir
							+ instanceName+".stp");
			
		}
		writeOptimalSolutionsValues.closeWrite();
	}

	/**
	 * From undirected instances in "steinLibMainDir/steinLibSubDir", create
	 * acyclic instances, and store them with the STP format in the directory
	 * "steinLibTargetMainDir/steinLibSubDir".
	 * <p>
	 * If the original instance name is ABC01.stp, the name of the computed
	 * instance is ABC01ac.stp
	 * 
	 * @param steinLibMainDir
	 * @param steinLibSubDir
	 * @param steinLibTargetMainDir
	 */
	public static void createAcyclicInstances(String steinLibMainDir,
			String steinLibSubDir, String steinLibTargetMainDir) {
		File f = new File(steinLibMainDir + steinLibSubDir);
		String name = f.listFiles()[0].getName();
		name = name.substring(0, name.length() - 4);
		SteinLibInstancesGroups slig = SteinLibInstancesGroups.getGroup(name);

		// File containing for each instance the  optimal solution cost
		String resultFilePath = slig.getResultFileName();


		// Make the target directories if they do not exists.
		File mkdir = new File(steinLibTargetMainDir+steinLibSubDir);
		mkdir.mkdirs();
		mkdir = new File(steinLibTargetMainDir+"Results");
		mkdir.mkdirs();
		
		STPUndirectedGenerator gen = new STPUndirectedGenerator(steinLibMainDir
				+ steinLibSubDir, steinLibMainDir + resultFilePath);

		FileManager writeOptimalSolutionsValues = new FileManager();
		writeOptimalSolutionsValues.openErase(steinLibTargetMainDir+resultFilePath);
		for (int i = 0; i < gen.getNumberOfInstances(); i++) {
			SteinerUndirectedInstance sui = gen.generate();
			
			@SuppressWarnings("unchecked")
			HashSet<Arc> arborescence = (HashSet<Arc>) sui.getGraph().getParam(
					STPGenerator.OUTPUT_OPTIMUM_PARAM_NAME);
			if(arborescence == null)
				continue;
			
			SteinerDirectedInstance sdi = SteinerDirectedInstance
					.getAcyclicGraphFromUndirectedInstance(
							sui, arborescence);
			
			String instanceName = sui.getGraph().getParam(
					STPGenerator.OUTPUT_NAME_PARAM_NAME)
			+ "ac";

			Integer instanceOptimumValue = sui.getGraph().getParamInteger(
					STPGenerator.OUTPUT_OPTIMUM_VALUE_PARAM_NAME);
			
			writeOptimalSolutionsValues.writeln(instanceName+" "+instanceOptimumValue);
			
			STPTranslator.translateSteinerGraph(
					sdi,
					steinLibTargetMainDir
							+ steinLibSubDir
							+ instanceName+".stp");
		}
		writeOptimalSolutionsValues.closeWrite();
	}

	/**
	 * From undirected instances in "steinLibMainDir/steinLibSubDir", create
	 * strongly connected instances, and store them with the STP format in the
	 * directory "steinLibTargetMainDir/steinLibSubDir".
	 * <p>
	 * If the original instance name is ABC01.stp, the name of the computed
	 * instance is ABC01st.stp
	 * 
	 * @param steinLibMainDir
	 * @param steinLibSubDir
	 * @param steinLibTargetMainDir
	 */
	public static void createStronglyConnectedInstances(
			String steinLibMainDir, String steinLibSubDir,
			String steinLibTargetMainDir) {
		File f = new File(steinLibMainDir + steinLibSubDir);
		String name = f.listFiles()[0].getName();
		name = name.substring(0, name.length() - 4);
		SteinLibInstancesGroups slig = SteinLibInstancesGroups.getGroup(name);

		// File containing for each instance the  optimal solution cost
		String resultFilePath = slig.getResultFileName();


		// Make the target directories if they do not exists.
		File mkdir = new File(steinLibTargetMainDir+steinLibSubDir);
		mkdir.mkdirs();
		mkdir = new File(steinLibTargetMainDir+"Results");
		mkdir.mkdirs();
		
		STPUndirectedGenerator gen = new STPUndirectedGenerator(steinLibMainDir
				+ steinLibSubDir, steinLibMainDir + resultFilePath);

		FileManager writeOptimalSolutionsValues = new FileManager();
		writeOptimalSolutionsValues.openErase(steinLibTargetMainDir+resultFilePath);
		for (int i = 0; i < gen.getNumberOfInstances(); i++) {
			SteinerUndirectedInstance sui = gen.generate();
			@SuppressWarnings("unchecked")
			HashSet<Arc> arborescence = (HashSet<Arc>) sui.getGraph().getParam(
					STPGenerator.OUTPUT_OPTIMUM_PARAM_NAME);
			if(arborescence == null)
				continue;
			
			SteinerDirectedInstance sdi = SteinerDirectedInstance
					.getRandomGraphStronglyConnectedFromUndirectedInstance(
							sui, arborescence);
			
			String instanceName = sui.getGraph().getParam(
					STPGenerator.OUTPUT_NAME_PARAM_NAME)
			+ "st";

			Integer instanceOptimumValue = sui.getGraph().getParamInteger(
					STPGenerator.OUTPUT_OPTIMUM_VALUE_PARAM_NAME);
			
			writeOptimalSolutionsValues.writeln(instanceName+" "+instanceOptimumValue);
			
			STPTranslator.translateSteinerGraph(
					sdi,
					steinLibTargetMainDir
							+ steinLibSubDir
							+ instanceName+".stp");
		}
		writeOptimalSolutionsValues.closeWrite();
	}

	/*-------------------------------------------------------------------------------
	 * 
	 * 
	 * Example on how to test an algorithm.
	 * The directed instances test must have already been generated using 
	 * the createBidirectedInstances, createAcyclicInstances, and createStronglyConnectedInstances methods.
	 * 
	 * -------------------------------------------------------------------------------
	 */

	/**
	 * This example lauch the evaluation of one algorithm over the instances in
	 * the B category transformed into bidirected instances.
	 * 
	 * We assume those instances were put in the "SteinLibBidir/B/" directory.
	 */
	public static void exampleLaunchBidirTest() {
		// We test that algorithm
		SteinerArborescenceApproximationAlgorithm alg = new GFLACAlgorithm();
		int nbInstancesIgnored = 0; // We do not ignore any instance

		// The directory containing all the SteinLib category folders
		// and the "results" folder as explaine previously.
		String steinLibMainDir = "SteinLibBidir/";

		String steinLibSubDir = "B/"; // Inside the main dir, the subdirectory
		// containing the instance is B/

		testAlgorithm(steinLibMainDir, steinLibSubDir, nbInstancesIgnored, alg);
	}

	/**
	 * This example lauch the evaluation of one algorithm over the instances in
	 * the B category transformed into acyclic instances.
	 * 
	 * We assume those instances were put in the "SteinLibAcyclic/B/" directory.
	 */
	public static void exampleLaunchAcyclicTest() {
		// We test that algorithm
		SteinerArborescenceApproximationAlgorithm alg = new GFLACAlgorithm();
		int nbInstancesIgnored = 0; // We do not ignore any instance

		// The directory containing all the SteinLib category folders
		// and the "results" folder as explaine previously.
		String steinLibMainDir = "SteinLibAcyclic/";

		String steinLibSubDir = "B/"; // Inside the main dir, the subdirectory
		// containing the instance is B/

		testAlgorithm(steinLibMainDir, steinLibSubDir, nbInstancesIgnored, alg);
	}
	
	/**
	 * This example lauch the evaluation of one algorithm over the instances in
	 * the B category transformed into strongly connected instances.
	 * 
	 * We assume those instances were put in the "SteinLibStrongly/B/" directory.
	 */
	public static void exampleLaunchStronglyTest() {
		// We test that algorithm
		SteinerArborescenceApproximationAlgorithm alg = new GFLACAlgorithm();
		int nbInstancesIgnored = 0; // We do not ignore any instance

		// The directory containing all the SteinLib category folders
		// and the "results" folder as explaine previously.
		String steinLibMainDir = "SteinLibStrongly/";

		String steinLibSubDir = "B/"; // Inside the main dir, the subdirectory
		// containing the instance is B/

		testAlgorithm(steinLibMainDir, steinLibSubDir, nbInstancesIgnored, alg);
	}
	
	/**
	 * Test the algorithm alg over all instances in the directory
	 * steinLibDir/steinLibSubDir/ Ignore the nbInstancesIgnored first instances
	 * <p>
	 * Instances in the directory steinLibDir/steinLibSubDir/ must use the STP
	 * format from the SteinLib benchmark.
	 * 
	 * @param steinLibMainDir
	 * @param steinLibSubDir
	 * @param nbInstancesIgnored
	 * @param alg
	 */
	public static void testAlgorithm(String steinLibMainDir,
			String steinLibSubDir, int nbInstancesIgnored,
			SteinerArborescenceApproximationAlgorithm alg) {

		// Description
		System.out.println("# Name OptimalCost NbNodes NbArcs NbTerminals MaximumArcCost AlgorithmAnswer AlgorithRunningTime");
		
		File f = new File(steinLibMainDir + steinLibSubDir);
		String name = f.listFiles()[0].getName();
		name = name.substring(0, name.length() - 6);
		SteinLibInstancesGroups slig = SteinLibInstancesGroups.getGroup(name);
		String path2 = slig.getResultFileName(); // File containing for each
		// instance the optimal
		// solution cost
		STPDirectedGenerator gen = new STPDirectedGenerator(steinLibMainDir
				+ steinLibSubDir, steinLibMainDir + path2);

		gen.incrIndex(nbInstancesIgnored);
		alg.setCheckFeasibility(false);
		for (int i = nbInstancesIgnored; i < gen.getNumberOfInstances(); i++) {
			SteinerDirectedInstance sdi = gen.generate();

			System.out.print(sdi.getGraph().getParam(
					STPDirectedGenerator.OUTPUT_NAME_PARAM_NAME)
					+ " "); // Show the name of the instance
			System.out.print(sdi.getGraph().getParam(
					STPDirectedGenerator.OUTPUT_OPTIMUM_VALUE_PARAM_NAME)
					+ " "); // Show the optimal cost of the instance
			System.out.print(sdi.getGraph().getNumberOfVertices() + " "
					+ sdi.getGraph().getNumberOfEdges() + " "
					+ sdi.getNumberOfRequiredVertices() + " " + sdi.maxCost()
					+ " "); // Show some informations of the instance

			alg.setInstance(sdi);
			alg.compute(); // Run the algorithm over the instance
			// Show the results and the running time
			System.out.print(alg.getCost() + " " + alg.getTime() + " ");
			System.out.println();
		}
	}
}
