package graphTheory.instances;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Graph;

/**
 * 
 * An instance is, in the algorithm and complexity theory, an input of a
 * problem, and more specifically in this project, a optimization problem.
 * 
 * This class contains more specific instances: ones defined over a graph.
 * 
 * Considering a specific instance of a specific problem, an {@link Algorithm}
 * is able to build the output of that problem, with this instance as input. A
 * optimization problem is a problem where various possible output exists
 * (called the feasible solutions), and we want to find one with minimum or
 * maximum cost.
 * 
 * As it is generally easy to determine if there is at least one feasible
 * solution or not, the instance contains a method {@link #hasSolution()} to do
 * so. If it answers false, the algorithm won't run over that instance.
 * 
 * @author Watel Dimitri
 * 
 */
public class GraphInstance extends Instance {

	protected Graph graph;

	public GraphInstance(Graph g) {
		this.graph = g;
	}

	public Graph getGraph() {
		return graph;
	}

	@Override
	public boolean hasSolution() {
		return true;
	}
}
