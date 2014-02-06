package graphTheory.instances.shortestPath;

import graphTheory.graph.Graph;
import graphTheory.instances.ArcCostGraphInstance;

/**
 * Instance of the shortest path problem : given a graph and one node
 * {@link #destination} of that graph, and weight over the arcs, return the
 * shortest paths to {@link #destination} from all nodes.
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcShortestPathOneDestinationInstance extends ArcCostGraphInstance {

	public ArcShortestPathOneDestinationInstance(Graph g) {
		super(g);
	}

	private Integer destination;

	public Integer getDestination() {
		return destination;
	}

	public void setDestination(Integer destination) {
		this.destination = destination;
	}

	@Override
	public boolean hasSolution() {
		if (graph == null || this.costs == null)
			return false;
		else
			return true;
	}
}
