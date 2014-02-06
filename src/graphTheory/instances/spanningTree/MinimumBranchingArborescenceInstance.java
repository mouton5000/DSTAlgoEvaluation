package graphTheory.instances.spanningTree;

import graphTheory.graph.DirectedGraph;
import graphTheory.instances.ArcCostGraphInstance;

import java.util.Iterator;

/**
 * Instance of the minimum branching arborescence problem : given a graph and
 * one node {@link #root} of that graph, and weight over the arcs, return the
 * minimum cost directed tree rooted in {@link #root} spanning all the other
 * nodes.
 * 
 * @author Watel Dimitri
 * 
 */
public class MinimumBranchingArborescenceInstance extends ArcCostGraphInstance {

	public MinimumBranchingArborescenceInstance(DirectedGraph g) {
		super(g);
	}

	private Integer root;

	public Integer getRoot() {
		return root;
	}

	public void setRoot(Integer root) {
		this.root = root;
	}

	@Override
	public boolean hasSolution() {
		Iterator<Integer> it = this.getGraph().getVerticesIterator();
		Integer v;
		while (it.hasNext()) {
			v = it.next();
			if (!this.getGraph().areConnectedByDirectedPath(root, v))
				return false;
		}
		return true;
	}

}
