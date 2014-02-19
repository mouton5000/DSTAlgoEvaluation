package graphTheory.algorithms.minimumBranching;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.Instance;
import graphTheory.instances.spanningTree.MinimumBranchingArborescenceInstance;

import java.util.HashSet;

/**
 * 
 * The minimum branching problem asks, in a directed weighted graph containing a
 * specific node r, for a directed tree rooted in r spanning all the graph at
 * minimum cost.
 * 
 * A MinimumBranchingAlgorithm instance is an algorithm which solve the minimum
 * branching problem instances.
 * 
 * @author Watel Dimitri
 * 
 */

public abstract class MinimumBranchingAlgorithm extends Algorithm<MinimumBranchingArborescenceInstance> {

	

	/**
	 * Output arborescence
	 */
	protected HashSet<Arc> arborescence;

	/**
	 * Cost of the output arborescence
	 */
	protected Integer cost;

	public HashSet<Arc> getArborescence() {
		return this.arborescence;
	}

	public Integer getCost() {
		return cost;
	}

	@Override
	protected void setNoSolution() {
		arborescence = null;
		cost = -1;
	}

}
