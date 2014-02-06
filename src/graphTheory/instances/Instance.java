package graphTheory.instances;

import graphTheory.algorithms.Algorithm;

/**
 * 
 * An instance is, in the algorithm and complexity theory, an input of a
 * problem, and more specifically in this project, a optimization problem.
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
public abstract class Instance {

	/**
	 * @return True if this instance has a solution. If not, no algorithm will
	 *         run over that instance.
	 */
	public abstract boolean hasSolution();
}
