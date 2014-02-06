package graphTheory.instances.shortestPath;

import graphTheory.graph.Graph;
import graphTheory.instances.ArcCostGraphInstance;

/**
 * Instance of the shortest path problem : given a graph and one node
 * {@link #source} of that graph, and weight over the arcs, return the shortest
 * paths from {@link #source} to all nodes.
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcShortestPathOneSourceInstance extends ArcCostGraphInstance {

	public ArcShortestPathOneSourceInstance(Graph g) {
		super(g);
	}

	private Integer source;

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	@Override
	public boolean hasSolution() {
		if (graph == null || this.costs == null)
			return false;
		else
			return true;
	}
}
