import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.GFLACAlgorithm;
import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.RoosAlgorithm;
import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.ShPAlgorithm;
import graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation.SteinerArborescenceApproximationAlgorithm;
import graphTheory.generators.steinLib.STPDirectedGenerator;
import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.graphDrawer.EnergyAnalogyGraphDrawer;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.steinLib.SteinLibInstancesGroups;

import java.awt.Color;
import java.io.File;

/**
 * 
 * @author Watel Dimitri
 *
 */
public class Main {
	
	public static void main(String[] args) {
		example();
	}
	
	
	/**
	 * Create a Steiner instance and run an approximation over it.
	 * Finally draw the instance and the returned solution on the screen. 
	 */
	private static void example(){
		
		DirectedGraph dg = new DirectedGraph();
		
		// Add 5 vertices : 1, 2, 3, 4 and 5
		for(int i = 1; i<=5; i++)
			dg.addVertice(i);
		
		// Add arcs
		dg.addDirectedEdges(1, 2, 3); // Add an arc (1,2) and an arc (1,3)
		dg.addDirectedEdges(2, 4, 5); // Add an arc (2,4) and an arc (2,5)
		dg.addDirectedEdges(3, 1, 5);
		dg.addDirectedEdges(4, 5);
		
		SteinerDirectedInstance sdi = new SteinerDirectedInstance(dg);
		sdi.setRoot(1); // Set the node 1 as the root of the instance
		sdi.setRequiredNodes(4,5); // Set the nodes 4 and 5 as the terminals
		sdi.setCost(3, 5, 5); // Set the cost of the arc (3,5) as 5
		sdi.setCost(4, 5, 3); // Set the cost of the arc (4,5) as 3
		sdi.setCost(2, 5, 2); // Set the cost of the arc (2,5) as 2
		// Every other cost is the default cost: 1
		
		
		// Create an algorithm to solve or approximate the instance
		ShPAlgorithm alg = new ShPAlgorithm();
		alg.setInstance(sdi);
		alg.compute();
		
		// Display the solution, the cost of the solution and the time needed to compute them
		System.out.println("Returned solution : "+alg.getArborescence());
		System.out.println("Cost: "+alg.getCost());
		System.out.println("Running Time: "+alg.getTime()+" ms");
		
		// Display the graph on the screen
		
		// We display the returned solution in red
		for(Arc a : alg.getArborescence())
			sdi.getGraph().setColor(a, Color.RED);
		// We now display the graph and the cost of the edges on the screen
		new EnergyAnalogyGraphDrawer(sdi.getGraph(), sdi.getCosts());
	}
	
	/**
	 * Test the algorithm alg over all instances in the directory steinLibDir/steinLibSubDir/
	 * Ignore the nbInstancesIgnored first instances
	 * 
	 * @param steinLibDir
	 * @param steinLibSubDir
	 * @param nbInstancesIgnored
	 * @param alg
	 */
	private static void testAlgorithm(String steinLibDir, String steinLibSubDir, 
			int nbInstancesIgnored, SteinerArborescenceApproximationAlgorithm alg){
		

		File f = new File(steinLibDir+steinLibSubDir);
		String name = f.listFiles()[0].getName();
		name = name.substring(0, name.length()-6);
		SteinLibInstancesGroups slig = SteinLibInstancesGroups.getGroup(name);
		String path2 = slig.getResultFileName(); // File containing for each instance the optimal solution cost 
		STPDirectedGenerator gen = new STPDirectedGenerator(steinLibDir+steinLibSubDir,steinLibDir+path2);

		gen.incrIndex(nbInstancesIgnored);
		alg.setCheckFeasibility(false);
		for(int i = nbInstancesIgnored; i<gen.getNumberOfInstances(); i++){
			SteinerDirectedInstance sdi = gen.generate();

			System.out.print(sdi.getGraph().getParam(STPDirectedGenerator.OUTPUT_NAME_PARAM_NAME)+" "); // Show the name of the instance
			System.out.print(sdi.getGraph().getParam(STPDirectedGenerator.OUTPUT_OPTIMUM_VALUE_PARAM_NAME)+" "); // Show the optimal cost of the instance
			System.out.print(sdi.getGraph().getNumberOfVertices()
					+" "+sdi.getGraph().getNumberOfEdges()
					+" "+sdi.getNumberOfRequiredVertices()
					+" "+sdi.maxCost()+" "); // Show some informations of the instance

			alg.setInstance(sdi);
			alg.compute(); // Run the algorithm over the instance
			System.out.print(alg.getCost()+" "+alg.getTime()+" "); // Show the results and the running time
			
			System.out.println();
		}
	}
}
